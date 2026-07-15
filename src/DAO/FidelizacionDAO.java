package DAO;

import Conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FidelizacionDAO {

    // LISTAR CLIENTES CON PUNTOS
    public List<Object[]> listarClientes() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT c.idCliente, c.nombre, c.apellido, c.dni_ruc, " +
                     "COALESCE(f.puntosAcum, 0) as puntos, " +
                     "COALESCE(f.puntosCanjeados, 0) as canjeados, " +
                     "COALESCE(f.solesPorPunto, 10) as solesPorPunto " +
                     "FROM cliente c LEFT JOIN Fidelizacion f ON c.idCliente = f.idCliente " +
                     "WHERE c.estado = 1 ORDER BY puntos DESC";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("idCliente"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("dni_ruc"),
                    rs.getInt("puntos"),
                    rs.getInt("canjeados"),
                    rs.getDouble("solesPorPunto")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // AGREGAR PUNTOS AL CLIENTE
    public boolean agregarPuntos(int idCliente, int puntos, double solesPorPunto) {
        String sql = "INSERT INTO Fidelizacion (idCliente, puntosAcum, solesPorPunto, ultimaCompra) " +
                     "VALUES (?, ?, ?, NOW()) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "puntosAcum = puntosAcum + ?, ultimaCompra = NOW()";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.setInt(2, puntos);
            ps.setDouble(3, solesPorPunto);
            ps.setInt(4, puntos);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // CANJEAR PUNTOS
    public boolean canjear(int idCliente, int puntosUsados,
                            String premio, int idUsuario) {
        // Verificar puntos disponibles
        String sqlCheck = "SELECT puntosAcum FROM Fidelizacion WHERE idCliente=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sqlCheck)) {
            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();
            if (!rs.next() || rs.getInt("puntosAcum") < puntosUsados) return false;

            // Descontar puntos
            PreparedStatement ps2 = con.prepareStatement(
                "UPDATE Fidelizacion SET puntosAcum = puntosAcum - ?, " +
                "puntosCanjeados = puntosCanjeados + ? WHERE idCliente=?");
            ps2.setInt(1, puntosUsados);
            ps2.setInt(2, puntosUsados);
            ps2.setInt(3, idCliente);
            ps2.executeUpdate();

            // Registrar canje
            PreparedStatement ps3 = con.prepareStatement(
                "INSERT INTO FidelizacionCanje (idCliente, premio, puntosUsados, idUsuario) " +
                "VALUES (?,?,?,?)");
            ps3.setInt(1, idCliente);
            ps3.setString(2, premio);
            ps3.setInt(3, puntosUsados);
            ps3.setInt(4, idUsuario);
            ps3.executeUpdate();

            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // OBTENER PUNTOS DE UN CLIENTE
    public int getPuntos(int idCliente) {
        String sql = "SELECT COALESCE(puntosAcum, 0) as puntos " +
                     "FROM Fidelizacion WHERE idCliente=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("puntos");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}