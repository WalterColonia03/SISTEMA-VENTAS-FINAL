package DAO;

import Conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlanillaDAO {

    // REGISTRAR ASISTENCIA DEL DÍA (usado por IFrmPlanillaAsistencia)
    public boolean registrarAsistencia(int idEmpleado, String fecha,
                                        String horaEntrada, String horaSalida,
                                        double horas, String estado) {
        String sql = "INSERT INTO PlanillaAsistencia (idEmpleado, fecha, horaEntrada, horaSalida, horas, estado) " +
                     "VALUES (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " +
                     "horaEntrada=?, horaSalida=?, horas=?, estado=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ps.setString(2, fecha);
            ps.setString(3, horaEntrada);
            ps.setString(4, horaSalida);
            ps.setDouble(5, horas);
            ps.setString(6, estado);
            ps.setString(7, horaEntrada);
            ps.setString(8, horaSalida);
            ps.setDouble(9, horas);
            ps.setString(10, estado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // LISTAR ASISTENCIA POR EMPLEADO Y MES
    public List<Object[]> listarAsistencia(int idEmpleado, int mes, int anio) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT fecha, horaEntrada, horaSalida, horas, estado " +
                     "FROM PlanillaAsistencia " +
                     "WHERE idEmpleado=? AND MONTH(fecha)=? AND YEAR(fecha)=? " +
                     "ORDER BY fecha";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ps.setInt(2, mes);
            ps.setInt(3, anio);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("fecha"),
                    rs.getString("horaEntrada") != null ? rs.getString("horaEntrada") : "",
                    rs.getString("horaSalida") != null ? rs.getString("horaSalida") : "",
                    rs.getDouble("horas"),
                    rs.getString("estado")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // CONTAR DÍAS TRABAJADOS Y FALTAS
    public int[] contarDias(int idEmpleado, int mes, int anio) {
        int trabajados = 0, faltas = 0, vacaciones = 0;
        String sql = "SELECT estado, COUNT(*) as total FROM PlanillaAsistencia " +
                     "WHERE idEmpleado=? AND MONTH(fecha)=? AND YEAR(fecha)=? " +
                     "GROUP BY estado";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ps.setInt(2, mes);
            ps.setInt(3, anio);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String estado = rs.getString("estado");
                int total = rs.getInt("total");
                if (estado.equals("Presente") || estado.equals("Tardanza")) trabajados += total;
                else if (estado.equals("Ausente")) faltas += total;
                else if (estado.equals("Vacaciones")) vacaciones += total;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return new int[]{trabajados, faltas, vacaciones};
    }

    // GUARDAR PLANILLA MENSUAL
    public boolean guardarPlanilla(int idEmpleado, int mes, int anio,
                                    int diasTrabajados, int faltas, int diasVacaciones,
                                    double salarioBase, double bonificacion,
                                    double descuento, double pagoFinal, int idUsuario) {
        String sql = "INSERT INTO PlanillaMensual (idEmpleado, mes, anio, diasTrabajados, faltas, " +
                     "diasVacaciones, salarioBase, bonificacion, descuento, pagoFinal, idUsuario) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?,?) " +
                     "ON DUPLICATE KEY UPDATE diasTrabajados=?, faltas=?, diasVacaciones=?, " +
                     "salarioBase=?, bonificacion=?, descuento=?, pagoFinal=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ps.setInt(2, mes);
            ps.setInt(3, anio);
            ps.setInt(4, diasTrabajados);
            ps.setInt(5, faltas);
            ps.setInt(6, diasVacaciones);
            ps.setDouble(7, salarioBase);
            ps.setDouble(8, bonificacion);
            ps.setDouble(9, descuento);
            ps.setDouble(10, pagoFinal);
            ps.setInt(11, idUsuario);
            ps.setInt(12, diasTrabajados);
            ps.setInt(13, faltas);
            ps.setInt(14, diasVacaciones);
            ps.setDouble(15, salarioBase);
            ps.setDouble(16, bonificacion);
            ps.setDouble(17, descuento);
            ps.setDouble(18, pagoFinal);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ═══════════════════════════════════════════════════════════
    //  MÉTODOS NUEVOS — Marcador de Asistencia
    // ═══════════════════════════════════════════════════════════

    // MARCAR SOLO ENTRADA
    public boolean marcarEntrada(int idEmpleado, String fecha, String horaEntrada) {
        String sql = "INSERT INTO PlanillaAsistencia (idEmpleado, fecha, horaEntrada, horaSalida, horas, estado) " +
                     "VALUES (?,?,?,'',0,'Presente') " +
                     "ON DUPLICATE KEY UPDATE horaEntrada=?, estado='Presente'";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ps.setString(2, fecha);
            ps.setString(3, horaEntrada);
            ps.setString(4, horaEntrada);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // MARCAR SOLO SALIDA (calcula horas automáticamente)
    public boolean marcarSalida(int idEmpleado, String fecha, String horaSalida) {
        // Obtener hora de entrada
        String sqlGet = "SELECT horaEntrada FROM PlanillaAsistencia WHERE idEmpleado=? AND fecha=?";
        String horaEntrada = "";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sqlGet)) {
            ps.setInt(1, idEmpleado);
            ps.setString(2, fecha);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) horaEntrada = rs.getString("horaEntrada");
            else return false;
        } catch (SQLException e) { e.printStackTrace(); return false; }

        // Calcular horas
        double horas = 0;
        try {
            String[] e1 = horaEntrada.split(":");
            String[] s1 = horaSalida.split(":");
            int minE = Integer.parseInt(e1[0]) * 60 + Integer.parseInt(e1[1]);
            int minS = Integer.parseInt(s1[0]) * 60 + Integer.parseInt(s1[1]);
            horas = Math.round((minS - minE) / 60.0 * 100.0) / 100.0;
        } catch (Exception ex) { horas = 0; }

        // Actualizar solo salida y horas
        String sqlUpd = "UPDATE PlanillaAsistencia SET horaSalida=?, horas=? " +
                        "WHERE idEmpleado=? AND fecha=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sqlUpd)) {
            ps.setString(1, horaSalida);
            ps.setDouble(2, horas);
            ps.setInt(3, idEmpleado);
            ps.setString(4, fecha);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // VERIFICAR SI YA MARCÓ ENTRADA HOY
    public boolean yaMarcoEntrada(int idEmpleado, String fecha) {
        String sql = "SELECT 1 FROM PlanillaAsistencia WHERE idEmpleado=? AND fecha=? AND horaEntrada != ''";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ps.setString(2, fecha);
            return ps.executeQuery().next();
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}