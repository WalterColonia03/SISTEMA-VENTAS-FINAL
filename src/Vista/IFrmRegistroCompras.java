package Vista;

import Clases.Categoria;
import Clases.Producto;
import Conexion.Conexion;
import DAO.CategoriaDAO;
import DAO.CompraDAO;
import DAO.CotizacionProveedorDAO;
import DAO.CuentasCobrarPagarDAO;
import DAO.KardexDAO;
import DAO.ProductoDAO;
import DAO.ProveedorDAO;
import DAO.ProveedorDAO.Proveedor;
import Servicio.CompraService;
import Servicio.CompraService.ItemCompra;
import Vista.Estilos.UIKit;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class IFrmRegistroCompras extends JInternalFrame {

    // ── PESTAÑA 1 ──
    private JComboBox<String> cbProveedor;
    private JComboBox<String> cbCondicionPago;
    private JTextField txtDocumento;
    private JTextField txtFecha;
    private JTextField txtCodProducto;
    private JTextField txtProductoNombre;
    private JTextField txtCantidad;
    private JTextField txtPrecioUnitario;
    private JTextField txtLote;
    private JTextField txtVencimiento;
    private JTable tblDetalle;
    private DefaultTableModel modelDetalle;
    private JLabel lblSubtotal;
    private JLabel lblIgv;
    private JLabel lblTotal;
    private JButton btnAgregarProducto;
    private JButton btnNuevoProducto;
    private JButton btnQuitarProducto;
    private JButton btnRegistrarCompra;
    private JButton btnLimpiar;

    // ── PESTAÑA 2 ──
    private JTable tblHistorial;
    private DefaultTableModel modelHistorial;
    private JButton btnVerComprobante;
    private JButton btnRefrescarHistorial;
    private JTextField txtBuscarHistorial;
    private JButton btnBuscarHistorial;

    // ── PESTAÑA 3: Comparativo de Precios (GAP 4) ──
    private JTable tblComparativo;
    private DefaultTableModel modelComparativo;
    private JButton btnComparar;
    private JButton btnRefrescarComparativo;
    private JLabel lblProductoComparativo;

    private List<Proveedor> listaProveedores;

    public IFrmRegistroCompras() {
        super("Registro de Compras", true, true, true, true);
        initComponents();
        buildLayout();
        attachEvents();
        setSize(1100, 650);
    }

    private void initComponents() {
        cbProveedor = new JComboBox<>();
        cbProveedor.setFont(UIKit.BODY);
        cargarProveedores();

        cbCondicionPago = new JComboBox<>(new String[]{
            "Contado", "Crédito 30 días", "Crédito 60 días", "Crédito 90 días"});
        cbCondicionPago.setFont(UIKit.BODY);
        cbCondicionPago.setPreferredSize(new Dimension(0, 36));

        txtDocumento = UIKit.textField();
        txtFecha = UIKit.textField();
        txtFecha.setEditable(false);
        txtFecha.setText(LocalDate.now().toString());
        txtCodProducto = UIKit.textField();
        txtProductoNombre = UIKit.textField();
        txtProductoNombre.setEditable(false);
        txtCantidad = UIKit.textField();
        txtCantidad.setText("1");
        txtCantidad.setHorizontalAlignment(JTextField.RIGHT);
        txtPrecioUnitario = UIKit.textField();
        txtPrecioUnitario.setHorizontalAlignment(JTextField.RIGHT);
        txtLote = UIKit.textField();
        txtVencimiento = UIKit.textField();

        String[] colsDetalle = {"ID", "Producto", "Cant", "P. Unit", "Subtotal", "Lote", "Vencimiento"};
        modelDetalle = new DefaultTableModel(colsDetalle, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblDetalle = UIKit.styledTable(modelDetalle);

        lblSubtotal = new JLabel("S/ 0.00"); lblSubtotal.setFont(UIKit.BODY_BOLD);
        lblIgv      = new JLabel("S/ 0.00"); lblIgv.setFont(UIKit.BODY_BOLD);
        lblTotal    = new JLabel("S/ 0.00"); lblTotal.setFont(UIKit.H1);
        lblTotal.setForeground(UIKit.ACCENT);

        btnAgregarProducto = UIKit.secondaryButton("Agregar");
        btnNuevoProducto   = UIKit.primaryButton("+ Nuevo Producto");
        btnQuitarProducto  = UIKit.dangerOutlineButton("Quitar");
        btnRegistrarCompra = UIKit.primaryButton("Registrar Compra");
        btnRegistrarCompra.setPreferredSize(new Dimension(0, 44));
        btnLimpiar         = UIKit.secondaryButton("Limpiar");

        String[] colsHist = {"#Compra", "Proveedor", "N° Factura", "Subtotal",
                              "IGV", "Total", "Condición", "Estado Pago", "Fecha"};
        modelHistorial = new DefaultTableModel(colsHist, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblHistorial          = UIKit.styledTable(modelHistorial);
        btnVerComprobante     = UIKit.primaryButton("Generar PDF");
        btnRefrescarHistorial = UIKit.secondaryButton("Refrescar");
        txtBuscarHistorial    = UIKit.textField();
        txtBuscarHistorial.setPreferredSize(new Dimension(200, 36));
        txtBuscarHistorial.putClientProperty("JTextField.placeholderText", "Buscar proveedor...");
        btnBuscarHistorial = UIKit.secondaryButton("Buscar");

        // ── Comparativo de precios (GAP 4) ──
        String[] colsComp = {"Proveedor", "Precio Unitario", "Última Cotización", "Indicador"};
        modelComparativo = new DefaultTableModel(colsComp, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblComparativo = UIKit.styledTable(modelComparativo);
        // Colorear columna Indicador (col 3)
        tblComparativo.getColumnModel().getColumn(3).setCellRenderer(
            new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int col) {
                    Component c = super.getTableCellRendererComponent(
                            table, value, isSelected, hasFocus, row, col);
                    if (!isSelected && value != null) {
                        if (value.toString().contains("MEJOR")) {
                            c.setForeground(UIKit.SUCCESS);
                            ((javax.swing.JLabel) c).setFont(UIKit.BODY_BOLD);
                        } else {
                            c.setForeground(UIKit.TEXT_SECONDARY);
                            ((javax.swing.JLabel) c).setFont(UIKit.BODY);
                        }
                    }
                    return c;
                }
            });
        btnComparar            = UIKit.primaryButton("Ver Comparativo del Producto");
        btnRefrescarComparativo = UIKit.secondaryButton("Mostrar Todos");
        lblProductoComparativo = new JLabel("Seleccione un producto y pulse 'Ver Comparativo'");
        lblProductoComparativo.setFont(UIKit.BODY); lblProductoComparativo.setForeground(UIKit.TEXT_SECONDARY);

        tblHistorial.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);
                if (!isSelected && value != null) {
                    if (value.toString().equals("Pagado")) {
                        c.setForeground(UIKit.SUCCESS);
                    } else {
                        c.setForeground(UIKit.DANGER);
                    }
                    ((JLabel) c).setFont(UIKit.BODY_BOLD);
                }
                return c;
            }
        });

        tblHistorial.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);
                if (!isSelected && value != null) {
                    c.setForeground(value.toString().equals("Contado") ? UIKit.SUCCESS : UIKit.WARNING);
                }
                return c;
            }
        });
    }

    private void cargarProveedores() {
        cbProveedor.removeAllItems();
        cbProveedor.addItem("-- Seleccione proveedor --");
        ProveedorDAO dao = new ProveedorDAO();
        listaProveedores = dao.listar();
        for (Proveedor p : listaProveedores)
            cbProveedor.addItem(p.razonSocial + " (" + p.ruc + ")");
    }

    private void buildLayout() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UIKit.BG_APP);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(
                UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));

        getContentPane().add(
                UIKit.screenHeader("Registro de Compras", "Compras  ›  Ingreso de Mercadería"),
                BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIKit.BODY);
        tabs.addTab("Nueva Compra", buildTabNuevaCompra());
        tabs.addTab("Historial de Compras", buildTabHistorial());
        tabs.addTab("Comparativo de Precios", buildTabComparativo());
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 1) cargarHistorial("");
            if (tabs.getSelectedIndex() == 2) cargarComparativoTodos();
        });

        getContentPane().add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildTabNuevaCompra() {
        JPanel tab = new JPanel(new BorderLayout(UIKit.SPACE_LG, 0));
        tab.setOpaque(false);
        tab.setBorder(new EmptyBorder(UIKit.SPACE_MD, 0, 0, 0));

        JPanel pnlIzquierda = new JPanel(new BorderLayout(0, UIKit.SPACE_MD));
        pnlIzquierda.setOpaque(false);

        // ── Cabecera: Proveedor, Documento, Fecha ──
        JPanel pnlCabecera = UIKit.card();
        pnlCabecera.setLayout(new GridBagLayout());
        GridBagConstraints gbcC = new GridBagConstraints();
        gbcC.fill = GridBagConstraints.HORIZONTAL; gbcC.weightx = 1.0;

        gbcC.gridx = 0; gbcC.gridy = 0; gbcC.gridwidth = 3;
        gbcC.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlCabecera.add(UIKit.sectionHeader("Datos del Comprobante", null), gbcC);

        gbcC.gridwidth = 1; gbcC.gridy = 1;
        gbcC.gridx = 0; gbcC.insets = new Insets(0, 0, UIKit.SPACE_XS, UIKit.SPACE_SM);
        pnlCabecera.add(UIKit.fieldLabel("Proveedor"), gbcC);
        gbcC.gridx = 1; pnlCabecera.add(UIKit.fieldLabel("N° Factura"), gbcC);
        gbcC.gridx = 2; gbcC.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlCabecera.add(UIKit.fieldLabel("Fecha"), gbcC);

        gbcC.gridy = 2; gbcC.gridx = 0;
        gbcC.insets = new Insets(0, 0, 0, UIKit.SPACE_SM);
        pnlCabecera.add(cbProveedor, gbcC);
        gbcC.gridx = 1; pnlCabecera.add(txtDocumento, gbcC);
        gbcC.gridx = 2; gbcC.insets = new Insets(0, 0, 0, 0);
        pnlCabecera.add(txtFecha, gbcC);
        pnlIzquierda.add(pnlCabecera, BorderLayout.NORTH);

        // ── Detalle de Mercadería ──
        JPanel pnlDetalle = UIKit.card();
        pnlDetalle.setLayout(new BorderLayout(0, UIKit.SPACE_SM));
        pnlDetalle.add(UIKit.sectionHeader("Detalle de Mercadería", btnQuitarProducto), BorderLayout.NORTH);

        JPanel pnlFormProd = new JPanel(new GridBagLayout());
        pnlFormProd.setOpaque(false);
        GridBagConstraints gbcP = new GridBagConstraints();
        gbcP.fill = GridBagConstraints.HORIZONTAL;

        // ── Fila 0: Labels ──
        gbcP.gridy = 0; gbcP.gridx = 0; gbcP.weightx = 0.20;
        gbcP.insets = new Insets(0, 0, UIKit.SPACE_XS, UIKit.SPACE_SM);
        pnlFormProd.add(UIKit.fieldLabel("Producto"), gbcP);
        gbcP.gridx = 1; gbcP.weightx = 0.35;
        pnlFormProd.add(UIKit.fieldLabel("Nombre"), gbcP);
        gbcP.gridx = 2; gbcP.weightx = 0.1;
        pnlFormProd.add(UIKit.fieldLabel("Cant."), gbcP);
        gbcP.gridx = 3; gbcP.weightx = 0.15;
        gbcP.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlFormProd.add(UIKit.fieldLabel("P. Unitario"), gbcP);

        // ── Fila 1: Campos ──
        gbcP.gridy = 1; gbcP.gridx = 0;
        gbcP.insets = new Insets(0, 0, UIKit.SPACE_SM, UIKit.SPACE_SM);
        JButton btnBuscarProdCompra = UIKit.secondaryButton("Buscar Producto");
        btnBuscarProdCompra.addActionListener(e -> {
            DlgBuscadorProducto dlg = new DlgBuscadorProducto(this);
            dlg.setVisible(true);
            if (dlg.isSeleccionado()) {
                txtCodProducto.setText(String.valueOf(dlg.getIdProducto()));
                txtCodProducto.postActionEvent();
            }
        });
        pnlFormProd.add(btnBuscarProdCompra, gbcP);
        gbcP.gridx = 1; pnlFormProd.add(txtProductoNombre, gbcP);
        gbcP.gridx = 2; pnlFormProd.add(txtCantidad, gbcP);
        gbcP.gridx = 3; gbcP.insets = new Insets(0, 0, UIKit.SPACE_SM, 0);
        pnlFormProd.add(txtPrecioUnitario, gbcP);

        // ── Fila 2: Labels Lote y Vencimiento ──
        gbcP.gridy = 2; gbcP.gridx = 0;
        gbcP.insets = new Insets(0, 0, UIKit.SPACE_XS, UIKit.SPACE_SM);
        pnlFormProd.add(UIKit.fieldLabel("Lote *"), gbcP);
        gbcP.gridx = 1;
        pnlFormProd.add(UIKit.fieldLabel("Vencimiento * (AAAA-MM-DD)"), gbcP);

        // ── Fila 3: Campos Lote y Vencimiento ──
        gbcP.gridy = 3; gbcP.gridx = 0;
        gbcP.insets = new Insets(0, 0, UIKit.SPACE_SM, UIKit.SPACE_SM);
        pnlFormProd.add(txtLote, gbcP);
        gbcP.gridx = 1; pnlFormProd.add(txtVencimiento, gbcP);

        // ── Fila 4: Botones ──
        gbcP.gridy = 4; gbcP.gridx = 0; gbcP.gridwidth = 2;
        gbcP.insets = new Insets(0, 0, UIKit.SPACE_SM, UIKit.SPACE_SM);
        pnlFormProd.add(btnNuevoProducto, gbcP);
        gbcP.gridx = 2; gbcP.gridwidth = 2;
        gbcP.insets = new Insets(0, 0, UIKit.SPACE_SM, 0);
        pnlFormProd.add(btnAgregarProducto, gbcP);

        JPanel pnlBody = new JPanel(new BorderLayout(0, UIKit.SPACE_SM));
        pnlBody.setOpaque(false);
        pnlBody.add(pnlFormProd, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(tblDetalle);
        scroll.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlBody.add(scroll, BorderLayout.CENTER);

        pnlDetalle.add(pnlBody, BorderLayout.CENTER);
        pnlIzquierda.add(pnlDetalle, BorderLayout.CENTER);
        tab.add(pnlIzquierda, BorderLayout.CENTER);

        // ── Resumen derecha ──
        JPanel pnlResumen = UIKit.card();
        pnlResumen.setPreferredSize(new Dimension(260, 0));
        pnlResumen.setLayout(new GridBagLayout());
        GridBagConstraints gbcR = new GridBagConstraints();
        gbcR.fill = GridBagConstraints.HORIZONTAL; gbcR.weightx = 1.0; gbcR.gridx = 0;

        gbcR.gridy = 0; gbcR.insets = new Insets(0, 0, UIKit.SPACE_LG, 0);
        pnlResumen.add(UIKit.sectionHeader("Resumen", null), gbcR);
        gbcR.gridy = 1; gbcR.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlResumen.add(UIKit.fieldLabel("Subtotal"), gbcR);
        gbcR.gridy = 2; gbcR.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlResumen.add(lblSubtotal, gbcR);
        gbcR.gridy = 3; gbcR.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlResumen.add(UIKit.fieldLabel("IGV (18%)"), gbcR);
        gbcR.gridy = 4; gbcR.insets = new Insets(0, 0, UIKit.SPACE_LG, 0);
        pnlResumen.add(lblIgv, gbcR);
        gbcR.gridy = 5; gbcR.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        JLabel lblCondTitle = new JLabel("CONDICIÓN DE PAGO");
        lblCondTitle.setFont(UIKit.BODY_BOLD); lblCondTitle.setForeground(UIKit.TEXT_SECONDARY);
        pnlResumen.add(lblCondTitle, gbcR);
        gbcR.gridy = 6; gbcR.insets = new Insets(0, 0, UIKit.SPACE_LG, 0);
        pnlResumen.add(cbCondicionPago, gbcR);
        gbcR.gridy = 7; gbcR.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        JLabel lblTT = new JLabel("TOTAL A PAGAR");
        lblTT.setFont(UIKit.BODY_BOLD); lblTT.setForeground(UIKit.TEXT_SECONDARY);
        pnlResumen.add(lblTT, gbcR);
        gbcR.gridy = 8; gbcR.insets = new Insets(0, 0, UIKit.SPACE_LG, 0);
        pnlResumen.add(lblTotal, gbcR);
        gbcR.gridy = 9; gbcR.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlResumen.add(btnRegistrarCompra, gbcR);
        gbcR.gridy = 10; gbcR.weighty = 1.0; gbcR.anchor = GridBagConstraints.NORTH;
        gbcR.insets = new Insets(0, 0, 0, 0);
        pnlResumen.add(btnLimpiar, gbcR);

        tab.add(pnlResumen, BorderLayout.EAST);
        return tab;
    }

    private JPanel buildTabHistorial() {
        JPanel tab = new JPanel(new BorderLayout(0, UIKit.SPACE_MD));
        tab.setOpaque(false);
        tab.setBorder(new EmptyBorder(UIKit.SPACE_MD, 0, 0, 0));

        JPanel pnlCard = UIKit.card();
        pnlCard.setLayout(new BorderLayout(0, UIKit.SPACE_SM));
        pnlCard.add(UIKit.sectionHeader("Historial de Compras", null), BorderLayout.NORTH);

        JPanel pnlAcciones = new JPanel(new BorderLayout());
        pnlAcciones.setOpaque(false);

        JPanel pnlBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, UIKit.SPACE_SM, 0));
        pnlBusqueda.setOpaque(false);
        pnlBusqueda.add(txtBuscarHistorial);
        pnlBusqueda.add(btnBuscarHistorial);
        pnlBusqueda.add(btnRefrescarHistorial);

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIKit.SPACE_SM, 0));
        pnlBotones.setOpaque(false);
        pnlBotones.add(btnVerComprobante);

        pnlAcciones.add(pnlBusqueda, BorderLayout.WEST);
        pnlAcciones.add(pnlBotones, BorderLayout.EAST);
        pnlCard.add(pnlAcciones, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tblHistorial);
        scroll.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlCard.add(scroll, BorderLayout.CENTER);

        tab.add(pnlCard, BorderLayout.CENTER);
        return tab;
    }

    private void attachEvents() {
        txtCodProducto.addActionListener(e -> buscarProducto());
        btnNuevoProducto.addActionListener(e -> mostrarFormNuevoProducto());

        btnAgregarProducto.addActionListener(e -> {
            if (cbProveedor.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un proveedor antes de agregar productos");
                return;
            }
            if (txtProductoNombre.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Primero busque un producto");
                return;
            }
            String cantStr = txtCantidad.getText().trim();
            String precStr = txtPrecioUnitario.getText().trim();
            String lote    = txtLote.getText().trim();
            String venc    = txtVencimiento.getText().trim();

            if (cantStr.isEmpty() || precStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese cantidad y precio unitario"); return;
            }
            if (lote.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El lote es obligatorio"); return;
            }
            if (venc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "La fecha de vencimiento es obligatoria"); return;
            }
            try {
                int idProducto  = Integer.parseInt(txtCodProducto.getText().trim());
                int cantidad    = Integer.parseInt(cantStr);
                double precio   = Double.parseDouble(precStr.replace(",", "."));
                double subtotal = cantidad * precio;
                modelDetalle.addRow(new Object[]{
                    idProducto, txtProductoNombre.getText(), cantidad,
                    String.format("%.2f", precio), String.format("%.2f", subtotal), lote, venc
                });
                recalcularTotales();
                limpiarFormProducto();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Cantidad y precio deben ser números válidos");
            }
        });

        btnQuitarProducto.addActionListener(e -> {
            int row = tblDetalle.getSelectedRow();
            if (row != -1) { modelDetalle.removeRow(row); recalcularTotales(); }
            else JOptionPane.showMessageDialog(this, "Seleccione un producto");
        });

        btnRegistrarCompra.addActionListener(e -> registrarCompra());
        btnLimpiar.addActionListener(e -> limpiarTodo());
        btnRefrescarHistorial.addActionListener(e -> cargarHistorial(""));
        btnBuscarHistorial.addActionListener(e -> cargarHistorial(txtBuscarHistorial.getText().trim()));
        btnVerComprobante.addActionListener(e -> generarComprobantePDF());
        // GAP 4 — Comparativo de precios
        btnComparar.addActionListener(e -> compararPreciosProductoActual());
        btnRefrescarComparativo.addActionListener(e -> cargarComparativoTodos());
    }

    private void generarComprobantePDF() {
        int row = tblHistorial.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una compra del historial");
            return;
        }

        int idCompra = Integer.parseInt(
            modelHistorial.getValueAt(row, 0).toString().replace("#", ""));

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Guardar Comprobante PDF");
        fc.setSelectedFile(new java.io.File("Comprobante_Compra_" + idCompra + ".pdf"));
        fc.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        String rutaPDF = fc.getSelectedFile().getAbsolutePath();
        if (!rutaPDF.endsWith(".pdf")) rutaPDF += ".pdf";

        try {
            Document doc = new Document(PageSize.A4, 40, 40, 50, 80);
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(rutaPDF));

            final BaseColor AZUL_PIE = new BaseColor(25, 118, 210);
            final com.itextpdf.text.Font fPie = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 8,
                    com.itextpdf.text.Font.ITALIC, new BaseColor(120, 120, 120));
            final String fechaGen = LocalDate.now().toString();

            writer.setPageEvent(new com.itextpdf.text.pdf.PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter w, Document d) {
                    try {
                        PdfPTable tblPie = new PdfPTable(1);
                        tblPie.setTotalWidth(d.right() - d.left());
                        PdfPCell cell = new PdfPCell();
                        cell.setBorder(Rectangle.TOP);
                        cell.setBorderColor(AZUL_PIE);
                        cell.setBorderWidth(1.5f);
                        cell.setPadding(6);
                        cell.setBackgroundColor(BaseColor.WHITE);
                        Paragraph pieTxt = new Paragraph(
                            "Este documento es un comprobante interno del Minimarket LAREDO.\n" +
                            "Generado el: " + fechaGen +
                            "  |  Sistema ERP Minimarket LAREDO", fPie);
                        pieTxt.setAlignment(Element.ALIGN_CENTER);
                        cell.addElement(pieTxt);
                        tblPie.addCell(cell);
                        tblPie.writeSelectedRows(0, -1,
                            d.left(), d.bottom() + 60, w.getDirectContent());
                    } catch (Exception e) { e.printStackTrace(); }
                }
            });

            doc.open();

            BaseColor AZUL       = new BaseColor(25, 118, 210);
            BaseColor AZUL_CLARO = new BaseColor(227, 242, 253);
            BaseColor GRIS       = new BaseColor(245, 245, 245);
            BaseColor TEXTO      = new BaseColor(33, 33, 33);
            BaseColor VERDE      = new BaseColor(46, 125, 50);
            BaseColor ROJO       = new BaseColor(198, 40, 40);

            com.itextpdf.text.Font fTitulo    = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 22, com.itextpdf.text.Font.BOLD,   BaseColor.WHITE);
            com.itextpdf.text.Font fSubtitulo = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.NORMAL, BaseColor.WHITE);
            com.itextpdf.text.Font fComp      = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 14, com.itextpdf.text.Font.BOLD,   AZUL);
            com.itextpdf.text.Font fNumero    = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 11, com.itextpdf.text.Font.NORMAL, TEXTO);
            com.itextpdf.text.Font fLabel     = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,  9, com.itextpdf.text.Font.BOLD,   AZUL);
            com.itextpdf.text.Font fValor     = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,  9, com.itextpdf.text.Font.NORMAL, TEXTO);
            com.itextpdf.text.Font fHeader    = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,  9, com.itextpdf.text.Font.BOLD,   BaseColor.WHITE);
            com.itextpdf.text.Font fCelda     = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,  9, com.itextpdf.text.Font.NORMAL, TEXTO);
            com.itextpdf.text.Font fTotal     = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 11, com.itextpdf.text.Font.BOLD,   AZUL);

            // ── ENCABEZADO ──
            PdfPTable tblHeader = new PdfPTable(2);
            tblHeader.setWidthPercentage(100);
            tblHeader.setWidths(new float[]{60f, 40f});
            tblHeader.setSpacingAfter(16);

            PdfPCell cellLogo = new PdfPCell();
            cellLogo.setBackgroundColor(AZUL);
            cellLogo.setPadding(16);
            cellLogo.setBorder(Rectangle.NO_BORDER);
            Paragraph pTitulo = new Paragraph("Minimarket LAREDO", fTitulo);
            pTitulo.setSpacingAfter(4);
            Paragraph pSlogan = new Paragraph("Sistema de Gestión Empresarial", fSubtitulo);
            cellLogo.addElement(pTitulo);
            cellLogo.addElement(pSlogan);
            tblHeader.addCell(cellLogo);

            PdfPCell cellInfo = new PdfPCell();
            cellInfo.setBackgroundColor(AZUL_CLARO);
            cellInfo.setPadding(16);
            cellInfo.setBorder(Rectangle.NO_BORDER);
            Paragraph pComp = new Paragraph("COMPROBANTE DE COMPRA", fComp);
            pComp.setSpacingAfter(6);
            Paragraph pNum  = new Paragraph("N° " + modelHistorial.getValueAt(row, 0), fNumero);
            pNum.setSpacingAfter(4);
            String estadoPago = modelHistorial.getValueAt(row, 7).toString();
            com.itextpdf.text.Font fEstado = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.BOLD,
                    estadoPago.equals("Pagado") ? VERDE : ROJO);
            Paragraph pEstado = new Paragraph("Estado: " + estadoPago, fEstado);
            cellInfo.addElement(pComp);
            cellInfo.addElement(pNum);
            cellInfo.addElement(pEstado);
            tblHeader.addCell(cellInfo);
            doc.add(tblHeader);

            // ── DATOS ──
            PdfPTable tblDatos = new PdfPTable(4);
            tblDatos.setWidthPercentage(100);
            tblDatos.setWidths(new float[]{20f, 30f, 20f, 30f});
            tblDatos.setSpacingAfter(16);

            String[][] datos = {
                {"Proveedor",   modelHistorial.getValueAt(row, 1).toString(),
                 "N° Factura", modelHistorial.getValueAt(row, 2).toString()},
                {"Fecha",       modelHistorial.getValueAt(row, 8).toString(),
                 "Condición",   modelHistorial.getValueAt(row, 6).toString()},
            };

            for (String[] fila : datos) {
                for (int i = 0; i < 4; i++) {
                    PdfPCell cell = new PdfPCell();
                    cell.setPadding(8);
                    cell.setBorderColor(new BaseColor(200, 200, 200));
                    if (i % 2 == 0) {
                        cell.setBackgroundColor(GRIS);
                        cell.setPhrase(new Phrase(fila[i], fLabel));
                    } else {
                        cell.setBackgroundColor(BaseColor.WHITE);
                        cell.setPhrase(new Phrase(fila[i], fValor));
                    }
                    tblDatos.addCell(cell);
                }
            }
            doc.add(tblDatos);

            // ── DETALLE ──
            Paragraph pDetalle = new Paragraph("DETALLE DE PRODUCTOS", fLabel);
            pDetalle.setSpacingBefore(8);
            pDetalle.setSpacingAfter(6);
            doc.add(pDetalle);

            PdfPTable tblProd = new PdfPTable(6);
            tblProd.setWidthPercentage(100);
            tblProd.setWidths(new float[]{5f, 30f, 10f, 15f, 15f, 25f});
            tblProd.setSpacingAfter(16);

            String[] headers = {"#", "Producto", "Cant.", "P. Unit.", "Subtotal", "Lote / Vencimiento"};
            for (String h : headers) {
                PdfPCell hCell = new PdfPCell(new Phrase(h, fHeader));
                hCell.setBackgroundColor(AZUL);
                hCell.setPadding(8);
                hCell.setBorder(Rectangle.NO_BORDER);
                hCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tblProd.addCell(hCell);
            }

            String sqlDet =
                "SELECT p.nombre, dc.cantidad, dc.precioUnitario, dc.subtotal, " +
                "dc.lote, dc.fechaVencimiento " +
                "FROM DetalleCompra dc JOIN producto p ON dc.idProducto = p.idProducto " +
                "WHERE dc.idCompra = ? ORDER BY dc.idDetalleCompra";

            int numFila = 1;
            try (Connection con = Conexion.getConexion();
                 PreparedStatement ps = con.prepareStatement(sqlDet)) {
                ps.setInt(1, idCompra);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    BaseColor bgFila = numFila % 2 == 0 ? GRIS : BaseColor.WHITE;
                    addCeldaTabla(tblProd, String.valueOf(numFila), fCelda, bgFila, Element.ALIGN_CENTER);
                    addCeldaTabla(tblProd, rs.getString("nombre"), fCelda, bgFila, Element.ALIGN_LEFT);
                    addCeldaTabla(tblProd, String.valueOf(rs.getInt("cantidad")), fCelda, bgFila, Element.ALIGN_CENTER);
                    addCeldaTabla(tblProd, String.format("S/ %.2f", rs.getDouble("precioUnitario")),
                            fCelda, bgFila, Element.ALIGN_RIGHT);
                    addCeldaTabla(tblProd, String.format("S/ %.2f", rs.getDouble("subtotal")),
                            fCelda, bgFila, Element.ALIGN_RIGHT);
                    String loteVenc = (rs.getString("lote") != null ? rs.getString("lote") : "-") +
                            "\n" + (rs.getString("fechaVencimiento") != null ?
                            "Vence: " + rs.getString("fechaVencimiento") : "");
                    addCeldaTabla(tblProd, loteVenc, fCelda, bgFila, Element.ALIGN_LEFT);
                    numFila++;
                }
            }
            doc.add(tblProd);

            // ── TOTALES ──
            PdfPTable tblTotales = new PdfPTable(2);
            tblTotales.setWidthPercentage(40);
            tblTotales.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tblTotales.setWidths(new float[]{50f, 50f});
            tblTotales.setSpacingAfter(20);

            addFilaTotales(tblTotales, "Subtotal:", modelHistorial.getValueAt(row, 3).toString(), fLabel, fValor, GRIS);
            addFilaTotales(tblTotales, "IGV (18%):", modelHistorial.getValueAt(row, 4).toString(), fLabel, fValor, BaseColor.WHITE);
            addFilaTotales(tblTotales, "TOTAL:", modelHistorial.getValueAt(row, 5).toString(), fTotal, fTotal, AZUL_CLARO);
            doc.add(tblTotales);

            doc.close();

            int op = JOptionPane.showConfirmDialog(this,
                "PDF generado correctamente.\n¿Desea abrirlo ahora?",
                "PDF Generado", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                java.awt.Desktop.getDesktop().open(new java.io.File(rutaPDF));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al generar PDF: " + ex.getMessage());
        }
    }

    private void addCeldaTabla(PdfPTable tabla, String texto, com.itextpdf.text.Font fuente,
                                BaseColor bg, int alineacion) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, fuente));
        cell.setBackgroundColor(bg);
        cell.setPadding(7);
        cell.setBorderColor(new BaseColor(220, 220, 220));
        cell.setHorizontalAlignment(alineacion);
        tabla.addCell(cell);
    }

    private void addFilaTotales(PdfPTable tabla, String label, String valor,
                                 com.itextpdf.text.Font fLabel, com.itextpdf.text.Font fValor, BaseColor bg) {
        PdfPCell cLabel = new PdfPCell(new Phrase(label, fLabel));
        cLabel.setBackgroundColor(bg);
        cLabel.setPadding(7);
        cLabel.setBorderColor(new BaseColor(200, 200, 200));
        cLabel.setHorizontalAlignment(Element.ALIGN_LEFT);

        PdfPCell cValor = new PdfPCell(new Phrase(valor, fValor));
        cValor.setBackgroundColor(bg);
        cValor.setPadding(7);
        cValor.setBorderColor(new BaseColor(200, 200, 200));
        cValor.setHorizontalAlignment(Element.ALIGN_RIGHT);

        tabla.addCell(cLabel);
        tabla.addCell(cValor);
    }

    private void cargarHistorial(String filtro) {
        modelHistorial.setRowCount(0);
        String sql =
            "SELECT c.idCompra, p.razonSocial, c.nroDocumento, " +
            "c.subtotal, c.igv, c.total, " +
            "COALESCE(c.condicionPago, 'Contado') as condicionPago, " +
            "COALESCE(c.estadoPago, 'Pagado') as estadoPago, " +
            "c.fecha " +
            "FROM Compra c JOIN proveedor p ON c.idProveedor = p.idProveedor ";
        if (!filtro.isEmpty())
            sql += "WHERE p.razonSocial LIKE '%" + filtro + "%' ";
        sql += "ORDER BY c.fecha DESC";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modelHistorial.addRow(new Object[]{
                    "#" + rs.getInt("idCompra"),
                    rs.getString("razonSocial"),
                    rs.getString("nroDocumento"),
                    String.format("S/ %.2f", rs.getDouble("subtotal")),
                    String.format("S/ %.2f", rs.getDouble("igv")),
                    String.format("S/ %.2f", rs.getDouble("total")),
                    rs.getString("condicionPago"),
                    rs.getString("estadoPago"),
                    rs.getString("fecha")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void mostrarFormNuevoProducto() {
        CategoriaDAO catDAO = new CategoriaDAO();
        List<Categoria> categorias = catDAO.listar();
        if (categorias.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay categorías. Cree una primero.");
            return;
        }

        JDialog dlg = new JDialog();
        dlg.setTitle("Registrar Nuevo Producto");
        dlg.setModal(true);
        dlg.setSize(420, 380);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.gridx = 0;
        gbc.insets = new Insets(6, 16, 4, 16);

        JTextField txtNombre      = UIKit.textField();
        JTextField txtDescripcion = UIKit.textField();
        JTextField txtPrecioVenta = UIKit.textField();
        txtPrecioVenta.setHorizontalAlignment(JTextField.RIGHT);
        JComboBox<String> cbCat = new JComboBox<>();
        cbCat.setFont(UIKit.BODY);
        for (Categoria c : categorias) cbCat.addItem(c.getDescripcion());

        gbc.gridy = 0; dlg.add(UIKit.fieldLabel("Nombre del Producto *"), gbc);
        gbc.gridy = 1; dlg.add(txtNombre, gbc);
        gbc.gridy = 2; dlg.add(UIKit.fieldLabel("Descripción"), gbc);
        gbc.gridy = 3; dlg.add(txtDescripcion, gbc);
        gbc.gridy = 4; dlg.add(UIKit.fieldLabel("Categoría *"), gbc);
        gbc.gridy = 5; dlg.add(cbCat, gbc);
        gbc.gridy = 6; dlg.add(UIKit.fieldLabel("Precio de Venta al Público (S/) *"), gbc);
        gbc.gridy = 7; dlg.add(txtPrecioVenta, gbc);

        JLabel lblNota = new JLabel("* Stock inicial = 0, se sumará al registrar la compra");
        lblNota.setFont(UIKit.CAPTION);
        lblNota.setForeground(UIKit.TEXT_SECONDARY);
        gbc.gridy = 8; dlg.add(lblNota, gbc);

        JButton btnGuardar = UIKit.primaryButton("Registrar y Agregar a Compra");
        gbc.gridy = 9; gbc.insets = new Insets(12, 16, 8, 16);
        dlg.add(btnGuardar, gbc);

        btnGuardar.addActionListener(ev -> {
            String nombre      = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            String precioStr   = txtPrecioVenta.getText().trim().replace(",", ".");
            if (nombre.isEmpty() || precioStr.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Nombre y precio son obligatorios"); return;
            }
            try {
                double precioVenta = Double.parseDouble(precioStr);
                int idCategoria    = categorias.get(cbCat.getSelectedIndex()).getIdCategoria();
                Producto nuevo = new Producto(0, nombre, 0, precioVenta,
                        descripcion.isEmpty() ? nombre : descripcion, idCategoria, 1);
                ProductoDAO dao = new ProductoDAO();
                if (!dao.insertar(nuevo)) {
                    JOptionPane.showMessageDialog(dlg, "Error al registrar el producto"); return;
                }
                Producto registrado = dao.listar().stream()
                        .filter(p -> p.getNombre().equals(nombre) && p.getIdCategoria() == idCategoria)
                        .reduce((first, second) -> second).orElse(null);
                if (registrado == null) {
                    JOptionPane.showMessageDialog(dlg, "Producto registrado. Búscalo por ID.");
                    dlg.dispose(); return;
                }
                txtCodProducto.setText(String.valueOf(registrado.getIdProducto()));
                txtProductoNombre.setText(registrado.getNombre());
                txtPrecioUnitario.setText("");
                txtCantidad.setText("1");
                txtLote.setText("");
                txtVencimiento.setText("");
                JOptionPane.showMessageDialog(dlg,
                    "Producto registrado con ID #" + registrado.getIdProducto() + "\n" +
                    "Ingresa cantidad, precio de compra, lote y vencimiento.");
                dlg.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, "Ingrese un precio de venta válido");
            }
        });
        dlg.setVisible(true);
    }

    private void buscarProducto() {
        String codStr = txtCodProducto.getText().trim();
        if (codStr.isEmpty()) return;
        try {
            int id = Integer.parseInt(codStr);
            Producto p = new ProductoDAO().listar().stream()
                    .filter(prod -> prod.getIdProducto() == id).findFirst().orElse(null);
            if (p != null) {
                txtProductoNombre.setText(p.getNombre());

                // Cargar último precio de compra como referencia
                String sqlPrecio = "SELECT precioUnitario FROM DetalleCompra " +
                                   "WHERE idProducto = ? ORDER BY idDetalleCompra DESC LIMIT 1";
                try (Connection con = Conexion.getConexion();
                     PreparedStatement ps = con.prepareStatement(sqlPrecio)) {
                    ps.setInt(1, id);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        txtPrecioUnitario.setText(String.format("%.2f", rs.getDouble("precioUnitario")));
                    } else {
                        txtPrecioUnitario.setText(""); // Producto nuevo, sin compras previas
                    }
                } catch (SQLException ex) {
                    txtPrecioUnitario.setText("");
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Producto no encontrado. Si es nuevo usa '+ Nuevo Producto'");
                txtProductoNombre.setText("");
                txtPrecioUnitario.setText("");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un ID válido");
        }
    }

    private void registrarCompra() {
        if (cbProveedor.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un proveedor"); return;
        }
        if (txtDocumento.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el número de documento"); return;
        }
        if (modelDetalle.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Agregue al menos un producto"); return;
        }

        // Calcular totales para el diálogo de confirmación
        double subtotal = 0;
        for (int i = 0; i < modelDetalle.getRowCount(); i++)
            subtotal += Double.parseDouble(modelDetalle.getValueAt(i, 4).toString().replace(",", "."));
        double igv   = subtotal * 0.18;
        double total = subtotal + igv;
        String condicion = cbCondicionPago.getSelectedItem().toString();

        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Confirmar registro de compra?\n\n" +
            "Proveedor  : " + cbProveedor.getSelectedItem() + "\n" +
            "Documento  : " + txtDocumento.getText().trim() + "\n" +
            "Subtotal   : S/ " + String.format("%.2f", subtotal) + "\n" +
            "IGV (18%)  : S/ " + String.format("%.2f", igv) + "\n" +
            "Total      : S/ " + String.format("%.2f", total) + "\n" +
            "Condición  : " + condicion,
            "Confirmar Compra", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        // Construir lista de ítems desde el modelDetalle
        java.util.List<ItemCompra> items = new java.util.ArrayList<>();
        for (int i = 0; i < modelDetalle.getRowCount(); i++) {
            int    idProducto = Integer.parseInt(modelDetalle.getValueAt(i, 0).toString());
            String nombre     = modelDetalle.getValueAt(i, 1).toString();
            int    cantidad   = Integer.parseInt(modelDetalle.getValueAt(i, 2).toString());
            double precio     = Double.parseDouble(modelDetalle.getValueAt(i, 3).toString().replace(",", "."));
            String lote       = modelDetalle.getValueAt(i, 5).toString();
            String venc       = modelDetalle.getValueAt(i, 6).toString();
            items.add(new ItemCompra(idProducto, nombre, cantidad, precio, lote, venc));
        }

        // Obtener datos del proveedor seleccionado
        int    idProveedor = listaProveedores.get(cbProveedor.getSelectedIndex() - 1).idProveedor;
        String nombreProv  = listaProveedores.get(cbProveedor.getSelectedIndex() - 1).razonSocial;

        // Delegar al servicio (mismo patrón que CompraService.registrarCompra)
        CompraService service = new CompraService();
        try {
            int idCompra = service.registrarCompra(idProveedor,
                    txtDocumento.getText().trim(), condicion, nombreProv, items);

            // GAP 4 — Guardar cotizaciones de proveedor automáticamente al registrar compra
            CotizacionProveedorDAO cotDAO = new CotizacionProveedorDAO();
            for (ItemCompra item : items) {
                cotDAO.registrar(item.idProducto, idProveedor, item.precioUnitario);
            }

            if (!condicion.equals("Contado")) {
                int dias  = condicion.contains("30") ? 30 : condicion.contains("60") ? 60 : 90;
                String fV = java.time.LocalDate.now().plusDays(dias).toString();
                JOptionPane.showMessageDialog(this,
                    "Compra #" + idCompra + " registrada\n" +
                    "Total: S/ " + String.format("%.2f", total) + "\n\n" +
                    "Deuda en Cuentas por Pagar\nVence: " + fV + " (" + condicion + ")",
                    "Compra al Crédito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Compra #" + idCompra + " registrada\n" +
                    "Total: S/ " + String.format("%.2f", total) + "\nEstado: Pagado al Contado",
                    "Compra Exitosa", JOptionPane.INFORMATION_MESSAGE);
            }
            limpiarTodo();

        } catch (CompraService.CompraException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error en la Compra", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void recalcularTotales() {
        double subtotal = 0;
        for (int i = 0; i < modelDetalle.getRowCount(); i++)
            subtotal += Double.parseDouble(modelDetalle.getValueAt(i, 4).toString().replace(",", "."));
        double igv   = subtotal * 0.18;
        double total = subtotal + igv;
        lblSubtotal.setText(String.format("S/ %.2f", subtotal));
        lblIgv.setText(String.format("S/ %.2f", igv));
        lblTotal.setText(String.format("S/ %.2f", total));
    }

    private void limpiarFormProducto() {
        txtCodProducto.setText("");
        txtProductoNombre.setText("");
        txtCantidad.setText("1");
        txtPrecioUnitario.setText("");
        txtLote.setText("");
        txtVencimiento.setText("");
    }

    private void limpiarTodo() {
        cbProveedor.setSelectedIndex(0);
        cbCondicionPago.setSelectedIndex(0);
        txtDocumento.setText("");
        txtFecha.setText(LocalDate.now().toString());
        modelDetalle.setRowCount(0);
        recalcularTotales();
        limpiarFormProducto();
    }

    // ─── PESTAÑA 3: COMPARATIVO DE PRECIOS (GAP 4) ──────────────────────────

    private JPanel buildTabComparativo() {
        JPanel tab = new JPanel(new BorderLayout(0, UIKit.SPACE_MD));
        tab.setOpaque(false);
        tab.setBorder(new EmptyBorder(UIKit.SPACE_MD, 0, 0, 0));

        JPanel pnlCard = UIKit.card();
        pnlCard.setLayout(new BorderLayout(0, UIKit.SPACE_SM));

        // Header con instrucción
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        pnlTop.add(UIKit.sectionHeader("Comparativo de Precios por Proveedor", null), BorderLayout.NORTH);

        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, UIKit.SPACE_SM, UIKit.SPACE_SM));
        pnlAcciones.setOpaque(false);
        pnlAcciones.add(lblProductoComparativo);
        pnlAcciones.add(btnComparar);
        pnlAcciones.add(btnRefrescarComparativo);
        pnlTop.add(pnlAcciones, BorderLayout.CENTER);

        // Nota informativa
        JLabel lblNota = new JLabel(
            "  ★ El precio más bajo aparece en verde. Busca un producto en la pestaña 'Nueva Compra' y luego pulsa 'Ver Comparativo'.");
        lblNota.setFont(UIKit.CAPTION);
        lblNota.setForeground(UIKit.TEXT_SECONDARY);
        pnlTop.add(lblNota, BorderLayout.SOUTH);

        pnlCard.add(pnlTop, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tblComparativo);
        scroll.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlCard.add(scroll, BorderLayout.CENTER);

        tab.add(pnlCard, BorderLayout.CENTER);
        return tab;
    }

    /** Compara precios del producto actualmente buscado en la pestaña 1. */
    private void compararPreciosProductoActual() {
        String codStr = txtCodProducto.getText().trim();
        if (codStr.isEmpty() || txtProductoNombre.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Primero busque un producto en la pestaña 'Nueva Compra'.",
                "Producto no seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int idProducto = Integer.parseInt(codStr);
            String nombreProd = txtProductoNombre.getText();
            lblProductoComparativo.setText("Producto: " + nombreProd + " (ID #" + idProducto + ")");
            lblProductoComparativo.setForeground(UIKit.PRIMARY);

            modelComparativo.setRowCount(0);
            CotizacionProveedorDAO dao = new CotizacionProveedorDAO();
            java.util.List<Object[]> lista = dao.compararPorProducto(idProducto);

            if (lista.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No hay cotizaciones registradas para " + nombreProd + ".\n" +
                    "Las cotizaciones se guardan automáticamente al registrar compras.",
                    "Sin datos", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            double precioMin = (double) lista.get(0)[1]; // ya ordenado ASC
            for (int i = 0; i < lista.size(); i++) {
                Object[] row = lista.get(i);
                double precio = (double) row[1];
                String indicador = (precio == precioMin) ? "✔ MEJOR PRECIO" : "";
                modelComparativo.addRow(new Object[]{
                    row[0],                                    // proveedor
                    String.format("S/ %.2f", precio),          // precio
                    row[2],                                    // fecha
                    indicador
                });
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID de producto inválido.");
        }
    }

    /** Muestra todas las cotizaciones vigentes de todos los productos. */
    private void cargarComparativoTodos() {
        modelComparativo.setRowCount(0);
        lblProductoComparativo.setText("Mostrando todas las cotizaciones vigentes");
        lblProductoComparativo.setForeground(UIKit.TEXT_SECONDARY);
        CotizacionProveedorDAO dao = new CotizacionProveedorDAO();
        // listarTodas devuelve: producto, proveedor, precio (formateado), fecha
        for (Object[] row : dao.listarTodas()) {
            modelComparativo.addRow(new Object[]{ row[1], row[2], row[3], "" });
        }
    }
}