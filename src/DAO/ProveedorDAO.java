package DAO;

import Conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {

    // Clase interna para manejar datos del proveedor
    public static class Proveedor {
        public int idProveedor;
        public String ruc, razonSocial, telefono, direccion, correo;
        public int estado;

        public Proveedor(int id, String ruc, String razonSocial, String telefono,
                         String direccion, String correo, int estado) {
            this.idProveedor  = id;
            this.ruc          = ruc;
            this.razonSocial  = razonSocial;
            this.telefono     = telefono;
            this.direccion    = direccion;
            this.correo       = correo;
            this.estado       = estado;
        }
    }

    // LISTAR TODOS
    public List<Proveedor> listar() {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT idProveedor, ruc, razonSocial, telefono, direccion, correo, 1 as estado FROM proveedor";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Proveedor(
                    rs.getInt("idProveedor"),
                    rs.getString("ruc"),
                    rs.getString("razonSocial"),
                    rs.getString("telefono") != null ? rs.getString("telefono") : "",
                    rs.getString("direccion") != null ? rs.getString("direccion") : "",
                    rs.getString("correo") != null ? rs.getString("correo") : "",
                    rs.getInt("estado")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // BUSCAR POR RUC
    public Proveedor buscarPorRuc(String ruc) {
        String sql = "SELECT * FROM proveedor WHERE ruc=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ruc);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Proveedor(
                    rs.getInt("idProveedor"),
                    rs.getString("ruc"),
                    rs.getString("razonSocial"),
                    rs.getString("telefono") != null ? rs.getString("telefono") : "",
                    rs.getString("direccion") != null ? rs.getString("direccion") : "",
                    rs.getString("correo") != null ? rs.getString("correo") : "",
                    1
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // INSERTAR
    public boolean insertar(Proveedor p) {
        String sql = "INSERT INTO proveedor (ruc, razonSocial, telefono, direccion, correo) VALUES (?,?,?,?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.ruc);
            ps.setString(2, p.razonSocial);
            ps.setString(3, p.telefono);
            ps.setString(4, p.direccion);
            ps.setString(5, p.correo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ACTUALIZAR
    public boolean actualizar(Proveedor p) {
        String sql = "UPDATE proveedor SET ruc=?, razonSocial=?, telefono=?, direccion=?, correo=? WHERE idProveedor=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.ruc);
            ps.setString(2, p.razonSocial);
            ps.setString(3, p.telefono);
            ps.setString(4, p.direccion);
            ps.setString(5, p.correo);
            ps.setInt(6, p.idProveedor);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ELIMINAR
    public boolean eliminar(int idProveedor) {
        String sql = "DELETE FROM proveedor WHERE idProveedor=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}