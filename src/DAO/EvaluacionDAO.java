package DAO;

import Conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvaluacionDAO {

    // ─── INSERTAR ────────────────────────────────────────────────────────────────
    public int insertar(int idEmpleado, int idEvaluador, String periodo,
                        int puntualidad, int productividad, int trabajoEquipo,
                        int actitud, int cumplimiento, String comentarios) {
        double promedio = (puntualidad + productividad + trabajoEquipo + actitud + cumplimiento) / 5.0;
        String sql = "INSERT INTO evaluacion_desempeno (idEmpleado, idEvaluador, periodo, "
                   + "puntualidad, productividad, trabajo_equipo, actitud, cumplimiento, promedio, comentarios) "
                   + "VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idEmpleado); ps.setInt(2, idEvaluador);
            ps.setString(3, periodo);
            ps.setInt(4, puntualidad); ps.setInt(5, productividad);
            ps.setInt(6, trabajoEquipo); ps.setInt(7, actitud); ps.setInt(8, cumplimiento);
            ps.setDouble(9, promedio); ps.setString(10, comentarios);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        } catch (SQLException e) { e.printStackTrace(); return -1; }
    }

    // ─── LISTAR HISTORIAL ────────────────────────────────────────────────────────
    public List<Object[]> listar(String filtroPeriodo) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT ev.idEvaluacion, CONCAT(e.nombre,' ',e.apellido) as empleado, "
                   + "e.cargo, ev.periodo, ev.puntualidad, ev.productividad, ev.trabajo_equipo, "
                   + "ev.actitud, ev.cumplimiento, ev.promedio, ev.comentarios, ev.fecha_evaluacion "
                   + "FROM evaluacion_desempeno ev "
                   + "JOIN empleado e ON ev.idEmpleado = e.idEmpleado "
                   + (filtroPeriodo.isEmpty() ? "" : "WHERE ev.periodo = ? ")
                   + "ORDER BY ev.fecha_evaluacion DESC";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (!filtroPeriodo.isEmpty()) ps.setString(1, filtroPeriodo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("idEvaluacion"),
                    rs.getString("empleado"),
                    rs.getString("cargo"),
                    rs.getString("periodo"),
                    rs.getInt("puntualidad"),
                    rs.getInt("productividad"),
                    rs.getInt("trabajo_equipo"),
                    rs.getInt("actitud"),
                    rs.getInt("cumplimiento"),
                    String.format("%.1f / 10", rs.getDouble("promedio")),
                    rs.getString("comentarios"),
                    rs.getString("fecha_evaluacion")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // ─── DATOS AUTOMÁTICOS DESDE ASISTENCIA ──────────────────────────────────────
    /**
     * Calcula puntualidad automática basada en asistencia del mes.
     * Días presentes / Días hábiles * 10 (escala 0-10)
     */
    public int calcularPuntualidad(int idEmpleado, String periodo) {
        String sql = "SELECT COUNT(*) as presentes FROM planilla_asistencia "
                   + "WHERE idEmpleado=? AND DATE_FORMAT(fecha,'%Y-%m')=? AND horaSalida IS NOT NULL";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado); ps.setString(2, periodo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int presentes = rs.getInt("presentes");
                // Asumimos 22 días hábiles por mes como base
                return Math.min(10, (int) Math.round(presentes * 10.0 / 22));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // ─── LISTAR EMPLEADOS PARA COMBO ─────────────────────────────────────────────
    public List<Object[]> listarEmpleados() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT idEmpleado, CONCAT(nombre,' ',apellido) as nombre, cargo "
                   + "FROM empleado WHERE estado='Activo' ORDER BY nombre";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("idEmpleado"),
                    rs.getString("nombre"),
                    rs.getString("cargo")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}
