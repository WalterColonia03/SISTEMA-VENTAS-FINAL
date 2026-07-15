package Vista;

import Clases.Producto;
import DAO.DevolucionDAO;
import DAO.ProductoDAO;
import Vista.Estilos.UIKit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class IFrmDevoluciones extends JInternalFrame {

    private JTable tblDevoluciones;
    private DefaultTableModel modelDevoluciones;

    private JTextField txtIdVenta;
    private JComboBox<String> cbProductos;
    private JTextField txtCantidad;
    private JLabel lblPrecioUnit;
    private JLabel lblStockVendido;
    private JLabel lblYaDevuelto;
    private JLabel lblMontoReembolso;
    private JComboBox<String> cbMotivo;
    private JComboBox<String> cbTipoReembolso;

    private JButton btnBuscarVenta;
    private JButton btnProcesar;
    private JButton btnCancelarVenta;
    private JButton btnLimpiar;
    private JButton btnRefrescar;
    private JButton btnEstadisticas;

    private List<Object[]> productosVenta;
    private int idVentaActual = -1;

    public IFrmDevoluciones() {
        super("Control de Devoluciones", true, true, true, true);
        initComponents();
        buildLayout();
        attachEvents();
        setSize(1100, 650);
        cargarTabla();
    }

    private void initComponents() {
        // Tabla historial con columna Tipo
        String[] columns = {"ID", "Venta", "Producto", "Cant", "Motivo", "Reembolso", "Monto", "Tipo", "Fecha"};
        modelDevoluciones = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tblDevoluciones = UIKit.styledTable(modelDevoluciones);

        // Colorear columna Tipo (col 7)
        tblDevoluciones.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);
                if (!isSelected && value != null) {
                    if (value.toString().equals("Cancelacion")) {
                        c.setForeground(UIKit.DANGER);
                        ((JLabel) c).setFont(UIKit.BODY_BOLD);
                    } else {
                        c.setForeground(UIKit.WARNING);
                        ((JLabel) c).setFont(UIKit.BODY_BOLD);
                    }
                }
                return c;
            }
        });

        txtIdVenta = UIKit.textField();
        txtIdVenta.putClientProperty("JTextField.placeholderText", "Ej: 5");

        cbProductos = new JComboBox<>();
        cbProductos.setFont(UIKit.BODY);

        txtCantidad = UIKit.textField();
        txtCantidad.setText("1");
        txtCantidad.setHorizontalAlignment(JTextField.RIGHT);

        lblPrecioUnit = new JLabel("S/ 0.00");
        lblPrecioUnit.setFont(UIKit.BODY_BOLD);

        lblStockVendido = new JLabel("0 unidades vendidas");
        lblStockVendido.setFont(UIKit.CAPTION);
        lblStockVendido.setForeground(UIKit.TEXT_SECONDARY);

        lblYaDevuelto = new JLabel("0 ya devueltas");
        lblYaDevuelto.setFont(UIKit.CAPTION);
        lblYaDevuelto.setForeground(UIKit.DANGER);

        lblMontoReembolso = new JLabel("S/ 0.00");
        lblMontoReembolso.setFont(UIKit.H1);
        lblMontoReembolso.setForeground(UIKit.ACCENT);

        cbMotivo = new JComboBox<>(new String[]{
            "Defecto de Fábrica", "Producto Vencido",
            "Cambio de Opinión", "Error de Despacho", "Otros"});
        cbMotivo.setFont(UIKit.BODY);

        cbTipoReembolso = new JComboBox<>(new String[]{
            "Efectivo", "Nota de Crédito", "Cambio de Producto"});
        cbTipoReembolso.setFont(UIKit.BODY);

        btnBuscarVenta   = UIKit.secondaryButton("Buscar Venta");
        btnProcesar      = UIKit.primaryButton("Devolver Producto");
        btnProcesar.setPreferredSize(new Dimension(0, 40));
        btnCancelarVenta = UIKit.dangerOutlineButton("Cancelar Venta Completa");
        btnCancelarVenta.setPreferredSize(new Dimension(0, 40));
        btnLimpiar       = UIKit.secondaryButton("Limpiar");
        btnEstadisticas = UIKit.secondaryButton("Ver Estadísticas");
        btnRefrescar     = UIKit.secondaryButton("Refrescar");
    }

    private void buildLayout() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UIKit.BG_APP);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(
                UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));

        getContentPane().add(
                UIKit.screenHeader("Devoluciones", "Ventas  ›  Control de Devoluciones"),
                BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(UIKit.SPACE_LG, 0));
        cuerpo.setOpaque(false);

        // ── Tabla historial ──
        JPanel pnlTabla = UIKit.card();
        pnlTabla.setLayout(new BorderLayout(0, UIKit.SPACE_SM));

        JPanel pnlTopTabla = new JPanel(new BorderLayout());
        pnlTopTabla.setOpaque(false);
        pnlTopTabla.add(UIKit.sectionHeader("Historial de Devoluciones", null), BorderLayout.NORTH);

        JPanel pnlAccTabla = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIKit.SPACE_SM, 0));
        pnlAccTabla.setOpaque(false);
        pnlAccTabla.add(btnRefrescar);
        pnlAccTabla.add(btnEstadisticas);
        pnlTopTabla.add(pnlAccTabla, BorderLayout.SOUTH);
        pnlTabla.add(pnlTopTabla, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tblDevoluciones);
        scroll.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlTabla.add(scroll, BorderLayout.CENTER);
        cuerpo.add(pnlTabla, BorderLayout.CENTER);

        // ── Formulario derecho ──
        JPanel pnlForm = UIKit.card();
        pnlForm.setPreferredSize(new Dimension(300, 0));
        pnlForm.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx   = 0;

        gbc.gridy = 0; gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlForm.add(UIKit.sectionHeader("Registrar Devolución", null), gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("N° Venta"), gbc);

        JPanel pnlVenta = new JPanel(new BorderLayout(UIKit.SPACE_SM, 0));
        pnlVenta.setOpaque(false);
        pnlVenta.add(txtIdVenta, BorderLayout.CENTER);
        pnlVenta.add(btnBuscarVenta, BorderLayout.EAST);
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlForm.add(pnlVenta, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("Producto"), gbc);
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(cbProductos, gbc);
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(lblStockVendido, gbc);
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(lblYaDevuelto, gbc);
        gbc.gridy = 7; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("Precio Unitario"), gbc);
        gbc.gridy = 8; gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlForm.add(lblPrecioUnit, gbc);

        gbc.gridy = 9; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("Cantidad a Devolver"), gbc);
        gbc.gridy = 10; gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlForm.add(txtCantidad, gbc);

        gbc.gridy = 11; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("Motivo"), gbc);
        gbc.gridy = 12; gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlForm.add(cbMotivo, gbc);

        gbc.gridy = 13; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("Tipo de Reembolso"), gbc);
        gbc.gridy = 14; gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlForm.add(cbTipoReembolso, gbc);

        gbc.gridy = 15; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        JLabel lblMontoTit = new JLabel("MONTO A REEMBOLSAR");
        lblMontoTit.setFont(UIKit.BODY_BOLD);
        lblMontoTit.setForeground(UIKit.TEXT_SECONDARY);
        pnlForm.add(lblMontoTit, gbc);
        gbc.gridy = 16; gbc.insets = new Insets(0, 0, UIKit.SPACE_LG, 0);
        pnlForm.add(lblMontoReembolso, gbc);

        gbc.gridy = 17; gbc.insets = new Insets(0, 0, UIKit.SPACE_SM, 0);
        pnlForm.add(btnProcesar, gbc);
        gbc.gridy = 18; gbc.insets = new Insets(0, 0, UIKit.SPACE_SM, 0);
        pnlForm.add(btnCancelarVenta, gbc);
        gbc.gridy = 19; gbc.weighty = 1.0; gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        pnlForm.add(btnLimpiar, gbc);

        cuerpo.add(pnlForm, BorderLayout.EAST);
        getContentPane().add(cuerpo, BorderLayout.CENTER);
    }

    private void attachEvents() {
        btnBuscarVenta.addActionListener(e -> buscarVenta());
        txtIdVenta.addActionListener(e -> buscarVenta());
        cbProductos.addActionListener(e -> actualizarInfoProducto());
        cbMotivo.addActionListener(e -> {
            if ("Otros".equals(cbMotivo.getSelectedItem())) {
                String motivo = JOptionPane.showInputDialog(this,
                    "Escriba el motivo de la devolución:",
                    "Motivo Personalizado", JOptionPane.QUESTION_MESSAGE);
                if (motivo != null && !motivo.trim().isEmpty()) {
                    cbMotivo.removeItem("Otros");
                    cbMotivo.addItem(motivo.trim());
                    cbMotivo.setSelectedItem(motivo.trim());
                    cbMotivo.addItem("Otros");
                } else {
                    cbMotivo.setSelectedIndex(0);
                }
            }
        });

        txtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                calcularMonto();
            }
        });

        btnProcesar.addActionListener(e -> procesarDevolucion());
        btnCancelarVenta.addActionListener(e -> cancelarVentaCompleta());
        btnLimpiar.addActionListener(e -> limpiar());
        btnRefrescar.addActionListener(e -> cargarTabla());
        btnEstadisticas.addActionListener(e -> abrirEstadisticas());
    }
       private void abrirEstadisticas() {
        JDialog dlg = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Estadísticas de Devoluciones", true);
        dlg.setSize(900, 500);
        dlg.setLocationRelativeTo(this);
        dlg.getContentPane().setBackground(UIKit.BG_APP);
        dlg.setLayout(new BorderLayout(0, UIKit.SPACE_MD));
        ((JComponent) dlg.getContentPane()).setBorder(
            new EmptyBorder(UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));

        JPanel pnlGraficas = new JPanel(new GridLayout(1, 2, UIKit.SPACE_MD, 0));
        pnlGraficas.setOpaque(false);

        // Gráfica 1: Motivos de devolución (pastel)
        org.jfree.data.general.DefaultPieDataset datasetMotivos = new org.jfree.data.general.DefaultPieDataset();
        String sqlMotivos =
            "SELECT motivo, COUNT(*) AS total FROM Devolucion GROUP BY motivo ORDER BY total DESC";
        try (java.sql.Connection con = Conexion.Conexion.getConexion();
             java.sql.PreparedStatement ps = con.prepareStatement(sqlMotivos);
             java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                datasetMotivos.setValue(rs.getString("motivo"), rs.getInt("total"));
            }
        } catch (java.sql.SQLException e) { e.printStackTrace(); }

        org.jfree.chart.JFreeChart chartMotivos = org.jfree.chart.ChartFactory.createPieChart(
            "Motivos de Devolución", datasetMotivos, true, true, false);
        chartMotivos.setBackgroundPaint(Color.WHITE);
        org.jfree.chart.plot.PiePlot plotMotivos = (org.jfree.chart.plot.PiePlot) chartMotivos.getPlot();
        plotMotivos.setBackgroundPaint(Color.WHITE);
        plotMotivos.setOutlineVisible(false);
        plotMotivos.setSectionPaint("Defecto de Fábrica", new Color(198, 40, 40));
        plotMotivos.setSectionPaint("Producto Vencido", new Color(255, 143, 0));
        plotMotivos.setSectionPaint("Cambio de Opinión", new Color(25, 118, 210));
        plotMotivos.setSectionPaint("Error de Despacho", new Color(46, 125, 50));
        plotMotivos.setLabelFont(new Font("Segoe UI", Font.PLAIN, 11));

        org.jfree.chart.ChartPanel panelMotivos = new org.jfree.chart.ChartPanel(chartMotivos);
        panelMotivos.setBackground(Color.WHITE);
        panelMotivos.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlGraficas.add(panelMotivos);

        // Gráfica 2: Productos más devueltos (barra horizontal)
        org.jfree.data.category.DefaultCategoryDataset datasetProductos = new org.jfree.data.category.DefaultCategoryDataset();
        String sqlProductos =
            "SELECT p.nombre, SUM(d.cantidad) AS totalDev " +
            "FROM Devolucion d JOIN producto p ON d.idProducto = p.idProducto " +
            "GROUP BY p.nombre ORDER BY totalDev DESC LIMIT 5";
        try (java.sql.Connection con = Conexion.Conexion.getConexion();
             java.sql.PreparedStatement ps = con.prepareStatement(sqlProductos);
             java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                datasetProductos.addValue(rs.getInt("totalDev"), "Devueltos", rs.getString("nombre"));
            }
        } catch (java.sql.SQLException e) { e.printStackTrace(); }

        org.jfree.chart.JFreeChart chartProductos = org.jfree.chart.ChartFactory.createBarChart(
            "Top 5 Productos Más Devueltos", "Producto", "Cantidad",
            datasetProductos, org.jfree.chart.plot.PlotOrientation.HORIZONTAL, false, true, false);
        chartProductos.setBackgroundPaint(Color.WHITE);
        chartProductos.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 13));
        org.jfree.chart.plot.CategoryPlot plotProd = (org.jfree.chart.plot.CategoryPlot) chartProductos.getPlot();
        plotProd.setBackgroundPaint(Color.WHITE);
        plotProd.setOutlineVisible(false);
        plotProd.setRangeGridlinePaint(new Color(230, 230, 230));
        org.jfree.chart.renderer.category.BarRenderer rendererProd = (org.jfree.chart.renderer.category.BarRenderer) plotProd.getRenderer();
        rendererProd.setSeriesPaint(0, new Color(198, 40, 40));
        rendererProd.setShadowVisible(false);

        org.jfree.chart.ChartPanel panelProductos = new org.jfree.chart.ChartPanel(chartProductos);
        panelProductos.setBackground(Color.WHITE);
        panelProductos.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlGraficas.add(panelProductos);

        dlg.add(pnlGraficas, BorderLayout.CENTER);

        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBot.setOpaque(false);
        JButton btnCerrar = UIKit.secondaryButton("Cerrar");
        btnCerrar.addActionListener(ev -> dlg.dispose());
        pnlBot.add(btnCerrar);
        dlg.add(pnlBot, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }
    private void buscarVenta() {
        String idStr = txtIdVenta.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el N° de venta");
            return;
        }
        try {
            int idVenta = Integer.parseInt(idStr);
            DevolucionDAO dao = new DevolucionDAO();

            if (!dao.ventaExisteYActiva(idVenta)) {
                JOptionPane.showMessageDialog(this,
                    "La venta #" + idVenta + " no existe o ya fue anulada",
                    "Venta no válida", JOptionPane.WARNING_MESSAGE);
                return;
            }

            productosVenta = dao.getProductosVenta(idVenta);
            if (productosVenta.isEmpty()) {
                JOptionPane.showMessageDialog(this, "La venta no tiene productos registrados");
                return;
            }

            idVentaActual = idVenta;
            cbProductos.removeAllItems();
            for (Object[] prod : productosVenta)
                cbProductos.addItem(prod[0] + " - " + prod[1]);
            cbProductos.setSelectedIndex(0);
            actualizarInfoProducto();

            JOptionPane.showMessageDialog(this,
                "Venta #" + idVenta + " encontrada con " + productosVenta.size() + " producto(s)",
                "Venta Encontrada", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un número válido");
        }
    }

    private void actualizarInfoProducto() {
        if (cbProductos.getSelectedIndex() == -1 || productosVenta == null) return;

        Object[] prod   = productosVenta.get(cbProductos.getSelectedIndex());
        int idProducto  = (int) prod[0];
        int cantVendida = (int) prod[2];
        double precio   = (double) prod[3];

        DevolucionDAO dao  = new DevolucionDAO();
        int yaDevuelto     = dao.getCantidadDevuelta(idVentaActual, idProducto);
        int disponible     = cantVendida - yaDevuelto;

        lblPrecioUnit.setText(String.format("S/ %.2f", precio));
        lblStockVendido.setText(cantVendida + " vendidas — " + disponible + " disponibles para devolver");
        lblYaDevuelto.setText(yaDevuelto > 0 ? yaDevuelto + " ya devueltas anteriormente" : "Sin devoluciones previas");
        lblYaDevuelto.setForeground(yaDevuelto > 0 ? UIKit.DANGER : UIKit.SUCCESS);
        calcularMonto();
    }

    private void calcularMonto() {
        try {
            if (cbProductos.getSelectedIndex() == -1 || productosVenta == null) return;
            Object[] prod  = productosVenta.get(cbProductos.getSelectedIndex());
            double precio  = (double) prod[3];
            int cantidad   = Integer.parseInt(txtCantidad.getText().trim());
            lblMontoReembolso.setText(String.format("S/ %.2f", precio * cantidad));
        } catch (NumberFormatException ex) {
            lblMontoReembolso.setText("S/ 0.00");
        }
    }

    private void procesarDevolucion() {
        if (idVentaActual == -1) {
            JOptionPane.showMessageDialog(this, "Primero busque una venta válida"); return;
        }
        if (cbProductos.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto"); return;
        }

        try {
            int cant = Integer.parseInt(txtCantidad.getText().trim());
            if (cant <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0"); return;
            }

            Object[] prod   = productosVenta.get(cbProductos.getSelectedIndex());
            int idProducto  = (int) prod[0];
            String nombre   = (String) prod[1];
            int cantVendida = (int) prod[2];
            double precio   = (double) prod[3];

            DevolucionDAO dao = new DevolucionDAO();
            int yaDevuelto    = dao.getCantidadDevuelta(idVentaActual, idProducto);
            int disponible    = cantVendida - yaDevuelto;

            if (cant > disponible) {
                JOptionPane.showMessageDialog(this,
                    "No puede devolver " + cant + " unidades.\n" +
                    "Disponibles: " + disponible,
                    "Cantidad inválida", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double monto     = cant * precio;
            String motivo    = cbMotivo.getSelectedItem().toString();
            String reembolso = cbTipoReembolso.getSelectedItem().toString();

            int op = JOptionPane.showConfirmDialog(this,
                "¿Confirmar devolución?\n\n" +
                "Venta: #" + idVentaActual + "\n" +
                "Producto: " + nombre + "\n" +
                "Cantidad: " + cant + "\n" +
                "Motivo: " + motivo + "\n" +
                "Reembolso: " + reembolso + "\n" +
                "Monto: S/ " + String.format("%.2f", monto),
                "Confirmar Devolución", JOptionPane.YES_NO_OPTION);

            if (op != JOptionPane.YES_OPTION) return;

            if (dao.registrar(idVentaActual, idProducto, 1, cant, motivo, reembolso, monto)) {
                // Devolver stock
                ProductoDAO prodDAO = new ProductoDAO();
                Producto p = prodDAO.listar().stream()
                        .filter(pr -> pr.getIdProducto() == idProducto)
                        .findFirst().orElse(null);
                if (p != null) prodDAO.actualizarStock(idProducto, p.getCantidad() + cant);

                JOptionPane.showMessageDialog(this,
                    "Devolución registrada correctamente\n" +
                    "Monto a reembolsar: S/ " + String.format("%.2f", monto),
                    "Devolución Exitosa", JOptionPane.INFORMATION_MESSAGE);

                cargarTabla();
                actualizarInfoProducto();
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar la devolución");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad válida");
        }
    }

    private void cancelarVentaCompleta() {
        if (idVentaActual == -1) {
            JOptionPane.showMessageDialog(this, "Primero busque una venta válida"); return;
        }

        String motivo    = cbMotivo.getSelectedItem().toString();
        String reembolso = cbTipoReembolso.getSelectedItem().toString();

        int op = JOptionPane.showConfirmDialog(this,
            "¿Cancelar la venta #" + idVentaActual + " COMPLETA?\n\n" +
            "Devolverá TODOS los productos al stock\n" +
            "y marcará la venta como Anulada.\n\n" +
            "Motivo: " + motivo + "\n" +
            "Reembolso: " + reembolso + "\n\n" +
            "Esta acción NO se puede deshacer.",
            "Cancelar Venta Completa",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (op != JOptionPane.YES_OPTION) return;

        DevolucionDAO dao = new DevolucionDAO();
        if (dao.cancelarVenta(idVentaActual, motivo, reembolso, 1)) {
            JOptionPane.showMessageDialog(this,
                "Venta #" + idVentaActual + " cancelada.\n" +
                "Stock revertido y venta Anulada.",
                "Venta Cancelada", JOptionPane.INFORMATION_MESSAGE);
            cargarTabla();
            limpiar();
        } else {
            JOptionPane.showMessageDialog(this, "Error al cancelar la venta");
        }
    }

    private void cargarTabla() {
        modelDevoluciones.setRowCount(0);
        DevolucionDAO dao = new DevolucionDAO();
        for (Object[] row : dao.listar())
            modelDevoluciones.addRow(row);
    }

    private void limpiar() {
        txtIdVenta.setText("");
        cbProductos.removeAllItems();
        txtCantidad.setText("1");
        lblPrecioUnit.setText("S/ 0.00");
        lblStockVendido.setText("0 unidades vendidas");
        lblYaDevuelto.setText("0 ya devueltas");
        lblMontoReembolso.setText("S/ 0.00");
        cbMotivo.setSelectedIndex(0);
        cbTipoReembolso.setSelectedIndex(0);
        productosVenta = null;
        idVentaActual  = -1;
    }
}