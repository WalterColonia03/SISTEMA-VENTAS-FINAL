package Vista;

import DAO.FidelizacionDAO;
import Vista.Estilos.UIKit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class IFrmFidelizacion extends JInternalFrame {

    private JTable tblClientesPuntos;
    private DefaultTableModel modelClientesPuntos;
    private JTextField txtBuscar;
    private JButton btnBuscar;

    private JTextField txtIdCliente;
    private JTextField txtNombreCliente;
    private JTextField txtPuntosDisponibles;
    private JTextField txtPuntosCanjeados;
    private JTextField txtSolesPorPunto;
    private JTextField txtPuntosACanjear;
    private JComboBox<String> cbPremios;

    private JButton btnCanjear;
    private JButton btnAgregarPuntos;
    private JButton btnLimpiar;
    private JLabel lblPuntosNecesarios;

    public IFrmFidelizacion() {
        super("Módulo de Fidelización", true, true, true, true);
        initComponents();
        buildLayout();
        attachEvents();
        setSize(1000, 620);
        cargarTabla();
    }

    private void initComponents() {
        txtBuscar = UIKit.textField();
        txtBuscar.setPreferredSize(new Dimension(200, 36));
        btnBuscar = UIKit.secondaryButton("Buscar");

        String[] columns = {"ID", "Nombre", "Apellido", "DNI/RUC", "Puntos", "Canjeados", "S/ por Punto"};
        modelClientesPuntos = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tblClientesPuntos = UIKit.styledTable(modelClientesPuntos);

        txtIdCliente       = UIKit.readOnlyField(); txtIdCliente.setEditable(false);
        txtNombreCliente   = UIKit.readOnlyField(); txtNombreCliente.setEditable(false);
        txtPuntosDisponibles = UIKit.readOnlyField(); txtPuntosDisponibles.setEditable(false);
        txtPuntosCanjeados = UIKit.readOnlyField(); txtPuntosCanjeados.setEditable(false);

        txtSolesPorPunto = UIKit.textField();
        txtSolesPorPunto.setText("10");
        txtSolesPorPunto.setHorizontalAlignment(JTextField.RIGHT);

        txtPuntosACanjear = UIKit.textField();
        txtPuntosACanjear.setHorizontalAlignment(JTextField.RIGHT);
        txtPuntosACanjear.setText("0");

        cbPremios = new JComboBox<>(new String[]{
            "Vale de Descuento S/ 10 (100 Ptos)",
            "Vale de Descuento S/ 25 (200 Ptos)",
            "Bolsa Ecológica Laredo (50 Ptos)",
            "Taza de Regalo (80 Ptos)"
        });
        cbPremios.setFont(UIKit.BODY);

        lblPuntosNecesarios = new JLabel("Puntos necesarios: 100");
        lblPuntosNecesarios.setFont(UIKit.CAPTION);
        lblPuntosNecesarios.setForeground(UIKit.TEXT_SECONDARY);

        btnCanjear       = UIKit.primaryButton("Canjear Premio");
        btnAgregarPuntos = UIKit.secondaryButton("Agregar Puntos Manualmente");
        btnLimpiar       = UIKit.secondaryButton("Limpiar");
    }

    private void buildLayout() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UIKit.BG_APP);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(
                UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));

        getContentPane().add(
                UIKit.screenHeader("Fidelización de Clientes", "Ventas  ›  Fidelización"),
                BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(UIKit.SPACE_LG, 0));
        cuerpo.setOpaque(false);

        // Tabla
        JPanel pnlTabla = UIKit.card();
        pnlTabla.setLayout(new BorderLayout(0, UIKit.SPACE_SM));

        JPanel pnlBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, UIKit.SPACE_SM, 0));
        pnlBusqueda.setOpaque(false);
        pnlBusqueda.add(txtBuscar);
        pnlBusqueda.add(btnBuscar);

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        pnlHeader.add(UIKit.sectionHeader("Clientes y Puntos Acumulados", null), BorderLayout.NORTH);
        pnlHeader.add(pnlBusqueda, BorderLayout.CENTER);

        pnlTabla.add(pnlHeader, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tblClientesPuntos);
        scroll.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlTabla.add(scroll, BorderLayout.CENTER);
        cuerpo.add(pnlTabla, BorderLayout.CENTER);

        // Panel derecho
        JPanel pnlDerecho = new JPanel(new BorderLayout(0, UIKit.SPACE_LG));
        pnlDerecho.setPreferredSize(new Dimension(320, 0));
        pnlDerecho.setOpaque(false);

        // Info cliente
        JPanel pnlCliente = UIKit.card();
        pnlCliente.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        gbc.gridx = 0;

        gbc.gridy = 0; gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlCliente.add(UIKit.sectionHeader("Cliente Seleccionado", null), gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, UIKit.SPACE_SM);
        pnlCliente.add(UIKit.fieldLabel("ID"), gbc);
        gbc.gridx = 1; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlCliente.add(UIKit.fieldLabel("Nombre"), gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_SM, UIKit.SPACE_SM);
        pnlCliente.add(txtIdCliente, gbc);
        gbc.gridx = 1; gbc.insets = new Insets(0, 0, UIKit.SPACE_SM, 0);
        pnlCliente.add(txtNombreCliente, gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, UIKit.SPACE_SM);
        pnlCliente.add(UIKit.fieldLabel("Puntos Disponibles"), gbc);
        gbc.gridx = 1; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlCliente.add(UIKit.fieldLabel("Puntos Canjeados"), gbc);

        gbc.gridy = 4; gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 0, UIKit.SPACE_SM);
        pnlCliente.add(txtPuntosDisponibles, gbc);
        gbc.gridx = 1; gbc.insets = new Insets(0, 0, 0, 0);
        pnlCliente.add(txtPuntosCanjeados, gbc);

        pnlDerecho.add(pnlCliente, BorderLayout.NORTH);

        // Canje
        JPanel pnlCanje = UIKit.card();
        pnlCanje.setLayout(new GridBagLayout());
        GridBagConstraints gbcC = new GridBagConstraints();
        gbcC.fill = GridBagConstraints.HORIZONTAL;
        gbcC.weightx = 1.0;
        gbcC.gridx = 0;

        gbcC.gridy = 0; gbcC.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlCanje.add(UIKit.sectionHeader("Canjear Puntos", null), gbcC);

        gbcC.gridy = 1; gbcC.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlCanje.add(UIKit.fieldLabel("Premio"), gbcC);
        gbcC.gridy = 2; gbcC.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlCanje.add(cbPremios, gbcC);
        gbcC.gridy = 3; gbcC.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlCanje.add(lblPuntosNecesarios, gbcC);

        gbcC.gridy = 4; gbcC.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlCanje.add(UIKit.fieldLabel("S/ por Punto"), gbcC);
        gbcC.gridy = 5; gbcC.insets = new Insets(0, 0, UIKit.SPACE_LG, 0);
        pnlCanje.add(txtSolesPorPunto, gbcC);

        JPanel pnlBotones = new JPanel(new GridLayout(3, 1, 0, UIKit.SPACE_SM));
        pnlBotones.setOpaque(false);
        pnlBotones.add(btnCanjear);
        pnlBotones.add(btnAgregarPuntos);
        pnlBotones.add(btnLimpiar);

        gbcC.gridy = 6; gbcC.weighty = 1.0;
        gbcC.anchor = GridBagConstraints.NORTH;
        gbcC.insets = new Insets(0, 0, 0, 0);
        pnlCanje.add(pnlBotones, gbcC);

        pnlDerecho.add(pnlCanje, BorderLayout.CENTER);
        cuerpo.add(pnlDerecho, BorderLayout.EAST);
        getContentPane().add(cuerpo, BorderLayout.CENTER);
    }

    private void attachEvents() {

        // BUSCAR
        btnBuscar.addActionListener(e -> {
            String texto = txtBuscar.getText().trim().toLowerCase();
            modelClientesPuntos.setRowCount(0);
            FidelizacionDAO dao = new FidelizacionDAO();
            for (Object[] row : dao.listarClientes()) {
                String nombre = row[1].toString() + " " + row[2].toString();
                if (nombre.toLowerCase().contains(texto) ||
                        row[3].toString().contains(texto)) {
                    modelClientesPuntos.addRow(row);
                }
            }
        });

        // CAMBIAR PREMIO → actualizar puntos necesarios
        cbPremios.addActionListener(e -> {
            String[] ptos = {"100", "200", "50", "80"};
            int idx = cbPremios.getSelectedIndex();
            lblPuntosNecesarios.setText("Puntos necesarios: " + ptos[idx]);
            txtPuntosACanjear.setText(ptos[idx]);
        });

        // CANJEAR
        btnCanjear.addActionListener(e -> {
            if (txtIdCliente.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione un cliente");
                return;
            }
            int idCliente     = Integer.parseInt(txtIdCliente.getText());
            int puntosDisp    = Integer.parseInt(txtPuntosDisponibles.getText().isEmpty() ? "0" : txtPuntosDisponibles.getText());
            int puntosNecesarios = Integer.parseInt(txtPuntosACanjear.getText());
            String premio     = cbPremios.getSelectedItem().toString();

            if (puntosDisp < puntosNecesarios) {
                JOptionPane.showMessageDialog(this,
                    "Puntos insuficientes.\nDisponibles: " + puntosDisp +
                    "\nNecesarios: " + puntosNecesarios);
                return;
            }

            int op = JOptionPane.showConfirmDialog(this,
                "¿Canjear " + puntosNecesarios + " puntos por:\n" + premio + "?",
                "Confirmar Canje", JOptionPane.YES_NO_OPTION);
            if (op != JOptionPane.YES_OPTION) return;

            FidelizacionDAO dao = new FidelizacionDAO();
            if (dao.canjear(idCliente, puntosNecesarios, premio, 1)) {
                JOptionPane.showMessageDialog(this, "✅ Canje realizado correctamente");
                cargarTabla();
                limpiar();
            } else {
                JOptionPane.showMessageDialog(this, "Error al procesar el canje");
            }
        });

        // AGREGAR PUNTOS MANUALMENTE
        btnAgregarPuntos.addActionListener(e -> {
            if (txtIdCliente.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione un cliente");
                return;
            }
            String puntosStr = JOptionPane.showInputDialog(this,
                "¿Cuántos puntos desea agregar?", "Agregar Puntos",
                JOptionPane.QUESTION_MESSAGE);
            if (puntosStr == null || puntosStr.trim().isEmpty()) return;
            try {
                int puntos = Integer.parseInt(puntosStr.trim());
                double solesPorPunto = Double.parseDouble(
                    txtSolesPorPunto.getText().replace(",", "."));
                FidelizacionDAO dao = new FidelizacionDAO();
                if (dao.agregarPuntos(Integer.parseInt(txtIdCliente.getText()),
                        puntos, solesPorPunto)) {
                    JOptionPane.showMessageDialog(this,
                        "✅ " + puntos + " puntos agregados correctamente");
                    cargarTabla();
                    limpiar();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un número válido");
            }
        });

        // LIMPIAR
        btnLimpiar.addActionListener(e -> limpiar());

        // SELECCIONAR FILA
        tblClientesPuntos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblClientesPuntos.getSelectedRow() != -1) {
                int row = tblClientesPuntos.getSelectedRow();
                txtIdCliente.setText(modelClientesPuntos.getValueAt(row, 0).toString());
                txtNombreCliente.setText(
                    modelClientesPuntos.getValueAt(row, 1) + " " +
                    modelClientesPuntos.getValueAt(row, 2));
                txtPuntosDisponibles.setText(modelClientesPuntos.getValueAt(row, 4).toString());
                txtPuntosCanjeados.setText(modelClientesPuntos.getValueAt(row, 5).toString());
                txtSolesPorPunto.setText(modelClientesPuntos.getValueAt(row, 6).toString());
            }
        });
    }

    private void cargarTabla() {
        modelClientesPuntos.setRowCount(0);
        FidelizacionDAO dao = new FidelizacionDAO();
        for (Object[] row : dao.listarClientes()) {
            modelClientesPuntos.addRow(row);
        }
    }

    private void limpiar() {
        txtIdCliente.setText("");
        txtNombreCliente.setText("");
        txtPuntosDisponibles.setText("");
        txtPuntosCanjeados.setText("");
        cbPremios.setSelectedIndex(0);
        txtPuntosACanjear.setText("100");
        tblClientesPuntos.clearSelection();
    }
}