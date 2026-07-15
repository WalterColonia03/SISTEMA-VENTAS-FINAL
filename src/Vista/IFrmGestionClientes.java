package Vista;

import Clases.Cliente;
import DAO.ClienteDAO;
import API.ApiClient;
import Servicio.Validador;
import Vista.Estilos.UIKit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class IFrmGestionClientes extends JInternalFrame {

    private JTable tblClientes;
    private DefaultTableModel modelClientes;
    private JTextField txtBuscar;
    private JLabel lblEmptyState;

    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtDni;
    private JTextField txtTelefono;
    private JTextField txtCorreo;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnDesactivar;
    private JButton btnLimpiar;
    private JButton btnConsultarDni;

    public IFrmGestionClientes() {
        super("Gestión de Clientes", true, true, true, true);
        initComponents();
        buildLayout();
        attachEvents();
        setSize(960, 600);
        cargarTabla();
    }

    private void initComponents() {
        txtBuscar = UIKit.searchField("Buscar cliente por nombre o DNI...", null);

        String[] columns = {"ID", "Nombre", "Apellido", "DNI/RUC", "Teléfono", "Correo", "Estado"};
        modelClientes = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tblClientes = UIKit.styledTable(modelClientes);

        lblEmptyState = new JLabel("No hay clientes registrados", SwingConstants.CENTER);
        lblEmptyState.setFont(UIKit.BODY); lblEmptyState.setForeground(UIKit.TEXT_SECONDARY);
        lblEmptyState.setVisible(false);

        txtId       = UIKit.readOnlyField(); txtId.setEditable(false); txtId.setFocusable(false);
        txtNombre   = UIKit.textField(); txtApellido = UIKit.textField();
        txtDni      = UIKit.textField(); txtTelefono = UIKit.textField(); txtCorreo = UIKit.textField();

        btnNuevo      = UIKit.primaryButton("+ Nuevo Cliente");
        btnGuardar    = UIKit.primaryButton("Guardar / Actualizar");
        btnLimpiar    = UIKit.secondaryButton("Limpiar / Nuevo");
        btnDesactivar = UIKit.secondaryButton("Desactivar / Activar");
        btnConsultarDni = UIKit.secondaryButton("RENIEC");
    }

    private void buildLayout() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UIKit.BG_APP);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(
                UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        pnlTop.add(UIKit.screenHeader("Gestión de Clientes", "Clientes y Proveedores  ›  Clientes"), BorderLayout.WEST);
        pnlTop.add(btnNuevo, BorderLayout.EAST);
        getContentPane().add(pnlTop, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(UIKit.SPACE_LG, 0));
        cuerpo.setOpaque(false);

        JPanel pnlTabla = UIKit.card();
        pnlTabla.setLayout(new BorderLayout(0, UIKit.SPACE_SM));

        JPanel pnlBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlBusqueda.setOpaque(false); pnlBusqueda.add(txtBuscar);
        pnlTabla.add(UIKit.sectionHeader("Listado de Clientes", null), BorderLayout.NORTH);
        pnlTabla.add(pnlBusqueda, BorderLayout.BEFORE_FIRST_LINE);

        JPanel pnlWrapper = new JPanel(new BorderLayout()); pnlWrapper.setOpaque(false);
        JScrollPane scroll = new JScrollPane(tblClientes);
        scroll.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlWrapper.add(scroll, BorderLayout.CENTER); pnlWrapper.add(lblEmptyState, BorderLayout.SOUTH);
        pnlTabla.add(pnlWrapper, BorderLayout.CENTER);
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

        gbc.gridy = 0; gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlForm.add(UIKit.sectionHeader("Detalle del Cliente", null), gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("ID Cliente"), gbc);
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlForm.add(txtId, gbc);

        // Nombre y Apellido
        gbc.gridwidth = 1;
        gbc.gridy = 3; gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, UIKit.SPACE_SM);
        pnlForm.add(UIKit.fieldLabel("Nombre"), gbc);
        gbc.gridx = 1; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("Apellido"), gbc);

        gbc.gridy = 4; gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, UIKit.SPACE_SM);
        pnlForm.add(txtNombre, gbc);
        gbc.gridx = 1; gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlForm.add(txtApellido, gbc);

        // DNI y Teléfono
        gbc.gridy = 5; gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, UIKit.SPACE_SM);
        pnlForm.add(UIKit.fieldLabel("DNI / RUC"), gbc);
        gbc.gridx = 1; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("Teléfono"), gbc);

        gbc.gridy = 6; gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, UIKit.SPACE_SM);
        JPanel pnlDni = new JPanel(new BorderLayout(5, 0));
        pnlDni.setOpaque(false);
        pnlDni.add(txtDni, BorderLayout.CENTER);
        pnlDni.add(btnConsultarDni, BorderLayout.EAST);
        pnlForm.add(pnlDni, gbc);
        gbc.gridx = 1; gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlForm.add(txtTelefono, gbc);

        // Correo
        gbc.gridwidth = 2; gbc.gridx = 0;
        gbc.gridy = 7; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("Correo Electrónico (opcional)"), gbc);
        gbc.gridy = 8; gbc.insets = new Insets(0, 0, UIKit.SPACE_LG, 0);
        pnlForm.add(txtCorreo, gbc);

        // Botones
        JPanel pnlBotones = new JPanel(new GridLayout(3, 1, 0, UIKit.SPACE_SM));
        pnlBotones.setOpaque(false);
        pnlBotones.add(btnGuardar);
        pnlBotones.add(btnLimpiar);
        pnlBotones.add(btnDesactivar);

        gbc.gridy = 9; gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        pnlForm.add(pnlBotones, gbc);

        cuerpo.add(pnlForm, BorderLayout.EAST);
        getContentPane().add(cuerpo, BorderLayout.CENTER);
    }

    private void attachEvents() {

        // BUSCAR en tiempo real
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) {
                String txt = txtBuscar.getText().trim().toLowerCase();
                modelClientes.setRowCount(0);
                for (Cliente c : new ClienteDAO().listarTodos()) {
                    if (c.getNombre().toLowerCase().contains(txt)
                            || c.getApellido().toLowerCase().contains(txt)
                            || c.getDni().toLowerCase().contains(txt)) {
                        modelClientes.addRow(new Object[]{
                            c.getIdCliente(), c.getNombre(), c.getApellido(),
                            c.getDni(), c.getTelefono(), c.getDireccion(),
                            c.getEstado() == 1 ? "Activo" : "Inactivo"
                        });
                    }
                }
                lblEmptyState.setVisible(modelClientes.getRowCount() == 0);
            }
        });
        btnNuevo.addActionListener(e -> limpiar());

        // GUARDAR / ACTUALIZAR
        btnGuardar.addActionListener(e -> {
            String nombre   = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String dni      = txtDni.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String correo   = txtCorreo.getText().trim();

            // Validación centralizada
            String error = Validador.validarCliente(nombre, apellido, dni);
            if (error != null) {
                JOptionPane.showMessageDialog(this, error, "Campo inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // No permitir editar inactivos
            if (!txtId.getText().isEmpty()) {
                int row = tblClientes.getSelectedRow();
                if (row != -1) {
                    String estado = modelClientes.getValueAt(row, 6).toString();
                    if (estado.equals("Inactivo")) {
                        JOptionPane.showMessageDialog(this,
                            "No puedes editar un cliente inactivo.\nActívalo primero.");
                        return;
                    }
                }
            }

            ClienteDAO dao = new ClienteDAO();
            Cliente c = new Cliente(0, nombre, apellido, dni, telefono, correo, 1);

            if (txtId.getText().isEmpty()) {
                if (dao.insertar(c)) {
                    JOptionPane.showMessageDialog(this, "Cliente registrado correctamente");
                    cargarTabla();
                    limpiar();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar. ¿DNI ya existe?");
                }
            } else {
                c = new Cliente(Integer.parseInt(txtId.getText()), nombre,
                        apellido, dni, telefono, correo, 1);
                if (dao.actualizar(c)) {
                    JOptionPane.showMessageDialog(this, "Cliente actualizado");
                    cargarTabla();
                    limpiar();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar");
                }
            }
        });

        // DESACTIVAR / ACTIVAR
        btnDesactivar.addActionListener(e -> {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione un cliente");
                return;
            }
            int id  = Integer.parseInt(txtId.getText());
            int row = tblClientes.getSelectedRow();
            String estadoActual = modelClientes.getValueAt(row, 6).toString();
            ClienteDAO dao = new ClienteDAO();

            if (estadoActual.equals("Activo")) {
                int op = JOptionPane.showConfirmDialog(this,
                    "¿Desactivar este cliente?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (op != JOptionPane.YES_OPTION) return;
                if (dao.eliminar(id)) {
                    JOptionPane.showMessageDialog(this, "Cliente desactivado");
                    cargarTabla();
                    limpiar();
                }
            } else {
                if (dao.reactivar(id)) {
                    JOptionPane.showMessageDialog(this, "Cliente reactivado");
                    cargarTabla();
                    limpiar();
                }
            }
        });

        // LIMPIAR
        btnLimpiar.addActionListener(e -> limpiar());

        // SELECCIONAR FILA
        tblClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblClientes.getSelectedRow() != -1) {
                int row = tblClientes.getSelectedRow();
                txtId.setText(modelClientes.getValueAt(row, 0).toString());
                txtNombre.setText(modelClientes.getValueAt(row, 1).toString());
                txtApellido.setText(modelClientes.getValueAt(row, 2).toString());
                txtDni.setText(modelClientes.getValueAt(row, 3).toString());
                txtTelefono.setText(modelClientes.getValueAt(row, 4).toString());
                txtCorreo.setText(modelClientes.getValueAt(row, 5).toString());
                String estado = modelClientes.getValueAt(row, 6).toString();
                btnDesactivar.setText(estado.equals("Activo") ? "Desactivar" : "Activar");
            }
        });

        // CONSULTAR DNI
        btnConsultarDni.addActionListener(e -> {
            String dni = txtDni.getText().trim();
            if (dni.length() == 8) {
                String[] datos = ApiClient.consultarDni(dni);
                if (datos != null) {
                    txtNombre.setText(datos[0]);
                    txtApellido.setText(datos[1]);
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró el DNI o error de conexión.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "El DNI debe tener 8 dígitos.");
            }
        });
    }

    private void cargarTabla() {
        modelClientes.setRowCount(0);
        for (Cliente c : new ClienteDAO().listarTodos()) {
            modelClientes.addRow(new Object[]{
                c.getIdCliente(), c.getNombre(), c.getApellido(),
                c.getDni(), c.getTelefono(), c.getDireccion(),
                c.getEstado() == 1 ? "Activo" : "Inactivo"
            });
        }
        lblEmptyState.setVisible(modelClientes.getRowCount() == 0);
    }

    private void limpiar() {
        txtId.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtDni.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        tblClientes.clearSelection();
        btnDesactivar.setText("Desactivar / Activar");
    }
}