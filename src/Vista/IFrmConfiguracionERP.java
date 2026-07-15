package Vista;

import Conexion.Conexion;
import Vista.Estilos.UIKit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class IFrmConfiguracionERP extends JInternalFrame {

    private JTextField txtRazonSocial;
    private JTextField txtRuc;
    private JTextField txtDireccion;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JTextField txtIgv;
    private JTextField txtStockMinimo;
    private JTextField txtMpPublicKey;
    private JTextField txtMpClientId;
    private JPasswordField txtMpToken;
    private JButton btnOjoToken;
    private JButton btnGuardar;
    private JButton btnLimpiar;

    public IFrmConfiguracionERP() {
        super("Configuración Global del ERP", true, true, true, true);
        initComponents();
        buildLayout();
        attachEvents();
        setSize(960, 600);
        cargarConfiguracion();
    }

    private void initComponents() {
        txtRazonSocial = UIKit.textField();
        txtRuc = UIKit.textField();
        txtDireccion = UIKit.textField();
        txtTelefono = UIKit.textField();
        txtCorreo = UIKit.textField();
        txtIgv = UIKit.textField();
        txtIgv.setText("18");
        txtStockMinimo = UIKit.textField();
        txtStockMinimo.setText("10");
        txtMpPublicKey = UIKit.textField();
        txtMpClientId = UIKit.textField();

        txtMpToken = new JPasswordField();
        txtMpToken.setFont(UIKit.BODY);
        txtMpToken.setPreferredSize(new Dimension(0, 36));

        btnOjoToken = new JButton("Ver");
        btnOjoToken.setContentAreaFilled(false);
        btnOjoToken.setBorderPainted(false);
        btnOjoToken.setFocusPainted(false);
        btnOjoToken.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnGuardar = UIKit.primaryButton("Guardar Configuración");
        btnLimpiar = UIKit.secondaryButton("Restablecer");
    }

    private void buildLayout() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UIKit.BG_APP);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(
                UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));

        getContentPane().add(
                UIKit.screenHeader("Configuración ERP", "Administración  ›  Configuración"),
                BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new GridLayout(1, 2, UIKit.SPACE_LG, 0));
        cuerpo.setOpaque(false);

        // ── Panel 1: Datos de la Empresa ──
        JPanel pnlEmpresa = UIKit.card();
        pnlEmpresa.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlEmpresa.add(UIKit.sectionHeader("Datos del Establecimiento", null), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlEmpresa.add(UIKit.fieldLabel("Razón Social"), gbc);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlEmpresa.add(txtRazonSocial, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlEmpresa.add(UIKit.fieldLabel("RUC"), gbc);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlEmpresa.add(txtRuc, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlEmpresa.add(UIKit.fieldLabel("Dirección"), gbc);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlEmpresa.add(txtDireccion, gbc);

        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlEmpresa.add(UIKit.fieldLabel("Teléfono"), gbc);
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlEmpresa.add(txtTelefono, gbc);

        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlEmpresa.add(UIKit.fieldLabel("Correo"), gbc);
        gbc.gridy = 10;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlEmpresa.add(txtCorreo, gbc);

        // IGV y Stock Mínimo
        gbc.gridwidth = 1;
        gbc.gridy = 11;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, UIKit.SPACE_SM);
        pnlEmpresa.add(UIKit.fieldLabel("IGV (%)"), gbc);

        gbc.gridy = 12;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 0, UIKit.SPACE_SM);
        pnlEmpresa.add(txtIgv, gbc);

        cuerpo.add(pnlEmpresa);

        // ── Panel 2: Configuración Sistema + MP ──
        JPanel pnlSistema = UIKit.card();
        pnlSistema.setLayout(new GridBagLayout());
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        gbc2.weightx = 1.0;
        gbc2.gridx = 0;

        gbc2.gridy = 0;
        gbc2.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlSistema.add(UIKit.sectionHeader("Configuración del Sistema", null), gbc2);

        gbc2.gridy = 1;
        gbc2.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlSistema.add(UIKit.fieldLabel("Stock Mínimo por Defecto"), gbc2);
        gbc2.gridy = 2;
        gbc2.insets = new Insets(0, 0, UIKit.SPACE_LG, 0);
        pnlSistema.add(txtStockMinimo, gbc2);

        gbc2.gridy = 3;
        gbc2.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlSistema.add(UIKit.sectionHeader("Integración Mercado Pago", null), gbc2);

        gbc2.gridy = 4;
        gbc2.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlSistema.add(UIKit.fieldLabel("Access Token"), gbc2);
        gbc2.gridy = 5;
        gbc2.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        JPanel pnlToken = new JPanel(new BorderLayout(4, 0));
        pnlToken.setOpaque(false);
        pnlToken.add(txtMpToken, BorderLayout.CENTER);
        pnlToken.add(btnOjoToken, BorderLayout.EAST);
        pnlSistema.add(pnlToken, gbc2);

        gbc2.gridy = 6;
        gbc2.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlSistema.add(UIKit.fieldLabel("Public Key"), gbc2);
        gbc2.gridy = 7;
        gbc2.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlSistema.add(txtMpPublicKey, gbc2);

        gbc2.gridy = 8;
        gbc2.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlSistema.add(UIKit.fieldLabel("Client ID"), gbc2);
        gbc2.gridy = 9;
        gbc2.weighty = 1.0;
        gbc2.anchor = GridBagConstraints.NORTH;
        gbc2.insets = new Insets(0, 0, 0, 0);
        pnlSistema.add(txtMpClientId, gbc2);

        cuerpo.add(pnlSistema);

        // Botones
        JPanel pnlSur = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIKit.SPACE_SM, 0));
        pnlSur.setOpaque(false);
        pnlSur.setBorder(new EmptyBorder(UIKit.SPACE_MD, 0, 0, 0));
        pnlSur.add(btnLimpiar);
        pnlSur.add(btnGuardar);

        getContentPane().add(cuerpo, BorderLayout.CENTER);
        getContentPane().add(pnlSur, BorderLayout.SOUTH);
    }

    private void attachEvents() {

        // GUARDAR
        btnGuardar.addActionListener(e -> guardarConfiguracion());

        // LIMPIAR
        btnLimpiar.addActionListener(e -> {
            txtRazonSocial.setText("");
            txtRuc.setText("");
            txtDireccion.setText("");
            txtTelefono.setText("");
            txtCorreo.setText("");
            txtIgv.setText("18");
            txtStockMinimo.setText("10");
            txtMpToken.setText("");
            txtMpPublicKey.setText("");
            txtMpClientId.setText("");
        });

        // VER/OCULTAR TOKEN
        btnOjoToken.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                txtMpToken.setEchoChar((char) 0);
                btnOjoToken.setText("Ocultar");
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                txtMpToken.setEchoChar('•');
                btnOjoToken.setText("Ver");
            }
        });
    }

    private void guardarConfiguracion() {
        try (Connection con = Conexion.getConexion()) {
            // Verificar si ya existe un registro
            PreparedStatement check = con.prepareStatement(
                    "SELECT COUNT(*) FROM Configuracion");
            ResultSet rs = check.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            String sql;
            if (count == 0) {
                sql = "INSERT INTO Configuracion (razonSocial, ruc, direccion, "
                        + "telefono, correo, igvPorcentaje, mpToken, mpPublicKey, mpClientId) "
                        + "VALUES (?,?,?,?,?,?,?,?,?)";
            } else {
                sql = "UPDATE Configuracion SET razonSocial=?, ruc=?, direccion=?, "
                        + "telefono=?, correo=?, igvPorcentaje=?, mpToken=?, mpPublicKey=?, "
                        + "mpClientId=? WHERE idConfig=1";
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, txtRazonSocial.getText().trim());
                ps.setString(2, txtRuc.getText().trim());
                ps.setString(3, txtDireccion.getText().trim());
                ps.setString(4, txtTelefono.getText().trim());
                ps.setString(5, txtCorreo.getText().trim());
                ps.setDouble(6, Double.parseDouble(txtIgv.getText().trim().replace(",", ".")));
                ps.setString(7, new String(txtMpToken.getPassword()).trim());
                ps.setString(8, txtMpPublicKey.getText().trim());
                ps.setString(9, txtMpClientId.getText().trim());
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "✅ Configuración guardada correctamente");
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar configuración");
        }
    }

    private void cargarConfiguracion() {
        try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM Configuracion LIMIT 1"); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                txtRazonSocial.setText(rs.getString("razonSocial") != null ? rs.getString("razonSocial") : "");
                txtRuc.setText(rs.getString("ruc") != null ? rs.getString("ruc") : "");
                txtDireccion.setText(rs.getString("direccion") != null ? rs.getString("direccion") : "");
                txtTelefono.setText(rs.getString("telefono") != null ? rs.getString("telefono") : "");
                txtCorreo.setText(rs.getString("correo") != null ? rs.getString("correo") : "");
                txtIgv.setText(String.valueOf(rs.getDouble("igvPorcentaje")));
                txtMpToken.setText(rs.getString("mpToken") != null ? rs.getString("mpToken") : "");
                txtMpPublicKey.setText(rs.getString("mpPublicKey") != null ? rs.getString("mpPublicKey") : "");
                txtMpClientId.setText(rs.getString("mpClientId") != null ? rs.getString("mpClientId") : "");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
