package Vista;

import DAO.CuentasCobrarPagarDAO;
import Vista.Estilos.UIKit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class IFrmCuentasCobrarPagar extends JInternalFrame {

    private JTable tblPagar;
    private DefaultTableModel modelPagar;
    private JTextField txtBuscar;
    private JButton btnBuscar;
    private JButton btnMarcarPagado;
    private JButton btnRefrescar;
    private JLabel lblTotalPagar;
    private int idCuentaSeleccionada = -1;

    public IFrmCuentasCobrarPagar() {
        super("Cuentas por Pagar", true, true, true, true);
        initComponents();
        buildLayout();
        attachEvents();
        setSize(1000, 620);
        cargarDatos();
    }

    private void initComponents() {
        lblTotalPagar = new JLabel("S/ 0.00");
        lblTotalPagar.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotalPagar.setForeground(UIKit.DANGER);

        txtBuscar = UIKit.searchField("Buscar proveedor...", null);
        btnRefrescar    = UIKit.secondaryButton("Refrescar");
        btnMarcarPagado = UIKit.primaryButton("Marcar como Pagado");

        String[] cols = {"ID", "Proveedor", "N° Doc", "Monto Total",
                         "Saldo Pendiente", "Emisión", "Vencimiento",
                         "Condición", "Estado"};
        modelPagar = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblPagar = UIKit.styledTable(modelPagar);

        // Colorear estado
        tblPagar.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);
                if (!isSelected && value != null) {
                    if (value.toString().equals("Pagado")) {
                        c.setForeground(UIKit.SUCCESS);
                        ((JLabel)c).setFont(UIKit.BODY_BOLD);
                    } else {
                        c.setForeground(UIKit.DANGER);
                        ((JLabel)c).setFont(UIKit.BODY_BOLD);
                    }
                }
                return c;
            }
        });

        // Colorear condición
        tblPagar.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);
                if (!isSelected && value != null) {
                    if (value.toString().equals("Contado")) {
                        c.setForeground(UIKit.SUCCESS);
                    } else {
                        c.setForeground(UIKit.WARNING);
                    }
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
                UIKit.screenHeader("Cuentas por Pagar", "Finanzas  ›  Cuentas por Pagar"),
                BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(0, UIKit.SPACE_MD));
        cuerpo.setOpaque(false);

        // KPI
        JPanel pnlKpi = new JPanel(new GridLayout(1, 3, UIKit.SPACE_MD, 0));
        pnlKpi.setOpaque(false);
        pnlKpi.setPreferredSize(new Dimension(0, 85));

        JPanel cardPagar = UIKit.card();
        cardPagar.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        JLabel lblTit = new JLabel("TOTAL DEUDAS PENDIENTES");
        lblTit.setFont(UIKit.CAPTION); lblTit.setForeground(UIKit.TEXT_SECONDARY);
        gbc.gridy = 0; gbc.insets = new Insets(10, 12, 2, 12); cardPagar.add(lblTit, gbc);
        gbc.gridy = 1; gbc.insets = new Insets(0, 12, 2, 12); cardPagar.add(lblTotalPagar, gbc);
        JLabel lblSub = new JLabel("Con proveedores");
        lblSub.setFont(UIKit.CAPTION); lblSub.setForeground(UIKit.TEXT_SECONDARY);
        gbc.gridy = 2; gbc.insets = new Insets(0, 12, 10, 12); cardPagar.add(lblSub, gbc);
        pnlKpi.add(cardPagar);

        // Panel vacío para ocupar espacio
        JPanel vacío1 = new JPanel(); vacío1.setOpaque(false); pnlKpi.add(vacío1);
        JPanel vacío2 = new JPanel(); vacío2.setOpaque(false); pnlKpi.add(vacío2);

        cuerpo.add(pnlKpi, BorderLayout.NORTH);

        // Tabla
        JPanel pnlTabla = UIKit.card();
        pnlTabla.setLayout(new BorderLayout(0, UIKit.SPACE_SM));
        pnlTabla.add(UIKit.sectionHeader("Deudas con Proveedores", null), BorderLayout.NORTH);

        JPanel pnlAcciones = new JPanel(new BorderLayout());
        pnlAcciones.setOpaque(false);

        JPanel pnlBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, UIKit.SPACE_SM, 0));
        pnlBusqueda.setOpaque(false);
        pnlBusqueda.add(txtBuscar);
        pnlBusqueda.add(btnRefrescar);

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIKit.SPACE_SM, 0));
        pnlBotones.setOpaque(false);
        pnlBotones.add(btnMarcarPagado);

        pnlAcciones.add(pnlBusqueda, BorderLayout.WEST);
        pnlAcciones.add(pnlBotones, BorderLayout.EAST);

        JPanel pnlInner = new JPanel(new BorderLayout(0, UIKit.SPACE_SM));
        pnlInner.setOpaque(false);
        pnlInner.add(pnlAcciones, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tblPagar);
        scroll.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlInner.add(scroll, BorderLayout.CENTER);

        pnlTabla.add(pnlInner, BorderLayout.CENTER);
        cuerpo.add(pnlTabla, BorderLayout.CENTER);
        getContentPane().add(cuerpo, BorderLayout.CENTER);
    }

    private void attachEvents() {
        // Live search por proveedor
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) {
                String texto = txtBuscar.getText().trim().toLowerCase();
                modelPagar.setRowCount(0);
                for (Object[] row : new CuentasCobrarPagarDAO().listarPagar()) {
                    if (row[1].toString().toLowerCase().contains(texto))
                        modelPagar.addRow(row);
                }
            }
        });

        btnRefrescar.addActionListener(e -> {
            txtBuscar.setText("");
            cargarDatos();
        });

        btnMarcarPagado.addActionListener(e -> {
            if (idCuentaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione una cuenta pendiente");
                return;
            }

            // Verificar que no esté ya pagada
            int row = tblPagar.getSelectedRow();
            String estado = modelPagar.getValueAt(row, 8).toString();
            if (estado.equals("Pagado")) {
                JOptionPane.showMessageDialog(this, "Esta cuenta ya fue pagada");
                return;
            }

            int op = JOptionPane.showConfirmDialog(this,
                "¿Confirmar pago de esta deuda?\n\n" +
                "Proveedor: " + modelPagar.getValueAt(row, 1) + "\n" +
                "Monto: " + modelPagar.getValueAt(row, 3) + "\n" +
                "Condición: " + modelPagar.getValueAt(row, 7),
                "Confirmar Pago", JOptionPane.YES_NO_OPTION);

            if (op != JOptionPane.YES_OPTION) return;

            CuentasCobrarPagarDAO dao = new CuentasCobrarPagarDAO();
            if (dao.marcarPagado(idCuentaSeleccionada)) {
                JOptionPane.showMessageDialog(this, "Deuda marcada como pagada correctamente");
                idCuentaSeleccionada = -1;
                cargarDatos();
            }
        });

        tblPagar.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblPagar.getSelectedRow() != -1) {
                idCuentaSeleccionada = Integer.parseInt(
                    modelPagar.getValueAt(tblPagar.getSelectedRow(), 0).toString());
            }
        });
    }

    private void cargarDatos() {
        modelPagar.setRowCount(0);
        CuentasCobrarPagarDAO dao = new CuentasCobrarPagarDAO();
        for (Object[] row : dao.listarPagar()) modelPagar.addRow(row);
        lblTotalPagar.setText(String.format("S/ %.2f", dao.getTotalPagar()));
        idCuentaSeleccionada = -1;
    }
}