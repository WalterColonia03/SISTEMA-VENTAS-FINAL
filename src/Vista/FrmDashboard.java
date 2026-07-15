package Vista;

import Clases.Sesion;
import Conexion.Conexion;
import Vista.Estilos.UIKit;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;

public class FrmDashboard extends JFrame {

    private JDesktopPane desktopPane;
    private JInternalFrame bgDashboardFrame;
    private JPanel pnlTopBar;
    private JLabel lblBreadcrumb;

    private JButton btnPOS, btnVentas, btnDevoluciones, btnFidelizacion, btnCotizaciones;
    private JButton btnClientes, btnProveedores;
    private JButton btnCategorias, btnProductos, btnKardex, btnAlertasInventario, btnRepInventario;
    private JButton btnCompras;
    private JButton btnFlujoCaja, btnLibroMayor, btnCuentasCP, btnRepVentas, btnEstadosFinancieros;
    private JButton btnEmpleados, btnPlanilla, btnMarcador, btnEvaluacion;
    private JButton btnUsuarios, btnAuditoria, btnConfig;
    private JButton btnLogout;

    private JButton btnSeleccionado = null;

    private JLabel lblVentasHoy, lblVentasHoyTx;
    private JLabel lblVentasSemana, lblVentasSemanaTx;
    private JLabel lblAlertasStock, lblAlertasStockDesc;
    private JLabel lblComprasMes, lblComprasMesDesc;
    private JLabel lblCxpVencidas, lblCxpVencidasDesc;

    private JTable tblAlerts, tblSales;
    private DefaultTableModel modelAlerts, modelSales;

    private DefaultCategoryDataset datasetBarras;
    private DefaultPieDataset datasetDona;
    private JPanel pnlBarras, pnlDona;
    private JButton btnGestionCaja;
    public FrmDashboard() {
        super("Minimarket LAREDO - Sistema ERP");
        initComponents();
        buildLayout();
        attachEvents();
        aplicarRol();
        configFrame();
        cargarDatosDashboard();
    }

    private void initComponents() {
        desktopPane = new JDesktopPane();
        desktopPane.setBackground(UIKit.BG_APP);

        btnPOS = buildMenuButton("Punto de Venta (POS)");
        btnVentas = buildMenuButton("Gestión de Ventas");
        btnDevoluciones = buildMenuButton("Devoluciones");
        btnCotizaciones          = buildMenuButton("Cotizaciones");
        btnFidelizacion          = buildMenuButton("Fidelización");
        btnClientes              = buildMenuButton("Clientes");
        btnProveedores           = buildMenuButton("Proveedores");
        btnCategorias            = buildMenuButton("Categorías");
        btnProductos             = buildMenuButton("Productos");
        btnKardex                = buildMenuButton("Kardex");
        btnAlertasInventario     = buildMenuButton("Alertas de Inventario");
        btnRepInventario         = buildMenuButton("Reporte de Inventario");
        btnCompras               = buildMenuButton("Registro de Compras");
        btnFlujoCaja             = buildMenuButton("Flujo de Caja");
        btnGestionCaja           = buildMenuButton("Gestión de Caja");
        btnLibroMayor            = buildMenuButton("Libro Mayor");
        btnCuentasCP             = buildMenuButton("Cuentas por Cobrar y Pagar");
        btnRepVentas             = buildMenuButton("Reporte de Ventas");
        btnEstadosFinancieros    = buildMenuButton("Estados Financieros");
        btnEmpleados             = buildMenuButton("Ficha de Empleados");
        btnPlanilla              = buildMenuButton("Planilla y Asistencia");
        btnMarcador              = buildMenuButton("Marcar Asistencia");
        btnEvaluacion            = buildMenuButton("Evaluación de Desempeño");
        btnUsuarios              = buildMenuButton("Gestión Usuarios");
        btnAuditoria             = buildMenuButton("Bitácora Auditoría");
        btnConfig                = buildMenuButton("Configuración ERP");
        btnLogout                = buildMenuButton("Cerrar Sesión");
        
        
    }

    private void buildLayout() {
        setLayout(new BorderLayout());

        // ── Sidebar ──
        JPanel pnlSidebar = new JPanel();
        pnlSidebar.setLayout(new BoxLayout(pnlSidebar, BoxLayout.Y_AXIS));
        pnlSidebar.setBackground(UIKit.PRIMARY);
        pnlSidebar.setPreferredSize(new Dimension(200, 0));
        pnlSidebar.setBorder(new EmptyBorder(15, 0, 15, 0));

        JLabel lblLogo = new JLabel("Minimarket LAREDO", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLogo.setBorder(new EmptyBorder(10, 0, 20, 0));
        pnlSidebar.add(lblLogo);

        JPanel pnlMenuContainer = new JPanel();
        pnlMenuContainer.setLayout(new BoxLayout(pnlMenuContainer, BoxLayout.Y_AXIS));
        pnlMenuContainer.setBackground(UIKit.PRIMARY);

        pnlMenuContainer.add(navGroup("Ventas", btnPOS, btnVentas, btnDevoluciones, btnFidelizacion, btnCotizaciones));
        pnlMenuContainer.add(navGroup("Clientes y Proveedores", btnClientes, btnProveedores));
        pnlMenuContainer.add(navGroup("Inventario", btnCategorias, btnProductos, btnKardex, btnAlertasInventario, btnRepInventario));
        pnlMenuContainer.add(navGroup("Compras", btnCompras));
        pnlMenuContainer.add(navGroup("Finanzas", btnFlujoCaja, btnGestionCaja, btnLibroMayor, btnCuentasCP, btnRepVentas, btnEstadosFinancieros));
        pnlMenuContainer.add(navGroup("Personal", btnEmpleados, btnPlanilla, btnMarcador, btnEvaluacion));
        pnlMenuContainer.add(navGroup("Administración", btnUsuarios, btnAuditoria, btnConfig));
        pnlMenuContainer.add(Box.createVerticalGlue());

        JScrollPane scrollMenu = new JScrollPane(pnlMenuContainer);
        scrollMenu.setBorder(null);
        scrollMenu.setOpaque(false);
        scrollMenu.getViewport().setOpaque(false);
        scrollMenu.getVerticalScrollBar().setUnitIncrement(16);
        pnlSidebar.add(scrollMenu);

        JPanel pnlLogout = new JPanel(new BorderLayout());
        pnlLogout.setOpaque(false);
        pnlLogout.setBorder(new EmptyBorder(10, 0, 0, 0));
        pnlLogout.add(btnLogout, BorderLayout.CENTER);
        pnlSidebar.add(pnlLogout);

        add(pnlSidebar, BorderLayout.WEST);

        // ── Center ──
        JPanel pnlCenter = new JPanel(new BorderLayout());

        pnlTopBar = new JPanel(new BorderLayout());
        pnlTopBar.setBackground(Color.WHITE);
        pnlTopBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIKit.BORDER),
                new EmptyBorder(12, 24, 12, 24)));

        lblBreadcrumb = new JLabel("Inicio › Panel de Control ERP");
        lblBreadcrumb.setFont(UIKit.BODY_BOLD);
        lblBreadcrumb.setForeground(UIKit.TEXT_PRIMARY);

        JButton btnVolver = new JButton("← Inicio");
        btnVolver.setFont(UIKit.BODY);
        btnVolver.setForeground(UIKit.PRIMARY);
        btnVolver.setContentAreaFilled(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVolver.addActionListener(e -> {
            for (JInternalFrame f : desktopPane.getAllFrames()) {
                if (f != bgDashboardFrame) {
                    f.dispose();
                }
            }
        });

        JPanel pnlWest = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlWest.setOpaque(false);
        pnlWest.add(btnVolver);
        pnlWest.add(lblBreadcrumb);
        pnlTopBar.add(pnlWest, BorderLayout.WEST);

        JPanel pnlUserInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlUserInfo.setOpaque(false);

        JTextField txtSearch = UIKit.textField();
        txtSearch.setPreferredSize(new Dimension(200, 32));
        txtSearch.putClientProperty("JTextField.placeholderText", "Buscar módulo...");
        pnlUserInfo.add(txtSearch);

        String nombreUsuario = Sesion.getUsuario() != null ? Sesion.getUsuario() : "Admin";
        String rolUsuario = Sesion.getRol() != null ? Sesion.getRol() : "Administrador";
        JLabel lblUser = new JLabel(nombreUsuario);
        lblUser.setFont(UIKit.BODY_BOLD);
        lblUser.setForeground(UIKit.TEXT_PRIMARY);
        // Badge de rol — igual al <span className="rounded-full bg-[#6366f1]"> de minimarket
        JLabel badgeRol = UIKit.statusBadgeSolid(rolUsuario, UIKit.ACCENT);
        pnlUserInfo.add(badgeRol);
        pnlUserInfo.add(lblUser);

        pnlTopBar.add(pnlUserInfo, BorderLayout.EAST);
        pnlCenter.add(pnlTopBar, BorderLayout.NORTH);
        pnlCenter.add(desktopPane, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        initBgDashboard();
    }

    private void initBgDashboard() {
        bgDashboardFrame = new JInternalFrame("Dashboard", false, false, false, false);
        bgDashboardFrame.setBorder(null);
        ((javax.swing.plaf.basic.BasicInternalFrameUI) bgDashboardFrame.getUI()).setNorthPane(null);

        JPanel pnlContent = new JPanel(new BorderLayout(12, 12));
        pnlContent.setBackground(UIKit.BG_APP);
        pnlContent.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel pnlCards = new JPanel(new GridLayout(1, 5, 12, 0));
        pnlCards.setOpaque(false);
        pnlCards.setPreferredSize(new Dimension(0, 90));

        lblVentasHoy      = new JLabel("S/ 0.00");
        lblVentasHoyTx    = new JLabel("0 Transacciones");
        lblVentasSemana   = new JLabel("S/ 0.00");
        lblVentasSemanaTx = new JLabel("0 Transacciones");
        lblAlertasStock   = new JLabel("0 Productos");
        lblAlertasStockDesc = new JLabel("Requieren reposición");

        lblComprasMes      = new JLabel("S/ 0.00");
        lblComprasMesDesc  = new JLabel("Este mes");
        lblCxpVencidas     = new JLabel("0");
        lblCxpVencidasDesc = new JLabel("Requieren pago");

        pnlCards.add(buildKpiCard("VENTAS HOY",       lblVentasHoy,    lblVentasHoyTx,    UIKit.ACCENT));
        pnlCards.add(buildKpiCard("VENTAS SEMANA",    lblVentasSemana, lblVentasSemanaTx, UIKit.SUCCESS));
        pnlCards.add(buildKpiCard("ALERTAS DE STOCK", lblAlertasStock, lblAlertasStockDesc, UIKit.WARNING));
        pnlCards.add(buildKpiCard("COMPRAS DEL MES",  lblComprasMes,   lblComprasMesDesc,   UIKit.PRIMARY));
        pnlCards.add(buildKpiCard("CxP VENCIDAS",     lblCxpVencidas,  lblCxpVencidasDesc,  UIKit.DANGER));
        pnlContent.add(pnlCards, BorderLayout.NORTH);

        JPanel pnlGraficas = new JPanel(new GridLayout(1, 2, 12, 0));
        pnlGraficas.setOpaque(false);
        pnlGraficas.setPreferredSize(new Dimension(0, 260));

        datasetBarras = new DefaultCategoryDataset();
        JFreeChart chartBarras = ChartFactory.createBarChart(
                "Ventas Últimos 7 Días", "Día", "S/ Total", datasetBarras);
        chartBarras.setBackgroundPaint(Color.WHITE);
        chartBarras.getPlot().setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = (CategoryPlot) chartBarras.getPlot();
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(new Color(230, 230, 230));
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, UIKit.ACCENT);
        renderer.setShadowVisible(false);
        chartBarras.getLegend().setVisible(false);

        pnlBarras = new ChartPanel(chartBarras);
        pnlBarras.setBackground(Color.WHITE);
        pnlBarras.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));

        datasetDona = new DefaultPieDataset();
        JFreeChart chartDona = ChartFactory.createRingChart(
                "Métodos de Pago", datasetDona, true, true, false);
        chartDona.setBackgroundPaint(Color.WHITE);
        PiePlot piePlot = (PiePlot) chartDona.getPlot();
        piePlot.setBackgroundPaint(Color.WHITE);
        piePlot.setOutlineVisible(false);
        piePlot.setSectionPaint("Efectivo", new Color(25, 118, 210));
        piePlot.setSectionPaint("Tarjeta de Débito", new Color(46, 125, 50));
        piePlot.setSectionPaint("Tarjeta de Crédito", new Color(198, 40, 40));
        piePlot.setSectionPaint("Mercado Pago (QR)", new Color(255, 143, 0));

        pnlDona = new ChartPanel(chartDona);
        pnlDona.setBackground(Color.WHITE);
        pnlDona.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));

        pnlGraficas.add(pnlBarras);
        pnlGraficas.add(pnlDona);
        pnlContent.add(pnlGraficas, BorderLayout.CENTER);

        JPanel pnlTables = new JPanel(new GridLayout(1, 2, 12, 0));
        pnlTables.setOpaque(false);

        JPanel pnlAlerts = UIKit.card();
        pnlAlerts.setLayout(new BorderLayout(0, UIKit.SPACE_SM));
        pnlAlerts.add(UIKit.sectionHeader("Alertas de Inventario Crítico", null), BorderLayout.NORTH);
        String[] alertCols = {"Producto", "Stock Actual", "Mínimo"};
        modelAlerts = new DefaultTableModel(alertCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblAlerts = UIKit.styledTable(modelAlerts);
        pnlAlerts.add(new JScrollPane(tblAlerts), BorderLayout.CENTER);
        pnlTables.add(pnlAlerts);

        JPanel pnlSales = UIKit.card();
        pnlSales.setLayout(new BorderLayout(0, UIKit.SPACE_SM));
        pnlSales.add(UIKit.sectionHeader("Últimas Ventas", null), BorderLayout.NORTH);
        String[] salesCols = {"#", "Cliente", "Total", "Método", "Hora"};
        modelSales = new DefaultTableModel(salesCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblSales = UIKit.styledTable(modelSales);
        pnlSales.add(new JScrollPane(tblSales), BorderLayout.CENTER);
        pnlTables.add(pnlSales);

        pnlContent.add(pnlTables, BorderLayout.SOUTH);

        bgDashboardFrame.add(pnlContent);
        desktopPane.add(bgDashboardFrame);
        bgDashboardFrame.setVisible(true);

        desktopPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                bgDashboardFrame.setBounds(0, 0, desktopPane.getWidth(), desktopPane.getHeight());
            }
        });
    }

    private void cargarDatosDashboard() {
        SwingUtilities.invokeLater(() -> {
            cargarKPIs();
            cargarGraficaBarras();
            cargarGraficaDona();
            cargarAlertasStock();
            cargarUltimasVentas();
        });
    }

    private void cargarKPIs() {
        try (Connection con = Conexion.getConexion()) {
            if (con == null) return;

            // Ventas hoy
            PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) as tx, COALESCE(SUM(total),0) as total "
                    + "FROM Venta WHERE DATE(fecha) = CURDATE() AND estado=1");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                lblVentasHoy.setText(String.format("S/ %.2f", rs.getDouble("total")));
                lblVentasHoyTx.setText(rs.getInt("tx") + " Transacciones");
            }

            // Ventas semana
            ps = con.prepareStatement(
                    "SELECT COUNT(*) as tx, COALESCE(SUM(total),0) as total "
                    + "FROM Venta WHERE fecha >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND estado=1");
            rs = ps.executeQuery();
            if (rs.next()) {
                lblVentasSemana.setText(String.format("S/ %.2f", rs.getDouble("total")));
                lblVentasSemanaTx.setText(rs.getInt("tx") + " Transacciones");
            }

            // Alertas de stock
            ps = con.prepareStatement(
                    "SELECT COUNT(*) as total FROM producto WHERE cantidad < 10 AND estado=1");
            rs = ps.executeQuery();
            if (rs.next()) {
                int alertas = rs.getInt("total");
                lblAlertasStock.setText(alertas + " Productos");
                lblAlertasStockDesc.setText(alertas > 0 ? "Requieren reposición" : "Stock OK");
            }

            // KPI Compras del mes
            ps = con.prepareStatement(
                    "SELECT COALESCE(SUM(total),0) as total FROM Compra "
                    + "WHERE DATE_FORMAT(fecha,'%Y-%m') = DATE_FORMAT(CURDATE(),'%Y-%m')");
            rs = ps.executeQuery();
            if (rs.next()) {
                lblComprasMes.setText(String.format("S/ %.2f", rs.getDouble("total")));
                lblComprasMesDesc.setText("Este mes");
            }

            // KPI Cuentas por Pagar vencidas
            ps = con.prepareStatement(
                    "SELECT COUNT(*) as cnt FROM CuentasCobrarPagar "
                    + "WHERE tipo='pagar' AND estado='Pendiente' AND fechaVencimiento < CURDATE()");
            rs = ps.executeQuery();
            if (rs.next()) {
                int venc = rs.getInt("cnt");
                lblCxpVencidas.setText(venc + " deuda" + (venc != 1 ? "s" : ""));
                lblCxpVencidasDesc.setText(venc > 0 ? "Requieren pago" : "Al día");
                lblCxpVencidas.setForeground(venc > 0 ? UIKit.DANGER : UIKit.SUCCESS);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarGraficaBarras() {
        datasetBarras.clear();
        try (Connection con = Conexion.getConexion()) {
            if (con == null) {
                return;
            }
            PreparedStatement ps = con.prepareStatement(
                    "SELECT DATE(fecha) as dia, COALESCE(SUM(total),0) as total "
                    + "FROM Venta WHERE fecha >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) "
                    + "AND estado=1 GROUP BY DATE(fecha) ORDER BY dia");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                datasetBarras.addValue(rs.getDouble("total"), "Ventas", rs.getString("dia"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarGraficaDona() {
        datasetDona.clear();
        try (Connection con = Conexion.getConexion()) {
            if (con == null) {
                return;
            }
            PreparedStatement ps = con.prepareStatement(
                    "SELECT metodoPago, COALESCE(SUM(total),0) as total "
                    + "FROM Venta WHERE estado=1 GROUP BY metodoPago");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                double total = rs.getDouble("total");
                if (total > 0) {
                    datasetDona.setValue(rs.getString("metodoPago"), total);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarAlertasStock() {
        modelAlerts.setRowCount(0);
        try (Connection con = Conexion.getConexion()) {
            if (con == null) {
                return;
            }
            PreparedStatement ps = con.prepareStatement(
                    "SELECT nombre, cantidad FROM producto WHERE cantidad < 10 AND estado=1 "
                    + "ORDER BY cantidad ASC LIMIT 8");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modelAlerts.addRow(new Object[]{
                    rs.getString("nombre"),
                    rs.getInt("cantidad") + " unidades",
                    "10 unidades"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarUltimasVentas() {
        modelSales.setRowCount(0);
        try (Connection con = Conexion.getConexion()) {
            if (con == null) {
                return;
            }
            PreparedStatement ps = con.prepareStatement(
                    "SELECT v.idVenta, CONCAT(c.nombre,' ',c.apellido) as cliente, "
                    + "v.total, v.metodoPago, TIME(v.fecha) as hora "
                    + "FROM Venta v JOIN cliente c ON v.idCliente=c.idCliente "
                    + "WHERE v.estado=1 ORDER BY v.fecha DESC LIMIT 8");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modelSales.addRow(new Object[]{
                    "#" + rs.getInt("idVenta"),
                    rs.getString("cliente"),
                    String.format("S/ %.2f", rs.getDouble("total")),
                    rs.getString("metodoPago"),
                    rs.getString("hora")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JPanel buildKpiCard(String titulo, JLabel lblValor, JLabel lblDesc, Color accentColor) {
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(UIKit.CAPTION);
        lblTitulo.setForeground(UIKit.TEXT_SECONDARY);
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 14, 2, 14);
        card.add(lblTitulo, gbc);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValor.setForeground(accentColor);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 14, 2, 14);
        card.add(lblValor, gbc);

        lblDesc.setFont(UIKit.CAPTION);
        lblDesc.setForeground(UIKit.TEXT_SECONDARY);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 14, 10, 14);
        card.add(lblDesc, gbc);

        return card;
    }

    private JPanel navGroup(String titulo, JButton... botones) {
        JPanel grupo = new JPanel();
        grupo.setOpaque(false);
        grupo.setLayout(new BoxLayout(grupo, BoxLayout.Y_AXIS));
        grupo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblGrupo = new JLabel(titulo.toUpperCase());
        lblGrupo.setFont(UIKit.CAPTION);
        lblGrupo.setForeground(new Color(255, 255, 255, 130));
        lblGrupo.setBorder(new EmptyBorder(UIKit.SPACE_MD, UIKit.SPACE_MD, UIKit.SPACE_XS, UIKit.SPACE_MD));
        grupo.add(lblGrupo);

        for (JButton b : botones) {
            grupo.add(b);
            grupo.add(Box.createVerticalStrut(2));
        }
        return grupo;
    }

    private JButton buildMenuButton(String text) {
        JButton btn = new JButton("  " + text);
        btn.setFont(UIKit.BODY);
        btn.setForeground(UIKit.SIDEBAR_TEXT_INACTIVE); // gray-400 inactivo
        btn.setBackground(UIKit.PRIMARY);               // gray-900
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        btn.addActionListener(e -> {
            if (btnSeleccionado != null) {
                // Restaurar estado inactivo
                btnSeleccionado.setBackground(UIKit.PRIMARY);
                btnSeleccionado.setForeground(UIKit.SIDEBAR_TEXT_INACTIVE);
                btnSeleccionado.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
            }
            btnSeleccionado = btn;
            // Estado activo — fondo ACCENT sólido (indigo), texto blanco
            btn.setBackground(UIKit.ACCENT);
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        });

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn != btnSeleccionado) {
                    btn.setBackground(UIKit.SIDEBAR_HOVER); // gray-800
                    btn.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn != btnSeleccionado) {
                    btn.setBackground(UIKit.PRIMARY);
                    btn.setForeground(UIKit.SIDEBAR_TEXT_INACTIVE);
                }
            }
        });
        return btn;
    }

    private void attachEvents() {
        btnPOS.addActionListener(e -> openFrame(new IFrmPuntoVenta(), "Punto de Venta"));
        btnVentas.addActionListener(e -> openFrame(new IFrmGestionVentas(), "Gestión de Ventas"));
        btnDevoluciones.addActionListener(e -> openFrame(new IFrmDevoluciones(), "Devoluciones"));
        btnCotizaciones.addActionListener(e -> openFrame(new IFrmCotizaciones(), "Cotizaciones"));
        btnFidelizacion.addActionListener(e -> openFrame(new IFrmFidelizacion(), "Fidelización"));
        btnClientes.addActionListener(e -> openFrame(new IFrmGestionClientes(), "Gestión de Clientes"));
        btnProveedores.addActionListener(e -> openFrame(new IFrmGestionProveedores(), "Proveedores"));
        btnCategorias.addActionListener(e -> openFrame(new IFrmGestionCategorias(), "Categorías"));
        btnProductos.addActionListener(e -> openFrame(new IFrmGestionProductos(), "Productos"));
        btnKardex.addActionListener(e -> openFrame(new IFrmKardex(), "Kardex"));
        btnAlertasInventario.addActionListener(e -> openFrame(new IFrmAlertasInventario(), "Alertas de Inventario"));
        btnRepInventario.addActionListener(e -> openFrame(new IFrmReporteInventario(), "Reporte de Inventario"));
        btnCompras.addActionListener(e -> openFrame(new IFrmRegistroCompras(), "Registro de Compras"));
        btnFlujoCaja.addActionListener(e -> openFrame(new IFrmFlujoCaja(), "Flujo de Caja"));
        btnGestionCaja.addActionListener(e -> openFrame(new IFrmGestionCaja(), "Gestión de Caja"));
        btnLibroMayor.addActionListener(e -> openFrame(new IFrmLibroMayor(), "Libro Mayor"));
        btnCuentasCP.addActionListener(e -> openFrame(new IFrmCuentasCobrarPagar(), "Cuentas Cobrar / Pagar"));
        btnRepVentas.addActionListener(e -> openFrame(new IFrmReporteVentas(), "Reporte de Ventas"));
        btnEmpleados.addActionListener(e -> openFrame(new IFrmFichaEmpleados(), "Empleados"));
        btnPlanilla.addActionListener(e -> openFrame(new IFrmPlanillaAsistencia(), "Planilla y Asistencia"));
        btnMarcador.addActionListener(e -> openFrame(new IFrmMarcadorAsistencia(), "Marcar Asistencia"));
        btnEvaluacion.addActionListener(e -> openFrame(new IFrmEvaluacionDesempeno(), "Evaluación de Desempeño"));
        btnUsuarios.addActionListener(e -> openFrame(new IFrmGestionUsuarios(), "Usuarios"));
        btnAuditoria.addActionListener(e -> openFrame(new IFrmBitacoraAuditoria(), "Auditoría"));
        btnConfig.addActionListener(e -> openFrame(new IFrmConfiguracionERP(), "Configuración"));

        btnLogout.addActionListener(e -> {
            int op = JOptionPane.showConfirmDialog(this, "¿Cerrar sesión?", "Salir", JOptionPane.YES_NO_OPTION);
            if (op != JOptionPane.YES_OPTION) return;

            // Marcar salida automática si es Vendedor
            if ("Vendedor".equalsIgnoreCase(Sesion.getRol())) {
                int idEmpleado = Sesion.getIdEmpleado();
                String fechaHoy    = java.time.LocalDate.now().toString();
                String horaActual  = java.time.LocalTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));

                // Marcar salida (una sola vez)
                DAO.PlanillaDAO planillaDAO = new DAO.PlanillaDAO();
                if (planillaDAO.marcarSalida(idEmpleado, fechaHoy, horaActual)) {
                    JOptionPane.showMessageDialog(FrmDashboard.this,
                        "Salida registrada a las " + horaActual,
                        "Asistencia", JOptionPane.INFORMATION_MESSAGE);
                }

                // Cierre de caja
                DAO.CajaChicaDAO cajaDAO = new DAO.CajaChicaDAO();
                Object[] caja = cajaDAO.getCajaAbierta(idEmpleado);
                if (caja != null) {
                    double apertura = (double) caja[1];
                    double ingresos = (double) caja[2];
                    double egresos  = (double) caja[3];
                    double esperado = apertura + ingresos - egresos;

                    JPanel panel = new JPanel(new GridLayout(0, 1, 0, 6));
                    panel.add(new JLabel("Apertura:  S/ " + String.format("%.2f", apertura)));
                    panel.add(new JLabel("Ventas:     S/ " + String.format("%.2f", ingresos)));
                    panel.add(new JLabel("Egresos:   S/ " + String.format("%.2f", egresos)));
                    panel.add(new JLabel("─────────────────"));
                    panel.add(new JLabel("Esperado: S/ " + String.format("%.2f", esperado)));
                    panel.add(new JLabel(" "));
                    panel.add(new JLabel("¿Cuánto hay en caja? (S/)"));
                    JTextField txtCierre = new JTextField();
                    txtCierre.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
                    txtCierre.setHorizontalAlignment(JTextField.CENTER);
                    panel.add(txtCierre);
                    panel.add(new JLabel("Observaciones (opcional):"));
                    JTextField txtObs = new JTextField();
                    panel.add(txtObs);

                    int result = JOptionPane.showConfirmDialog(FrmDashboard.this,
                        panel, "Cierre de Caja", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.OK_OPTION) {
                        try {
                            double montoCierre = Double.parseDouble(
                                txtCierre.getText().trim().replace(",", "."));
                            double diferencia = montoCierre - esperado;
                            cajaDAO.cerrarCaja(idEmpleado, montoCierre, txtObs.getText().trim());

                            String msg = "Cierre registrado\n" +
                                "Esperado: S/ " + String.format("%.2f", esperado) + "\n" +
                                "Real: S/ "     + String.format("%.2f", montoCierre) + "\n";
                            if (diferencia == 0)     msg += "Diferencia: S/ 0.00 ✔ Cuadrado";
                            else if (diferencia > 0) msg += "Sobrante: S/ "  + String.format("%.2f", diferencia);
                            else                     msg += "Faltante: S/ "  + String.format("%.2f", Math.abs(diferencia));
                            JOptionPane.showMessageDialog(FrmDashboard.this, msg,
                                "Cierre de Caja", JOptionPane.INFORMATION_MESSAGE);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(FrmDashboard.this,
                                "Monto inválido. La caja quedó abierta.");
                        }
                    }
                }
            } // fin Vendedor

            new FrmLogin().setVisible(true);
            this.dispose();
        });
    }
    

    private void openFrame(JInternalFrame iframe, String frameName) {
        lblBreadcrumb.setText("Inicio › " + frameName);

        // Cerrar frames anteriores
        for (JInternalFrame f : desktopPane.getAllFrames()) {
            if (f != bgDashboardFrame) {
                f.dispose();
            }
        }

        // Ocultar dashboard de fondo
        bgDashboardFrame.setVisible(false);

        // Quitar barra de título interna
        iframe.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
        ((javax.swing.plaf.basic.BasicInternalFrameUI) iframe.getUI()).setNorthPane(null);
        iframe.setBorder(null);

        desktopPane.add(iframe);
        iframe.setVisible(true);

        // Maximizar para ocupar todo el espacio
        try {
            iframe.setMaximum(true);
            iframe.setSelected(true);
        } catch (java.beans.PropertyVetoException ex) {
            ex.printStackTrace();
        }

        // Al cerrar volver al dashboard
        iframe.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent e) {
                bgDashboardFrame.setVisible(true);
                lblBreadcrumb.setText("Inicio › Panel de Control ERP");
                // Resetear botón seleccionado
                if (btnSeleccionado != null) {
                    btnSeleccionado.setBackground(UIKit.PRIMARY);
                    btnSeleccionado.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
                    btnSeleccionado = null;
                }
                cargarDatosDashboard();
            }
        });
    }

    /**
     * RBAC — Sistema de control de acceso basado en roles.
     *
     * Mapa de permisos por rol:
     * ┌───────────────────────────────────────────────────────┐
     * |                 | Gerente | Vendedor | Almacenero | Contador |
     * |─────────────────|─────────|──────────|────────────|──────────|
     * | POS / Ventas    |   ✓     |   ✓      |    ✗      |   ✗     |
     * | Clientes/Prov.  |   ✓     |   ✓      |    ✓      |   ✗     |
     * | Inventario      |   ✓     |   ✗      |    ✓      |   ✗     |
     * | Compras         |   ✓     |   ✗      |    ✓      |   ✗     |
     * | Finanzas        |   ✓     |   ✗      |    ✗      |   ✓     |
     * | Personal        |   ✓     |   ✓*     |    ✗      |   ✗     |
     * | Administración  |   ✓     |   ✗      |    ✗      |   ✗     |
     * └───────────────────────────────────────────────────────┘
     * * Solo btnMarcador (marcar su propia asistencia)
     */
    private void aplicarRol() {
        String rol = Sesion.getRol() != null ? Sesion.getRol().toLowerCase() : "vendedor";

        // Por defecto, ocultar todo (principio de menor privilegio)
        JButton[] todos = {
            btnPOS, btnVentas, btnDevoluciones, btnFidelizacion, btnCotizaciones,
            btnClientes, btnProveedores,
            btnCategorias, btnProductos, btnKardex, btnAlertasInventario, btnRepInventario,
            btnCompras,
            btnFlujoCaja, btnGestionCaja, btnLibroMayor, btnCuentasCP, btnRepVentas, btnEstadosFinancieros,
            btnEmpleados, btnPlanilla, btnMarcador, btnEvaluacion,
            btnUsuarios, btnAuditoria, btnConfig
        };
        for (JButton b : todos) b.setVisible(false);

        switch (rol) {

            case "gerente":
            case "admin":
            case "administrador":
                // ACCESO TOTAL
                for (JButton b : todos) b.setVisible(true);
                break;

            case "vendedor":
                // Ventas + Cotizaciones + Clientes + Marcar Asistencia
                btnPOS.setVisible(true);
                btnVentas.setVisible(true);
                btnDevoluciones.setVisible(true);
                btnFidelizacion.setVisible(true);
                btnCotizaciones.setVisible(true);
                btnClientes.setVisible(true);
                btnMarcador.setVisible(true);
                break;

            case "almacenero":
                // Inventario completo + Compras + Proveedores + Clientes (consulta)
                btnCategorias.setVisible(true);
                btnProductos.setVisible(true);
                btnKardex.setVisible(true);
                btnAlertasInventario.setVisible(true);
                btnRepInventario.setVisible(true);
                btnCompras.setVisible(true);
                btnProveedores.setVisible(true);
                btnClientes.setVisible(true);
                btnMarcador.setVisible(true);
                break;

            case "contador":
                // Finanzas completa + Reportes + Vista de ventas (solo lectura)
                btnFlujoCaja.setVisible(true);
                btnGestionCaja.setVisible(true);
                btnLibroMayor.setVisible(true);
                btnCuentasCP.setVisible(true);
                btnRepVentas.setVisible(true);
                btnRepInventario.setVisible(true);
                btnEstadosFinancieros.setVisible(true);
                btnVentas.setVisible(true);      // Solo historial, no POS
                btnMarcador.setVisible(true);
                break;

            default:
                // Rol desconocido — acceso mínimo (igual que Vendedor)
                System.err.println("[RBAC] Rol no reconocido: '" + rol + "'. Aplicando permisos mínimos.");
                btnPOS.setVisible(true);
                btnVentas.setVisible(true);
                btnMarcador.setVisible(true);
                break;
        }
    }

    private void configFrame() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1024, 720));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
