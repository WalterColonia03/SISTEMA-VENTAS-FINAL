package Servicio;

import Clases.Sesion;
import DAO.BitacoraDAO;
import DAO.CompraDAO;
import DAO.CuentasCobrarPagarDAO;
import DAO.FlujoCajaDAO;
import DAO.KardexDAO;
import DAO.LibroMayorDAO;
import DAO.ProductoDAO;
import Conexion.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * CompraService — capa de servicio para el módulo de compras.
 *
 * Centraliza toda la lógica que estaba en IFrmRegistroCompras.registrarCompra():
 *
 *   ┌──────────────────────┐      ┌───────────────┐      ┌─────────┐
 *   │ IFrmRegistroCompras  │ ───► │ CompraService │ ───► │   DAO   │
 *   └──────────────────────┘      └───────────────┘      └─────────┘
 *
 * Responsabilidades:
 *   1. Validar datos de la cabecera y los ítems
 *   2. Insertar la cabecera en Compra         (CompraDAO)
 *   3. Actualizar condición/estado de pago    (SQL directo)
 *   4. Insertar detalles en DetalleCompra     (CompraDAO)
 *   5. Sumar stock al producto                (ProductoDAO)
 *   6. Registrar kardex (ENTRADA)             (KardexDAO)
 *   7. Registrar CxP si es a crédito          (CuentasCobrarPagarDAO)
 *   8. Registrar egreso en flujo de caja si es contado (FlujoCajaDAO)
 *   9. Asentar en libro mayor                 (LibroMayorDAO)
 */
public class CompraService {

    private final CompraDAO              compraDAO  = new CompraDAO();
    private final ProductoDAO            productoDAO = new ProductoDAO();
    private final KardexDAO              kardexDAO   = new KardexDAO();
    private final FlujoCajaDAO           flujoCajaDAO = new FlujoCajaDAO();
    private final LibroMayorDAO          libroDAO    = new LibroMayorDAO();
    private final CuentasCobrarPagarDAO  cxpDAO      = new CuentasCobrarPagarDAO();

    // ─── REGISTRAR COMPRA COMPLETA ───────────────────────────────────────────────
    /**
     * Registra una compra completa:
     *   cabecera + detalles + stock (ENTRADA) + kardex + contabilidad.
     *
     * @param idProveedor   ID del proveedor seleccionado
     * @param nroDocumento  Número de factura/guía
     * @param condicion     "Contado", "Crédito 30 días", etc.
     * @param nombreProv    Razón social del proveedor (para CxP y mensajes)
     * @param items         Lista de líneas del detalle de compra
     * @return idCompra generado
     * @throws CompraException si hay datos inválidos o error de BD
     */
    public int registrarCompra(int idProveedor, String nroDocumento,
                               String condicion, String nombreProv,
                               List<ItemCompra> items) throws CompraException {

        // ── 1. Validaciones ─────────────────────────────────────────────────────
        if (nroDocumento == null || nroDocumento.trim().isEmpty()) {
            throw new CompraException("El número de documento es obligatorio.");
        }
        if (items == null || items.isEmpty()) {
            throw new CompraException("La compra debe tener al menos un producto.");
        }
        for (ItemCompra item : items) {
            if (item.cantidad <= 0) {
                throw new CompraException("La cantidad del producto \"" + item.nombreProducto
                                          + "\" debe ser mayor a 0.");
            }
            if (item.precioUnitario <= 0) {
                throw new CompraException("El precio del producto \"" + item.nombreProducto
                                          + "\" debe ser mayor a 0.");
            }
        }

        // ── 2. Calcular totales ─────────────────────────────────────────────────
        double subtotal = items.stream().mapToDouble(i -> i.subtotal).sum();
        double igv      = subtotal * 0.18;
        double total    = subtotal + igv;

        int idUsuario = Sesion.getIdUsuario() > 0 ? Sesion.getIdUsuario() : 1;

        // ── 3. Insertar cabecera ─────────────────────────────────────────────────
        int idCompra = compraDAO.insertar(idProveedor, idUsuario,
                                          nroDocumento.trim(), subtotal, igv, total);
        if (idCompra == -1) {
            throw new CompraException("Error interno al registrar la compra en la base de datos.");
        }

        // ── 4. Actualizar condición y estado de pago ─────────────────────────────
        String estadoPago = condicion.equals("Contado") ? "Pagado" : "Pendiente";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(
                 "UPDATE Compra SET condicionPago=?, estadoPago=? WHERE idCompra=?")) {
            ps.setString(1, condicion);
            ps.setString(2, estadoPago);
            ps.setInt(3, idCompra);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new CompraException("Error al actualizar condición de pago: " + ex.getMessage());
        }

        // ── 5. Detalles + Stock + Kardex + Lote (GAP 8) ─────────────────────────
        for (ItemCompra item : items) {
            int idDetalle = compraDAO.insertarDetalle(idCompra, item.idProducto, item.cantidad,
                                      item.precioUnitario, item.lote, item.fechaVencimiento,
                                      item.subtotal);

            var prod = productoDAO.listar().stream()
                    .filter(p -> p.getIdProducto() == item.idProducto)
                    .findFirst().orElse(null);

            if (prod != null) {
                int stockAnterior = prod.getCantidad();
                int stockNuevo    = stockAnterior + item.cantidad;
                productoDAO.actualizarStock(item.idProducto, stockNuevo);
                kardexDAO.registrar(item.idProducto, "ENTRADA", item.cantidad,
                                    stockAnterior, stockNuevo,
                                    "COMPRA #" + idCompra, idUsuario);

                // GAP 8 — Trazabilidad de lotes: insertar en loteinventario
                registrarLote(item.idProducto, idDetalle > 0 ? idDetalle : -1,
                              item.lote, item.cantidad, item.precioUnitario, item.fechaVencimiento);

                // GAP 6 — Actualizar costo promedio ponderado
                actualizarCostoPromedio(item.idProducto, stockAnterior, prod.getCostoPromedio(), item.cantidad, item.precioUnitario);
            }
        }

        // ── 6. Contabilidad y flujo de caja ──────────────────────────────────────
        if (!condicion.equals("Contado")) {
            // Crédito: registrar CxP y asiento de crédito
            int dias   = condicion.contains("30") ? 30 : condicion.contains("60") ? 60 : 90;
            String fVenc = LocalDate.now().plusDays(dias).toString();
            cxpDAO.registrarPagar(nombreProv, nroDocumento.trim(),
                                  total, LocalDate.now().toString(),
                                  fVenc, condicion, idCompra, idUsuario);
            libroDAO.registrarAsientoCompraCredito(
                    idCompra, nroDocumento.trim(), total, subtotal, igv, idUsuario);
        } else {
            // Contado: registrar egreso en flujo de caja y asiento de contado
            flujoCajaDAO.registrar("EGRESO",
                    "Compra #" + idCompra + " - " + nroDocumento.trim(),
                    total, idUsuario, "COMPRA #" + idCompra);
            libroDAO.registrarAsientoCompraContado(
                    idCompra, nroDocumento.trim(), total, subtotal, igv, idUsuario);
        }

        return idCompra;
    }

    // ─── DATA CLASS ──────────────────────────────────────────────────────────────
    /**
     * Representa una línea del detalle de compra.
     * La Vista la construye leyendo cada fila del modelDetalle.
     */
    public static class ItemCompra {
        public int    idProducto;
        public String nombreProducto;
        public int    cantidad;
        public double precioUnitario;
        public double subtotal;
        public String lote;
        public String fechaVencimiento;

        public ItemCompra(int idProducto, String nombreProducto, int cantidad,
                          double precioUnitario, String lote, String fechaVencimiento) {
            this.idProducto       = idProducto;
            this.nombreProducto   = nombreProducto;
            this.cantidad         = cantidad;
            this.precioUnitario   = precioUnitario;
            this.subtotal         = cantidad * precioUnitario;
            this.lote             = lote;
            this.fechaVencimiento = fechaVencimiento;
        }
    }

    // ─── EXCEPCIÓN DE NEGOCIO ────────────────────────────────────────────────────
    public static class CompraException extends Exception {
        public CompraException(String message) { super(message); }
    }

    // ─── GAP 8: Registrar lote en loteinventario ─────────────────────────────────
    private void registrarLote(int idProducto, int idDetalleCompra,
                                String numeroLote, int cantidad,
                                double costoUnitario, String fechaVencimiento) {
        String sql = "INSERT INTO loteinventario " +
                     "(idProducto, idDetalleCompra, numeroLote, cantidadInicial, " +
                     "cantidadRestante, costoUnitario, fechaVencimiento) " +
                     "VALUES (?,?,?,?,?,?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            if (idDetalleCompra > 0) ps.setInt(2, idDetalleCompra);
            else ps.setNull(2, java.sql.Types.INTEGER);
            ps.setString(3, (numeroLote == null || numeroLote.isEmpty()) ? null : numeroLote);
            ps.setInt(4, cantidad);
            ps.setInt(5, cantidad);
            ps.setDouble(6, costoUnitario);
            ps.setString(7, (fechaVencimiento == null || fechaVencimiento.isEmpty()) ? null : fechaVencimiento);
            ps.executeUpdate();
        } catch (SQLException e) {
            // No es crítico; log sin lanzar excepción para no abortar la compra
            System.err.println("[CompraService] Error al registrar lote: " + e.getMessage());
        }
    }

    // ─── GAP 6: Actualizar costo promedio ponderado ──────────────────────────────
    private void actualizarCostoPromedio(int idProducto, int stockAnterior,
                                          double costoAnterior, int cantidadNueva,
                                          double costoNuevo) {
        double costoActualizado;
        if (stockAnterior + cantidadNueva == 0) {
            costoActualizado = costoNuevo;
        } else {
            costoActualizado = ((costoAnterior * stockAnterior) + (costoNuevo * cantidadNueva))
                               / (double)(stockAnterior + cantidadNueva);
        }
        String sql = "UPDATE producto SET costoPromedio=? WHERE idProducto=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, costoActualizado);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[CompraService] Error al actualizar costoPromedio: " + e.getMessage());
        }
    }
}
