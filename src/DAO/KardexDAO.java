package DAO;

import Conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KardexDAO {

    // REGISTRAR MOVIMIENTO
    public boolean registrar(int idProducto, String tipoMovimiento,
                             int cantidad, int stockAnterior,
                             int stockActual, String referencia, int idUsuario) {
        String sql = "INSERT INTO Kardex (idProducto, tipoMovimiento, cantidad, " +
                     "stockAnterior, stockActual, referencia, idUsuario) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ps.setString(2, tipoMovimiento);
            ps.setInt(3, cantidad);
            ps.setInt(4, stockAnterior);
            ps.setInt(5, stockActual);
            ps.setString(6, referencia);
            ps.setInt(7, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // LISTAR MOVIMIENTOS POR PRODUCTO
    public List<Object[]> listarPorProducto(int idProducto) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT fecha, tipoMovimiento, cantidad, stockActual, referencia " +
                     "FROM Kardex WHERE idProducto=? ORDER BY fecha DESC";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("fecha"),
                    rs.getString("tipoMovimiento"),
                    rs.getInt("cantidad"),
                    rs.getInt("stockActual"),
                    rs.getString("referencia")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // TOTALES POR PRODUCTO (entradas y salidas)
    public int[] getTotales(int idProducto) {
        int entradas = 0, salidas = 0;
        String sql = "SELECT tipoMovimiento, SUM(cantidad) as total FROM Kardex " +
                     "WHERE idProducto=? GROUP BY tipoMovimiento";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("tipoMovimiento").equals("ENTRADA"))
                    entradas = rs.getInt("total");
                else if (rs.getString("tipoMovimiento").equals("SALIDA"))
                    salidas = rs.getInt("total");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return new int[]{entradas, salidas};
    }
}