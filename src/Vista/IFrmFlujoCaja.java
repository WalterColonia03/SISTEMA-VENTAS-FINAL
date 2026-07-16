package Vista;

import DAO.FlujoCajaDAO;
import Vista.Estilos.UIKit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;


public class IFrmFlujoCaja extends JInternalFrame {

    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JButton btnFiltrar;

    private JLabel lblIngresos;
    private JLabel lblEgresos;
    private JLabel lblSaldoNeto;
    private JLabel lblTipoCambio;
    private JTextField txtNuevoTipoCambio;
    private JButton btnActualizarTC;

    private JTable tblMovimientos;
    private DefaultTableModel modelMovimientos;

    private JButton btnNuevoIngreso;
    private JButton btnNuevoEgreso;
    private JButton btnRefrescar;

    public IFrmFlujoCaja() {
        super("Flujo de Caja", true, true, true, true);
        initComponents();
        buildLayout();
        attachEvents();
        setSize(1000, 620);
        cargarDatos();
    }

    private void initComponents() {
        String hoy = LocalDate.now().toString();
        String inicio = LocalDate.now().withDayOfMonth(1).toString();

        txtFechaInicio = UIKit.textField();
        txtFechaInicio.setText(inicio);
        txtFechaInicio.setPreferredSize(new Dimension(130, 36));

        txtFechaFin = UIKit.textField();
        txtFechaFin.setText(hoy);
        txtFechaFin.setPreferredSize(new Dimension(130, 36));

        btnFiltrar = UIKit.secondaryButton("Filtrar");

        lblIngresos = new JLabel("S/ 0.00");
        lblIngresos.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblIngresos.setForeground(UIKit.SUCCESS);

        lblEgresos = new JLabel("S/ 0.00");
        lblEgresos.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblEgresos.setForeground(UIKit.DANGER);

        lblSaldoNeto = new JLabel("S/ 0.00");
        lblSaldoNeto.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblSaldoNeto.setForeground(UIKit.ACCENT);

        lblTipoCambio = new JLabel("3.72");
        lblTipoCambio.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTipoCambio.setForeground(UIKit.TEXT_PRIMARY);

        txtNuevoTipoCambio = UIKit.textField();
        txtNuevoTipoCambio.setPreferredSize(new Dimension(100, 36));
        txtNuevoTipoCambio.setHorizontalAlignment(JTextField.RIGHT);

        btnActualizarTC = UIKit.secondaryButton("Actualizar TC");

        String[] columns = {"Fecha", "Tipo", "Concepto", "Monto (S/)", "Referencia"};
        modelMovimientos = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tblMovimientos = UIKit.styledTable(modelMovimientos);

        // Colorear INGRESO/EGRESO
        tblMovimientos.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected && value != null) {
                    if (value.toString().equals("INGRESO")) {
                        c.setForeground(UIKit.SUCCESS);
                        ((JLabel) c).setFont(UIKit.BODY_BOLD);
                    } else {
                        c.setForeground(UIKit.DANGER);
                        ((JLabel) c).setFont(UIKit.BODY_BOLD);
                    }
                }
                return c;
            }
        });

        btnNuevoIngreso = UIKit.primaryButton("+ Nuevo Ingreso");
        btnNuevoEgreso = UIKit.dangerOutlineButton("- Nuevo Egreso");
        btnRefrescar = UIKit.secondaryButton("Refrescar");
    }

    private void buildLayout() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UIKit.BG_APP);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(
                UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));

        getContentPane().add(
                UIKit.screenHeader("Flujo de Caja", "Finanzas  ›  Flujo de Caja"),
                BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(0, UIKit.SPACE_MD));
        cuerpo.setOpaque(false);

        // ── KPIs + Tipo de Cambio ──
        JPanel pnlSuperior = new JPanel(new BorderLayout(UIKit.SPACE_LG, 0));
        pnlSuperior.setOpaque(false);
        pnlSuperior.setPreferredSize(new Dimension(0, 110));

        JPanel pnlCards = new JPanel(new GridLayout(1, 3, UIKit.SPACE_MD, 0));
        pnlCards.setOpaque(false);
        pnlCards.add(buildKpiCard("TOTAL INGRESOS", lblIngresos, "del período", UIKit.SUCCESS));
        pnlCards.add(buildKpiCard("TOTAL EGRESOS", lblEgresos, "del período", UIKit.DANGER));
        pnlCards.add(buildKpiCard("SALDO NETO", lblSaldoNeto, "Ingresos - Egresos", UIKit.PRIMARY));
        pnlSuperior.add(pnlCards, BorderLayout.CENTER);

        // Panel tipo de cambio
        JPanel pnlTC = UIKit.card();
        pnlTC.setPreferredSize(new Dimension(260, 0));
        pnlTC.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        gbc.gridx = 0;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_SM, 0);
        pnlTC.add(UIKit.sectionHeader("Tipo de Cambio USD", null), gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, UIKit.SPACE_SM);
        pnlTC.add(UIKit.fieldLabel("TC Actual"), gbc);
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlTC.add(UIKit.fieldLabel("Nuevo TC"), gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 0, UIKit.SPACE_SM);
        pnlTC.add(lblTipoCambio, gbc);
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        JPanel pnlTCInput = new JPanel(new BorderLayout(4, 0));
        pnlTCInput.setOpaque(false);
        pnlTCInput.add(txtNuevoTipoCambio, BorderLayout.CENTER);
        pnlTCInput.add(btnActualizarTC, BorderLayout.EAST);
        pnlTC.add(pnlTCInput, gbc);

        pnlSuperior.add(pnlTC, BorderLayout.EAST);
        cuerpo.add(pnlSuperior, BorderLayout.NORTH);

        // ── Tabla movimientos ──
        JPanel pnlCentral = UIKit.card();
        pnlCentral.setLayout(new BorderLayout(0, UIKit.SPACE_SM));

        JPanel pnlAcciones = new JPanel(new BorderLayout());
        pnlAcciones.setOpaque(false);

        JPanel pnlFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, UIKit.SPACE_SM, 0));
        pnlFiltros.setOpaque(false);
        pnlFiltros.add(UIKit.fieldLabel("Desde:"));
        pnlFiltros.add(txtFechaInicio);
        pnlFiltros.add(UIKit.fieldLabel("Hasta:"));
        pnlFiltros.add(txtFechaFin);
        pnlFiltros.add(btnFiltrar);
        pnlFiltros.add(btnRefrescar);

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIKit.SPACE_SM, 0));
        pnlBotones.setOpaque(false);
        pnlBotones.add(btnNuevoEgreso);
        pnlBotones.add(btnNuevoIngreso);

        pnlAcciones.add(pnlFiltros, BorderLayout.WEST);
        pnlAcciones.add(pnlBotones, BorderLayout.EAST);

        pnlCentral.add(UIKit.sectionHeader("Movimientos del Período", null), BorderLayout.NORTH);

        JPanel pnlInner = new JPanel(new BorderLayout(0, UIKit.SPACE_SM));
        pnlInner.setOpaque(false);
        pnlInner.add(pnlAcciones, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tblMovimientos);
        scroll.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlInner.add(scroll, BorderLayout.CENTER);

        pnlCentral.add(pnlInner, BorderLayout.CENTER);
        cuerpo.add(pnlCentral, BorderLayout.CENTER);

        getContentPane().add(cuerpo, BorderLayout.CENTER);
    }

    private JPanel buildKpiCard(String titulo, JLabel lblValor, String subtitulo, Color color) {
        JPanel card = UIKit.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel lblT = new JLabel(titulo);
        lblT.setFont(UIKit.CAPTION);
        lblT.setForeground(UIKit.TEXT_SECONDARY);
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 12, 2, 12);
        card.add(lblT, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 12, 2, 12);
        card.add(lblValor, gbc);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(UIKit.CAPTION);
        lblSub.setForeground(UIKit.TEXT_SECONDARY);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 12, 10, 12);
        card.add(lblSub, gbc);

        return card;
    }

    private void attachEvents() {

        // FILTRAR
        btnFiltrar.addActionListener(e -> cargarDatos());
        btnRefrescar.addActionListener(e -> cargarDatos());

        // ACTUALIZAR TIPO DE CAMBIO
        btnActualizarTC.addActionListener(e -> {
            String nuevoTC = txtNuevoTipoCambio.getText().trim();
            if (nuevoTC.isEmpty()) {
                return;
            }
            try {
                double valor = Double.parseDouble(nuevoTC.replace(",", "."));
                FlujoCajaDAO dao = new FlujoCajaDAO();
                if (dao.actualizarTipoCambio(valor, 1)) {
                    lblTipoCambio.setText(String.format("%.4f", valor));
                    txtNuevoTipoCambio.setText("");
                    JOptionPane.showMessageDialog(this, "Tipo de cambio actualizado correctamente");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un valor numérico válido");
            }
        });

        // NUEVO INGRESO
        btnNuevoIngreso.addActionListener(e -> mostrarFormMovimiento("INGRESO"));

        // NUEVO EGRESO
        btnNuevoEgreso.addActionListener(e -> mostrarFormMovimiento("EGRESO"));
    }

    private void mostrarFormMovimiento(String tipo) {
        JDialog dlg = new JDialog();
        dlg.setTitle((tipo.equals("INGRESO") ? "Nuevo Ingreso" : "Nuevo Egreso"));
        dlg.setModal(true);
        dlg.setSize(420, 320);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.insets = new Insets(8, 16, 4, 16);

        JTextField txtConcepto = UIKit.textField();
        JTextField txtMonto = UIKit.textField();
        txtMonto.setHorizontalAlignment(JTextField.RIGHT);
        JTextField txtRef = UIKit.textField();

        gbc.gridy = 0;
        dlg.add(UIKit.fieldLabel("Concepto"), gbc);
        gbc.gridy = 1;
        dlg.add(txtConcepto, gbc);
        gbc.gridy = 2;
        dlg.add(UIKit.fieldLabel("Monto (S/)"), gbc);
        gbc.gridy = 3;
        dlg.add(txtMonto, gbc);
        gbc.gridy = 4;
        dlg.add(UIKit.fieldLabel("Referencia (opcional)"), gbc);
        gbc.gridy = 5;
        dlg.add(txtRef, gbc);

        JButton btnGuardar = UIKit.primaryButton("Guardar");
        gbc.gridy = 6;
        gbc.insets = new Insets(12, 16, 8, 16);
        dlg.add(btnGuardar, gbc);

        btnGuardar.addActionListener(ev -> {
            String concepto = txtConcepto.getText().trim();
            String montoStr = txtMonto.getText().trim();
            if (concepto.isEmpty() || montoStr.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Concepto y monto son obligatorios");
                return;
            }
            try {
                double monto = Double.parseDouble(montoStr.replace(",", "."));
                FlujoCajaDAO dao = new FlujoCajaDAO();
                if (dao.registrar(tipo, concepto, monto, 1, txtRef.getText().trim())) {
                    JOptionPane.showMessageDialog(dlg, "✅ Movimiento registrado");
                    dlg.dispose();
                    cargarDatos();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, "Ingrese un monto válido");
            }
        });

        dlg.setVisible(true);
    }

    private void cargarDatos() {
        String inicio = txtFechaInicio.getText().trim();
        String fin = txtFechaFin.getText().trim();

        if (inicio.isEmpty()) {
            inicio = LocalDate.now().withDayOfMonth(1).toString();
        }
        if (fin.isEmpty()) {
            fin = LocalDate.now().toString();
        }

        // KPIs
        FlujoCajaDAO dao = new FlujoCajaDAO();
        double[] totales = dao.getTotales(inicio, fin);
        double ingresos = totales[0];
        double egresos = totales[1];
        double saldo = ingresos - egresos;

        lblIngresos.setText(String.format("S/ %.2f", ingresos));
        lblEgresos.setText(String.format("S/ %.2f", egresos));
        lblSaldoNeto.setText(String.format("S/ %.2f", saldo));
        lblSaldoNeto.setForeground(saldo >= 0 ? UIKit.SUCCESS : UIKit.DANGER);

        // Tipo de cambio
        lblTipoCambio.setText(String.format("%.4f", dao.getTipoCambio()));

        // Tabla
        modelMovimientos.setRowCount(0);
        for (Object[] row : dao.listar(inicio, fin)) {
            modelMovimientos.addRow(row);
        }
    }
}
