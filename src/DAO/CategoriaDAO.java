package DAO;

import Clases.Categoria;
import Conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    // LISTAR SOLO ACTIVAS
    public List<Categoria> listar() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT idCategoria, descripcion, estado FROM categoria WHERE estado=1";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Categoria(
                    rs.getInt("idCategoria"),
                    rs.getString("descripcion"),
                    rs.getInt("estado")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // LISTAR TODAS (activas e inactivas)
    public List<Categoria> listarTodas() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT idCategoria, descripcion, estado FROM categoria";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Categoria(
                    rs.getInt("idCategoria"),
                    rs.getString("descripcion"),
                    rs.getInt("estado")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // INSERTAR
    public boolean insertar(Categoria c) {
        String sql = "INSERT INTO categoria (descripcion, estado) VALUES (?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getDescripcion());
            ps.setInt(2, c.getEstado());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ACTUALIZAR
    public boolean actualizar(Categoria c) {
        String sql = "UPDATE categoria SET descripcion=?, estado=? WHERE idCategoria=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getDescripcion());
            ps.setInt(2, c.getEstado());
            ps.setInt(3, c.getIdCategoria());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ELIMINAR (desactiva en vez de borrar)
    public boolean eliminar(int idCategoria) {
        String sql = "UPDATE categoria SET estado=0 WHERE idCategoria=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCategoria);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // REACTIVAR
    public boolean reactivar(int idCategoria) {
        String sql = "UPDATE categoria SET estado=1 WHERE idCategoria=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCategoria);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}