package DAO;

import Conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DevolucionDAO {

    // REGISTRAR DEVOLUCIÓN PARCIAL
    public boolean registrar(int idVenta, int idProducto, int idUsuario,
                              int cantidad, String motivo,
                              String tipoReembolso, double montoReembolso) {
        String sql = "INSERT INTO Devolucion (idVenta, idProducto, idUsuario, " +
                     "cantidad, motivo, tipoReembolso, montoReembolso, tipoDev) " +
                     "VALUES (?,?,?,?,?,?,?,'Parcial')";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            ps.setInt(2, idProducto);
            ps.setInt(3, idUsuario);
            ps.setInt(4, cantidad);
            ps.setString(5, motivo);
            ps.setString(6, tipoReembolso);
            ps.setDouble(7, montoReembolso);

            boolean ok = ps.executeUpdate() > 0;

            if (ok) {
                // Actualizar estado venta a Parcial
                try (Connection con2 = Conexion.getConexion();
                     PreparedStatement ps2 = con2.prepareStatement(
                        "UPDATE Venta SET estado='Parcial' WHERE idVenta=? AND estado='Activa'")) {
                    ps2.setInt(1, idVenta);
                    ps2.executeUpdate();
                }

                // Registrar EGRESO en Flujo de Caja si reembolso en efectivo
                if (tipoReembolso.equals("Efectivo") && montoReembolso > 0) {
                    new FlujoCajaDAO().registrar("EGRESO",
                        "Devolución parcial Venta #" + idVenta,
                        montoReembolso, idUsuario, "DEV VENTA #" + idVenta);
                    if (tipoReembolso.equals("Efectivo") && montoReembolso > 0) {
                    new CajaChicaDAO().sumarEgreso(
                        Clases.Sesion.getIdEmpleado(), montoReembolso);
                }
                }
                
                // Asiento contable automático
                if (montoReembolso > 0) {
                    new LibroMayorDAO().registrarAsientoDevolucion(
                        idVenta, montoReembolso, idUsuario);
                }
            }
            return ok;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // VERIFICAR SI LA VENTA EXISTE Y ESTÁ ACTIVA O PARCIAL
    public boolean ventaExisteYActiva(int idVenta) {
        String sql = "SELECT estado FROM Venta WHERE idVenta=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String estado = rs.getString("estado");
                return estado.equals("Activa") || estado.equals("Parcial");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // OBTENER PRODUCTOS DE UNA VENTA
    public List<Object[]> getProductosVenta(int idVenta) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT dv.idProducto, p.nombre, dv.cantidad, dv.precioUnitario " +
                     "FROM DetalleVenta dv JOIN producto p ON dv.idProducto = p.idProducto " +
                     "WHERE dv.idVenta = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("idProducto"),
                    rs.getString("nombre"),
                    rs.getInt("cantidad"),
                    rs.getDouble("precioUnitario")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // VERIFICAR CANTIDAD YA DEVUELTA DE UN PRODUCTO EN UNA VENTA
    public int getCantidadDevuelta(int idVenta, int idProducto) {
        String sql = "SELECT COALESCE(SUM(cantidad), 0) as total " +
                     "FROM Devolucion WHERE idVenta=? AND idProducto=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            ps.setInt(2, idProducto);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // CANCELAR VENTA COMPLETA
    public boolean cancelarVenta(int idVenta, String motivo, String tipoReembolso, int idUsuario) {
        try (Connection con = Conexion.getConexion()) {
            con.setAutoCommit(false);

            // Obtener productos de la venta
            PreparedStatement psDetalle = con.prepareStatement(
                "SELECT idProducto, cantidad, precioUnitario FROM DetalleVenta WHERE idVenta=?");
            psDetalle.setInt(1, idVenta);
            ResultSet rs = psDetalle.executeQuery();

            while (rs.next()) {
                int idProducto   = rs.getInt("idProducto");
                int cantidad     = rs.getInt("cantidad");
                double precio    = rs.getDouble("precioUnitario");
                int yaDevuelto   = getCantidadDevueltaCon(con, idVenta, idProducto);
                int cantRestante = cantidad - yaDevuelto;

                if (cantRestante > 0) {
                    // Registrar devolución con tipoDev = Cancelacion
                    PreparedStatement psDev = con.prepareStatement(
                        "INSERT INTO Devolucion (idVenta, idProducto, idUsuario, " +
                        "cantidad, motivo, tipoReembolso, montoReembolso, tipoDev) " +
                        "VALUES (?,?,?,?,?,?,?,'Cancelacion')");
                    psDev.setInt(1, idVenta);
                    psDev.setInt(2, idProducto);
                    psDev.setInt(3, idUsuario);
                    psDev.setInt(4, cantRestante);
                    psDev.setString(5, motivo);
                    psDev.setString(6, tipoReembolso);
                    psDev.setDouble(7, cantRestante * precio);
                    psDev.executeUpdate();

                    // Devolver stock
                    PreparedStatement psStock = con.prepareStatement(
                        "UPDATE producto SET cantidad = cantidad + ? WHERE idProducto=?");
                    psStock.setInt(1, cantRestante);
                    psStock.setInt(2, idProducto);
                    psStock.executeUpdate();
                }
            }

            // Marcar venta como Anulada
            PreparedStatement psAnular = con.prepareStatement(
                "UPDATE Venta SET estado='Anulada' WHERE idVenta=?");
            psAnular.setInt(1, idVenta);
            psAnular.executeUpdate();

            con.commit();
            BitacoraDAO.registrar(idUsuario, "CANCELAR", "VENTAS",
                "Venta #" + idVenta + " cancelada - " + motivo);

            // Registrar EGRESO en Flujo de Caja por cancelación completa
            if (tipoReembolso.equals("Efectivo")) {
                try (Connection con3 = Conexion.getConexion();
                     PreparedStatement ps3 = con3.prepareStatement(
                        "SELECT total FROM Venta WHERE idVenta=?")) {
                    ps3.setInt(1, idVenta);
                    ResultSet rs3 = ps3.executeQuery();
                    if (rs3.next()) {
                        double totalVenta = rs3.getDouble("total");
                        new FlujoCajaDAO().registrar("EGRESO",
                            "Cancelación Venta #" + idVenta,
                            totalVenta, idUsuario, "CANCEL VENTA #" + idVenta);
                        new LibroMayorDAO().registrarAsientoDevolucion(
                            idVenta, totalVenta, idUsuario);
                    }
                }
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getCantidadDevueltaCon(Connection con, int idVenta, int idProducto) throws SQLException {
        PreparedStatement ps = con.prepareStatement(
            "SELECT COALESCE(SUM(cantidad), 0) as total FROM Devolucion " +
            "WHERE idVenta=? AND idProducto=?");
        ps.setInt(1, idVenta);
        ps.setInt(2, idProducto);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt("total");
        return 0;
    }

    // LISTAR TODAS
    public List<Object[]> listar() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT d.idDevolucion, d.idVenta, p.nombre, " +
                     "d.cantidad, d.motivo, d.tipoReembolso, " +
                     "d.montoReembolso, d.tipoDev, d.fecha " +
                     "FROM Devolucion d " +
                     "JOIN producto p ON d.idProducto = p.idProducto " +
                     "ORDER BY d.fecha DESC";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("idDevolucion"),
                    "#" + rs.getInt("idVenta"),
                    rs.getString("nombre"),
                    rs.getInt("cantidad"),
                    rs.getString("motivo"),
                    rs.getString("tipoReembolso"),
                    String.format("S/ %.2f", rs.getDouble("montoReembolso")),
                    rs.getString("tipoDev"),
                    rs.getString("fecha")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}