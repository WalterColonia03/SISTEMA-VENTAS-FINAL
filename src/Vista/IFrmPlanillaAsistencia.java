package Vista;

import DAO.EmpleadoDAO;
import DAO.EmpleadoDAO.Empleado;
import DAO.PlanillaDAO;
import Vista.Estilos.UIKit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public class IFrmPlanillaAsistencia extends JInternalFrame {

    private JComboBox<String> cbEmpleado;
    private JComboBox<String> cbMes;
    private JTextField txtAnio;

    private JTable tblAsistencia;
    private DefaultTableModel modelAsistencia;

    // Registro de asistencia diaria
    private JComboBox<String> cbEstadoDia;
    private JTextField txtFechaDia;
    private JTextField txtHoraEntrada;
    private JTextField txtHoraSalida;
    private JButton btnRegistrarDia;

    // Cálculo de planilla
    private JTextField txtDiasTrabajados;
    private JTextField txtFaltas;
    private JTextField txtDiasVacaciones;
    private JTextField txtSalarioBase;
    private JTextField txtBonificacion;
    private JTextField txtDescuento;
    private JTextField txtPagoFinal;

    private JButton btnCargar;
    private JButton btnCalcular;
    private JButton btnGenerarPlanilla;

    private List<Empleado> listaEmpleados;

    public IFrmPlanillaAsistencia() {
        super("Planilla y Control de Asistencia", true, true, true, true);
        initComponents();
        buildLayout();
        attachEvents();
        setSize(1100, 650);
    }

    private void initComponents() {
        cbEmpleado = new JComboBox<>();
        cbEmpleado.setFont(UIKit.BODY);
        cbEmpleado.setPreferredSize(new Dimension(220, 36));
        cargarEmpleados();

        cbMes = new JComboBox<>(new String[]{
            "01 - Enero", "02 - Febrero", "03 - Marzo", "04 - Abril",
            "05 - Mayo", "06 - Junio", "07 - Julio", "08 - Agosto",
            "09 - Septiembre", "10 - Octubre", "11 - Noviembre", "12 - Diciembre"
        });
        cbMes.setFont(UIKit.BODY);
        cbMes.setPreferredSize(new Dimension(150, 36));
        cbMes.setSelectedIndex(LocalDate.now().getMonthValue() - 1);

        txtAnio = UIKit.textField();
        txtAnio.setPreferredSize(new Dimension(80, 36));
        txtAnio.setText(String.valueOf(LocalDate.now().getYear()));

        String[] columns = {"Fecha", "Hora Entrada", "Hora Salida", "Horas", "Estado"};
        modelAsistencia = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tblAsistencia = UIKit.styledTable(modelAsistencia);
        
        tblAsistencia.getColumnModel().getColumn(0).setPreferredWidth(120); // Fecha
        tblAsistencia.getColumnModel().getColumn(1).setPreferredWidth(100); // Hora Entrada
        tblAsistencia.getColumnModel().getColumn(2).setPreferredWidth(100); // Hora Salida
        tblAsistencia.getColumnModel().getColumn(3).setPreferredWidth(60);  // Horas
        tblAsistencia.getColumnModel().getColumn(4).setPreferredWidth(100); // Estado

        // Registro diario
        txtFechaDia = UIKit.textField();
        txtFechaDia.setText(LocalDate.now().toString());
        txtFechaDia.setPreferredSize(new Dimension(120, 36));

        txtHoraEntrada = UIKit.textField();
        txtHoraEntrada.setPreferredSize(new Dimension(80, 36));
        txtHoraEntrada.setText("08:00");

        txtHoraSalida = UIKit.textField();
        txtHoraSalida.setPreferredSize(new Dimension(80, 36));
        txtHoraSalida.setText("17:00");

        cbEstadoDia = new JComboBox<>(new String[]{"Presente", "Ausente", "Tardanza", "Permiso", "Vacaciones"});
        cbEstadoDia.setFont(UIKit.BODY);
        cbEstadoDia.setPreferredSize(new Dimension(120, 36));

        btnRegistrarDia = UIKit.secondaryButton("Registrar Día");
        btnCargar       = UIKit.secondaryButton("Cargar Asistencia");

        // Planilla
        txtDiasTrabajados = UIKit.readOnlyField();
        txtDiasTrabajados.setEditable(false);
        txtFaltas         = UIKit.readOnlyField();
        txtFaltas.setEditable(false);
        txtDiasVacaciones = UIKit.readOnlyField();
        txtDiasVacaciones.setEditable(false);
        txtSalarioBase    = UIKit.textField();
        txtSalarioBase.setHorizontalAlignment(JTextField.RIGHT);
        txtBonificacion   = UIKit.textField();
        txtBonificacion.setHorizontalAlignment(JTextField.RIGHT);
        txtBonificacion.setText("0.00");
        txtDescuento      = UIKit.textField();
        txtDescuento.setHorizontalAlignment(JTextField.RIGHT);
        txtDescuento.setText("0.00");
        txtPagoFinal      = UIKit.readOnlyField();
        txtPagoFinal.setEditable(false);
        txtPagoFinal.setFont(UIKit.H1);
        txtPagoFinal.setForeground(UIKit.ACCENT);

        btnCalcular       = UIKit.secondaryButton("Calcular Pago");
        btnGenerarPlanilla = UIKit.primaryButton("Guardar Planilla del Mes");
        btnGenerarPlanilla.setPreferredSize(new Dimension(0, 44));
    }

    private void cargarEmpleados() {
        cbEmpleado.removeAllItems();
        cbEmpleado.addItem("-- Seleccione empleado --");
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
                UIKit.screenHeader("Planilla y Asistencia", "Personal  ›  Planilla y Asistencia"),
                BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(UIKit.SPACE_LG, 0));
        cuerpo.setOpaque(false);

        // ── Izquierda: Selector + Tabla ──
        JPanel pnlIzquierda = new JPanel(new BorderLayout(0, UIKit.SPACE_MD));
        pnlIzquierda.setOpaque(false);

        // Selector
        JPanel pnlSelector = UIKit.card();
        pnlSelector.setLayout(new FlowLayout(FlowLayout.LEFT, UIKit.SPACE_SM, 0));
        pnlSelector.add(UIKit.fieldLabel("Empleado:"));
        pnlSelector.add(cbEmpleado);
        pnlSelector.add(UIKit.fieldLabel("Mes:"));
        pnlSelector.add(cbMes);
        pnlSelector.add(UIKit.fieldLabel("Año:"));
        pnlSelector.add(txtAnio);
        pnlSelector.add(btnCargar);
        pnlIzquierda.add(pnlSelector, BorderLayout.NORTH);

        // Tabla asistencia
        JPanel pnlTabla = UIKit.card();
        pnlTabla.setLayout(new BorderLayout(0, UIKit.SPACE_SM));

        // Registro diario
        JPanel pnlRegistro = new JPanel(new FlowLayout(FlowLayout.LEFT, UIKit.SPACE_SM, 0));
        pnlRegistro.setOpaque(false);
        pnlRegistro.add(UIKit.fieldLabel("Fecha:"));
        pnlRegistro.add(txtFechaDia);
        pnlRegistro.add(UIKit.fieldLabel("Entrada:"));
        pnlRegistro.add(txtHoraEntrada);
        pnlRegistro.add(UIKit.fieldLabel("Salida:"));
        pnlRegistro.add(txtHoraSalida);
        pnlRegistro.add(UIKit.fieldLabel("Estado:"));
        pnlRegistro.add(cbEstadoDia);
        pnlRegistro.add(btnRegistrarDia);

        JPanel pnlTopTabla = new JPanel(new BorderLayout(0, UIKit.SPACE_SM));
        pnlTopTabla.setOpaque(false);
        pnlTopTabla.add(UIKit.sectionHeader("Registro de Asistencia", null), BorderLayout.NORTH);
        pnlTopTabla.add(pnlRegistro, BorderLayout.SOUTH);

        pnlTabla.add(pnlTopTabla, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tblAsistencia);
        scroll.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlTabla.add(scroll, BorderLayout.CENTER);

        pnlIzquierda.add(pnlTabla, BorderLayout.CENTER);
        cuerpo.add(pnlIzquierda, BorderLayout.CENTER);

        // ── Derecha: Planilla ──
        JPanel pnlPlanilla = UIKit.card();
        pnlPlanilla.setPreferredSize(new Dimension(280, 0));
        pnlPlanilla.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx   = 0;

        gbc.gridy = 0; gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlPlanilla.add(UIKit.sectionHeader("Cálculo de Planilla", null), gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlPlanilla.add(UIKit.fieldLabel("Días Trabajados"), gbc);
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, UIKit.SPACE_SM, 0);
        pnlPlanilla.add(txtDiasTrabajados, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlPlanilla.add(UIKit.fieldLabel("Faltas"), gbc);
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, UIKit.SPACE_SM, 0);
        pnlPlanilla.add(txtFaltas, gbc);

        gbc.gridy = 5; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlPlanilla.add(UIKit.fieldLabel("Días Vacaciones"), gbc);
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlPlanilla.add(txtDiasVacaciones, gbc);

        gbc.gridy = 7; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlPlanilla.add(UIKit.fieldLabel("Salario Base (S/)"), gbc);
        gbc.gridy = 8; gbc.insets = new Insets(0, 0, UIKit.SPACE_SM, 0);
        pnlPlanilla.add(txtSalarioBase, gbc);

        gbc.gridy = 9; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlPlanilla.add(UIKit.fieldLabel("Bonificación (S/)"), gbc);
        gbc.gridy = 10; gbc.insets = new Insets(0, 0, UIKit.SPACE_SM, 0);
        pnlPlanilla.add(txtBonificacion, gbc);

        gbc.gridy = 11; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlPlanilla.add(UIKit.fieldLabel("Descuentos (S/)"), gbc);
        gbc.gridy = 12; gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlPlanilla.add(txtDescuento, gbc);

        gbc.gridy = 13; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        JLabel lblPago = new JLabel("PAGO FINAL");
        lblPago.setFont(UIKit.BODY_BOLD);
        lblPago.setForeground(UIKit.TEXT_SECONDARY);
        pnlPlanilla.add(lblPago, gbc);
        gbc.gridy = 14; gbc.insets = new Insets(0, 0, UIKit.SPACE_LG, 0);
        pnlPlanilla.add(txtPagoFinal, gbc);

        gbc.gridy = 15; gbc.insets = new Insets(0, 0, UIKit.SPACE_SM, 0);
        pnlPlanilla.add(btnCalcular, gbc);

        gbc.gridy = 16; gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        pnlPlanilla.add(btnGenerarPlanilla, gbc);

        cuerpo.add(pnlPlanilla, BorderLayout.EAST);
        getContentPane().add(cuerpo, BorderLayout.CENTER);
    }

    private void attachEvents() {

        // CARGAR ASISTENCIA
        btnCargar.addActionListener(e -> cargarAsistencia());

        // REGISTRAR DÍA
        btnRegistrarDia.addActionListener(e -> {
            if (cbEmpleado.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un empleado");
                return;
            }
            String fecha      = txtFechaDia.getText().trim();
            String entrada    = txtHoraEntrada.getText().trim();
            String salida     = txtHoraSalida.getText().trim();
            String estado     = cbEstadoDia.getSelectedItem().toString();

            // Calcular horas
            double horas = 0;
            try {
                if (!entrada.isEmpty() && !salida.isEmpty()) {
                    String[] e1 = entrada.split(":");
                    String[] s1 = salida.split(":");
                    int minE = Integer.parseInt(e1[0]) * 60 + Integer.parseInt(e1[1]);
                    int minS = Integer.parseInt(s1[0]) * 60 + Integer.parseInt(s1[1]);
                    horas = (minS - minE) / 60.0;
                }
            } catch (Exception ex) { horas = 0; }

            int idEmpleado = listaEmpleados.get(cbEmpleado.getSelectedIndex() - 1).idEmpleado;
            PlanillaDAO dao = new PlanillaDAO();
            if (dao.registrarAsistencia(idEmpleado, fecha, entrada, salida, horas, estado)) {
                JOptionPane.showMessageDialog(this, "Asistencia registrada correctamente");
                cargarAsistencia();
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar asistencia");
            }
        });

        // CALCULAR PAGO
        btnCalcular.addActionListener(e -> {
            try {
                double salario     = Double.parseDouble(txtSalarioBase.getText().replace(",", "."));
                double bonif       = Double.parseDouble(txtBonificacion.getText().replace(",", "."));
                double desc        = Double.parseDouble(txtDescuento.getText().replace(",", "."));
                double pagoFinal   = salario + bonif - desc;
                txtPagoFinal.setText(String.format("S/ %.2f", pagoFinal));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese valores numéricos válidos");
            }
        });

        // GUARDAR PLANILLA
        btnGenerarPlanilla.addActionListener(e -> {
            if (cbEmpleado.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un empleado");
                return;
            }
            if (txtSalarioBase.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese el salario base");
                return;
            }

            try {
                int idEmpleado    = listaEmpleados.get(cbEmpleado.getSelectedIndex() - 1).idEmpleado;
                int mes           = cbMes.getSelectedIndex() + 1;
                int anio          = Integer.parseInt(txtAnio.getText().trim());
                int diasTrab      = txtDiasTrabajados.getText().isEmpty() ? 0 : Integer.parseInt(txtDiasTrabajados.getText());
                int faltas        = txtFaltas.getText().isEmpty() ? 0 : Integer.parseInt(txtFaltas.getText());
                int diasVac       = txtDiasVacaciones.getText().isEmpty() ? 0 : Integer.parseInt(txtDiasVacaciones.getText());
                double salario    = Double.parseDouble(txtSalarioBase.getText().replace(",", "."));
                double bonif      = Double.parseDouble(txtBonificacion.getText().replace(",", "."));
                double desc       = Double.parseDouble(txtDescuento.getText().replace(",", "."));
                double pagoFinal  = salario + bonif - desc;

                PlanillaDAO dao = new PlanillaDAO();
                if (dao.guardarPlanilla(idEmpleado, mes, anio, diasTrab, faltas,
                        diasVac, salario, bonif, desc, pagoFinal, 1)) {
                    JOptionPane.showMessageDialog(this,
                        "✅ Planilla guardada correctamente\nPago Final: S/ " +
                        String.format("%.2f", pagoFinal));
                } else {
                    JOptionPane.showMessageDialog(this, "Error al guardar planilla");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Verifique los valores ingresados");
            }
        });
    }

    private void cargarAsistencia() {
        if (cbEmpleado.getSelectedIndex() == 0) return;

        int idEmpleado = listaEmpleados.get(cbEmpleado.getSelectedIndex() - 1).idEmpleado;
        int mes        = cbMes.getSelectedIndex() + 1;
        int anio       = Integer.parseInt(txtAnio.getText().trim());

        modelAsistencia.setRowCount(0);
        PlanillaDAO dao = new PlanillaDAO();
        List<Object[]> lista = dao.listarAsistencia(idEmpleado, mes, anio);
        for (Object[] row : lista) {
            modelAsistencia.addRow(row);
        }

        // Actualizar contadores
        int[] totales = dao.contarDias(idEmpleado, mes, anio);
        txtDiasTrabajados.setText(String.valueOf(totales[0]));
        txtFaltas.setText(String.valueOf(totales[1]));
        txtDiasVacaciones.setText(String.valueOf(totales[2]));
    }
}