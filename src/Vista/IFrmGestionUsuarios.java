package Vista;

import DAO.EmpleadoDAO;
import DAO.EmpleadoDAO.Empleado;
import DAO.UsuarioDAO;
import Vista.Estilos.UIKit;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class IFrmGestionUsuarios extends JInternalFrame {

    private JTable tblUsuarios;
    private DefaultTableModel modelUsuarios;
    private JTextField txtBuscar;

    private JTextField txtId;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnVerPass;
    private JComboBox<String> cbEmpleado;

    private JButton btnBuscar;
    private JButton btnGuardar;
    private JButton btnDesactivar;
    private JButton btnLimpiar;

    private List<Empleado> listaEmpleados;

    public IFrmGestionUsuarios() {
        super("Gestión de Usuarios y Roles", true, true, true, true);
        initComponents();
        buildLayout();
        attachEvents();
        setSize(960, 560);
        cargarTabla();
    }

    private void initComponents() {
        txtBuscar = UIKit.textField();
        txtBuscar.setPreferredSize(new Dimension(200, 36));
        btnBuscar = UIKit.secondaryButton("Buscar");

        // Columna Contraseña oculta en índice 2
        String[] columns = {"ID", "Usuario", "Contraseña", "Empleado", "Cargo", "Estado"};
        modelUsuarios = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tblUsuarios = UIKit.styledTable(modelUsuarios);

        // Ocultar columna contraseña de la vista
        tblUsuarios.getColumnModel().getColumn(2).setMinWidth(0);
        tblUsuarios.getColumnModel().getColumn(2).setMaxWidth(0);
        tblUsuarios.getColumnModel().getColumn(2).setWidth(0);

        txtId = UIKit.readOnlyField();
        txtId.setEditable(false);
        txtId.setFocusable(false);

        txtUsuario = UIKit.textField();

        txtPassword = new JPasswordField();
        txtPassword.setFont(UIKit.BODY);
        txtPassword.setPreferredSize(new Dimension(0, 36));
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Contraseña");

        // Botón ver/ocultar contraseña
        btnVerPass = new JButton("👁");
        btnVerPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnVerPass.setFocusPainted(false);
        btnVerPass.setContentAreaFilled(false);
        btnVerPass.setBorderPainted(false);
        btnVerPass.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVerPass.addActionListener(e -> {
            if (txtPassword.getEchoChar() == 0) {
                txtPassword.setEchoChar('•');
                btnVerPass.setText("👁");
            } else {
                txtPassword.setEchoChar((char) 0);
                btnVerPass.setText("🙈");
            }
        });

        cbEmpleado = new JComboBox<>();
        cbEmpleado.setFont(UIKit.BODY);
        cbEmpleado.setPreferredSize(new Dimension(0, 36));
        cargarEmpleados();

        btnGuardar    = UIKit.primaryButton("Guardar / Actualizar");
        btnLimpiar    = UIKit.secondaryButton("Limpiar / Nuevo");
        btnDesactivar = UIKit.secondaryButton("Desactivar / Activar");
    }

    private void cargarEmpleados() {
        cbEmpleado.removeAllItems();
        EmpleadoDAO dao = new EmpleadoDAO();
        listaEmpleados = dao.listar();
        for (Empleado emp : listaEmpleados) {
            cbEmpleado.addItem(emp.nombres + " " + emp.apellidos);
        }
    }

    private void buildLayout() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UIKit.BG_APP);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(
                UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));

        getContentPane().add(
                UIKit.screenHeader("Gestión de Usuarios", "Administración  ›  Usuarios y Roles"),
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
        pnlHeader.add(UIKit.sectionHeader("Usuarios del Sistema", null), BorderLayout.NORTH);
        pnlHeader.add(pnlBusqueda, BorderLayout.CENTER);

        pnlTabla.add(pnlHeader, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tblUsuarios);
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
        pnlForm.add(UIKit.sectionHeader("Detalle del Usuario", null), gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("ID Usuario"), gbc);
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlForm.add(txtId, gbc);

        // Usuario y Contraseña
        gbc.gridwidth = 1;
        gbc.gridy = 3; gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, UIKit.SPACE_SM);
        pnlForm.add(UIKit.fieldLabel("Usuario"), gbc);
        gbc.gridx = 1; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("Contraseña"), gbc);

        gbc.gridy = 4; gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, UIKit.SPACE_SM);
        pnlForm.add(txtUsuario, gbc);

        // Panel contraseña con botón ojo
        gbc.gridx = 1; gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        JPanel pnlPass = new JPanel(new BorderLayout(4, 0));
        pnlPass.setOpaque(false);
        pnlPass.add(txtPassword, BorderLayout.CENTER);
        pnlPass.add(btnVerPass, BorderLayout.EAST);
        pnlForm.add(pnlPass, gbc);

        // Empleado
        gbc.gridwidth = 2; gbc.gridx = 0;
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("Empleado vinculado"), gbc);
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, UIKit.SPACE_LG, 0);
        pnlForm.add(cbEmpleado, gbc);

        // Botones
        JPanel pnlBotones = new JPanel(new GridLayout(3, 1, 0, UIKit.SPACE_SM));
        pnlBotones.setOpaque(false);
        pnlBotones.add(btnGuardar);
        pnlBotones.add(btnLimpiar);
        pnlBotones.add(btnDesactivar);

        gbc.gridy = 7; gbc.weighty = 1.0;
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
            cargarTablaFiltrada(texto);
        });

        // GUARDAR / ACTUALIZAR
        btnGuardar.addActionListener(e -> {
            String usuario = txtUsuario.getText().trim();
            String pass    = new String(txtPassword.getPassword()).trim();

            if (usuario.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Usuario y contraseña son obligatorios");
                return;
            }

            int idEmpleado = obtenerIdEmpleado(cbEmpleado.getSelectedItem().toString());
            UsuarioDAO dao = new UsuarioDAO();

            if (txtId.getText().isEmpty()) {
                // NUEVO
                if (dao.insertar(usuario, pass, idEmpleado)) {
                    JOptionPane.showMessageDialog(this, "Usuario creado correctamente");
                    cargarTabla();
                    limpiar();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al crear. ¿Usuario ya existe?");
                }
            } else {
                // ACTUALIZAR contraseña
                int id = Integer.parseInt(txtId.getText());
                if (dao.actualizarContrasena(id, pass)) {
                    JOptionPane.showMessageDialog(this, "Contraseña actualizada");
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
                JOptionPane.showMessageDialog(this, "Seleccione un usuario");
                return;
            }
            int id  = Integer.parseInt(txtId.getText());
            int row = tblUsuarios.getSelectedRow();
            String estadoActual = modelUsuarios.getValueAt(row, 5).toString();
            UsuarioDAO dao = new UsuarioDAO();

            if (estadoActual.equals("Activo")) {
                int op = JOptionPane.showConfirmDialog(this,
                    "¿Desactivar este usuario?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (op != JOptionPane.YES_OPTION) return;
                if (dao.desactivar(id)) {
                    JOptionPane.showMessageDialog(this, "Usuario desactivado");
                    cargarTabla(); limpiar();
                }
            } else {
                if (dao.reactivar(id)) {
                    JOptionPane.showMessageDialog(this, "Usuario reactivado");
                    cargarTabla(); limpiar();
                }
            }
        });

        // LIMPIAR
        btnLimpiar.addActionListener(e -> limpiar());

        // SELECCIONAR FILA
        tblUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblUsuarios.getSelectedRow() != -1) {
                int row = tblUsuarios.getSelectedRow();
                txtId.setText(modelUsuarios.getValueAt(row, 0).toString());
                txtUsuario.setText(modelUsuarios.getValueAt(row, 1).toString());
                // Cargar contraseña oculta desde columna 2
                txtPassword.setText(modelUsuarios.getValueAt(row, 2).toString());
                txtPassword.setEchoChar('•');
                btnVerPass.setText("👁");

                String estado = modelUsuarios.getValueAt(row, 5).toString();
                btnDesactivar.setText(estado.equals("Activo") ? "Desactivar" : "Activar");

                String nombreEmp = modelUsuarios.getValueAt(row, 3).toString();
                for (int i = 0; i < cbEmpleado.getItemCount(); i++) {
                    if (cbEmpleado.getItemAt(i).equals(nombreEmp)) {
                        cbEmpleado.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });
    }

    private void cargarTabla() {
        cargarTablaFiltrada("");
    }

    private void cargarTablaFiltrada(String filtro) {
        modelUsuarios.setRowCount(0);
        String sql = "SELECT u.idUsuario, u.usuario, u.contrasena, e.nombres, e.apellidos, " +
                     "c.nombreCargo, u.estado " +
                     "FROM usuario u " +
                     "JOIN empleado e ON u.idEmpleado = e.idEmpleado " +
                     "JOIN cargo c ON e.idCargo = c.idCargo";
        try (java.sql.Connection con = Conexion.Conexion.getConexion();
             java.sql.PreparedStatement ps = con.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String nombreEmp = rs.getString("nombres") + " " + rs.getString("apellidos");
                String usuario   = rs.getString("usuario");
                if (filtro.isEmpty() || usuario.toLowerCase().contains(filtro)
                        || nombreEmp.toLowerCase().contains(filtro)) {
                    modelUsuarios.addRow(new Object[]{
                        rs.getInt("idUsuario"),
                        usuario,
                        rs.getString("contrasena"),  // col 2 oculta
                        nombreEmp,
                        rs.getString("nombreCargo"),
                        rs.getInt("estado") == 1 ? "Activo" : "Inactivo"
                    });
                }
            }
        } catch (java.sql.SQLException ex) { ex.printStackTrace(); }
    }

    private int obtenerIdEmpleado(String nombre) {
        if (listaEmpleados != null) {
            for (Empleado emp : listaEmpleados) {
                if ((emp.nombres + " " + emp.apellidos).equals(nombre))
                    return emp.idEmpleado;
            }
        }
        return 1;
    }

    private void limpiar() {
        txtId.setText("");
        txtUsuario.setText("");
        txtPassword.setText("");
        txtPassword.setEchoChar('•');
        btnVerPass.setText("👁");
        cbEmpleado.setSelectedIndex(0);
        tblUsuarios.clearSelection();
        btnDesactivar.setText("Desactivar / Activar");
    }
}