package DAO;

import Conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CotizacionDAO {

    // ─── INSERTAR CABECERA ───────────────────────────────────────────────────────
    public int insertar(int idCliente, int idUsuario, String vigencia,
                        double subtotal, double igv, double total, String observaciones) {
        String sql = "INSERT INTO cotizacion (idCliente, idUsuario, vigencia, subtotal, igv, total, estado, observaciones) "
                   + "VALUES (?,?,?,?,?,?,'Pendiente',?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idCliente);
            ps.setInt(2, idUsuario);
            ps.setString(3, vigencia);
            ps.setDouble(4, subtotal);
            ps.setDouble(5, igv);
            ps.setDouble(6, total);
            ps.setString(7, observaciones);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        } catch (SQLException e) { e.printStackTrace(); return -1; }
    }

    // ─── INSERTAR DETALLE ────────────────────────────────────────────────────────
    public boolean insertarDetalle(int idCotizacion, int idProducto, int cantidad,
                                   double precioUnitario, double subtotal) {
        String sql = "INSERT INTO detalle_cotizacion (idCotizacion, idProducto, cantidad, precioUnitario, subtotal) VALUES (?,?,?,?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCotizacion); ps.setInt(2, idProducto);
            ps.setInt(3, cantidad); ps.setDouble(4, precioUnitario); ps.setDouble(5, subtotal);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ─── CAMBIAR ESTADO ──────────────────────────────────────────────────────────
    public boolean cambiarEstado(int idCotizacion, String nuevoEstado) {
        String sql = "UPDATE cotizacion SET estado=? WHERE idCotizacion=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado); ps.setInt(2, idCotizacion);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ─── LISTAR HISTORIAL ────────────────────────────────────────────────────────
    public List<Object[]> listar(String filtro) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT co.idCotizacion, CONCAT(c.nombre,' ',c.apellido) as cliente, "
                   + "co.fecha, co.vigencia, co.total, co.estado "
                   + "FROM cotizacion co JOIN cliente c ON co.idCliente = c.idCliente "
                   + (filtro.isEmpty() ? "" : "WHERE CONCAT(c.nombre,' ',c.apellido) LIKE ? ")
                   + "ORDER BY co.fecha DESC";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (!filtro.isEmpty()) ps.setString(1, "%" + filtro + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                    "#" + rs.getInt("idCotizacion"),
                    rs.getString("cliente"),
                    rs.getString("fecha"),
                    rs.getString("vigencia"),
                    String.format("S/ %.2f", rs.getDouble("total")),
                    rs.getString("estado")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // ─── OBTENER DETALLE ─────────────────────────────────────────────────────────
    public List<Object[]> getDetalle(int idCotizacion) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT p.nombre, dc.cantidad, dc.precioUnitario, dc.subtotal "
                   + "FROM detalle_cotizacion dc JOIN producto p ON dc.idProducto = p.idProducto "
                   + "WHERE dc.idCotizacion=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCotizacion);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("nombre"),
                    rs.getInt("cantidad"),
                    String.format("%.2f", rs.getDouble("precioUnitario")),
                    String.format("%.2f", rs.getDouble("subtotal"))
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // ─── OBTENER CABECERA (para convertir a venta) ───────────────────────────────
    public int getIdCliente(int idCotizacion) {
        String sql = "SELECT idCliente FROM cotizacion WHERE idCotizacion=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCotizacion);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("idCliente") : 1;
        } catch (SQLException e) { e.printStackTrace(); return 1; }
    }

    public List<Object[]> getDetalleConIds(int idCotizacion) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT dc.idProducto, p.nombre, dc.cantidad, dc.precioUnitario, dc.subtotal "
                   + "FROM detalle_cotizacion dc JOIN producto p ON dc.idProducto = p.idProducto "
                   + "WHERE dc.idCotizacion=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCotizacion);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("idProducto"),
                    rs.getString("nombre"),
                    rs.getInt("cantidad"),
                    rs.getDouble("precioUnitario"),
                    rs.getDouble("subtotal")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}
