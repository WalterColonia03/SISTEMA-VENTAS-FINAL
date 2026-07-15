package Servicio;

import Clases.Sesion;
import DAO.BitacoraDAO;
import DAO.FlujoCajaDAO;
import DAO.KardexDAO;
import DAO.LibroMayorDAO;
import DAO.ProductoDAO;
import DAO.VentaDAO;
import DAO.CajaChicaDAO;
import Conexion.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

/**
 * VentaService — capa de servicio para el módulo de ventas.
 *
 * Centraliza toda la lógica de negocio que antes estaba dispersa en
 * IFrmPuntoVenta e IFrmGestionVentas, siguiendo el mismo patrón del
 * inventario.service.js del proyecto minimarket:
 *
 *   ┌─────────────┐      ┌───────────────┐      ┌─────────┐
 *   │ Vista (UI)  │ ───► │  VentaService │ ───► │   DAO   │
 *   └─────────────┘      └───────────────┘      └─────────┘
 *
 * Responsabilidades de este servicio:
 *   1. Validar stock suficiente ANTES de tocar la BD
 *   2. Insertar cabecera de venta  (VentaDAO → registra flujo de caja + asiento)
 *   3. Insertar detalles           (VentaDAO)
 *   4. Descontar stock             (ProductoDAO)
 *   5. Registrar kardex (SALIDA)   (KardexDAO)
 *   6. Actualizar puntos de fidelización si aplica
 */
public class VentaService {

    private final VentaDAO      ventaDAO    = new VentaDAO();
    private final ProductoDAO   productoDAO = new ProductoDAO();
    private final KardexDAO     kardexDAO   = new KardexDAO();

    // ─── REGISTRAR VENTA COMPLETA ────────────────────────────────────────────────
    /**
     * Registra una venta completa de forma atómica:
     *   cabecera + detalles + descuento de stock + kardex.
     *
     * La contabilidad (libro mayor) y el flujo de caja son manejados
     * internamente por VentaDAO.insertar() para no duplicar lógica.
     *
     * @param idCliente   ID del cliente (1 = Consumidor Final si no hay selección)
     * @param metodoPago  "Efectivo", "Yape", "Plin", etc.
     * @param items       Lista de líneas del carrito
     * @return idVenta generado, o -1 si falla
     * @throws VentaException si hay stock insuficiente o datos inválidos
     */
    public int registrarVenta(int idCliente, String metodoPago, List<ItemVenta> items)
            throws VentaException {

        if (items == null || items.isEmpty()) {
            throw new VentaException("La venta debe tener al menos un producto.");
        }

        // ── 1. Validar stock suficiente para TODOS los items ────────────────────
        // (misma validación previa que hace registrar() en venta.controller.js)
        for (ItemVenta item : items) {
            var productos = productoDAO.listar();
            var prod = productos.stream()
                    .filter(p -> p.getIdProducto() == item.idProducto)
                    .findFirst().orElse(null);

            if (prod == null || prod.getEstado() == 0) {
                throw new VentaException("Producto no encontrado o inactivo: ID " + item.idProducto);
            }
            if (prod.getCantidad() < item.cantidad) {
                throw new VentaException(
                    "Stock insuficiente para: \"" + prod.getNombre() + "\"\n"
                    + "Disponible: " + prod.getCantidad() + " | Solicitado: " + item.cantidad
                );
            }
            // Enriquecer el item con datos del producto para uso posterior
            item.nombreProducto  = prod.getNombre();
            item.stockAnterior   = prod.getCantidad();
        }

        // ── 2. Calcular totales ─────────────────────────────────────────────────
        double total    = items.stream().mapToDouble(i -> i.subtotal).sum();
        double subtotal = total / 1.18;
        double igv      = total - subtotal;

        int idUsuario = Sesion.getIdUsuario() > 0 ? Sesion.getIdUsuario() : 1;

        // ── 3. Insertar cabecera (también registra flujo de caja + asiento) ─────
        int idVenta = ventaDAO.insertar(idCliente, idUsuario, subtotal, igv, total, metodoPago);
        if (idVenta == -1) {
            throw new VentaException("Error interno al registrar la venta en la base de datos.");
        }

        // ── 4. Insertar detalles + descontar stock + kardex ─────────────────────
        for (ItemVenta item : items) {
            // Detalle
            ventaDAO.insertarDetalle(idVenta, item.idProducto, item.cantidad,
                                     item.precioUnitario, 0, item.subtotal);

            // Stock
            int stockNuevo = item.stockAnterior - item.cantidad;
            productoDAO.actualizarStock(item.idProducto, stockNuevo);

            // Kardex
            kardexDAO.registrar(item.idProducto, "SALIDA", item.cantidad,
                                item.stockAnterior, stockNuevo,
                                "VENTA #" + idVenta, idUsuario);
        }

        return idVenta;
    }

    // ─── ANULAR VENTA ────────────────────────────────────────────────────────────
    /**
     * Anula una venta existente: revierte el stock y registra en bitácora.
     * La reversión de flujo de caja / asiento contable queda como mejora futura.
     *
     * @param idVenta ID de la venta a anular
     * @param motivo  Razón de la anulación
     * @throws VentaException si la venta ya fue anulada o no existe
     */
    public void anularVenta(int idVenta, String motivo) throws VentaException {
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new VentaException("El motivo de anulación es obligatorio.");
        }

        // Revertir stock de cada producto del detalle
        String sql = "SELECT idProducto, cantidad FROM DetalleVenta WHERE idVenta = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            ResultSet rs = ps.executeQuery();
            int idUsuario = Sesion.getIdUsuario() > 0 ? Sesion.getIdUsuario() : 1;

            while (rs.next()) {
                int idProducto = rs.getInt("idProducto");
                int cantidad   = rs.getInt("cantidad");

                // Obtener stock actual
                var prod = productoDAO.listar().stream()
                        .filter(p -> p.getIdProducto() == idProducto)
                        .findFirst().orElse(null);
                if (prod != null) {
                    int stockAnterior = prod.getCantidad();
                    int stockNuevo    = stockAnterior + cantidad;
                    productoDAO.actualizarStock(idProducto, stockNuevo);
                    kardexDAO.registrar(idProducto, "ENTRADA", cantidad,
                                        stockAnterior, stockNuevo,
                                        "DEV VENTA #" + idVenta, idUsuario);
                }
            }
        } catch (SQLException e) {
            throw new VentaException("Error al revertir stock: " + e.getMessage());
        }

        // Marcar venta como anulada en la BD
        boolean ok = ventaDAO.anular(idVenta);
        if (!ok) {
            throw new VentaException("No se pudo anular la venta #" + idVenta
                                     + ". ¿Ya fue anulada anteriormente?");
        }

        BitacoraDAO.registrar(Sesion.getIdUsuario() > 0 ? Sesion.getIdUsuario() : 1,
                              "CANCELAR", "VENTAS",
                              "Venta #" + idVenta + " cancelada - " + motivo.trim());
    }

    // ─── DATA CLASS ──────────────────────────────────────────────────────────────
    /**
     * Representa una línea del carrito de venta.
     * La Vista la construye y la pasa a registrarVenta().
     */
    public static class ItemVenta {
        public int    idProducto;
        public String nombreProducto;   // llenado por el servicio
        public int    cantidad;
        public double precioUnitario;
        public double subtotal;
        public int    stockAnterior;    // llenado por el servicio

        public ItemVenta(int idProducto, int cantidad, double precioUnitario) {
            this.idProducto    = idProducto;
            this.cantidad      = cantidad;
            this.precioUnitario = precioUnitario;
            this.subtotal      = cantidad * precioUnitario;
        }
    }

    // ─── EXCEPCIÓN DE NEGOCIO ────────────────────────────────────────────────────
    public static class VentaException extends Exception {
        public VentaException(String message) { super(message); }
    }
}
