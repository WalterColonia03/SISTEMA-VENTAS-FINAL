package DAO;

import Conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CajaChicaDAO {

    // ABRIR CAJA
    public boolean abrirCaja(int idEmpleado, double montoApertura) {
        String sql = "INSERT INTO CajaChica (idEmpleado, montoApertura) VALUES (?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ps.setDouble(2, montoApertura);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // VERIFICAR SI TIENE CAJA ABIERTA HOY
    public boolean tieneCajaAbierta(int idEmpleado) {
        String sql = "SELECT 1 FROM CajaChica WHERE idEmpleado=? AND estado='Abierta' " +
                     "AND DATE(fechaApertura)=CURDATE()";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            return ps.executeQuery().next();
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // OBTENER CAJA ABIERTA DEL EMPLEADO
    public Object[] getCajaAbierta(int idEmpleado) {
        String sql = "SELECT idCaja, montoApertura, totalIngresos, totalEgresos " +
                     "FROM CajaChica WHERE idEmpleado=? AND estado='Abierta' " +
                     "ORDER BY idCaja DESC LIMIT 1";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Object[]{
                    rs.getInt("idCaja"),
                    rs.getDouble("montoApertura"),
                    rs.getDouble("totalIngresos"),
                    rs.getDouble("totalEgresos")
                };
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // SUMAR INGRESO (cuando se hace una venta en efectivo)
    public void sumarIngreso(int idEmpleado, double monto) {
        String sql = "UPDATE CajaChica SET totalIngresos = totalIngresos + ? " +
                     "WHERE idEmpleado=? AND estado='Abierta'";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, monto);
            ps.setInt(2, idEmpleado);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // SUMAR EGRESO (cuando se hace una devolución en efectivo)
    public void sumarEgreso(int idEmpleado, double monto) {
        String sql = "UPDATE CajaChica SET totalEgresos = totalEgresos + ? " +
                     "WHERE idEmpleado=? AND estado='Abierta'";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, monto);
            ps.setInt(2, idEmpleado);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // CERRAR CAJA
    public boolean cerrarCaja(int idEmpleado, double montoCierre, String observaciones) {
        String sql = "UPDATE CajaChica SET montoCierre=?, " +
                     "montoEsperado = montoApertura + totalIngresos - totalEgresos, " +
                     "diferencia = ? - (montoApertura + totalIngresos - totalEgresos), " +
                     "fechaCierre = NOW(), estado='Cerrada', observaciones=? " +
                     "WHERE idEmpleado=? AND estado='Abierta'";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, montoCierre);
            ps.setDouble(2, montoCierre);
            ps.setString(3, observaciones);
            ps.setInt(4, idEmpleado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // LISTAR HISTORIAL DE CAJAS (para el admin)
    public List<Object[]> listarHistorial() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT c.idCaja, CONCAT(e.nombres,' ',e.apellidos) AS empleado, " +
                     "c.montoApertura, c.totalIngresos, c.totalEgresos, " +
                     "c.montoEsperado, c.montoCierre, c.diferencia, " +
                     "c.fechaApertura, c.fechaCierre, c.estado, c.observaciones " +
                     "FROM CajaChica c JOIN empleado e ON c.idEmpleado = e.idEmpleado " +
                     "ORDER BY c.fechaApertura DESC";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("idCaja"),
                    rs.getString("empleado"),
                    String.format("S/ %.2f", rs.getDouble("montoApertura")),
                    String.format("S/ %.2f", rs.getDouble("totalIngresos")),
                    String.format("S/ %.2f", rs.getDouble("totalEgresos")),
                    String.format("S/ %.2f", rs.getDouble("montoEsperado")),
                    rs.getObject("montoCierre") != null ?
                        String.format("S/ %.2f", rs.getDouble("montoCierre")) : "---",
                    rs.getObject("diferencia") != null ?
                        String.format("S/ %.2f", rs.getDouble("diferencia")) : "---",
                    rs.getString("fechaApertura"),
                    rs.getString("fechaCierre") != null ? rs.getString("fechaCierre") : "---",
                    rs.getString("estado"),
                    rs.getString("observaciones") != null ? rs.getString("observaciones") : ""
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
    // REGISTRAR RETIRO
    public boolean registrarRetiro(int idCaja, double montoRetiro) {
        String sql = "UPDATE CajaChica SET retiros = retiros + ? WHERE idCaja=? AND estado='Cerrada'";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, montoRetiro);
            ps.setInt(2, idCaja);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // OBTENER RESUMEN DEL DÍA
    public Object[] getResumenDia() {
        String sql = "SELECT " +
                     "COALESCE(SUM(montoApertura), 0) AS totalApertura, " +
                     "COALESCE(SUM(totalIngresos), 0) AS totalIngresos, " +
                     "COALESCE(SUM(totalEgresos), 0) AS totalEgresos, " +
                     "COALESCE(SUM(montoCierre), 0) AS totalCierre, " +
                     "COALESCE(SUM(retiros), 0) AS totalRetiros, " +
                     "COALESCE(SUM(diferencia), 0) AS totalDiferencia " +
                     "FROM CajaChica WHERE DATE(fechaApertura) = CURDATE()";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new Object[]{
                    rs.getDouble("totalApertura"),
                    rs.getDouble("totalIngresos"),
                    rs.getDouble("totalEgresos"),
                    rs.getDouble("totalCierre"),
                    rs.getDouble("totalRetiros"),
                    rs.getDouble("totalDiferencia")
                };
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return new Object[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    }

    // LISTAR CAJAS DEL DÍA
    public List<Object[]> listarCajasHoy() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT c.idCaja, CONCAT(e.nombres,' ',e.apellidos) AS empleado, " +
                     "c.montoApertura, c.totalIngresos, c.totalEgresos, " +
                     "c.montoEsperado, c.montoCierre, c.diferencia, c.retiros, " +
                     "c.fechaApertura, c.fechaCierre, c.estado, c.observaciones " +
                     "FROM CajaChica c JOIN empleado e ON c.idEmpleado = e.idEmpleado " +
                     "WHERE DATE(c.fechaApertura) = CURDATE() " +
                     "ORDER BY c.fechaApertura DESC";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("idCaja"),
                    rs.getString("empleado"),
                    String.format("S/ %.2f", rs.getDouble("montoApertura")),
                    String.format("S/ %.2f", rs.getDouble("totalIngresos")),
                    String.format("S/ %.2f", rs.getDouble("totalEgresos")),
                    String.format("S/ %.2f", rs.getDouble("montoEsperado")),
                    rs.getObject("montoCierre") != null ?
                        String.format("S/ %.2f", rs.getDouble("montoCierre")) : "---",
                    rs.getObject("diferencia") != null ?
                        String.format("S/ %.2f", rs.getDouble("diferencia")) : "---",
                    String.format("S/ %.2f", rs.getDouble("retiros")),
                    rs.getString("estado")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}