package Vista;

import Conexion.Conexion;
import Vista.Estilos.UIKit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class IFrmAlertasInventario extends JInternalFrame {

    private JTable tblPorVencer;
    private DefaultTableModel modelPorVencer;
    private JTable tblSinRotacion;
    private DefaultTableModel modelSinRotacion;
    private JTable tblStockBajo;
    private DefaultTableModel modelStockBajo;

    private JButton btnRefrescar;
    private JLabel lblTotalAlertas;

    public IFrmAlertasInventario() {
        super("Alertas de Inventario", true, true, true, true);
        initComponents();
        buildLayout();
        attachEvents();
        setSize(950, 600);
        cargarAlertas();
    }

    private void initComponents() {
        btnRefrescar = UIKit.secondaryButton("Refrescar Alertas");
        lblTotalAlertas = new JLabel("Cargando...");
        lblTotalAlertas.setFont(UIKit.BODY_BOLD);
        lblTotalAlertas.setForeground(UIKit.WARNING);

        // Tabla: Próximos a Vencer
        String[] colsVencer = {"Producto", "Lote", "Vencimiento", "Días Rest.", "Stock"};
        modelPorVencer = new DefaultTableModel(colsVencer, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tblPorVencer = UIKit.styledTable(modelPorVencer);
        tblPorVencer.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    try {
                        int dias = Integer.parseInt(value.toString());
                        if (dias < 10) {
                            c.setBackground(UIKit.DANGER);
                            c.setForeground(Color.WHITE);
                        } else if (dias < 30) {
                            c.setBackground(UIKit.WARNING);
                            c.setForeground(Color.WHITE);
                        } else {
                            c.setBackground(Color.WHITE);
                            c.setForeground(UIKit.TEXT_PRIMARY);
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
                return c;
            }
        });

        // Tabla: Sin Rotación
        String[] colsSinRot = {"Producto", "Categoría", "Stock Actual", "Último Movimiento"};
        modelSinRotacion = new DefaultTableModel(colsSinRot, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tblSinRotacion = UIKit.styledTable(modelSinRotacion);

        // Tabla: Stock Bajo
        String[] colsStock = {"Producto", "Categoría", "Stock Actual", "Stock Mínimo", "Déficit"};
        modelStockBajo = new DefaultTableModel(colsStock, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tblStockBajo = UIKit.styledTable(modelStockBajo);
        tblStockBajo.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(new Color(255, 235, 238));
                    c.setForeground(UIKit.DANGER);
                    ((JLabel) c).setFont(UIKit.BODY_BOLD);
                }
                return c;
            }
        });
    }

    private void buildLayout() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UIKit.BG_APP);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(
                UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));

        getContentPane().add(
                UIKit.screenHeader("Alertas de Inventario", "Inventario  ›  Alertas"),
                BorderLayout.NORTH);

        JPanel cuerpo = UIKit.card();
        cuerpo.setLayout(new BorderLayout(0, UIKit.SPACE_MD));

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        pnlHeader.add(UIKit.sectionHeader("Monitor de Alertas", btnRefrescar), BorderLayout.CENTER);
        pnlHeader.add(lblTotalAlertas, BorderLayout.EAST);
        cuerpo.add(pnlHeader, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIKit.BODY);

        // Tab 1: Stock Bajo
        JPanel pnlStock = new JPanel(new BorderLayout());
        pnlStock.setOpaque(false);
        JScrollPane scrollStock = new JScrollPane(tblStockBajo);
        scrollStock.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlStock.add(scrollStock, BorderLayout.CENTER);
        tabbedPane.addTab("⚠ Stock Bajo", pnlStock);

        // Tab 2: Próximos a Vencer
        JPanel pnlVencer = new JPanel(new BorderLayout());
        pnlVencer.setOpaque(false);
        JScrollPane scrollVencer = new JScrollPane(tblPorVencer);
        scrollVencer.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlVencer.add(scrollVencer, BorderLayout.CENTER);
        tabbedPane.addTab("📅 Próximos a Vencer", pnlVencer);

        // Tab 3: Sin Rotación
        JPanel pnlSinRot = new JPanel(new BorderLayout());
        pnlSinRot.setOpaque(false);
        JScrollPane scrollSinRot = new JScrollPane(tblSinRotacion);
        scrollSinRot.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlSinRot.add(scrollSinRot, BorderLayout.CENTER);
        tabbedPane.addTab("📦 Sin Rotación", pnlSinRot);

        cuerpo.add(tabbedPane, BorderLayout.CENTER);
        getContentPane().add(cuerpo, BorderLayout.CENTER);
    }

    private void attachEvents() {
        btnRefrescar.addActionListener(e -> cargarAlertas());
    }

    private void cargarAlertas() {
        cargarStockBajo();
        cargarPorVencer();
        cargarSinRotacion();
    }

    private void cargarStockBajo() {
        modelStockBajo.setRowCount(0);
        int total = 0;
        try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(
                "SELECT p.nombre, cat.descripcion, p.cantidad, 10 as minimo "
                + "FROM producto p JOIN categoria cat ON p.idCategoria = cat.idCategoria "
                + "WHERE p.cantidad < 10 AND p.estado = 1 ORDER BY p.cantidad ASC"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int stock = rs.getInt("cantidad");
                int minimo = rs.getInt("minimo");
                int deficit = stock - minimo;
                modelStockBajo.addRow(new Object[]{
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    stock + " uds",
                    minimo + " uds",
                    deficit + " uds"
                });
                total++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        lblTotalAlertas.setText(total + " producto(s) con stock bajo");
    }

    private void cargarPorVencer() {
        modelPorVencer.setRowCount(0);
        try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(
                "SELECT p.nombre, dc.lote, dc.fechaVencimiento, "
                + "DATEDIFF(dc.fechaVencimiento, CURDATE()) as diasRestantes, "
                + "p.cantidad "
                + "FROM DetalleCompra dc "
                + "JOIN producto p ON dc.idProducto = p.idProducto "
                + "WHERE dc.fechaVencimiento IS NOT NULL "
                + "AND dc.fechaVencimiento >= CURDATE() "
                + "AND DATEDIFF(dc.fechaVencimiento, CURDATE()) <= 60 "
                + "ORDER BY diasRestantes ASC"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modelPorVencer.addRow(new Object[]{
                    rs.getString("nombre"),
                    rs.getString("lote") != null ? rs.getString("lote") : "Sin lote",
                    rs.getString("fechaVencimiento"),
                    rs.getInt("diasRestantes"),
                    rs.getInt("cantidad") + " uds"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarSinRotacion() {
        modelSinRotacion.setRowCount(0);
        try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(
                "SELECT p.nombre, cat.descripcion, p.cantidad, "
                + "COALESCE(MAX(k.fecha), 'Sin movimiento') as ultimoMov "
                + "FROM producto p "
                + "JOIN categoria cat ON p.idCategoria = cat.idCategoria "
                + "LEFT JOIN Kardex k ON p.idProducto = k.idProducto "
                + "WHERE p.estado = 1 "
                + "GROUP BY p.idProducto, p.nombre, cat.descripcion, p.cantidad "
                + "HAVING ultimoMov = 'Sin movimiento' OR "
                + "DATEDIFF(CURDATE(), MAX(k.fecha)) > 30 "
                + "ORDER BY ultimoMov ASC"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modelSinRotacion.addRow(new Object[]{
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getInt("cantidad") + " uds",
                    rs.getString("ultimoMov")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
