package Vista;

import DAO.EmpleadoDAO;
import DAO.EmpleadoDAO.Empleado;
import Vista.Estilos.UIKit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class IFrmFichaEmpleados extends JInternalFrame {

    private JTable tblEmpleados;
    private DefaultTableModel modelEmpleados;
    private JTextField txtBuscar;

    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtDni;
    private JTextField txtTelefono;
    private JTextField txtDireccion;
    private JComboBox<String> cbCargo;
    private JComboBox<String> cbEstado;

    private JButton btnBuscar;
    private JButton btnGuardar;
    private JButton btnDesactivar;
    private JButton btnLimpiar;

    private List<String[]> listaCargos;

    public IFrmFichaEmpleados() {
        super("Ficha de Empleados", true, true, true, true);
        initComponents();
        buildLayout();
        attachEvents();
        setSize(960, 600);
        cargarTabla();
    }

    private void initComponents() {
        txtBuscar = UIKit.textField();
        txtBuscar.setPreferredSize(new Dimension(200, 36));
        btnBuscar = UIKit.secondaryButton("Buscar");

        String[] columns = {"ID", "Nombres", "Apellidos", "DNI", "Cargo", "Teléfono", "Estado"};
        modelEmpleados = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tblEmpleados = UIKit.styledTable(modelEmpleados);

        txtId = UIKit.readOnlyField();
        txtId.setEditable(false);
        txtId.setFocusable(false);
        txtNombre    = UIKit.textField();
        txtApellido  = UIKit.textField();
        txtDni       = UIKit.textField();
        txtTelefono  = UIKit.textField();
        txtDireccion = UIKit.textField();

        cbCargo = new JComboBox<>();
        cbCargo.setFont(UIKit.BODY);
        cbCargo.setPreferredSize(new Dimension(0, 36));
        cargarCargos();

        cbEstado = new JComboBox<>(new String[]{"Activo", "Inactivo"});
        cbEstado.setFont(UIKit.BODY);

        btnGuardar    = UIKit.primaryButton("Guardar / Actualizar");
        btnLimpiar    = UIKit.secondaryButton("Limpiar / Nuevo");
        btnDesactivar = UIKit.secondaryButton("Desactivar / Activar");
    }

    private void cargarCargos() {
        cbCargo.removeAllItems();
        EmpleadoDAO dao = new EmpleadoDAO();
        listaCargos = dao.listarCargos();
        for (String[] cargo : listaCargos) {
            cbCargo.addItem(cargo[1]);
        }
    }

    private void buildLayout() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UIKit.BG_APP);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(
                UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));

        getContentPane().add(
                UIKit.screenHeader("Ficha de Empleados", "Personal  ›  Empleados"),
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
        pnlHeader.add(UIKit.sectionHeader("Listado de Empleados", null), BorderLayout.NORTH);
        pnlHeader.add(pnlBusqueda, BorderLayout.CENTER);

        pnlTabla.add(pnlHeader, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tblEmpleados);
        scroll.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlTabla.add(scroll, BorderLayout.CENTER);
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
        pnlForm.add(UIKit.sectionHeader("Datos del Empleado", null), gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("ID Empleado"), gbc);
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlForm.add(txtId, gbc);

        // Nombre y Apellido
        gbc.gridwidth = 1;
        gbc.gridy = 3; gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, UIKit.SPACE_SM);
        pnlForm.add(UIKit.fieldLabel("Nombres"), gbc);
        gbc.gridx = 1; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("Apellidos"), gbc);

        gbc.gridy = 4; gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, UIKit.SPACE_SM);
        pnlForm.add(txtNombre, gbc);
        gbc.gridx = 1; gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlForm.add(txtApellido, gbc);

        // DNI y Teléfono
        gbc.gridy = 5; gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, UIKit.SPACE_SM);
        pnlForm.add(UIKit.fieldLabel("DNI"), gbc);
        gbc.gridx = 1; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("Teléfono"), gbc);

        gbc.gridy = 6; gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, UIKit.SPACE_SM);
        pnlForm.add(txtDni, gbc);
        gbc.gridx = 1; gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlForm.add(txtTelefono, gbc);

        // Cargo
        gbc.gridwidth = 2; gbc.gridx = 0;
        gbc.gridy = 7; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("Cargo"), gbc);
        gbc.gridy = 8; gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlForm.add(cbCargo, gbc);

        // Dirección
        gbc.gridy = 9; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("Dirección"), gbc);
        gbc.gridy = 10; gbc.insets = new Insets(0, 0, UIKit.SPACE_LG, 0);
        pnlForm.add(txtDireccion, gbc);

        // Botones
        JPanel pnlBotones = new JPanel(new GridLayout(3, 1, 0, UIKit.SPACE_SM));
        pnlBotones.setOpaque(false);
        pnlBotones.add(btnGuardar);
        pnlBotones.add(btnLimpiar);
        pnlBotones.add(btnDesactivar);

        gbc.gridy = 11; gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        pnlForm.add(pnlBotones, gbc);

        cuerpo.add(pnlForm, BorderLayout.EAST);
        getContentPane().add(cuerpo, BorderLayout.CENTER);
    }

    private void attachEvents() {

        // BUSCAR
        btnBuscar.addActionListener(e -> {
            String texto = txtBuscar.getText().trim().toLowerCase();
            modelEmpleados.setRowCount(0);
            EmpleadoDAO dao = new EmpleadoDAO();
            for (Empleado emp : dao.listar()) {
                if (emp.nombres.toLowerCase().contains(texto)
                        || emp.apellidos.toLowerCase().contains(texto)
                        || emp.dni.toLowerCase().contains(texto)) {
                    modelEmpleados.addRow(new Object[]{
                        emp.idEmpleado, emp.nombres, emp.apellidos,
                        emp.dni, emp.cargo, emp.telefono,
                        emp.estado == 1 ? "Activo" : "Inactivo"
                    });
                }
            }
        });

        // GUARDAR / ACTUALIZAR
        btnGuardar.addActionListener(e -> {
            String nombre   = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String dni      = txtDni.getText().trim();

            if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nombres, apellidos y DNI son obligatorios");
                return;
            }

            int idCargo = obtenerIdCargo(cbCargo.getSelectedItem().toString());
            Empleado emp = new Empleado(0, nombre, apellido, dni,
                txtTelefono.getText().trim(), txtDireccion.getText().trim(),
                idCargo, "", "", 1);

            EmpleadoDAO dao = new EmpleadoDAO();

            if (txtId.getText().isEmpty()) {
                if (dao.insertar(emp)) {
                    JOptionPane.showMessageDialog(this, "Empleado registrado correctamente");
                    cargarTabla();
                    limpiar();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar");
                }
            } else {
                emp.idEmpleado = Integer.parseInt(txtId.getText());
                if (dao.actualizar(emp)) {
                    JOptionPane.showMessageDialog(this, "Empleado actualizado");
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
                JOptionPane.showMessageDialog(this, "Seleccione un empleado");
                return;
            }
            int id  = Integer.parseInt(txtId.getText());
            int row = tblEmpleados.getSelectedRow();
            String estadoActual = modelEmpleados.getValueAt(row, 6).toString();
            EmpleadoDAO dao = new EmpleadoDAO();

            if (estadoActual.equals("Activo")) {
                int op = JOptionPane.showConfirmDialog(this,
                    "¿Desactivar este empleado?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (op != JOptionPane.YES_OPTION) return;
                if (dao.desactivar(id)) {
                    JOptionPane.showMessageDialog(this, "Empleado desactivado");
                    cargarTabla(); limpiar();
                }
            } else {
                if (dao.reactivar(id)) {
                    JOptionPane.showMessageDialog(this, "Empleado reactivado");
                    cargarTabla(); limpiar();
                }
            }
        });

        // LIMPIAR
        btnLimpiar.addActionListener(e -> limpiar());

        // SELECCIONAR FILA
        tblEmpleados.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblEmpleados.getSelectedRow() != -1) {
                int row = tblEmpleados.getSelectedRow();
                txtId.setText(modelEmpleados.getValueAt(row, 0).toString());
                txtNombre.setText(modelEmpleados.getValueAt(row, 1).toString());
                txtApellido.setText(modelEmpleados.getValueAt(row, 2).toString());
                txtDni.setText(modelEmpleados.getValueAt(row, 3).toString());
                String cargo = modelEmpleados.getValueAt(row, 4).toString();
                for (int i = 0; i < cbCargo.getItemCount(); i++) {
                    if (cbCargo.getItemAt(i).equals(cargo)) {
                        cbCargo.setSelectedIndex(i); break;
                    }
                }
                txtTelefono.setText(modelEmpleados.getValueAt(row, 5).toString());
                String estado = modelEmpleados.getValueAt(row, 6).toString();
                btnDesactivar.setText(estado.equals("Activo") ? "Desactivar" : "Activar");
            }
        });
    }

    private void cargarTabla() {
        modelEmpleados.setRowCount(0);
        EmpleadoDAO dao = new EmpleadoDAO();
        for (Empleado emp : dao.listar()) {
            modelEmpleados.addRow(new Object[]{
                emp.idEmpleado, emp.nombres, emp.apellidos,
                emp.dni, emp.cargo, emp.telefono,
                emp.estado == 1 ? "Activo" : "Inactivo"
            });
        }
    }

    private int obtenerIdCargo(String nombre) {
        if (listaCargos != null) {
            for (String[] cargo : listaCargos) {
                if (cargo[1].equals(nombre)) return Integer.parseInt(cargo[0]);
            }
        }
        return 1;
    }

    private void limpiar() {
        txtId.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtDni.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");
        cbCargo.setSelectedIndex(0);
        tblEmpleados.clearSelection();
        btnDesactivar.setText("Desactivar / Activar");
    }
}