package DAO;

import Clases.Venta;
import Conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    // REGISTRAR VENTA
    public int insertar(int idCliente, int idUsuario, double subtotal,
                        double igv, double total, String metodoPago) {
        String sql = "INSERT INTO Venta (idCliente, idUsuario, subtotal, igv, total, metodoPago) " +
                     "VALUES (?,?,?,?,?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idCliente);
            ps.setInt(2, idUsuario);
            ps.setDouble(3, subtotal);
            ps.setDouble(4, igv);
            ps.setDouble(5, total);
            ps.setString(6, metodoPago);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int idVenta = rs.getInt(1);
                BitacoraDAO.registrar(idUsuario, "REGISTRAR", "VENTAS",
                    "Venta #" + idVenta + " registrada por S/ " +
                    String.format("%.2f", total) + " - Método: " + metodoPago);
                
                // Sumar a caja chica
                new CajaChicaDAO().sumarIngreso(
                    Clases.Sesion.getIdEmpleado(), total);

                // Registrar INGRESO en Flujo de Caja
                new FlujoCajaDAO().registrar("INGRESO",
                    "Venta #" + idVenta + " - " + metodoPago,
                    total, idUsuario, "VENTA #" + idVenta);
                
                
                // Asiento contable automático
                new LibroMayorDAO().registrarAsientoVenta(
                    idVenta, total, subtotal, igv, metodoPago, idUsuario);

                return idVenta;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    // INSERTAR DETALLE
    public boolean insertarDetalle(int idVenta, int idProducto, int cantidad,
                                    double precioUnitario, double descuento, double subtotal) {
        String sql = "INSERT INTO DetalleVenta (idVenta, idProducto, cantidad, " +
                     "precioUnitario, descuento, subtotal) VALUES (?,?,?,?,?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            ps.setInt(2, idProducto);
            ps.setInt(3, cantidad);
            ps.setDouble(4, precioUnitario);
            ps.setDouble(5, descuento);
            ps.setDouble(6, subtotal);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // LISTAR VENTAS
    public List<Venta> listar() {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT v.idVenta, CONCAT(c.nombre,' ',c.apellido) AS cliente, " +
                     "v.total, v.fecha FROM Venta v " +
                     "JOIN cliente c ON v.idCliente = c.idCliente " +
                     "ORDER BY v.fecha DESC";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Venta(
                    rs.getInt("idVenta"),
                    rs.getString("cliente"),
                    rs.getDouble("total"),
                    rs.getString("fecha")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // ANULAR VENTA
    public boolean anular(int idVenta) {
        String sql = "UPDATE Venta SET estado='Anulada' WHERE idVenta=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) BitacoraDAO.registrar(1, "ANULAR", "VENTAS",
                "Venta #" + idVenta + " anulada");
            return ok;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
}