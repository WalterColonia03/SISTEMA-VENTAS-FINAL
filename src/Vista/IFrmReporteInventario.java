package Vista;

import Conexion.Conexion;
import Vista.Estilos.UIKit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class IFrmReporteInventario extends JInternalFrame {

    private JTable tblStockBajo;
    private DefaultTableModel modelStockBajo;
    private JTable tblMasVendidos;
    private DefaultTableModel modelMasVendidos;
    private JTable tblInventario;
    private DefaultTableModel modelInventario;

    private JLabel lblTotalProductos;
    private JLabel lblTotalStock;
    private JLabel lblTotalValor;

    private JButton btnActualizar;
    private JComboBox<String> cbTopN;

    public IFrmReporteInventario() {
        super("Reporte de Inventario", true, true, true, true);
        initComponents();
        buildLayout();
        attachEvents();
        setSize(1000, 620);
        cargarDatos();
    }

    private void initComponents() {
        btnActualizar = UIKit.secondaryButton("Actualizar");

        cbTopN = new JComboBox<>(new String[]{"Top 5", "Top 10", "Top 20"});
        cbTopN.setFont(UIKit.BODY);
        cbTopN.setPreferredSize(new Dimension(100, 36));

        lblTotalProductos = new JLabel("0");
        lblTotalProductos.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotalProductos.setForeground(UIKit.PRIMARY);

        lblTotalStock = new JLabel("0");
        lblTotalStock.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotalStock.setForeground(UIKit.SUCCESS);

        lblTotalValor = new JLabel("S/ 0.00");
        lblTotalValor.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotalValor.setForeground(UIKit.ACCENT);

        // Tabla inventario completo
        String[] colsInv = {"ID", "Producto", "Categoría", "Stock", "Precio", "Valor Total"};
        modelInventario = new DefaultTableModel(colsInv, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tblInventario = UIKit.styledTable(modelInventario);

        // Tabla stock bajo
        String[] colsStock = {"Producto", "Stock", "Mínimo", "Déficit"};
        modelStockBajo = new DefaultTableModel(colsStock, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tblStockBajo = UIKit.styledTable(modelStockBajo);

        // Tabla más vendidos
        String[] colsVendidos = {"#", "Producto", "Unidades Vendidas", "Ingresos"};
        modelMasVendidos = new DefaultTableModel(colsVendidos, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tblMasVendidos = UIKit.styledTable(modelMasVendidos);
    }

    private void buildLayout() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UIKit.BG_APP);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(
                UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));

        getContentPane().add(
                UIKit.screenHeader("Reporte de Inventario", "Inventario  ›  Reporte"),
                BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(0, UIKit.SPACE_MD));
        cuerpo.setOpaque(false);

        // ── KPI Cards ──
        JPanel pnlCards = new JPanel(new GridLayout(1, 3, UIKit.SPACE_MD, 0));
        pnlCards.setOpaque(false);
        pnlCards.setPreferredSize(new Dimension(0, 85));
        pnlCards.add(buildKpiCard("TOTAL PRODUCTOS", lblTotalProductos, "Productos activos"));
        pnlCards.add(buildKpiCard("TOTAL UNIDADES", lblTotalStock, "En stock"));
        pnlCards.add(buildKpiCard("VALOR INVENTARIO", lblTotalValor, "Precio × Stock"));
        cuerpo.add(pnlCards, BorderLayout.NORTH);

        // ── Tabs ──
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIKit.BODY);

        // Tab 1: Inventario completo
        JPanel pnlInv = UIKit.card();
        pnlInv.setLayout(new BorderLayout(0, UIKit.SPACE_SM));

        JPanel pnlBtnInv = new JPanel(new FlowLayout(FlowLayout.LEFT, UIKit.SPACE_SM, 0));
        pnlBtnInv.setOpaque(false);
        pnlBtnInv.add(btnActualizar);

        pnlInv.add(UIKit.sectionHeader("Inventario Completo", pnlBtnInv), BorderLayout.NORTH);
        JScrollPane scrollInv = new JScrollPane(tblInventario);
        scrollInv.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlInv.add(scrollInv, BorderLayout.CENTER);
        tabs.addTab("Inventario Completo", pnlInv);

        // Tab 2: Stock bajo
        JPanel pnlStock = UIKit.card();
        pnlStock.setLayout(new BorderLayout(0, UIKit.SPACE_SM));
        pnlStock.add(UIKit.sectionHeader("Productos con Stock Bajo", null), BorderLayout.NORTH);
        JScrollPane scrollStock = new JScrollPane(tblStockBajo);
        scrollStock.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlStock.add(scrollStock, BorderLayout.CENTER);
        tabs.addTab("Stock Bajo", pnlStock);

        // Tab 3: Más vendidos
        JPanel pnlVendidos = UIKit.card();
        pnlVendidos.setLayout(new BorderLayout(0, UIKit.SPACE_SM));

        JPanel pnlTopHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, UIKit.SPACE_SM, 0));
        pnlTopHeader.setOpaque(false);
        pnlTopHeader.add(cbTopN);

        pnlVendidos.add(UIKit.sectionHeader("Productos Más Vendidos", pnlTopHeader), BorderLayout.NORTH);
        JScrollPane scrollVendidos = new JScrollPane(tblMasVendidos);
        scrollVendidos.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlVendidos.add(scrollVendidos, BorderLayout.CENTER);
        tabs.addTab("Más Vendidos", pnlVendidos);

        cuerpo.add(tabs, BorderLayout.CENTER);
        getContentPane().add(cuerpo, BorderLayout.CENTER);
    }

    private JPanel buildKpiCard(String titulo, JLabel lblValor, String subtitulo) {
        JPanel card = UIKit.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(UIKit.CAPTION);
        lblTitulo.setForeground(UIKit.TEXT_SECONDARY);
        gbc.gridy = 0;
        gbc.insets = new Insets(8, 12, 2, 12);
        card.add(lblTitulo, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 12, 2, 12);
        card.add(lblValor, gbc);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(UIKit.CAPTION);
        lblSub.setForeground(UIKit.TEXT_SECONDARY);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 12, 8, 12);
        card.add(lblSub, gbc);

        return card;
    }

    private void attachEvents() {
        btnActualizar.addActionListener(e -> cargarDatos());
        cbTopN.addActionListener(e -> cargarMasVendidos());
    }

    private void cargarDatos() {
        cargarKPIs();
        cargarInventario();
        cargarStockBajo();
        cargarMasVendidos();
    }

    private void cargarKPIs() {
        try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(
                "SELECT COUNT(*) as total, SUM(cantidad) as stock, "
                + "SUM(cantidad * precio) as valor FROM producto WHERE estado=1"); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                lblTotalProductos.setText(String.valueOf(rs.getInt("total")));
                lblTotalStock.setText(String.valueOf(rs.getInt("stock")));
                lblTotalValor.setText(String.format("S/ %.2f", rs.getDouble("valor")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarInventario() {
        modelInventario.setRowCount(0);
        try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(
                "SELECT p.idProducto, p.nombre, c.descripcion, p.cantidad, p.precio, "
                + "(p.cantidad * p.precio) as valorTotal "
                + "FROM producto p JOIN categoria c ON p.idCategoria = c.idCategoria "
                + "WHERE p.estado=1 ORDER BY valorTotal DESC"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modelInventario.addRow(new Object[]{
                    rs.getInt("idProducto"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getInt("cantidad"),
                    String.format("S/ %.2f", rs.getDouble("precio")),
                    String.format("S/ %.2f", rs.getDouble("valorTotal"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarStockBajo() {
        modelStockBajo.setRowCount(0);
        try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(
                "SELECT nombre, cantidad, 10 as minimo FROM producto "
                + "WHERE cantidad < 10 AND estado=1 ORDER BY cantidad ASC"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int stock = rs.getInt("cantidad");
                int minimo = rs.getInt("minimo");
                modelStockBajo.addRow(new Object[]{
                    rs.getString("nombre"),
                    stock + " uds",
                    minimo + " uds",
                    (stock - minimo) + " uds"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarMasVendidos() {
        modelMasVendidos.setRowCount(0);
        int limite = (cbTopN.getSelectedIndex() + 1) * 5;
        try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(
                "SELECT p.nombre, SUM(dv.cantidad) as totalVendido, "
                + "SUM(dv.subtotal) as ingresos "
                + "FROM DetalleVenta dv "
                + "JOIN producto p ON dv.idProducto = p.idProducto "
                + "JOIN Venta v ON dv.idVenta = v.idVenta "
                + "WHERE v.estado = 1 "
                + "GROUP BY p.idProducto, p.nombre "
                + "ORDER BY totalVendido DESC LIMIT " + limite)) {
            ResultSet rs = ps.executeQuery();
            int pos = 1;
            while (rs.next()) {
                modelMasVendidos.addRow(new Object[]{
                    "#" + pos++,
                    rs.getString("nombre"),
                    rs.getInt("totalVendido") + " uds",
                    String.format("S/ %.2f", rs.getDouble("ingresos"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
