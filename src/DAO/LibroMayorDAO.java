package DAO;

import Conexion.Conexion;
import java.sql.*;

public class LibroMayorDAO {

    // REGISTRAR ASIENTO CONTABLE
    public boolean registrar(String glosa, String cuentaDebe, String cuentaHaber,
                              double debe, double haber, String nroAsiento, int idUsuario) {
        String sql = "INSERT INTO LibroMayor (fecha, glosa, cuentaDebe, cuentaHaber, " +
                     "debe, haber, nroAsiento, idUsuario) VALUES (CURDATE(),?,?,?,?,?,?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, glosa);
            ps.setString(2, cuentaDebe);
            ps.setString(3, cuentaHaber);
            ps.setDouble(4, debe);
            ps.setDouble(5, haber);
            ps.setString(6, nroAsiento);
            ps.setInt(7, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // REGISTRAR ASIENTO DOBLE (Debe y Haber en registros separados)
    public void registrarAsientoVenta(int idVenta, double total, double subtotal,
                                       double igv, String metodoPago, int idUsuario) {
        String nro = "VTA-" + idVenta;
        String glosa = "Venta #" + idVenta + " - " + metodoPago;

        // Debe: Efectivo recibe el total
        registrar(glosa, "101 Efectivo", "", total, 0, nro, idUsuario);
        // Haber: Ventas por el subtotal sin IGV
        registrar(glosa, "", "701 Ventas", 0, subtotal, nro, idUsuario);
        // Haber: IGV por pagar
        registrar(glosa, "", "4011 IGV por Pagar", 0, igv, nro, idUsuario);
    }

    public void registrarAsientoCompraContado(int idCompra, String nroDoc,
                                               double total, double subtotal,
                                               double igv, int idUsuario) {
        String nro = "CMP-" + idCompra;
        String glosa = "Compra #" + idCompra + " - " + nroDoc;

        // Debe: Mercaderías por el subtotal
        registrar(glosa, "201 Mercaderías", "", subtotal, 0, nro, idUsuario);
        // Debe: IGV crédito fiscal
        registrar(glosa, "4011 IGV Crédito Fiscal", "", igv, 0, nro, idUsuario);
        // Haber: Efectivo sale el total
        registrar(glosa, "", "101 Efectivo", 0, total, nro, idUsuario);
    }

    public void registrarAsientoCompraCredito(int idCompra, String nroDoc,
                                               double total, double subtotal,
                                               double igv, int idUsuario) {
        String nro = "CMP-" + idCompra;
        String glosa = "Compra #" + idCompra + " - " + nroDoc + " (Crédito)";

        // Debe: Mercaderías
        registrar(glosa, "201 Mercaderías", "", subtotal, 0, nro, idUsuario);
        // Debe: IGV crédito fiscal
        registrar(glosa, "4011 IGV Crédito Fiscal", "", igv, 0, nro, idUsuario);
        // Haber: Cuenta por pagar
        registrar(glosa, "", "421 Cuentas por Pagar", 0, total, nro, idUsuario);
    }

    public void registrarAsientoPagoDeuda(int idCuenta, String nroDoc,
                                           double monto, int idUsuario) {
        String nro = "PAG-" + idCuenta;
        String glosa = "Pago deuda - " + nroDoc;

        // Debe: Se cancela la cuenta por pagar
        registrar(glosa, "421 Cuentas por Pagar", "", monto, 0, nro, idUsuario);
        // Haber: Sale efectivo
        registrar(glosa, "", "101 Efectivo", 0, monto, nro, idUsuario);
    }

    public void registrarAsientoDevolucion(int idVenta, double monto, int idUsuario) {
        String nro = "DEV-" + idVenta;
        String glosa = "Devolución Venta #" + idVenta;

        // Debe: Ventas se reduce
        registrar(glosa, "701 Ventas", "", monto, 0, nro, idUsuario);
        // Haber: Efectivo sale
        registrar(glosa, "", "101 Efectivo", 0, monto, nro, idUsuario);
    }
}