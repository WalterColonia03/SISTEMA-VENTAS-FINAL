package DAO;

import Clases.Producto;
import Conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    // LISTAR SOLO ACTIVOS
    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT idProducto, nombre, descripcion, idCategoria, precio, costoPromedio, cantidad FROM producto WHERE estado=1";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Producto p = new Producto(
                    rs.getInt("idProducto"),
                    rs.getString("nombre"),
                    rs.getInt("cantidad"),
                    rs.getDouble("precio"),
                    rs.getString("descripcion"),
                    rs.getInt("idCategoria"),
                    1
                );
                p.setCostoPromedio(rs.getDouble("costoPromedio"));
                lista.add(p);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // LISTAR TODOS (activos e inactivos)
    public List<Producto> listarTodos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT idProducto, nombre, descripcion, idCategoria, precio, costoPromedio, cantidad, estado FROM producto";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Producto p = new Producto(
                    rs.getInt("idProducto"),
                    rs.getString("nombre"),
                    rs.getInt("cantidad"),
                    rs.getDouble("precio"),
                    rs.getString("descripcion"),
                    rs.getInt("idCategoria"),
                    rs.getInt("estado")
                );
                p.setCostoPromedio(rs.getDouble("costoPromedio"));
                lista.add(p);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // INSERTAR
    public boolean insertar(Producto p) {
        String sql = "INSERT INTO producto (nombre, descripcion, idCategoria, precio, cantidad) VALUES (?,?,?,?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());
            ps.setInt(3, p.getIdCategoria());
            ps.setDouble(4, p.getPrecio());
            ps.setInt(5, p.getCantidad());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ACTUALIZAR
    public boolean actualizar(Producto p) {
        String sql = "UPDATE producto SET nombre=?, descripcion=?, idCategoria=?, precio=?, cantidad=? WHERE idProducto=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());
            ps.setInt(3, p.getIdCategoria());
            ps.setDouble(4, p.getPrecio());
            ps.setInt(5, p.getCantidad());
            ps.setInt(6, p.getIdProducto());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // DESACTIVAR (eliminación lógica)
    public boolean eliminar(int idProducto) {
        String sql = "UPDATE producto SET estado=0 WHERE idProducto=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // REACTIVAR
    public boolean reactivar(int idProducto) {
        String sql = "UPDATE producto SET estado=1 WHERE idProducto=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ACTUALIZAR STOCK
    public boolean actualizarStock(int idProducto, int nuevaCantidad) {
        String sql = "UPDATE producto SET cantidad=? WHERE idProducto=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nuevaCantidad);
            ps.setInt(2, idProducto);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}