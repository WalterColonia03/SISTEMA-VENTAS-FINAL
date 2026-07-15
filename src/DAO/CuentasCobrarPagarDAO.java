package DAO;

import Conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CuentasCobrarPagarDAO {

    // LISTAR CUENTAS POR PAGAR
    public List<Object[]> listarPagar() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT c.idCuenta, p.razonSocial, c.nroDocumento, c.montoTotal, "
                + "c.saldoPendiente, c.fechaEmision, c.fechaVencimiento, "
                + "c.condicionPago, c.estado "
                + "FROM CuentasCobrarPagar c "
                + "LEFT JOIN proveedor p ON c.idProveedor = p.idProveedor "
                + "WHERE c.tipo='PAGAR' ORDER BY c.fechaVencimiento ASC";
        try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("idCuenta"),
                    rs.getString("razonSocial"),
                    rs.getString("nroDocumento"),
                    String.format("S/ %.2f", rs.getDouble("montoTotal")),
                    String.format("S/ %.2f", rs.getDouble("saldoPendiente")),
                    rs.getString("fechaEmision"),
                    rs.getString("fechaVencimiento"),
                    rs.getString("condicionPago"),
                    rs.getString("estado")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // REGISTRAR CUENTA POR PAGAR
    public boolean registrarPagar(String entidad, String documento,
            double montoTotal, String fechaEmision,
            String fechaVencimiento, String condicionPago,
            int idCompra, int idUsuario) {
        // Buscar idProveedor por nombre
        String sqlProv = "SELECT idProveedor FROM proveedor WHERE CONCAT(razonSocial, ' (', ruc, ')') = ? OR razonSocial = ? LIMIT 1";
        int idProveedor = 0;
        try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sqlProv)) {
            ps.setString(1, entidad);
            ps.setString(2, entidad);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idProveedor = rs.getInt("idProveedor");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sql = "INSERT INTO CuentasCobrarPagar (tipo, idProveedor, nroDocumento, "
                + "montoTotal, saldoPendiente, fechaEmision, fechaVencimiento, "
                + "condicionPago, idCompra, estado) "
                + "VALUES ('PAGAR',?,?,?,?,?,?,?,?,'Pendiente')";
        try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            ps.setString(2, documento);
            ps.setDouble(3, montoTotal);
            ps.setDouble(4, montoTotal);
            ps.setString(5, fechaEmision);
            ps.setString(6, fechaVencimiento);
            ps.setString(7, condicionPago);
            ps.setInt(8, idCompra);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // MARCAR COMO PAGADO — actualiza CuentasCobrarPagar Y Compra
    public boolean marcarPagado(int idCuenta) {
        try (Connection con = Conexion.getConexion()) {
            // Obtener idCompra asociado
            PreparedStatement psGet = con.prepareStatement(
                    "SELECT idCompra FROM CuentasCobrarPagar WHERE idCuenta=?");
            psGet.setInt(1, idCuenta);
            ResultSet rs = psGet.executeQuery();
            int idCompra = -1;
            if (rs.next()) {
                idCompra = rs.getInt("idCompra");
            }

            // Marcar cuenta como pagada
            PreparedStatement psUpd = con.prepareStatement(
                    "UPDATE CuentasCobrarPagar SET estado='Pagado', saldoPendiente=0 WHERE idCuenta=?");
            psUpd.setInt(1, idCuenta);
            psUpd.executeUpdate();

            // Actualizar estado en Compra
            if (idCompra > 0) {
                PreparedStatement psComp = con.prepareStatement(
                        "UPDATE Compra SET estadoPago='Pagado' WHERE idCompra=?");
                psComp.setInt(1, idCompra);
                psComp.executeUpdate();
            }

            // Registrar EGRESO en Flujo de Caja al pagar deuda
            PreparedStatement psMonto = con.prepareStatement(
                "SELECT montoTotal, nroDocumento FROM CuentasCobrarPagar WHERE idCuenta=?");
            psMonto.setInt(1, idCuenta);
            ResultSet rsMonto = psMonto.executeQuery();
            if (rsMonto.next()) {
                new FlujoCajaDAO().registrar("EGRESO",
                    "Pago deuda - " + rsMonto.getString("nroDocumento"),
                    rsMonto.getDouble("montoTotal"), 1,
                    "CXP #" + idCuenta);
                
                // Asiento contable automático
                new LibroMayorDAO().registrarAsientoPagoDeuda(
                    idCuenta, rsMonto.getString("nroDocumento"),
                    rsMonto.getDouble("montoTotal"), 1);
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // TOTALES PENDIENTES
    public double getTotalPagar() {
        String sql = "SELECT SUM(saldoPendiente) as total FROM CuentasCobrarPagar "
                + "WHERE tipo='PAGAR' AND estado='Pendiente'";
        try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
