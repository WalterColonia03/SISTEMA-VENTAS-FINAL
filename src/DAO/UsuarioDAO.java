package DAO;

import Clases.Usuario;
import Conexion.Conexion;
import java.sql.*;

public class UsuarioDAO {

    // LOGIN — verifica usuario y contraseña
    public Usuario login(String usuario, String contrasena) {
        String sql = "SELECT u.idUsuario, u.usuario, u.contrasena, u.idEmpleado, "
                + "e.nombres, e.apellidos, c.nombreCargo "
                + "FROM usuario u "
                + "JOIN empleado e ON u.idEmpleado = e.idEmpleado "
                + "JOIN cargo c ON e.idCargo = c.idCargo "
                + "WHERE u.usuario=? AND u.contrasena=?";
        try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, contrasena);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Usuario(
                        rs.getInt("idUsuario"),
                        rs.getString("nombres"),
                        rs.getString("apellidos"),
                        rs.getString("usuario"),
                        rs.getString("contrasena"),
                        "",
                        rs.getString("nombreCargo"),
                        1
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // INSERTAR
    public boolean insertar(String usuario, String contrasena, int idEmpleado) {
        String sql = "INSERT INTO usuario (usuario, contrasena, idEmpleado) VALUES (?,?,?)";
        try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, contrasena);
            ps.setInt(3, idEmpleado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ELIMINAR
    public boolean eliminar(int idUsuario) {
        String sql = "DELETE FROM usuario WHERE idUsuario=?";
        try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ACTUALIZAR CONTRASEÑA
    public boolean actualizarContrasena(int idUsuario, String nuevaContrasena) {
        String sql = "UPDATE usuario SET contrasena=? WHERE idUsuario=?";
        try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevaContrasena);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DESACTIVAR
    public boolean desactivar(int idUsuario) {
        String sql = "UPDATE usuario SET estado=0 WHERE idUsuario=?";
        try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // REACTIVAR
    public boolean reactivar(int idUsuario) {
        String sql = "UPDATE usuario SET estado=1 WHERE idUsuario=?";
        try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
