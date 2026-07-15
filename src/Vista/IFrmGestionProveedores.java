package Vista;

import DAO.ProveedorDAO;
import DAO.ProveedorDAO.Proveedor;
import API.ApiClient;
import Servicio.Validador;
import Vista.Estilos.UIKit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class IFrmGestionProveedores extends JInternalFrame {

    private JTable tblProveedores;
    private DefaultTableModel modelProveedores;
    private JTextField txtBuscarRuc;
    private JLabel lblEmptyState;

    private JTextField txtId;
    private JTextField txtRuc;
    private JTextField txtRazonSocial;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JTextField txtDireccion;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnConsultarRuc;

    public IFrmGestionProveedores() {
        super("Gestión de Proveedores", true, true, true, true);
        initComponents();
        buildLayout();
        attachEvents();
        setSize(960, 600);
        cargarTabla();
    }

    private void initComponents() {
        txtBuscarRuc = UIKit.searchField("Buscar proveedor por RUC o nombre...", null);

        String[] columns = {"ID", "RUC", "Razón Social", "Teléfono", "Correo", "Dirección"};
        modelProveedores = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tblProveedores = UIKit.styledTable(modelProveedores);

        lblEmptyState = new JLabel("No hay proveedores registrados", SwingConstants.CENTER);
        lblEmptyState.setFont(UIKit.BODY);
        lblEmptyState.setForeground(UIKit.TEXT_SECONDARY);
        lblEmptyState.setVisible(false);

        txtId = UIKit.readOnlyField();
        txtId.setEditable(false);
        txtId.setFocusable(false);
        txtRuc          = UIKit.textField();
        txtRazonSocial  = UIKit.textField();
        txtTelefono     = UIKit.textField();
        txtCorreo       = UIKit.textField();
        txtDireccion    = UIKit.textField();

        btnNuevo    = UIKit.primaryButton("+ Nuevo Proveedor");
        btnGuardar  = UIKit.primaryButton("Guardar / Actualizar");
        btnLimpiar  = UIKit.secondaryButton("Limpiar / Nuevo");
        btnEliminar = UIKit.dangerOutlineButton("Eliminar");
        btnConsultarRuc = UIKit.secondaryButton("SUNAT");
    }

    private void buildLayout() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UIKit.BG_APP);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(
                UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));

        // Sección 5: título izquierda + botón primario derecha
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        pnlTop.add(UIKit.screenHeader("Gestión de Proveedores", "Clientes y Proveedores  ›  Proveedores"), BorderLayout.WEST);
        pnlTop.add(btnNuevo, BorderLayout.EAST);
        getContentPane().add(pnlTop, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(UIKit.SPACE_LG, 0));
        cuerpo.setOpaque(false);

        // Tabla
        JPanel pnlTabla = UIKit.card();
        pnlTabla.setLayout(new BorderLayout(0, UIKit.SPACE_SM));

        JPanel pnlBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlBusqueda.setOpaque(false);
        pnlBusqueda.add(txtBuscarRuc);

        pnlTabla.add(UIKit.sectionHeader("Listado de Proveedores", null), BorderLayout.NORTH);
        pnlTabla.add(pnlBusqueda, BorderLayout.BEFORE_FIRST_LINE);

        JPanel pnlTableWrapper = new JPanel(new BorderLayout());
        pnlTableWrapper.setOpaque(false);
        JScrollPane scroll = new JScrollPane(tblProveedores);
        scroll.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlTableWrapper.add(scroll, BorderLayout.CENTER);
        pnlTableWrapper.add(lblEmptyState, BorderLayout.SOUTH);
        pnlTabla.add(pnlTableWrapper, BorderLayout.CENTER);
        cuerpo.add(pnlTabla, BorderLayout.CENTER);

        // Formulario
        JPanel pnlForm = UIKit.card();
        pnlForm.setPreferredSize(new Dimension(340, 0));
        pnlForm.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        gbc.gridx = 0;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlForm.add(UIKit.sectionHeader("Detalle del Proveedor", null), gbc);

        // ID y RUC en la misma fila
        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, UIKit.SPACE_SM);
        pnlForm.add(UIKit.fieldLabel("ID"), gbc);
        gbc.gridx = 1; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("RUC"), gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, UIKit.SPACE_SM);
        pnlForm.add(txtId, gbc);
        gbc.gridx = 1; gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        JPanel pnlRuc = new JPanel(new BorderLayout(5, 0));
        pnlRuc.setOpaque(false);
        pnlRuc.add(txtRuc, BorderLayout.CENTER);
        pnlRuc.add(btnConsultarRuc, BorderLayout.EAST);
        pnlForm.add(pnlRuc, gbc);

        // Razón Social
        gbc.gridwidth = 2; gbc.gridx = 0;
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("Razón Social"), gbc);
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlForm.add(txtRazonSocial, gbc);

        // Teléfono y Correo
        gbc.gridwidth = 1;
        gbc.gridy = 5; gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, UIKit.SPACE_SM);
        pnlForm.add(UIKit.fieldLabel("Teléfono"), gbc);
        gbc.gridx = 1; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("Correo"), gbc);

        gbc.gridy = 6; gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, UIKit.SPACE_SM);
        pnlForm.add(txtTelefono, gbc);
        gbc.gridx = 1; gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlForm.add(txtCorreo, gbc);

        // Dirección
        gbc.gridwidth = 2; gbc.gridx = 0;
        gbc.gridy = 7; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("Dirección"), gbc);
        gbc.gridy = 8; gbc.insets = new Insets(0, 0, UIKit.SPACE_LG, 0);
        pnlForm.add(txtDireccion, gbc);

        // Botones
        JPanel pnlBotones = new JPanel(new GridLayout(3, 1, 0, UIKit.SPACE_SM));
        pnlBotones.setOpaque(false);
        pnlBotones.add(btnGuardar);
        pnlBotones.add(btnLimpiar);
        pnlBotones.add(btnEliminar);

        gbc.gridy = 9; gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        pnlForm.add(pnlBotones, gbc);

        cuerpo.add(pnlForm, BorderLayout.EAST);
        getContentPane().add(cuerpo, BorderLayout.CENTER);
    }

    private void attachEvents() {

        // BUSCAR en tiempo real por RUC o razón social
        txtBuscarRuc.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) {
                String texto = txtBuscarRuc.getText().trim().toLowerCase();
                modelProveedores.setRowCount(0);
                ProveedorDAO dao = new ProveedorDAO();
                for (Proveedor p : dao.listar()) {
                    if (p.ruc.contains(texto) || p.razonSocial.toLowerCase().contains(texto)) {
                        modelProveedores.addRow(new Object[]{
                            p.idProveedor, p.ruc, p.razonSocial,
                            p.telefono, p.correo, p.direccion
                        });
                    }
                }
                boolean vacio = modelProveedores.getRowCount() == 0;
                lblEmptyState.setVisible(vacio);
            }
        });

        // Botón Nuevo
        btnNuevo.addActionListener(e -> limpiar());

        // GUARDAR / ACTUALIZAR
        btnGuardar.addActionListener(e -> {
            String ruc         = txtRuc.getText().trim();
            String razonSocial = txtRazonSocial.getText().trim();

            // Validación centralizada
            String error = Validador.validarProveedor(razonSocial, ruc);
            if (error != null) {
                JOptionPane.showMessageDialog(this, error, "Campo inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Proveedor p = new Proveedor(0, ruc, razonSocial,
                txtTelefono.getText().trim(),
                txtDireccion.getText().trim(),
                txtCorreo.getText().trim(), 1);

            ProveedorDAO dao = new ProveedorDAO();

            if (txtId.getText().isEmpty()) {
                if (dao.insertar(p)) {
                    JOptionPane.showMessageDialog(this, "Proveedor agregado correctamente");
                    cargarTabla();
                    limpiar();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al agregar proveedor");
                }
            } else {
                p.idProveedor = Integer.parseInt(txtId.getText());
                if (dao.actualizar(p)) {
                    JOptionPane.showMessageDialog(this, "Proveedor actualizado");
                    cargarTabla();
                    limpiar();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar");
                }
            }
        });

        // ELIMINAR
        btnEliminar.addActionListener(e -> {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione un proveedor");
                return;
            }
            int op = JOptionPane.showConfirmDialog(this,
                "¿Eliminar este proveedor?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (op != JOptionPane.YES_OPTION) return;

            ProveedorDAO dao = new ProveedorDAO();
            if (dao.eliminar(Integer.parseInt(txtId.getText()))) {
                JOptionPane.showMessageDialog(this, "Proveedor eliminado");
                cargarTabla();
                limpiar();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar");
            }
        });

        // LIMPIAR
        btnLimpiar.addActionListener(e -> limpiar());

        // SELECCIONAR FILA
        tblProveedores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblProveedores.getSelectedRow() != -1) {
                int row = tblProveedores.getSelectedRow();
                txtId.setText(modelProveedores.getValueAt(row, 0).toString());
                txtRuc.setText(modelProveedores.getValueAt(row, 1).toString());
                txtRazonSocial.setText(modelProveedores.getValueAt(row, 2).toString());
                txtTelefono.setText(modelProveedores.getValueAt(row, 3).toString());
                txtCorreo.setText(modelProveedores.getValueAt(row, 4).toString());
                txtDireccion.setText(modelProveedores.getValueAt(row, 5).toString());
            }
        });

        // CONSULTAR RUC
        btnConsultarRuc.addActionListener(e -> {
            String ruc = txtRuc.getText().trim();
            if (ruc.length() == 11) {
                String[] datos = ApiClient.consultarRuc(ruc);
                if (datos != null) {
                    txtRazonSocial.setText(datos[0]);
                    txtDireccion.setText(datos[1]);
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró el RUC o error de conexión.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "El RUC debe tener 11 dígitos.");
            }
        });
    }

    private void cargarTabla() {
        modelProveedores.setRowCount(0);
        ProveedorDAO dao = new ProveedorDAO();
        for (Proveedor p : dao.listar()) {
            modelProveedores.addRow(new Object[]{
                p.idProveedor, p.ruc, p.razonSocial,
                p.telefono, p.correo, p.direccion
            });
        }
        boolean vacio = modelProveedores.getRowCount() == 0;
        lblEmptyState.setVisible(vacio);
    }

    private void limpiar() {
        txtId.setText("");
        txtRuc.setText("");
        txtRazonSocial.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtDireccion.setText("");
        tblProveedores.clearSelection();
    }
}