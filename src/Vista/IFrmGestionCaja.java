package Vista;

import DAO.CajaChicaDAO;
import DAO.FlujoCajaDAO;
import Vista.Estilos.UIKit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class IFrmGestionCaja extends JInternalFrame {

    private JTable tblCajasHoy;
    private DefaultTableModel modelCajasHoy;
    private JTable tblHistorial;
    private DefaultTableModel modelHistorial;

    private JLabel lblTotalVentas, lblTotalEnCaja, lblTotalRetiros, lblDineroDisponible;

    private JButton btnRetiro, btnRefrescar;

    private CajaChicaDAO cajaDAO = new CajaChicaDAO();

    public IFrmGestionCaja() {
        super("Gestión de Caja", true, true, true, true);
        initComponents();
        buildLayout();
        attachEvents();
        setSize(1100, 650);
        cargarDatos();
    }

    private void initComponents() {
        String[] colsHoy = {"#", "Vendedor", "Apertura", "Ventas", "Egresos",
                            "Esperado", "Cierre Real", "Diferencia", "Retiros", "Estado"};
        modelCajasHoy = new DefaultTableModel(colsHoy, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCajasHoy = UIKit.styledTable(modelCajasHoy);

        // Color para estado
        tblCajasHoy.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);
                if (!isSelected && value != null) {
                    ((JLabel) c).setFont(UIKit.BODY_BOLD);
                    if (value.toString().equals("Cerrada")) {
                        c.setForeground(UIKit.SUCCESS);
                    } else {
                        c.setForeground(UIKit.WARNING);
                    }
                }
                return c;
            }
        });

        // Color para diferencia
        tblCajasHoy.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);
                if (!isSelected && value != null && !value.toString().equals("---")) {
                    ((JLabel) c).setFont(UIKit.BODY_BOLD);
                    double dif = Double.parseDouble(value.toString()
                            .replace("S/ ", "").replace(",", "."));
                    if (dif == 0) c.setForeground(UIKit.SUCCESS);
                    else if (dif > 0) c.setForeground(UIKit.ACCENT);
                    else c.setForeground(UIKit.DANGER);
                }
                return c;
            }
        });

        String[] colsHist = {"#", "Vendedor", "Apertura", "Ventas", "Egresos",
                             "Esperado", "Cierre Real", "Diferencia", "Retiros",
                             "Fecha Apertura", "Fecha Cierre", "Estado"};
        modelHistorial = new DefaultTableModel(colsHist, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblHistorial = UIKit.styledTable(modelHistorial);

        lblTotalVentas = new JLabel("S/ 0.00");
        lblTotalVentas.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotalVentas.setForeground(UIKit.SUCCESS);

        lblTotalEnCaja = new JLabel("S/ 0.00");
        lblTotalEnCaja.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotalEnCaja.setForeground(UIKit.ACCENT);

        lblTotalRetiros = new JLabel("S/ 0.00");
        lblTotalRetiros.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotalRetiros.setForeground(UIKit.DANGER);

        lblDineroDisponible = new JLabel("S/ 0.00");
        lblDineroDisponible.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblDineroDisponible.setForeground(UIKit.PRIMARY);

        btnRetiro = UIKit.primaryButton("Registrar Retiro");
        btnRefrescar = UIKit.secondaryButton("Refrescar");
    }

    private void buildLayout() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UIKit.BG_APP);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(
                UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));

        getContentPane().add(
                UIKit.screenHeader("Gestión de Caja", "Finanzas  ›  Gestión de Caja"),
                BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIKit.BODY);
        tabs.addTab("Cajas del Día", buildTabHoy());
        tabs.addTab("Historial", buildTabHistorial());
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 1) cargarHistorial();
        });

        getContentPane().add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildTabHoy() {
        JPanel tab = new JPanel(new BorderLayout(UIKit.SPACE_LG, 0));
        tab.setOpaque(false);
        tab.setBorder(new EmptyBorder(UIKit.SPACE_MD, 0, 0, 0));

        JPanel pnlIzquierda = new JPanel(new BorderLayout(0, UIKit.SPACE_MD));
        pnlIzquierda.setOpaque(false);

        // KPIs
        JPanel pnlKpis = new JPanel(new GridLayout(1, 4, UIKit.SPACE_MD, 0));
        pnlKpis.setOpaque(false);
        pnlKpis.setPreferredSize(new Dimension(0, 80));
        pnlKpis.add(buildKpi("VENTAS DEL DÍA", lblTotalVentas));
        pnlKpis.add(buildKpi("TOTAL EN CAJA", lblTotalEnCaja));
        pnlKpis.add(buildKpi("RETIROS", lblTotalRetiros));
        pnlKpis.add(buildKpi("DISPONIBLE", lblDineroDisponible));
        pnlIzquierda.add(pnlKpis, BorderLayout.NORTH);

        // Tabla
        JPanel pnlTabla = UIKit.card();
        pnlTabla.setLayout(new BorderLayout(0, UIKit.SPACE_SM));

        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, UIKit.SPACE_SM, 0));
        pnlAcciones.setOpaque(false);
        pnlAcciones.add(btnRetiro);
        pnlAcciones.add(btnRefrescar);

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        pnlTop.add(UIKit.sectionHeader("Cajas de Hoy", null), BorderLayout.NORTH);
        pnlTop.add(pnlAcciones, BorderLayout.SOUTH);
        pnlTabla.add(pnlTop, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tblCajasHoy);
        scroll.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlTabla.add(scroll, BorderLayout.CENTER);

        pnlIzquierda.add(pnlTabla, BorderLayout.CENTER);
        tab.add(pnlIzquierda, BorderLayout.CENTER);

        return tab;
    }

    private JPanel buildTabHistorial() {
        JPanel tab = new JPanel(new BorderLayout(0, UIKit.SPACE_MD));
        tab.setOpaque(false);
        tab.setBorder(new EmptyBorder(UIKit.SPACE_MD, 0, 0, 0));

        JPanel pnlCard = UIKit.card();
        pnlCard.setLayout(new BorderLayout(0, UIKit.SPACE_SM));
        pnlCard.add(UIKit.sectionHeader("Historial Completo de Cajas", null), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tblHistorial);
        scroll.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlCard.add(scroll, BorderLayout.CENTER);

        tab.add(pnlCard, BorderLayout.CENTER);
        return tab;
    }

    private JPanel buildKpi(String titulo, JLabel valor) {
        JPanel card = UIKit.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(UIKit.CAPTION);
        lblTit.setForeground(UIKit.TEXT_SECONDARY);
        gbc.gridy = 0;
        gbc.insets = new Insets(8, 12, 2, 12);
        card.add(lblTit, gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 12, 8, 12);
        card.add(valor, gbc);
        return card;
    }

    private void attachEvents() {
        btnRefrescar.addActionListener(e -> cargarDatos());

        btnRetiro.addActionListener(e -> {
            int fila = tblCajasHoy.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this,
                    "Seleccione una caja cerrada de la tabla");
                return;
            }

            String estado = modelCajasHoy.getValueAt(fila, 9).toString();
            if (estado.equals("Abierta")) {
                JOptionPane.showMessageDialog(this,
                    "No se puede retirar de una caja abierta.\nEspere a que el vendedor cierre su caja.",
                    "Caja Abierta", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int idCaja = Integer.parseInt(modelCajasHoy.getValueAt(fila, 0).toString());
            String vendedor = modelCajasHoy.getValueAt(fila, 1).toString();
            String cierre = modelCajasHoy.getValueAt(fila, 6).toString();
            String retirosActuales = modelCajasHoy.getValueAt(fila, 8).toString();

            JPanel panel = new JPanel(new GridLayout(0, 1, 0, 6));
            panel.add(new JLabel("Vendedor: " + vendedor));
            panel.add(new JLabel("Cierre Real: " + cierre));
            panel.add(new JLabel("Retiros previos: " + retirosActuales));
            panel.add(new JLabel("─────────────────"));
            panel.add(new JLabel("Monto a retirar (S/):"));
            JTextField txtRetiro = new JTextField();
            txtRetiro.setFont(new Font("Segoe UI", Font.BOLD, 18));
            txtRetiro.setHorizontalAlignment(JTextField.CENTER);
            panel.add(txtRetiro);
            panel.add(new JLabel("Este monto se registrará como retiro de caja"));

            int result = JOptionPane.showConfirmDialog(this, panel,
                "Registrar Retiro", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    double monto = Double.parseDouble(
                        txtRetiro.getText().trim().replace(",", "."));
                    if (monto <= 0) {
                        JOptionPane.showMessageDialog(this, "El monto debe ser mayor a 0");
                        return;
                    }

                    if (cajaDAO.registrarRetiro(idCaja, monto)) {
                        // Registrar en Flujo de Caja
                        new FlujoCajaDAO().registrar("EGRESO",
                            "Retiro de caja - " + vendedor,
                            monto, Clases.Sesion.getIdUsuario(),
                            "RETIRO CAJA #" + idCaja);

                        JOptionPane.showMessageDialog(this,
                            "Retiro de S/ " + String.format("%.2f", monto) + " registrado\n" +
                            "Caja: " + vendedor,
                            "Retiro Exitoso", JOptionPane.INFORMATION_MESSAGE);
                        cargarDatos();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Ingrese un monto válido");
                }
            }
        });
    }

    private void cargarDatos() {
        // Cajas del día
        modelCajasHoy.setRowCount(0);
        List<Object[]> cajasHoy = cajaDAO.listarCajasHoy();
        for (Object[] fila : cajasHoy) {
            modelCajasHoy.addRow(fila);
        }

        // KPIs
        Object[] resumen = cajaDAO.getResumenDia();
        double ingresos = (double) resumen[1];
        double egresos = (double) resumen[2];
        double cierre = (double) resumen[3];
        double retiros = (double) resumen[4];
        double disponible = cierre - retiros;

        lblTotalVentas.setText(String.format("S/ %.2f", ingresos));
        lblTotalEnCaja.setText(String.format("S/ %.2f", cierre));
        lblTotalRetiros.setText(String.format("S/ %.2f", retiros));
        lblDineroDisponible.setText(String.format("S/ %.2f", disponible));
    }

    private void cargarHistorial() {
        modelHistorial.setRowCount(0);
        List<Object[]> historial = cajaDAO.listarHistorial();
        for (Object[] fila : historial) {
            modelHistorial.addRow(fila);
        }
    }
}