package DAO;

import Conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BitacoraDAO {

    // REGISTRAR ACCIÓN
    public static boolean registrar(int idUsuario, String accion,
                                     String modulo, String detalle) {
        String sql = "INSERT INTO Bitacora (idUsuario, accion, modulo, detalle) " +
                     "VALUES (?,?,?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setString(2, accion);
            ps.setString(3, modulo);
            ps.setString(4, detalle);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // LISTAR CON FILTROS
    public List<Object[]> listar(String fechaInicio, String fechaFin,
                                   String modulo, String usuario) {
        List<Object[]> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT b.idBitacora, u.usuario, b.accion, b.modulo, " +
            "b.detalle, b.fecha " +
            "FROM Bitacora b JOIN usuario u ON b.idUsuario = u.idUsuario " +
            "WHERE DATE(b.fecha) BETWEEN ? AND ? ");

        if (modulo != null && !modulo.equals("Todos"))
            sql.append("AND b.modulo = ? ");
        if (usuario != null && !usuario.isEmpty())
            sql.append("AND u.usuario LIKE ? ");
        sql.append("ORDER BY b.fecha DESC LIMIT 500");

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            ps.setString(1, fechaInicio);
            ps.setString(2, fechaFin);
            int idx = 3;
            if (modulo != null && !modulo.equals("Todos"))
                ps.setString(idx++, modulo);
            if (usuario != null && !usuario.isEmpty())
                ps.setString(idx, "%" + usuario + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("idBitacora"),
                    rs.getString("usuario"),
                    rs.getString("accion"),
                    rs.getString("modulo"),
                    rs.getString("detalle"),
                    rs.getString("fecha")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}