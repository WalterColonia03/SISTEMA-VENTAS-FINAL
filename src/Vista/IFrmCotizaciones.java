package Vista;

import Clases.Sesion;
import DAO.ClienteDAO;
import DAO.CotizacionDAO;
import DAO.ProductoDAO;
import Servicio.VentaService;
import Vista.Estilos.UIKit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * IFrmCotizaciones — módulo CRM de cotizaciones/prospectos.
 * Cubre el requisito del profesor: "captación de prospectos (leads)
 * hasta la emisión de cotizaciones".
 *
 * Funcionalidades:
 *   - Crear cotización (selección de cliente + productos sin descontar stock)
 *   - Ver historial con estados (Pendiente, Aprobada, Rechazada, Convertida)
 *   - Convertir cotización aprobada directamente en venta (usando VentaService)
 */
public class IFrmCotizaciones extends JInternalFrame {

    // ── TAB NUEVA COTIZACIÓN ──
    private JTextField txtDni, txtCodProducto, txtCantidad;
    private JLabel lblNombreCliente;
    private JComboBox<String> cbVigencia;
    private JTextArea txtObservaciones;
    private DefaultTableModel modelDetalle;
    private JLabel lblSubtotal, lblIgv, lblTotal;
    private JButton btnBuscarCliente, btnAgregarProd, btnQuitarProd, btnGuardar, btnLimpiar;

    // ── TAB HISTORIAL ──
    private DefaultTableModel modelHistorial;
    private JButton btnAprobar, btnRechazar, btnConvertir, btnRefrescar;
    private JTextField txtBuscar;

    private int idClienteActivo = 1;
    private int idCotizacionSeleccionada = -1;  // GAP 7: fila activa del historial
    private final CotizacionDAO dao = new CotizacionDAO();

    public IFrmCotizaciones() {
        super("Cotizaciones / Prospectos", true, true, true, true);
        initComponents();
        buildLayout();
        attachEvents();
        setSize(1050, 620);
    }

    private void initComponents() {
        txtDni          = UIKit.textField(); txtDni.setPreferredSize(new Dimension(120, 36));
        txtDni.putClientProperty("JTextField.placeholderText", "DNI / ID cliente");
        lblNombreCliente = new JLabel("Consumidor Final"); lblNombreCliente.setFont(UIKit.BODY_BOLD);
        txtCodProducto  = UIKit.textField(); txtCodProducto.setPreferredSize(new Dimension(100, 36));
        txtCodProducto.putClientProperty("JTextField.placeholderText", "ID Producto");
        txtCantidad     = UIKit.textField(); txtCantidad.setText("1");
        txtCantidad.setPreferredSize(new Dimension(60, 36));

        String[] vigencias = {"7 días", "15 días", "30 días", "60 días", "90 días"};
        cbVigencia = new JComboBox<>(vigencias); cbVigencia.setFont(UIKit.BODY);

        txtObservaciones = new JTextArea(3, 20);
        txtObservaciones.setFont(UIKit.BODY);
        txtObservaciones.setLineWrap(true);

        String[] cols = {"ID", "Producto", "Cant.", "P. Unit.", "Subtotal"};
        modelDetalle = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r,int c){return false;} };

        lblSubtotal = new JLabel("S/ 0.00"); lblSubtotal.setFont(UIKit.BODY_BOLD);
        lblIgv      = new JLabel("S/ 0.00"); lblIgv.setFont(UIKit.BODY_BOLD);
        lblTotal    = new JLabel("S/ 0.00"); lblTotal.setFont(UIKit.H1); lblTotal.setForeground(UIKit.ACCENT);

        btnBuscarCliente = UIKit.secondaryButton("Buscar Cliente");
        btnAgregarProd   = UIKit.secondaryButton("Agregar");
        btnQuitarProd    = UIKit.dangerOutlineButton("Quitar");
        btnGuardar       = UIKit.primaryButton("Guardar Cotización");
        btnLimpiar       = UIKit.secondaryButton("Limpiar");

        String[] colsH = {"#", "Cliente", "Fecha", "Vigencia", "Total", "Estado"};
        modelHistorial = new DefaultTableModel(colsH, 0) { @Override public boolean isCellEditable(int r,int c){return false;} };

        btnAprobar   = UIKit.primaryButton("✓ Aprobar");
        btnRechazar  = UIKit.dangerOutlineButton("✗ Rechazar");
        btnConvertir = UIKit.secondaryButton("→ Convertir en Venta");
        btnRefrescar = UIKit.secondaryButton("Refrescar");
        txtBuscar    = UIKit.textField(); txtBuscar.setPreferredSize(new Dimension(200, 36));
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar cliente...");
    }

    private void buildLayout() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UIKit.BG_APP);
        ((JComponent)getContentPane()).setBorder(new EmptyBorder(UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));
        getContentPane().add(UIKit.screenHeader("Cotizaciones", "Ventas › Cotizaciones / Prospectos"), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane(); tabs.setFont(UIKit.BODY);
        tabs.addTab("Nueva Cotización", buildTabNueva());
        tabs.addTab("Historial de Cotizaciones", buildTabHistorial());
        tabs.addChangeListener(e -> { if (tabs.getSelectedIndex() == 1) cargarHistorial(""); });
        getContentPane().add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildTabNueva() {
        JPanel tab = new JPanel(new BorderLayout(UIKit.SPACE_LG, 0));
        tab.setOpaque(false); tab.setBorder(new EmptyBorder(UIKit.SPACE_MD, 0, 0, 0));

        // ─── Izquierda ───
        JPanel pnlIzq = new JPanel(new BorderLayout(0, UIKit.SPACE_MD)); pnlIzq.setOpaque(false);

        // Cliente
        JPanel pnlCliente = UIKit.card(); pnlCliente.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints(); g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1;
        g.gridx = 0; g.gridy = 0; g.gridwidth = 3; g.insets = new Insets(0,0,UIKit.SPACE_MD,0);
        pnlCliente.add(UIKit.sectionHeader("Datos del Cliente", null), g);
        g.gridwidth = 1; g.gridy = 1; g.insets = new Insets(0,0,UIKit.SPACE_XS,UIKit.SPACE_SM);
        pnlCliente.add(UIKit.fieldLabel("DNI / ID"), g);
        g.gridx = 1; pnlCliente.add(UIKit.fieldLabel("Nombre"), g);
        g.gridx = 2; g.insets = new Insets(0,0,UIKit.SPACE_XS,0); pnlCliente.add(UIKit.fieldLabel("Vigencia"), g);
        g.gridy = 2; g.gridx = 0; g.insets = new Insets(0,0,0,UIKit.SPACE_SM);
        JPanel pnlDni = new JPanel(new BorderLayout(4,0)); pnlDni.setOpaque(false);
        pnlDni.add(txtDni, BorderLayout.CENTER); pnlDni.add(btnBuscarCliente, BorderLayout.EAST);
        pnlCliente.add(pnlDni, g);
        g.gridx = 1; pnlCliente.add(lblNombreCliente, g);
        g.gridx = 2; g.insets = new Insets(0,0,0,0); pnlCliente.add(cbVigencia, g);
        pnlIzq.add(pnlCliente, BorderLayout.NORTH);

        // Detalle
        JPanel pnlDetalle = UIKit.card(); pnlDetalle.setLayout(new BorderLayout(0, UIKit.SPACE_SM));
        pnlDetalle.add(UIKit.sectionHeader("Detalle de Productos", btnQuitarProd), BorderLayout.NORTH);

        JPanel pnlFila = new JPanel(new FlowLayout(FlowLayout.LEFT, UIKit.SPACE_SM, 0)); pnlFila.setOpaque(false);
        pnlFila.add(new JLabel("ID Prod:")); pnlFila.add(txtCodProducto);
        pnlFila.add(new JLabel("Cant:")); pnlFila.add(txtCantidad);
        pnlFila.add(btnAgregarProd);

        JPanel pnlBody = new JPanel(new BorderLayout(0, UIKit.SPACE_SM)); pnlBody.setOpaque(false);
        pnlBody.add(pnlFila, BorderLayout.NORTH);
        JTable tbl = UIKit.styledTable(modelDetalle);
        tbl.getColumnModel().getColumn(0).setMaxWidth(50);
        pnlBody.add(new JScrollPane(tbl), BorderLayout.CENTER);
        pnlDetalle.add(pnlBody, BorderLayout.CENTER);
        pnlIzq.add(pnlDetalle, BorderLayout.CENTER);

        // Observaciones
        JPanel pnlObs = UIKit.card(); pnlObs.setLayout(new BorderLayout(0, UIKit.SPACE_XS));
        pnlObs.add(UIKit.sectionHeader("Observaciones", null), BorderLayout.NORTH);
        pnlObs.add(new JScrollPane(txtObservaciones), BorderLayout.CENTER);
        pnlIzq.add(pnlObs, BorderLayout.SOUTH);

        // ─── Resumen derecha ───
        JPanel pnlResumen = UIKit.card(); pnlResumen.setPreferredSize(new Dimension(240, 0));
        pnlResumen.setLayout(new GridBagLayout());
        GridBagConstraints gr = new GridBagConstraints(); gr.fill = GridBagConstraints.HORIZONTAL; gr.weightx = 1; gr.gridx = 0;
        gr.gridy = 0; gr.insets = new Insets(0,0,UIKit.SPACE_LG,0); pnlResumen.add(UIKit.sectionHeader("Resumen", null), gr);
        gr.gridy = 1; gr.insets = new Insets(0,0,UIKit.SPACE_XS,0); pnlResumen.add(UIKit.fieldLabel("Subtotal (sin IGV)"), gr);
        gr.gridy = 2; gr.insets = new Insets(0,0,UIKit.SPACE_MD,0); pnlResumen.add(lblSubtotal, gr);
        gr.gridy = 3; gr.insets = new Insets(0,0,UIKit.SPACE_XS,0); pnlResumen.add(UIKit.fieldLabel("IGV (18%)"), gr);
        gr.gridy = 4; gr.insets = new Insets(0,0,UIKit.SPACE_LG,0); pnlResumen.add(lblIgv, gr);
        gr.gridy = 5; gr.insets = new Insets(0,0,UIKit.SPACE_XS,0);
        JLabel lTotal = new JLabel("TOTAL COTIZACIÓN"); lTotal.setFont(UIKit.BODY_BOLD); lTotal.setForeground(UIKit.TEXT_SECONDARY); pnlResumen.add(lTotal, gr);
        gr.gridy = 6; gr.insets = new Insets(0,0,UIKit.SPACE_LG,0); pnlResumen.add(lblTotal, gr);
        gr.gridy = 7; gr.insets = new Insets(0,0,UIKit.SPACE_SM,0); pnlResumen.add(btnGuardar, gr);
        gr.gridy = 8; gr.weighty = 1; gr.anchor = GridBagConstraints.NORTH; gr.insets = new Insets(0,0,0,0); pnlResumen.add(btnLimpiar, gr);

        tab.add(pnlIzq, BorderLayout.CENTER);
        tab.add(pnlResumen, BorderLayout.EAST);
        return tab;
    }

    private JPanel buildTabHistorial() {
        JPanel tab = new JPanel(new BorderLayout(0, UIKit.SPACE_MD));
        tab.setOpaque(false); tab.setBorder(new EmptyBorder(UIKit.SPACE_MD, 0, 0, 0));

        JPanel pnlCard = UIKit.card(); pnlCard.setLayout(new BorderLayout(0, UIKit.SPACE_SM));

        JPanel pnlAcciones = new JPanel(new BorderLayout()); pnlAcciones.setOpaque(false);
        JPanel pnlBus = new JPanel(new FlowLayout(FlowLayout.LEFT, UIKit.SPACE_SM, 0)); pnlBus.setOpaque(false);
        pnlBus.add(txtBuscar); pnlBus.add(btnRefrescar);
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIKit.SPACE_SM, 0)); pnlBtns.setOpaque(false);
        pnlBtns.add(btnAprobar); pnlBtns.add(btnRechazar); pnlBtns.add(btnConvertir);
        pnlAcciones.add(UIKit.sectionHeader("Historial de Cotizaciones", null), BorderLayout.NORTH);
        pnlAcciones.add(pnlBus, BorderLayout.WEST); pnlAcciones.add(pnlBtns, BorderLayout.EAST);

        JTable tblH = UIKit.styledTable(modelHistorial);
        // Colorear estado
        tblH.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel && v != null) {
                    switch (v.toString()) {
                        case "Aprobada":   comp.setForeground(UIKit.SUCCESS); break;
                        case "Rechazada":  comp.setForeground(UIKit.DANGER);  break;
                        case "Convertida": comp.setForeground(UIKit.ACCENT);  break;
                        default:           comp.setForeground(UIKit.WARNING); break;
                    }
                    ((JLabel)comp).setFont(UIKit.BODY_BOLD);
                }
                return comp;
            }
        });
        conectarListenerHistorial(tblH); // GAP 7

        pnlCard.add(pnlAcciones, BorderLayout.NORTH);
        pnlCard.add(new JScrollPane(tblH), BorderLayout.CENTER);
        tab.add(pnlCard, BorderLayout.CENTER);
        return tab;
    }

    private void attachEvents() {
        btnBuscarCliente.addActionListener(e -> buscarCliente());
        txtDni.addActionListener(e -> buscarCliente());
        txtCodProducto.addActionListener(e -> agregarProducto());
        btnAgregarProd.addActionListener(e -> agregarProducto());
        btnQuitarProd.addActionListener(e -> quitarProducto());
        btnGuardar.addActionListener(e -> guardarCotizacion());
        btnLimpiar.addActionListener(e -> limpiar());
        btnRefrescar.addActionListener(e -> cargarHistorial(txtBuscar.getText().trim()));
        txtBuscar.addActionListener(e -> cargarHistorial(txtBuscar.getText().trim()));
        btnAprobar.addActionListener(e -> cambiarEstadoSeleccionado("Aprobada"));
        btnRechazar.addActionListener(e -> cambiarEstadoSeleccionado("Rechazada"));
        btnConvertir.addActionListener(e -> convertirEnVenta());
    }

    /** Conecta el listener de selección de la tabla de historial. */
    private void conectarListenerHistorial(JTable tblH) {
        tblH.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblH.getSelectedRow() != -1) {
                String idStr = modelHistorial.getValueAt(tblH.getSelectedRow(), 0).toString().replace("#", "");
                idCotizacionSeleccionada = Integer.parseInt(idStr);
            }
        });
    }

    private void buscarCliente() {
        String txt = txtDni.getText().trim();
        if (txt.isEmpty()) return;
        try {
            int id = Integer.parseInt(txt);
            ClienteDAO clienteDAO = new ClienteDAO();
            var lista = clienteDAO.listar();
            var cli = lista.stream().filter(c -> c.getIdCliente() == id).findFirst().orElse(null);
            if (cli != null) {
                idClienteActivo = cli.getIdCliente();
                lblNombreCliente.setText(cli.getNombre() + " " + cli.getApellido());
            } else {
                JOptionPane.showMessageDialog(this, "Cliente no encontrado. ID: " + id);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un ID numérico válido.");
        }
    }

    private void agregarProducto() {
        String codStr = txtCodProducto.getText().trim();
        String cantStr = txtCantidad.getText().trim();
        if (codStr.isEmpty()) return;
        try {
            int id = Integer.parseInt(codStr);
            int cant = Integer.parseInt(cantStr.isEmpty() ? "1" : cantStr);
            var prod = new ProductoDAO().listar().stream()
                    .filter(p -> p.getIdProducto() == id && p.getEstado() == 1).findFirst().orElse(null);
            if (prod == null) { JOptionPane.showMessageDialog(this, "Producto no encontrado o inactivo."); return; }
            double sub = cant * prod.getPrecio();
            // Verificar si ya existe en el detalle
            for (int r = 0; r < modelDetalle.getRowCount(); r++) {
                if (modelDetalle.getValueAt(r,0).toString().equals(String.valueOf(id))) {
                    int nuevaCant = Integer.parseInt(modelDetalle.getValueAt(r,2).toString()) + cant;
                    modelDetalle.setValueAt(nuevaCant, r, 2);
                    modelDetalle.setValueAt(String.format("%.2f", nuevaCant * prod.getPrecio()), r, 4);
                    recalcular(); return;
                }
            }
            modelDetalle.addRow(new Object[]{id, prod.getNombre(), cant, String.format("%.2f", prod.getPrecio()), String.format("%.2f", sub)});
            recalcular();
            txtCodProducto.setText(""); txtCantidad.setText("1"); txtCodProducto.requestFocus();
        } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "ID y cantidad deben ser números."); }
    }

    private void quitarProducto() {
        int row = 0; // buscar tabla activa
        JTable tbl = findTable();
        if (tbl != null && tbl.getSelectedRow() >= 0) { modelDetalle.removeRow(tbl.getSelectedRow()); recalcular(); }
        else JOptionPane.showMessageDialog(this, "Seleccione un producto de la lista.");
    }

    private JTable findTable() {
        for (Component c : getContentPane().getComponents()) if (c instanceof JTable) return (JTable)c;
        return null;
    }

    private void recalcular() {
        double total = 0;
        for (int r = 0; r < modelDetalle.getRowCount(); r++)
            total += Double.parseDouble(modelDetalle.getValueAt(r,4).toString().replace(",","."));
        double sub = total / 1.18; double igv = total - sub;
        lblSubtotal.setText(String.format("S/ %.2f", sub));
        lblIgv.setText(String.format("S/ %.2f", igv));
        lblTotal.setText(String.format("S/ %.2f", total));
    }

    private void guardarCotizacion() {
        if (modelDetalle.getRowCount() == 0) { JOptionPane.showMessageDialog(this, "Agregue al menos un producto."); return; }
        double total = 0;
        for (int r = 0; r < modelDetalle.getRowCount(); r++)
            total += Double.parseDouble(modelDetalle.getValueAt(r,4).toString().replace(",","."));
        double sub = total / 1.18; double igv = total - sub;
        String vigStr = cbVigencia.getSelectedItem().toString();
        int dias = Integer.parseInt(vigStr.split(" ")[0]);
        String vigencia = LocalDate.now().plusDays(dias).toString();
        int idUsuario = Sesion.getIdUsuario() > 0 ? Sesion.getIdUsuario() : 1;

        int idCot = dao.insertar(idClienteActivo, idUsuario, vigencia, sub, igv, total, txtObservaciones.getText().trim());
        if (idCot == -1) { JOptionPane.showMessageDialog(this, "Error al guardar la cotización."); return; }

        for (int r = 0; r < modelDetalle.getRowCount(); r++) {
            int idProd = Integer.parseInt(modelDetalle.getValueAt(r,0).toString());
            int cant   = Integer.parseInt(modelDetalle.getValueAt(r,2).toString());
            double pu  = Double.parseDouble(modelDetalle.getValueAt(r,3).toString().replace(",","."));
            double s   = Double.parseDouble(modelDetalle.getValueAt(r,4).toString().replace(",","."));
            dao.insertarDetalle(idCot, idProd, cant, pu, s);
        }
        JOptionPane.showMessageDialog(this,
            "Cotización #" + idCot + " guardada.\nTotal: S/ " + String.format("%.2f", total) + "\nVigente hasta: " + vigencia,
            "Cotización Guardada", JOptionPane.INFORMATION_MESSAGE);
        limpiar();
    }

    private void limpiar() {
        modelDetalle.setRowCount(0); lblNombreCliente.setText("Consumidor Final");
        txtDni.setText(""); txtCodProducto.setText(""); txtCantidad.setText("1");
        txtObservaciones.setText(""); idClienteActivo = 1; recalcular();
    }

    private void cargarHistorial(String filtro) {
        modelHistorial.setRowCount(0);
        for (Object[] row : dao.listar(filtro)) modelHistorial.addRow(row);
    }

    private void cambiarEstadoSeleccionado(String estado) {
        if (idCotizacionSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Seleccione una cotización del historial primero.",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String estadoActual = "";
        for (int r = 0; r < modelHistorial.getRowCount(); r++) {
            if (modelHistorial.getValueAt(r, 0).toString().equals("#" + idCotizacionSeleccionada)) {
                estadoActual = modelHistorial.getValueAt(r, 5).toString();
                break;
            }
        }
        if (estadoActual.equals("Convertida")) {
            JOptionPane.showMessageDialog(this,
                "Esta cotización ya fue convertida en venta y no puede modificarse.",
                "No permitido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Marcar cotización #" + idCotizacionSeleccionada + " como " + estado + "?",
            "Cambiar estado", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        if (dao.cambiarEstado(idCotizacionSeleccionada, estado)) {
            JOptionPane.showMessageDialog(this,
                "Cotización #" + idCotizacionSeleccionada + " marcada como " + estado + ".",
                "Estado actualizado", JOptionPane.INFORMATION_MESSAGE);
            idCotizacionSeleccionada = -1;
            cargarHistorial("");
        } else {
            JOptionPane.showMessageDialog(this, "Error al cambiar el estado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void convertirEnVenta() {
        if (idCotizacionSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Seleccione una cotización del historial primero.",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Verificar que esté Aprobada
        String estadoActual = "";
        for (int r = 0; r < modelHistorial.getRowCount(); r++) {
            if (modelHistorial.getValueAt(r, 0).toString().equals("#" + idCotizacionSeleccionada)) {
                estadoActual = modelHistorial.getValueAt(r, 5).toString();
                break;
            }
        }
        if (!estadoActual.equals("Aprobada")) {
            JOptionPane.showMessageDialog(this,
                "Solo se pueden convertir cotizaciones con estado 'Aprobada'.\n" +
                "Estado actual: " + estadoActual,
                "Estado inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Convertir cotización #" + idCotizacionSeleccionada + " en una venta real?\n" +
            "Se descontará stock y se generará el asiento contable.",
            "Convertir en Venta", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        // Obtener detalle con IDs de producto
        java.util.List<Object[]> detalle = dao.getDetalleConIds(idCotizacionSeleccionada);
        int idCliente = dao.getIdCliente(idCotizacionSeleccionada);

        if (detalle.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La cotización no tiene productos registrados.",
                "Sin detalle", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Construir items para VentaService
        java.util.List<VentaService.ItemVenta> items = new java.util.ArrayList<>();
        for (Object[] fila : detalle) {
            // fila: { idProducto, nombre, cantidad, precioUnitario, subtotal }
            int idProducto = (int) fila[0];
            int cantidad   = (int) fila[2];
            double precio  = (double) fila[3];
            items.add(new VentaService.ItemVenta(idProducto, cantidad, precio));
        }

        VentaService service = new VentaService();
        try {
            int idVenta = service.registrarVenta(idCliente, "Efectivo", items);
            // Marcar cotización como Convertida
            dao.cambiarEstado(idCotizacionSeleccionada, "Convertida");

            JOptionPane.showMessageDialog(this,
                "¡Venta #" + idVenta + " generada correctamente!\n" +
                "Cotización #" + idCotizacionSeleccionada + " → Convertida.",
                "Conversión Exitosa", JOptionPane.INFORMATION_MESSAGE);

            idCotizacionSeleccionada = -1;
            cargarHistorial("");

        } catch (VentaService.VentaException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al convertir en venta:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
