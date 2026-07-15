package Vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import Clases.Usuario;
import DAO.UsuarioDAO;
import Vista.Estilos.UIKit;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatClientProperties;
import DAO.BitacoraDAO;
import DAO.PlanillaDAO;
import java.sql.*;
import DAO.CajaChicaDAO;

public class FrmLogin extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;
    private JButton btnSalir;

    private int mouseX, mouseY;

    public FrmLogin() {
        super("ERP Minimarket LAREDO");
        setUndecorated(true);
        initComponents();
        buildLayout();
        attachEvents();
        configFrame();
    }

    private void initComponents() {
        txtUsuario = buildTextField("Ingrese su usuario");
        txtPassword = buildPasswordField("••••••••");
        btnIngresar = UIKit.primaryButton("Ingresar");

        btnSalir = new JButton("Salir del sistema");
        btnSalir.setFont(UIKit.BODY);
        btnSalir.setForeground(UIKit.TEXT_SECONDARY);
        btnSalir.setContentAreaFilled(false);
        btnSalir.setBorderPainted(false);
        btnSalir.setFocusPainted(false);
        btnSalir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void buildLayout() {
        JPanel pnlRaiz = new JPanel(new BorderLayout());

        // ================= WEST (40%) =================
        JPanel pnlLeft = new JPanel(new GridBagLayout());
        pnlLeft.setBackground(UIKit.PRIMARY);
        pnlLeft.setPreferredSize(new Dimension(384, 600));

        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.gridx = 0;
        gbcLeft.gridy = 0;
        gbcLeft.insets = new Insets(0, 0, 20, 0);
        gbcLeft.anchor = GridBagConstraints.CENTER;

        JPanel pnlLogo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(20, 20, 60, 60, 15, 15);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
                g2.drawString("L", 40, 62);
                g2.dispose();
            }
        };
        pnlLogo.setPreferredSize(new Dimension(100, 100));
        pnlLogo.setOpaque(false);
        pnlLeft.add(pnlLogo, gbcLeft);

        gbcLeft.gridy = 1;
        gbcLeft.insets = new Insets(0, 0, 10, 0);
        JLabel lblBrand = new JLabel("Minimarket LAREDO");
        lblBrand.setFont(UIKit.H1);
        lblBrand.setForeground(Color.WHITE);
        pnlLeft.add(lblBrand, gbcLeft);

        gbcLeft.gridy = 2;
        JLabel lblSlogan = new JLabel("Sistema de Gestión Empresarial");
        lblSlogan.setFont(UIKit.SUBTITLE);
        lblSlogan.setForeground(new Color(255, 255, 255, 180));
        pnlLeft.add(lblSlogan, gbcLeft);

        pnlRaiz.add(pnlLeft, BorderLayout.WEST);

        // ================= EAST (60%) =================
        JPanel pnlRight = new JPanel(new GridBagLayout());
        pnlRight.setBackground(UIKit.BG_APP);

        JPanel pnlCard = UIKit.card();
        pnlCard.setPreferredSize(new Dimension(360, 420));
        pnlCard.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 30, 0);
        JLabel lblTitulo = new JLabel("Bienvenido", SwingConstants.CENTER);
        lblTitulo.setFont(UIKit.H2);
        lblTitulo.setForeground(UIKit.TEXT_PRIMARY);
        pnlCard.add(lblTitulo, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        pnlCard.add(UIKit.fieldLabel("Usuario"), gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 16, 0);
        pnlCard.add(txtUsuario, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 5, 0);
        pnlCard.add(UIKit.fieldLabel("Contraseña"), gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 30, 0);
        pnlCard.add(txtPassword, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 20, 0);
        pnlCard.add(btnIngresar, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 20, 0);
        pnlCard.add(btnSalir, gbc);

        gbc.gridy = 7;
        gbc.insets = new Insets(10, 0, 0, 0);
        JLabel lblVersion = new JLabel("ERP v1.0.0 · Minimarket LAREDO © 2025", SwingConstants.CENTER);
        lblVersion.setFont(UIKit.CAPTION);
        lblVersion.setForeground(UIKit.TEXT_SECONDARY);
        pnlCard.add(lblVersion, gbc);

        pnlRight.add(pnlCard, new GridBagConstraints());
        pnlRaiz.add(pnlRight, BorderLayout.CENTER);

        setContentPane(pnlRaiz);
    }

    private void attachEvents() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getXOnScreen();
                int y = e.getYOnScreen();
                setLocation(x - mouseX, y - mouseY);
            }
        });

        btnIngresar.addActionListener(e -> {
            String user = txtUsuario.getText().trim();
            String pass = new String(txtPassword.getPassword()).trim();

            // Validar campos vacíos antes de tocar la BD
            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Ingrese su usuario y contraseña.",
                    "Campos requeridos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            btnIngresar.setEnabled(false);
            btnIngresar.setText("Ingresando...");

            SwingUtilities.invokeLater(() -> {
                try {
                    UsuarioDAO dao = new UsuarioDAO();
                    Usuario u = dao.login(user, pass);

                    if (u != null) {
                        Clases.Sesion.setRol(u.getRol());
                        Clases.Sesion.setUsuario(u.getUsuario());
                        Clases.Sesion.setIdUsuario(u.getIdUsuario());

                        // Obtener idEmpleado desde la BD
                        try (Connection con = Conexion.Conexion.getConexion();
                             PreparedStatement ps = con.prepareStatement(
                                 "SELECT idEmpleado FROM usuario WHERE idUsuario=?")) {
                            ps.setInt(1, u.getIdUsuario());
                            ResultSet rs = ps.executeQuery();
                            if (rs.next()) Clases.Sesion.setIdEmpleado(rs.getInt("idEmpleado"));
                        } catch (Exception ex) { ex.printStackTrace(); }

                        BitacoraDAO.registrar(u.getIdUsuario(), "LOGIN", "SISTEMA",
                                "Usuario " + u.getUsuario() + " inició sesión - Rol: " + u.getRol());

                        JOptionPane.showMessageDialog(this, "Bienvenido " + u.getNombre() + " - " + u.getRol());

                        // Si es Vendedor, mostrar diálogo de asistencia y apertura de caja
                        if (u.getRol().equalsIgnoreCase("Vendedor")) {
                            mostrarDialogoAsistencia(u.getNombre());
                            mostrarDialogoAperturaCaja(u.getNombre());
                        }

                        new FrmDashboard().setVisible(true);
                        this.dispose();

                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Usuario o contraseña incorrectos.",
                            "Error de acceso", JOptionPane.ERROR_MESSAGE);
                        btnIngresar.setEnabled(true);
                        btnIngresar.setText("Ingresar");
                    }

                } catch (UsuarioDAO.CuentaBloqueadaException bloqueada) {
                    // Cuenta bloqueada por intentos fallidos — mismo comportamiento que minimarket
                    JOptionPane.showMessageDialog(this,
                        "Cuenta bloqueada temporalmente.\n"
                        + "Demasiados intentos fallidos. Intente en "
                        + bloqueada.getMinutosRestantes() + " minuto(s).",
                        "Cuenta Bloqueada", JOptionPane.ERROR_MESSAGE);
                    btnIngresar.setEnabled(true);
                    btnIngresar.setText("Ingresar");
                }
            });
        });


        btnSalir.addActionListener(e -> System.exit(0));

        txtUsuario.addActionListener(e -> btnIngresar.doClick());
        txtPassword.addActionListener(e -> btnIngresar.doClick());
    }

    private void configFrame() {
        setSize(960, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JTextField buildTextField(String hint) {
        JTextField tf = UIKit.textField();
        tf.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, hint);
        return tf;
    }

    private JPasswordField buildPasswordField(String hint) {
        JPasswordField pf = new JPasswordField();
        pf.setFont(UIKit.BODY);
        pf.putClientProperty(FlatClientProperties.STYLE,
                "arc: 8; borderColor: " + String.format("#%02x%02x%02x", UIKit.BORDER.getRed(), UIKit.BORDER.getGreen(), UIKit.BORDER.getBlue())
                + "; focusedBorderColor: " + String.format("#%02x%02x%02x", UIKit.ACCENT.getRed(), UIKit.ACCENT.getGreen(), UIKit.ACCENT.getBlue()) + ";");
        pf.setPreferredSize(new Dimension(0, 36));
        pf.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, hint);
        return pf;
    }

    public JTextField getTxtUsuario() { return txtUsuario; }
    public JPasswordField getTxtPassword() { return txtPassword; }
    public JButton getBtnIngresar() { return btnIngresar; }
    public JButton getBtnSalir() { return btnSalir; }

    private void mostrarDialogoAsistencia(String nombreEmpleado) {
        int idEmpleado = Clases.Sesion.getIdEmpleado();
        String fechaHoy = java.time.LocalDate.now().toString();

        PlanillaDAO planillaDAO = new PlanillaDAO();
        if (planillaDAO.yaMarcoEntrada(idEmpleado, fechaHoy)) {
            return;
        }

        JDialog dlg = new JDialog(this, "Marcar Asistencia", true);
        dlg.setSize(420, 280);
        dlg.setLocationRelativeTo(this);
        dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dlg.setResizable(false);
        dlg.getContentPane().setBackground(Color.WHITE);
        dlg.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.insets = new Insets(8, 30, 8, 30);

        JLabel lblHora = new JLabel();
        lblHora.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lblHora.setForeground(new Color(0x1B3B6F));
        lblHora.setHorizontalAlignment(SwingConstants.CENTER);

        java.util.Timer timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override public void run() {
                SwingUtilities.invokeLater(() ->
                    lblHora.setText(java.time.LocalTime.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))));
            }
        }, 0, 1000);

        gbc.gridy = 0;
        dlg.add(lblHora, gbc);

        JLabel lblSaludo = new JLabel("Hola, " + nombreEmpleado);
        lblSaludo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSaludo.setForeground(new Color(0x666666));
        lblSaludo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        dlg.add(lblSaludo, gbc);

        JLabel lblMsg = new JLabel("Debes marcar tu entrada para continuar");
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMsg.setForeground(new Color(0x999999));
        lblMsg.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        dlg.add(lblMsg, gbc);

        JButton btnMarcar = new JButton("✔  MARCAR ENTRADA");
        btnMarcar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnMarcar.setBackground(new Color(46, 125, 50));
        btnMarcar.setForeground(Color.WHITE);
        btnMarcar.setFocusPainted(false);
        btnMarcar.setBorderPainted(false);
        btnMarcar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMarcar.setPreferredSize(new Dimension(0, 50));
        gbc.gridy = 3;
        gbc.insets = new Insets(16, 30, 8, 30);
        dlg.add(btnMarcar, gbc);

        btnMarcar.addActionListener(ev -> {
            String horaActual = java.time.LocalTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));

            if (planillaDAO.marcarEntrada(idEmpleado, fechaHoy, horaActual)) {
                timer.cancel();
                JOptionPane.showMessageDialog(dlg,
                    "Entrada registrada a las " + horaActual,
                    "Asistencia", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
            } else {
                JOptionPane.showMessageDialog(dlg,
                    "Error al registrar entrada", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.setVisible(true);
    }
    private void mostrarDialogoAperturaCaja(String nombreEmpleado) {
        int idEmpleado = Clases.Sesion.getIdEmpleado();

        CajaChicaDAO cajaDAO = new CajaChicaDAO();
        if (cajaDAO.tieneCajaAbierta(idEmpleado)) {
            return; // Ya tiene caja abierta hoy
        }

        JDialog dlg = new JDialog(this, "Apertura de Caja", true);
        dlg.setSize(420, 300);
        dlg.setLocationRelativeTo(this);
        dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dlg.setResizable(false);
        dlg.getContentPane().setBackground(Color.WHITE);
        dlg.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.insets = new Insets(8, 30, 8, 30);

        // Título
        JLabel lblTitulo = new JLabel("Apertura de Caja");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(0x1B3B6F));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        dlg.add(lblTitulo, gbc);

        // Saludo
        JLabel lblSaludo = new JLabel("Hola, " + nombreEmpleado);
        lblSaludo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSaludo.setForeground(new Color(0x666666));
        lblSaludo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        dlg.add(lblSaludo, gbc);

        // Label monto
        JLabel lblMonto = new JLabel("¿Con cuánto inicia la caja? (S/)");
        lblMonto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMonto.setForeground(new Color(0x444444));
        gbc.gridy = 2;
        gbc.insets = new Insets(16, 30, 4, 30);
        dlg.add(lblMonto, gbc);

        // Campo monto
        JTextField txtMonto = new JTextField();
        txtMonto.setFont(new Font("Segoe UI", Font.BOLD, 24));
        txtMonto.setHorizontalAlignment(JTextField.CENTER);
        txtMonto.setPreferredSize(new Dimension(0, 50));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 30, 8, 30);
        dlg.add(txtMonto, gbc);

        // Botón
        JButton btnAbrir = new JButton("ABRIR CAJA");
        btnAbrir.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAbrir.setBackground(new Color(25, 118, 210));
        btnAbrir.setForeground(Color.WHITE);
        btnAbrir.setFocusPainted(false);
        btnAbrir.setBorderPainted(false);
        btnAbrir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAbrir.setPreferredSize(new Dimension(0, 50));
        gbc.gridy = 4;
        gbc.insets = new Insets(12, 30, 8, 30);
        dlg.add(btnAbrir, gbc);

        btnAbrir.addActionListener(ev -> {
            String montoStr = txtMonto.getText().trim().replace(",", ".");
            if (montoStr.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Ingrese el monto de apertura");
                return;
            }
            try {
                double monto = Double.parseDouble(montoStr);
                if (cajaDAO.abrirCaja(idEmpleado, monto)) {
                    JOptionPane.showMessageDialog(dlg,
                        "Caja abierta con S/ " + String.format("%.2f", monto),
                        "Apertura de Caja", JOptionPane.INFORMATION_MESSAGE);
                    dlg.dispose();
                } else {
                    JOptionPane.showMessageDialog(dlg, "Error al abrir caja");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, "Ingrese un monto válido");
            }
        });

        dlg.setVisible(true);
    }
    public static void main(String[] args) {
        com.formdev.flatlaf.FlatLaf.setGlobalExtraDefaults(
            java.util.Map.of("@accentColor", "#6366F1"));
        FlatLightLaf.setup();

        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("CheckBox.arc", 4);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width", 10);
        UIManager.put("TabbedPane.showTabSeparators", true);
        UIManager.put("Table.showHorizontalLines", false);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.rowHeight", 34);
        UIManager.put("defaultFont", UIKit.BODY);

        SwingUtilities.invokeLater(() -> new FrmLogin().setVisible(true));
    }
}