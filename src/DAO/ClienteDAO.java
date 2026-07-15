package DAO;

import Clases.Cliente;
import Conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    // LISTAR SOLO ACTIVOS
    public List<Cliente> listar() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT idCliente, nombre, apellido, dni_ruc, telefono, correo, estado FROM cliente WHERE estado=1";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Cliente(
                    rs.getInt("idCliente"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("dni_ruc"),
                    rs.getString("telefono") != null ? rs.getString("telefono") : "",
                    rs.getString("correo") != null ? rs.getString("correo") : "",
                    rs.getInt("estado")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // LISTAR TODOS (activos e inactivos)
    public List<Cliente> listarTodos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT idCliente, nombre, apellido, dni_ruc, telefono, correo, estado FROM cliente";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Cliente(
                    rs.getInt("idCliente"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("dni_ruc"),
                    rs.getString("telefono") != null ? rs.getString("telefono") : "",
                    rs.getString("correo") != null ? rs.getString("correo") : "",
                    rs.getInt("estado")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // INSERTAR
    public boolean insertar(Cliente c) {
        String sql = "INSERT INTO cliente (nombre, apellido, dni_ruc, telefono, correo) VALUES (?,?,?,?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido());
            ps.setString(3, c.getDni());
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getDireccion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ACTUALIZAR
    public boolean actualizar(Cliente c) {
        String sql = "UPDATE cliente SET nombre=?, apellido=?, dni_ruc=?, telefono=?, correo=? WHERE idCliente=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido());
            ps.setString(3, c.getDni());
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getDireccion());
            ps.setInt(6, c.getIdCliente());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // DESACTIVAR
    public boolean eliminar(int idCliente) {
        String sql = "UPDATE cliente SET estado=0 WHERE idCliente=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // REACTIVAR
    public boolean reactivar(int idCliente) {
        String sql = "UPDATE cliente SET estado=1 WHERE idCliente=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // BUSCAR POR DNI
    public Cliente buscarPorDni(String dni) {
        String sql = "SELECT * FROM cliente WHERE dni_ruc=? AND estado=1";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Cliente(
                    rs.getInt("idCliente"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("dni_ruc"),
                    rs.getString("telefono") != null ? rs.getString("telefono") : "",
                    rs.getString("correo") != null ? rs.getString("correo") : "",
                    1
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}