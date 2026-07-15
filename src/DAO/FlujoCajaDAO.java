package DAO;

import Conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlujoCajaDAO {

    // REGISTRAR MOVIMIENTO
    public boolean registrar(String tipo, String concepto,
                              double monto, int idUsuario, String referencia) {
        String sql = "INSERT INTO FlujoCaja (tipo, concepto, monto, idUsuario, referencia) " +
                     "VALUES (?,?,?,?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tipo);
            ps.setString(2, concepto);
            ps.setDouble(3, monto);
            ps.setInt(4, idUsuario);
            ps.setString(5, referencia);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // LISTAR POR RANGO DE FECHAS
    public List<Object[]> listar(String fechaInicio, String fechaFin) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT fecha, tipo, concepto, monto, referencia " +
                     "FROM FlujoCaja WHERE DATE(fecha) BETWEEN ? AND ? " +
                     "ORDER BY fecha DESC";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, fechaInicio);
            ps.setString(2, fechaFin);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("fecha"),
                    rs.getString("tipo"),
                    rs.getString("concepto"),
                    String.format("S/ %.2f", rs.getDouble("monto")),
                    rs.getString("referencia") != null ? rs.getString("referencia") : ""
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // TOTALES POR PERÍODO
    public double[] getTotales(String fechaInicio, String fechaFin) {
        double ingresos = 0, egresos = 0;
        String sql = "SELECT tipo, SUM(monto) as total FROM FlujoCaja " +
                     "WHERE DATE(fecha) BETWEEN ? AND ? GROUP BY tipo";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, fechaInicio);
            ps.setString(2, fechaFin);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("tipo").equals("INGRESO"))
                    ingresos = rs.getDouble("total");
                else
                    egresos = rs.getDouble("total");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return new double[]{ingresos, egresos};
    }

    // TIPO DE CAMBIO ACTUAL
    public double getTipoCambio() {
        String sql = "SELECT valor FROM TipoCambio ORDER BY fecha DESC LIMIT 1";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble("valor");
        } catch (SQLException e) { e.printStackTrace(); }
        return 3.72;
    }

    // ACTUALIZAR TIPO DE CAMBIO
    public boolean actualizarTipoCambio(double valor, int idUsuario) {
        String sql = "INSERT INTO TipoCambio (valor, idUsuario) VALUES (?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, valor);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}