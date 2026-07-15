package DAO;

import Conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO {

    public static class Empleado {
        public int idEmpleado;
        public String nombres, apellidos, dni, telefono, direccion, cargo, fechaIngreso;
        public int idCargo, estado;

        public Empleado(int idEmpleado, String nombres, String apellidos, String dni,
                        String telefono, String direccion, int idCargo, String cargo,
                        String fechaIngreso, int estado) {
            this.idEmpleado   = idEmpleado;
            this.nombres      = nombres;
            this.apellidos    = apellidos;
            this.dni          = dni;
            this.telefono     = telefono;
            this.direccion    = direccion;
            this.idCargo      = idCargo;
            this.cargo        = cargo;
            this.fechaIngreso = fechaIngreso;
            this.estado       = estado;
        }
    }

    // LISTAR TODOS
    public List<Empleado> listar() {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT e.idEmpleado, e.nombres, e.apellidos, e.dni, " +
                     "e.telefono, e.direccion, e.idCargo, c.nombreCargo, " +
                     "e.fechaIngreso, e.estado " +
                     "FROM empleado e JOIN cargo c ON e.idCargo = c.idCargo";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Empleado(
                    rs.getInt("idEmpleado"),
                    rs.getString("nombres"),
                    rs.getString("apellidos"),
                    rs.getString("dni"),
                    rs.getString("telefono") != null ? rs.getString("telefono") : "",
                    rs.getString("direccion") != null ? rs.getString("direccion") : "",
                    rs.getInt("idCargo"),
                    rs.getString("nombreCargo"),
                    rs.getString("fechaIngreso") != null ? rs.getString("fechaIngreso") : "",
                    rs.getInt("estado")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // LISTAR CARGOS
    public List<String[]> listarCargos() {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT idCargo, nombreCargo FROM cargo";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new String[]{
                    rs.getString("idCargo"),
                    rs.getString("nombreCargo")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // INSERTAR
    public boolean insertar(Empleado emp) {
        String sql = "INSERT INTO empleado (nombres, apellidos, dni, telefono, direccion, idCargo, fechaIngreso, estado) " +
                     "VALUES (?,?,?,?,?,?,CURDATE(),1)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, emp.nombres);
            ps.setString(2, emp.apellidos);
            ps.setString(3, emp.dni);
            ps.setString(4, emp.telefono);
            ps.setString(5, emp.direccion);
            ps.setInt(6, emp.idCargo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ACTUALIZAR
    public boolean actualizar(Empleado emp) {
        String sql = "UPDATE empleado SET nombres=?, apellidos=?, dni=?, telefono=?, " +
                     "direccion=?, idCargo=? WHERE idEmpleado=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, emp.nombres);
            ps.setString(2, emp.apellidos);
            ps.setString(3, emp.dni);
            ps.setString(4, emp.telefono);
            ps.setString(5, emp.direccion);
            ps.setInt(6, emp.idCargo);
            ps.setInt(7, emp.idEmpleado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // DESACTIVAR
    public boolean desactivar(int idEmpleado) {
        String sql = "UPDATE empleado SET estado=0 WHERE idEmpleado=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // REACTIVAR
    public boolean reactivar(int idEmpleado) {
        String sql = "UPDATE empleado SET estado=1 WHERE idEmpleado=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // OBTENER ID GENERADO (para crear usuario al mismo tiempo)
    public int insertarYObtenerID(Empleado emp) {
        String sql = "INSERT INTO empleado (nombres, apellidos, dni, telefono, direccion, idCargo, fechaIngreso, estado) " +
                     "VALUES (?,?,?,?,?,?,CURDATE(),1)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, emp.nombres);
            ps.setString(2, emp.apellidos);
            ps.setString(3, emp.dni);
            ps.setString(4, emp.telefono);
            ps.setString(5, emp.direccion);
            ps.setInt(6, emp.idCargo);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }
}