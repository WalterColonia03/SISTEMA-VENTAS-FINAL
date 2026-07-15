package DAO;

import Conexion.Conexion;
import java.sql.*;

public class CompraDAO {

    // REGISTRAR COMPRA
    public int insertar(int idProveedor, int idUsuario, String nroDocumento,
                        double subtotal, double igv, double total) {
        String sql = "INSERT INTO Compra (idProveedor, idUsuario, nroDocumento, subtotal, igv, total) " +
                     "VALUES (?,?,?,?,?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idProveedor);
            ps.setInt(2, idUsuario);
            ps.setString(3, nroDocumento);
            ps.setDouble(4, subtotal);
            ps.setDouble(5, igv);
            ps.setDouble(6, total);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int idCompra = rs.getInt(1);
                BitacoraDAO.registrar(idUsuario, "REGISTRAR", "COMPRAS",
                    "Compra #" + idCompra + " doc: " + nroDocumento +
                    " por S/ " + String.format("%.2f", total));
                return idCompra;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    // INSERTAR DETALLE (retorna idDetalleCompra generado, -1 si falla)
    public int insertarDetalle(int idCompra, int idProducto, int cantidad,
                                    double precioUnitario, String lote,
                                    String fechaVencimiento, double subtotal) {
        String sql = "INSERT INTO DetalleCompra (idCompra, idProducto, cantidad, " +
                     "precioUnitario, lote, fechaVencimiento, subtotal) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idCompra);
            ps.setInt(2, idProducto);
            ps.setInt(3, cantidad);
            ps.setDouble(4, precioUnitario);
            ps.setString(5, lote.isEmpty() ? null : lote);
            ps.setString(6, fechaVencimiento.isEmpty() ? null : fechaVencimiento);
            ps.setDouble(7, subtotal);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        } catch (SQLException e) { e.printStackTrace(); return -1; }
    }
}