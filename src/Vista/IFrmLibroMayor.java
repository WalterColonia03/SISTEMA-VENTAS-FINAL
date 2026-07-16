package Vista;

import Conexion.Conexion;
import Vista.Estilos.UIKit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class IFrmLibroMayor extends JInternalFrame {

    private JComboBox<String> cbCuentaContable;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JButton btnFiltrar;
    private JButton btnRefrescar;
    private JButton btnNuevoAsiento;

    private JTable tblAsientos;
    private DefaultTableModel modelAsientos;

    private JLabel lblDebeTotal;
    private JLabel lblHaberTotal;
    private JLabel lblSaldoActual;

    public IFrmLibroMayor() {
        super("Libro Mayor", true, true, true, true);
        initComponents();
        buildLayout();
        attachEvents();
        setSize(1000, 620);
        cargarAsientos();
    }

    private void initComponents() {
        cbCuentaContable = new JComboBox<>(new String[]{
            "Todas las Cuentas",
            "101 Efectivo y Equivalentes",
            "201 Mercaderías",
            "121 Cuentas por Cobrar",
            "421 Cuentas por Pagar",
            "501 Capital",
            "701 Ventas",
            "601 Compras",
            "941 Gastos Administrativos",
            "4011 IGV por Pagar"
        });
        cbCuentaContable.setFont(UIKit.BODY);
        cbCuentaContable.setPreferredSize(new Dimension(220, 36));

        txtFechaInicio = UIKit.textField();
        txtFechaInicio.setText(LocalDate.now().withDayOfMonth(1).toString());
        txtFechaInicio.setPreferredSize(new Dimension(130, 36));

        txtFechaFin = UIKit.textField();
        txtFechaFin.setText(LocalDate.now().toString());
        txtFechaFin.setPreferredSize(new Dimension(130, 36));

        btnFiltrar = UIKit.secondaryButton("Filtrar");
        btnRefrescar = UIKit.secondaryButton("Refrescar");
        btnNuevoAsiento = UIKit.primaryButton("+ Nuevo Asiento");

        String[] columns = {"Fecha", "Glosa", "Cuenta Debe", "Cuenta Haber", "Debe (S/)", "Haber (S/)", "N° Asiento"};
        modelAsientos = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tblAsientos = UIKit.styledTable(modelAsientos);

        lblDebeTotal = new JLabel("S/ 0.00");
        lblDebeTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblDebeTotal.setForeground(UIKit.SUCCESS);

        lblHaberTotal = new JLabel("S/ 0.00");
        lblHaberTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHaberTotal.setForeground(UIKit.DANGER);

        lblSaldoActual = new JLabel("S/ 0.00");
        lblSaldoActual.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblSaldoActual.setForeground(UIKit.ACCENT);
    }

    private void buildLayout() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UIKit.BG_APP);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(
                UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));

        getContentPane().add(
                UIKit.screenHeader("Libro Mayor", "Finanzas  ›  Libro Mayor"),
                BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(UIKit.SPACE_LG, 0));
        cuerpo.setOpaque(false);

        // Tabla
        JPanel pnlTabla = UIKit.card();
        pnlTabla.setLayout(new BorderLayout(0, UIKit.SPACE_SM));

        JPanel pnlFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, UIKit.SPACE_SM, 0));
        pnlFiltros.setOpaque(false);
        pnlFiltros.add(UIKit.fieldLabel("Cuenta:"));
        pnlFiltros.add(cbCuentaContable);
        pnlFiltros.add(UIKit.fieldLabel("Desde:"));
        pnlFiltros.add(txtFechaInicio);
        pnlFiltros.add(UIKit.fieldLabel("Hasta:"));
        pnlFiltros.add(txtFechaFin);
        pnlFiltros.add(btnFiltrar);
        pnlFiltros.add(btnRefrescar);
        pnlFiltros.add(btnNuevoAsiento);

        JPanel pnlTopTabla = new JPanel(new BorderLayout(0, UIKit.SPACE_SM));
        pnlTopTabla.setOpaque(false);
        pnlTopTabla.add(UIKit.sectionHeader("Asientos Contables", null), BorderLayout.NORTH);
        pnlTopTabla.add(pnlFiltros, BorderLayout.CENTER);
        pnlTabla.add(pnlTopTabla, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tblAsientos);
        scroll.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlTabla.add(scroll, BorderLayout.CENTER);

        cuerpo.add(pnlTabla, BorderLayout.CENTER);

        // Resumen
        JPanel pnlResumen = UIKit.card();
        pnlResumen.setPreferredSize(new Dimension(240, 0));
        pnlResumen.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlResumen.add(UIKit.sectionHeader("Resumen de Saldos", null), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlResumen.add(UIKit.fieldLabel("Total Debe (Entradas)"), gbc);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_LG, 0);
        pnlResumen.add(lblDebeTotal, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlResumen.add(UIKit.fieldLabel("Total Haber (Salidas)"), gbc);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_LG, 0);
        pnlResumen.add(lblHaberTotal, gbc);

        JSeparator sep = new JSeparator();
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlResumen.add(sep, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        JLabel lblSaldoTitulo = new JLabel("SALDO ACTUAL");
        lblSaldoTitulo.setFont(UIKit.BODY_BOLD);
        lblSaldoTitulo.setForeground(UIKit.TEXT_SECONDARY);
        pnlResumen.add(lblSaldoTitulo, gbc);

        gbc.gridy = 7;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        pnlResumen.add(lblSaldoActual, gbc);

        cuerpo.add(pnlResumen, BorderLayout.EAST);
        getContentPane().add(cuerpo, BorderLayout.CENTER);
    }

    private void attachEvents() {
        btnFiltrar.addActionListener(e -> cargarAsientos());
        btnRefrescar.addActionListener(e -> {
            txtFechaInicio.setText(LocalDate.now().withDayOfMonth(1).toString());
            txtFechaFin.setText(LocalDate.now().toString());
            cbCuentaContable.setSelectedIndex(0);
            cargarAsientos();
        });
        btnNuevoAsiento.addActionListener(e -> mostrarFormAsiento());
    }

    private void mostrarFormAsiento() {
        JDialog dlg = new JDialog();
        dlg.setTitle("Nuevo Asiento Contable");
        dlg.setModal(true);
        dlg.setSize(440, 380);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.insets = new Insets(6, 16, 4, 16);

        JTextField txtGlosa = UIKit.textField();
        JTextField txtCuentaDebe = UIKit.textField();
        JTextField txtCuentaHaber = UIKit.textField();
        JTextField txtDebe = UIKit.textField();
        txtDebe.setHorizontalAlignment(JTextField.RIGHT);
        JTextField txtHaber = UIKit.textField();
        txtHaber.setHorizontalAlignment(JTextField.RIGHT);
        JTextField txtNroAsiento = UIKit.textField();

        gbc.gridy = 0;
        dlg.add(UIKit.fieldLabel("Glosa"), gbc);
        gbc.gridy = 1;
        dlg.add(txtGlosa, gbc);
        gbc.gridy = 2;
        dlg.add(UIKit.fieldLabel("Cuenta Debe"), gbc);
        gbc.gridy = 3;
        dlg.add(txtCuentaDebe, gbc);
        gbc.gridy = 4;
        dlg.add(UIKit.fieldLabel("Cuenta Haber"), gbc);
        gbc.gridy = 5;
        dlg.add(txtCuentaHaber, gbc);

        JPanel pnlMontos = new JPanel(new GridLayout(1, 2, 8, 0));
        pnlMontos.setOpaque(false);
        JPanel pnlD = new JPanel(new BorderLayout(0, 4));
        pnlD.setOpaque(false);
        pnlD.add(UIKit.fieldLabel("Debe (S/)"), BorderLayout.NORTH);
        pnlD.add(txtDebe, BorderLayout.CENTER);
        JPanel pnlH = new JPanel(new BorderLayout(0, 4));
        pnlH.setOpaque(false);
        pnlH.add(UIKit.fieldLabel("Haber (S/)"), BorderLayout.NORTH);
        pnlH.add(txtHaber, BorderLayout.CENTER);
        pnlMontos.add(pnlD);
        pnlMontos.add(pnlH);
        gbc.gridy = 6;
        dlg.add(pnlMontos, gbc);

        gbc.gridy = 7;
        dlg.add(UIKit.fieldLabel("N° Asiento"), gbc);
        gbc.gridy = 8;
        dlg.add(txtNroAsiento, gbc);

        JButton btnGuardar = UIKit.primaryButton("Guardar Asiento");
        gbc.gridy = 9;
        gbc.insets = new Insets(12, 16, 8, 16);
        dlg.add(btnGuardar, gbc);

        btnGuardar.addActionListener(ev -> {
            try {
                String glosa = txtGlosa.getText().trim();
                String cuentaDebe = txtCuentaDebe.getText().trim();
                String cuentaHaber = txtCuentaHaber.getText().trim();
                double debe = Double.parseDouble(txtDebe.getText().replace(",", "."));
                double haber = Double.parseDouble(txtHaber.getText().replace(",", "."));
                String nro = txtNroAsiento.getText().trim();

                if (glosa.isEmpty() || cuentaDebe.isEmpty() || nro.isEmpty()) {
                    JOptionPane.showMessageDialog(dlg, "Complete los campos obligatorios");
                    return;
                }

                try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO LibroMayor (fecha, glosa, cuentaDebe, cuentaHaber, "
                        + "debe, haber, nroAsiento, idUsuario) VALUES (CURDATE(),?,?,?,?,?,?,1)")) {
                    ps.setString(1, glosa);
                    ps.setString(2, cuentaDebe);
                    ps.setString(3, cuentaHaber);
                    ps.setDouble(4, debe);
                    ps.setDouble(5, haber);
                    ps.setString(6, nro);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(dlg, "✅ Asiento registrado correctamente");
                    dlg.dispose();
                    cargarAsientos();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, "Ingrese montos válidos");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dlg, "Error al guardar asiento");
            }
        });

        dlg.setVisible(true);
    }

    private void cargarAsientos() {
        modelAsientos.setRowCount(0);
        double totalDebe = 0, totalHaber = 0;

        String inicio = txtFechaInicio.getText().trim();
        String fin = txtFechaFin.getText().trim();
        String cuenta = cbCuentaContable.getSelectedIndex() == 0 ? null
                : cbCuentaContable.getSelectedItem().toString().split(" ")[0];

        StringBuilder sql = new StringBuilder(
                "SELECT fecha, glosa, cuentaDebe, cuentaHaber, debe, haber, nroAsiento "
                + "FROM LibroMayor WHERE DATE(fecha) BETWEEN ? AND ?");
        if (cuenta != null) {
            sql.append(" AND (cuentaDebe LIKE ? OR cuentaHaber LIKE ?)");
        }
        sql.append(" ORDER BY fecha DESC, nroAsiento");

        try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql.toString())) {
            ps.setString(1, inicio.isEmpty() ? "2000-01-01" : inicio);
            ps.setString(2, fin.isEmpty() ? LocalDate.now().toString() : fin);
            if (cuenta != null) {
                ps.setString(3, cuenta + "%");
                ps.setString(4, cuenta + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                double debe = rs.getDouble("debe");
                double haber = rs.getDouble("haber");
                totalDebe += debe;
                totalHaber += haber;
                modelAsientos.addRow(new Object[]{
                    rs.getString("fecha"),
                    rs.getString("glosa"),
                    rs.getString("cuentaDebe"),
                    rs.getString("cuentaHaber"),
                    String.format("S/ %.2f", debe),
                    String.format("S/ %.2f", haber),
                    rs.getString("nroAsiento")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        lblDebeTotal.setText(String.format("S/ %.2f", totalDebe));
        lblHaberTotal.setText(String.format("S/ %.2f", totalHaber));
        double saldo = totalDebe - totalHaber;
        lblSaldoActual.setText(String.format("S/ %.2f", saldo));
        lblSaldoActual.setForeground(saldo >= 0 ? UIKit.SUCCESS : UIKit.DANGER);
    }
}
