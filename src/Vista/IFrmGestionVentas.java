package Vista;

import DAO.ClienteDAO;
import DAO.DevolucionDAO;
import Clases.Cliente;
import Conexion.Conexion;
import Vista.Estilos.UIKit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class IFrmGestionVentas extends JInternalFrame {

    private JTable tblVentas;
    private DefaultTableModel modelVentas;

    private JTextField txtBuscar;
    private JComboBox<String> cbFiltroCliente;
    private JComboBox<String> cbFiltroEstado;
    private JButton btnBuscar;
    private JButton btnRefrescar;
    private JButton btnVerDetalle;

    private JLabel lblTotalVentas;
    private JLabel lblTotalTransacciones;
 
    

    // Gráficas
    private DefaultCategoryDataset datasetTopProductos;
    private DefaultCategoryDataset datasetVentasHora;
     private JButton btnEstadisticas;

    public IFrmGestionVentas() {
        super("Gestión de Ventas", true, true, true, true);
        initComponents();
        buildLayout();
        attachEvents();
        setSize(1100, 650);
        cargarTabla();
        cargarGraficas();
    }

    private void initComponents() {
        txtBuscar = UIKit.textField();
        txtBuscar.setPreferredSize(new Dimension(180, 36));
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar cliente...");

        btnBuscar      = UIKit.secondaryButton("Buscar");
        btnRefrescar   = UIKit.secondaryButton("Refrescar");
        btnVerDetalle  = UIKit.primaryButton("Ver Detalle");
        btnEstadisticas = UIKit.secondaryButton("Ver Estadísticas");
        
        cbFiltroCliente = new JComboBox<>();
        cbFiltroCliente.setFont(UIKit.BODY);
        cbFiltroCliente.setPreferredSize(new Dimension(160, 36));
        cargarFiltroClientes();

        cbFiltroEstado = new JComboBox<>(new String[]{"Todos", "Activa", "Parcial", "Anulada"});
        cbFiltroEstado.setFont(UIKit.BODY);
        cbFiltroEstado.setPreferredSize(new Dimension(120, 36));

        String[] colsVentas = {"#Venta", "Cliente", "Subtotal", "IGV", "Total", "Método", "Fecha", "Estado"};
        modelVentas = new DefaultTableModel(colsVentas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblVentas = UIKit.styledTable(modelVentas);

        tblVentas.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);
                if (!isSelected && value != null) {
                    ((JLabel) c).setFont(UIKit.BODY_BOLD);
                    switch (value.toString()) {
                        case "Activa":
                            c.setForeground(UIKit.SUCCESS);
                            break;
                        case "Parcial":
                            c.setForeground(UIKit.WARNING);
                            break;
                        case "Anulada":
                            c.setForeground(UIKit.DANGER);
                            break;
                        default:
                            c.setForeground(UIKit.DANGER);
                            break;
                    }
                }
                return c;
            }
        });

        lblTotalVentas = new JLabel("S/ 0.00");
        lblTotalVentas.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotalVentas.setForeground(UIKit.ACCENT);

        lblTotalTransacciones = new JLabel("0");
        lblTotalTransacciones.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotalTransacciones.setForeground(UIKit.PRIMARY);

        datasetTopProductos = new DefaultCategoryDataset();
        datasetVentasHora = new DefaultCategoryDataset();
    }

    private void cargarFiltroClientes() {
        cbFiltroCliente.removeAllItems();
        cbFiltroCliente.addItem("Todos los Clientes");
        ClienteDAO dao = new ClienteDAO();
        for (Cliente c : dao.listar())
            cbFiltroCliente.addItem(c.getNombre() + " " + c.getApellido());
    }

    private void buildLayout() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UIKit.BG_APP);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(
                UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));

        getContentPane().add(
                UIKit.screenHeader("Gestión de Ventas", "Ventas  ›  Historial de Ventas"),
                BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(0, UIKit.SPACE_MD));
        cuerpo.setOpaque(false);

        // KPIs
        JPanel pnlKpis = new JPanel(new GridLayout(1, 2, UIKit.SPACE_MD, 0));
        pnlKpis.setOpaque(false);
        pnlKpis.setPreferredSize(new Dimension(0, 80));
        pnlKpis.add(buildKpi("TOTAL VENTAS ACTIVAS", lblTotalVentas, "Ingresos registrados"));
        pnlKpis.add(buildKpi("TOTAL TRANSACCIONES", lblTotalTransacciones, "Ventas en el sistema"));
        cuerpo.add(pnlKpis, BorderLayout.NORTH);

        // Panel central con tabla + gráficas
        JPanel pnlCentral = new JPanel(new BorderLayout(0, UIKit.SPACE_MD));
        pnlCentral.setOpaque(false);

        // ── Tabla ventas (ancho completo) ──
        JPanel pnlTabla = UIKit.card();
        pnlTabla.setLayout(new BorderLayout(0, UIKit.SPACE_SM));

        JPanel pnlTop = new JPanel(new BorderLayout(0, UIKit.SPACE_SM));
        pnlTop.setOpaque(false);
        pnlTop.add(UIKit.sectionHeader("Historial de Ventas", null), BorderLayout.NORTH);

        JPanel pnlBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, UIKit.SPACE_SM, 0));
        pnlBusqueda.setOpaque(false);
        pnlBusqueda.add(txtBuscar);
        pnlBusqueda.add(cbFiltroCliente);
        pnlBusqueda.add(cbFiltroEstado);
        pnlBusqueda.add(btnBuscar);
        pnlBusqueda.add(btnRefrescar);
        pnlBusqueda.add(Box.createHorizontalStrut(UIKit.SPACE_MD));
        pnlBusqueda.add(btnVerDetalle);
        pnlBusqueda.add(btnEstadisticas);
        pnlTop.add(pnlBusqueda, BorderLayout.SOUTH);

        pnlTabla.add(pnlTop, BorderLayout.NORTH);

        JScrollPane scrollVentas = new JScrollPane(tblVentas);
        scrollVentas.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlTabla.add(scrollVentas, BorderLayout.CENTER);

        pnlCentral.add(pnlTabla, BorderLayout.CENTER);


        cuerpo.add(pnlCentral, BorderLayout.CENTER);
        getContentPane().add(cuerpo, BorderLayout.CENTER);
    }
private void abrirEstadisticas() {
        cargarGraficas();

        JDialog dlg = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Estadísticas de Ventas", true);
        dlg.setSize(900, 500);
        dlg.setLocationRelativeTo(this);
        dlg.getContentPane().setBackground(UIKit.BG_APP);
        dlg.setLayout(new BorderLayout(0, UIKit.SPACE_MD));
        ((JComponent) dlg.getContentPane()).setBorder(
            new EmptyBorder(UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));

        JPanel pnlGraficas = new JPanel(new GridLayout(1, 2, UIKit.SPACE_MD, 0));
        pnlGraficas.setOpaque(false);

        // Top 5 productos
        JFreeChart chartTop = ChartFactory.createBarChart(
                "Top 5 Productos Más Vendidos", "Producto", "Cantidad",
                datasetTopProductos, PlotOrientation.HORIZONTAL, false, true, false);
        estilizarGrafica(chartTop, new Color(25, 118, 210));
        ChartPanel panelTop = new ChartPanel(chartTop);
        panelTop.setBackground(Color.WHITE);
        panelTop.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlGraficas.add(panelTop);

        // Ventas por hora
        JFreeChart chartHora = ChartFactory.createBarChart(
                "Ventas por Hora del Día", "Hora", "S/ Total",
                datasetVentasHora, PlotOrientation.VERTICAL, false, true, false);
        estilizarGrafica(chartHora, new Color(46, 125, 50));
        ChartPanel panelHora = new ChartPanel(chartHora);
        panelHora.setBackground(Color.WHITE);
        panelHora.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlGraficas.add(panelHora);

        dlg.add(pnlGraficas, BorderLayout.CENTER);

        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBot.setOpaque(false);
        JButton btnCerrar = UIKit.secondaryButton("Cerrar");
        btnCerrar.addActionListener(ev -> dlg.dispose());
        pnlBot.add(btnCerrar);
        dlg.add(pnlBot, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }
    private void estilizarGrafica(JFreeChart chart, Color color) {
        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 13));
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(new Color(230, 230, 230));
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, color);
        renderer.setShadowVisible(false);
        plot.getDomainAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 10));
        plot.getRangeAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 10));
    }

    private JPanel buildKpi(String titulo, JLabel valor, String subtitulo) {
        JPanel card = UIKit.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.gridx = 0;

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(UIKit.CAPTION); lblTit.setForeground(UIKit.TEXT_SECONDARY);
        gbc.gridy = 0; gbc.insets = new Insets(8, 12, 2, 12); card.add(lblTit, gbc);
        gbc.gridy = 1; gbc.insets = new Insets(0, 12, 2, 12); card.add(valor, gbc);
        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(UIKit.CAPTION); lblSub.setForeground(UIKit.TEXT_SECONDARY);
        gbc.gridy = 2; gbc.insets = new Insets(0, 12, 8, 12); card.add(lblSub, gbc);
        return card;
    }

    private void attachEvents() {
        btnBuscar.addActionListener(e -> {
            cargarTablaFiltrada(
                txtBuscar.getText().trim().toLowerCase(),
                cbFiltroCliente.getSelectedItem().toString(),
                cbFiltroEstado.getSelectedItem().toString()
            );
            cargarGraficas();
        });

        btnRefrescar.addActionListener(e -> {
            txtBuscar.setText("");
            cbFiltroCliente.setSelectedIndex(0);
            cbFiltroEstado.setSelectedIndex(0);
            cargarTabla();
            cargarGraficas();
        });

       

        // Doble clic en la tabla
        tblVentas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tblVentas.getSelectedRow() != -1) {
                    abrirDetalle();
                }
            }
        });
         // Botón Ver Detalle
        btnVerDetalle.addActionListener(e -> abrirDetalle());
        btnEstadisticas.addActionListener(e -> abrirEstadisticas());
    }

    // ═══════════════════════════════════════════════════════════
    //  JDIALOG — Detalle de Venta
    // ═══════════════════════════════════════════════════════════
    private void abrirDetalle() {
        int fila = tblVentas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                "Selecciona una venta primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idVenta     = Integer.parseInt(modelVentas.getValueAt(fila, 0).toString().replace("#", ""));
        String cliente  = modelVentas.getValueAt(fila, 1).toString();
        String subtotal = modelVentas.getValueAt(fila, 2).toString();
        String igv      = modelVentas.getValueAt(fila, 3).toString();
        String total    = modelVentas.getValueAt(fila, 4).toString();
        String metodo   = modelVentas.getValueAt(fila, 5).toString();
        String fecha    = modelVentas.getValueAt(fila, 6).toString();
        String estado   = modelVentas.getValueAt(fila, 7).toString();

        JDialog dlg = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Detalle de Venta #" + idVenta, true);
        dlg.setSize(650, 480);
        dlg.setLocationRelativeTo(this);
        dlg.getContentPane().setBackground(UIKit.BG_APP);
        dlg.setLayout(new BorderLayout(0, UIKit.SPACE_MD));
        ((JComponent) dlg.getContentPane()).setBorder(
            new EmptyBorder(UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));

        // Encabezado
        JPanel pnlInfo = UIKit.card();
        pnlInfo.setLayout(new GridLayout(2, 4, UIKit.SPACE_MD, UIKit.SPACE_SM));

        pnlInfo.add(buildInfoLabel("Venta", "#" + idVenta));
        pnlInfo.add(buildInfoLabel("Cliente", cliente));
        pnlInfo.add(buildInfoLabel("Fecha", fecha));
        pnlInfo.add(buildInfoLabel("Estado", estado));
        pnlInfo.add(buildInfoLabel("Subtotal", subtotal));
        pnlInfo.add(buildInfoLabel("IGV", igv));
        pnlInfo.add(buildInfoLabel("Total", total));
        pnlInfo.add(buildInfoLabel("Método", metodo));

        dlg.add(pnlInfo, BorderLayout.NORTH);

        // Tabla de productos
        String[] cols = {"Producto", "Vendido", "Devuelto", "Pendiente", "P. Unit.", "Subtotal"};
        DefaultTableModel modelDet = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tblDet = UIKit.styledTable(modelDet);

        tblDet.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);
                if (!isSelected && value != null) {
                    int dev = Integer.parseInt(value.toString());
                    c.setForeground(dev > 0 ? UIKit.WARNING : UIKit.TEXT_SECONDARY);
                    if (dev > 0) ((JLabel) c).setFont(UIKit.BODY_BOLD);
                }
                return c;
            }
        });

        tblDet.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);
                if (!isSelected && value != null) {
                    int pend = Integer.parseInt(value.toString());
                    c.setForeground(pend == 0 ? UIKit.DANGER : UIKit.SUCCESS);
                    ((JLabel) c).setFont(UIKit.BODY_BOLD);
                }
                return c;
            }
        });

        DevolucionDAO devDAO = new DevolucionDAO();
        String sql =
            "SELECT p.nombre, dv.idProducto, dv.cantidad, " +
            "dv.precioUnitario, dv.subtotal " +
            "FROM DetalleVenta dv JOIN producto p ON dv.idProducto = p.idProducto " +
            "WHERE dv.idVenta = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int idProducto = rs.getInt("idProducto");
                int vendido    = rs.getInt("cantidad");
                int devuelto   = devDAO.getCantidadDevuelta(idVenta, idProducto);
                int pendiente  = vendido - devuelto;

                modelDet.addRow(new Object[]{
                    rs.getString("nombre"),
                    vendido,
                    devuelto,
                    pendiente,
                    String.format("S/ %.2f", rs.getDouble("precioUnitario")),
                    String.format("S/ %.2f", rs.getDouble("subtotal"))
                });
            }
        } catch (SQLException ex) { ex.printStackTrace(); }

        JPanel pnlTabla = UIKit.card();
        pnlTabla.setLayout(new BorderLayout(0, UIKit.SPACE_SM));
        pnlTabla.add(UIKit.sectionHeader("Productos", null), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tblDet);
        scroll.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlTabla.add(scroll, BorderLayout.CENTER);

        dlg.add(pnlTabla, BorderLayout.CENTER);

        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBot.setOpaque(false);
        JButton btnCerrar = UIKit.secondaryButton("Cerrar");
        btnCerrar.addActionListener(ev -> dlg.dispose());
        pnlBot.add(btnCerrar);
        dlg.add(pnlBot, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private JPanel buildInfoLabel(String titulo, String valor) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel lblT = new JLabel(titulo);
        lblT.setFont(UIKit.CAPTION);
        lblT.setForeground(UIKit.TEXT_SECONDARY);
        JLabel lblV = new JLabel(valor);
        lblV.setFont(UIKit.BODY_BOLD);
        lblV.setForeground(UIKit.TEXT_PRIMARY);
        p.add(lblT, BorderLayout.NORTH);
        p.add(lblV, BorderLayout.CENTER);
        return p;
    }

    // ═══════════════════════════════════════════════════════════
    //  GRÁFICAS
    // ═══════════════════════════════════════════════════════════
    private void cargarGraficas() {
        cargarTopProductos();
        cargarVentasPorHora();
    }

    private void cargarTopProductos() {
        datasetTopProductos.clear();
        String sql =
            "SELECT p.nombre, SUM(dv.cantidad) AS totalVendido " +
            "FROM DetalleVenta dv " +
            "JOIN producto p ON dv.idProducto = p.idProducto " +
            "JOIN Venta v ON dv.idVenta = v.idVenta " +
            "WHERE v.estado IN ('Activa', 'Parcial') " +
            "GROUP BY p.nombre ORDER BY totalVendido DESC LIMIT 5";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                datasetTopProductos.addValue(
                    rs.getInt("totalVendido"), "Vendidos",
                    rs.getString("nombre"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void cargarVentasPorHora() {
        datasetVentasHora.clear();
        String sql =
            "SELECT HOUR(fecha) AS hora, COALESCE(SUM(total), 0) AS totalVenta " +
            "FROM Venta " +
            "WHERE estado IN ('Activa', 'Parcial') " +
            "GROUP BY HOUR(fecha) ORDER BY hora";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int hora = rs.getInt("hora");
                String etiqueta = String.format("%02d:00", hora);
                datasetVentasHora.addValue(
                    rs.getDouble("totalVenta"), "Ventas", etiqueta);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ═══════════════════════════════════════════════════════════
    //  CARGA DE DATOS
    // ═══════════════════════════════════════════════════════════
    private void cargarTabla() {
        cargarTablaFiltrada("", "Todos los Clientes", "Todos");
    }

    private void cargarTablaFiltrada(String texto, String filtroCliente, String filtroEstado) {
        modelVentas.setRowCount(0);
        double totalVentas = 0;
        int totalTrans = 0;

        String sql =
            "SELECT v.idVenta, CONCAT(c.nombre,' ',c.apellido) as cliente, " +
            "v.subtotal, v.igv, v.total, v.metodoPago, v.fecha, v.estado " +
            "FROM Venta v JOIN cliente c ON v.idCliente = c.idCliente " +
            "ORDER BY v.fecha DESC";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String cliente = rs.getString("cliente");
                String estado  = rs.getString("estado");

                boolean matchTexto   = texto.isEmpty() || cliente.toLowerCase().contains(texto);
                boolean matchCliente = filtroCliente.equals("Todos los Clientes") || cliente.equals(filtroCliente);
                boolean matchEstado  = filtroEstado.equals("Todos") || estado.equals(filtroEstado);

                if (matchTexto && matchCliente && matchEstado) {
                    modelVentas.addRow(new Object[]{
                        "#" + rs.getInt("idVenta"),
                        cliente,
                        String.format("S/ %.2f", rs.getDouble("subtotal")),
                        String.format("S/ %.2f", rs.getDouble("igv")),
                        String.format("S/ %.2f", rs.getDouble("total")),
                        rs.getString("metodoPago"),
                        rs.getString("fecha"),
                        estado
                    });
                    if (estado.equals("Activa") || estado.equals("Parcial")) {
                        totalVentas += rs.getDouble("total");
                        totalTrans++;
                    }
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }

        lblTotalVentas.setText(String.format("S/ %.2f", totalVentas));
        lblTotalTransacciones.setText(String.valueOf(totalTrans));
    }
}