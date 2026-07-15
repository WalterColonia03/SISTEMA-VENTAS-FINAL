package DAO;

import Conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CotizacionProveedorDAO — GAP 4 del plan-gaps-erp.md
 *
 * Gestiona la tabla cotizacion_proveedor para comparar precios
 * entre proveedores para un mismo producto.
 */
public class CotizacionProveedorDAO {

    // ─── REGISTRAR / ACTUALIZAR COTIZACIÓN ───────────────────────────────────
    /**
     * Si ya existe una cotización vigente del mismo proveedor para el mismo
     * producto, la actualiza. Si no, inserta una nueva.
     */
    public boolean registrar(int idProducto, int idProveedor, double precio) {
        // Verificar si ya existe registro vigente para ese par producto-proveedor
        String sqlCheck = "SELECT idCotizacion FROM cotizacion_proveedor " +
                          "WHERE idProducto=? AND idProveedor=? AND vigente=1";
        try (Connection con = Conexion.getConexion();
             PreparedStatement psCheck = con.prepareStatement(sqlCheck)) {
            psCheck.setInt(1, idProducto);
            psCheck.setInt(2, idProveedor);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                // Actualizar precio existente
                int idCot = rs.getInt("idCotizacion");
                String sqlUpd = "UPDATE cotizacion_proveedor " +
                                "SET precioUnitario=?, fechaCotizacion=CURDATE() " +
                                "WHERE idCotizacion=?";
                try (PreparedStatement psUpd = con.prepareStatement(sqlUpd)) {
                    psUpd.setDouble(1, precio);
                    psUpd.setInt(2, idCot);
                    return psUpd.executeUpdate() > 0;
                }
            } else {
                // Insertar nueva cotización
                String sqlIns = "INSERT INTO cotizacion_proveedor " +
                                "(idProducto, idProveedor, precioUnitario, fechaCotizacion, vigente) " +
                                "VALUES (?,?,?,CURDATE(),1)";
                try (PreparedStatement psIns = con.prepareStatement(sqlIns)) {
                    psIns.setInt(1, idProducto);
                    psIns.setInt(2, idProveedor);
                    psIns.setDouble(3, precio);
                    return psIns.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ─── COMPARAR PRECIOS POR PRODUCTO ───────────────────────────────────────
    /**
     * Retorna todas las cotizaciones vigentes para un producto,
     * ordenadas de menor a mayor precio.
     * Cada Object[] = { proveedor, precio, fechaCotizacion, idProveedor }
     */
    public List<Object[]> compararPorProducto(int idProducto) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT p.razonSocial, cp.precioUnitario, cp.fechaCotizacion, p.idProveedor " +
                     "FROM cotizacion_proveedor cp " +
                     "JOIN proveedor p ON cp.idProveedor = p.idProveedor " +
                     "WHERE cp.idProducto = ? AND cp.vigente = 1 " +
                     "ORDER BY cp.precioUnitario ASC";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("razonSocial"),
                    rs.getDouble("precioUnitario"),
                    rs.getString("fechaCotizacion"),
                    rs.getInt("idProveedor")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ─── LISTAR TODOS (para la pestaña de administración) ────────────────────
    /**
     * Retorna todas las cotizaciones vigentes, con nombre de producto y proveedor.
     * Cada Object[] = { producto, proveedor, precio, fecha }
     */
    public List<Object[]> listarTodas() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT pr.nombre AS producto, pv.razonSocial AS proveedor, " +
                     "cp.precioUnitario, cp.fechaCotizacion " +
                     "FROM cotizacion_proveedor cp " +
                     "JOIN producto pr ON cp.idProducto = pr.idProducto " +
                     "JOIN proveedor pv ON cp.idProveedor = pv.idProveedor " +
                     "WHERE cp.vigente = 1 " +
                     "ORDER BY pr.nombre, cp.precioUnitario ASC";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("producto"),
                    rs.getString("proveedor"),
                    String.format("S/ %.2f", rs.getDouble("precioUnitario")),
                    rs.getString("fechaCotizacion")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ─── PRECIO MÍNIMO PARA UN PRODUCTO ──────────────────────────────────────
    /**
     * Devuelve el precio mínimo vigente registrado para un producto entre
     * todos los proveedores. Útil para resaltar el mejor precio en la UI.
     */
    public double getPrecioMinimo(int idProducto) {
        String sql = "SELECT MIN(precioUnitario) as minPrecio FROM cotizacion_proveedor " +
                     "WHERE idProducto=? AND vigente=1";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("minPrecio");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
