package Vista;

import DAO.EmpleadoDAO;
import DAO.EmpleadoDAO.Empleado;
import DAO.PlanillaDAO;
import Vista.Estilos.UIKit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class IFrmMarcadorAsistencia extends JInternalFrame {

    private JComboBox<String> cbEmpleado;
    private JLabel lblHora;
    private JLabel lblFecha;
    private JLabel lblEstado;
    private JButton btnEntrada;
    private JButton btnSalida;

    private List<Empleado> listaEmpleados;
    private Timer timerReloj;

    public IFrmMarcadorAsistencia() {
        super("Marcador de Asistencia", true, true, true, true);
        initComponents();
        buildLayout();
        attachEvents();
        setSize(500, 420);
        iniciarReloj();
    }

    private void initComponents() {
        cbEmpleado = new JComboBox<>();
        cbEmpleado.setFont(UIKit.H2);
        cbEmpleado.setPreferredSize(new Dimension(0, 48));
        cargarEmpleados();

        lblHora = new JLabel(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        lblHora.setFont(new Font("Segoe UI", Font.BOLD, 56));
        lblHora.setForeground(UIKit.ACCENT);
        lblHora.setHorizontalAlignment(SwingConstants.CENTER);

        lblFecha = new JLabel(LocalDate.now().format(
            DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy",
            new java.util.Locale("es", "PE"))));
        lblFecha.setFont(UIKit.SUBTITLE);
        lblFecha.setForeground(UIKit.TEXT_SECONDARY);
        lblFecha.setHorizontalAlignment(SwingConstants.CENTER);

        lblEstado = new JLabel(" ");
        lblEstado.setFont(UIKit.BODY_BOLD);
        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);

        btnEntrada = new JButton("✔  MARCAR ENTRADA");
        btnEntrada.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnEntrada.setBackground(new Color(46, 125, 50));
        btnEntrada.setForeground(Color.WHITE);
        btnEntrada.setFocusPainted(false);
        btnEntrada.setBorderPainted(false);
        btnEntrada.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEntrada.setPreferredSize(new Dimension(0, 56));

        btnSalida = new JButton("✖  MARCAR SALIDA");
        btnSalida.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSalida.setBackground(new Color(198, 40, 40));
        btnSalida.setForeground(Color.WHITE);
        btnSalida.setFocusPainted(false);
        btnSalida.setBorderPainted(false);
        btnSalida.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalida.setPreferredSize(new Dimension(0, 56));
    }

    private void cargarEmpleados() {
        cbEmpleado.removeAllItems();
        cbEmpleado.addItem("-- Seleccione su nombre --");
        EmpleadoDAO dao = new EmpleadoDAO();
        listaEmpleados = dao.listar();
        for (Empleado emp : listaEmpleados) {
            if (emp.estado == 1) {
                cbEmpleado.addItem(emp.nombres + " " + emp.apellidos);
            }
        }
    }

    private void buildLayout() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UIKit.BG_APP);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(
                UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));

        getContentPane().add(
                UIKit.screenHeader("Marcador de Asistencia", "Personal  ›  Asistencia"),
                BorderLayout.NORTH);

        JPanel cuerpo = UIKit.card();
        cuerpo.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx   = 0;

        // Reloj
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        cuerpo.add(lblHora, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, UIKit.SPACE_LG, 0);
        cuerpo.add(lblFecha, gbc);

        // Selector empleado
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        cuerpo.add(UIKit.fieldLabel("¿Quién eres?"), gbc);
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, UIKit.SPACE_LG, 0);
        cuerpo.add(cbEmpleado, gbc);

        // Botones
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, UIKit.SPACE_SM, 0);
        cuerpo.add(btnEntrada, gbc);

        gbc.gridy = 5; gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        cuerpo.add(btnSalida, gbc);

        // Estado
        gbc.gridy = 6; gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        cuerpo.add(lblEstado, gbc);

        getContentPane().add(cuerpo, BorderLayout.CENTER);
    }

    private void attachEvents() {

        btnEntrada.addActionListener(e -> marcarAsistencia("entrada"));
        btnSalida.addActionListener(e -> marcarAsistencia("salida"));
    }

    private void marcarAsistencia(String tipo) {
        if (cbEmpleado.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Selecciona tu nombre primero");
            return;
        }

        int idEmpleado = listaEmpleados.stream()
            .filter(emp -> emp.estado == 1)
            .toList()
            .get(cbEmpleado.getSelectedIndex() - 1).idEmpleado;

        String horaActual = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String fechaActual = LocalDate.now().toString();
        String nombreEmp  = cbEmpleado.getSelectedItem().toString();

        PlanillaDAO dao = new PlanillaDAO();

        if (tipo.equals("entrada")) {
            if (dao.registrarAsistencia(idEmpleado, fechaActual, horaActual, "", 0, "Presente")) {
                lblEstado.setText("✅ Entrada registrada a las " + horaActual + " - " + nombreEmp);
                lblEstado.setForeground(new Color(46, 125, 50));
            }
        } else {
            if (dao.registrarAsistencia(idEmpleado, fechaActual, "", horaActual, 0, "Presente")) {
                lblEstado.setText("👋 Salida registrada a las " + horaActual + " - " + nombreEmp);
                lblEstado.setForeground(new Color(198, 40, 40));
            }
        }
    }

    private void iniciarReloj() {
        timerReloj = new Timer();
        timerReloj.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() ->
                    lblHora.setText(LocalTime.now()
                        .format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
            }
        }, 0, 1000);
    }

    @Override
    public void dispose() {
        if (timerReloj != null) timerReloj.cancel();
        super.dispose();
    }
}