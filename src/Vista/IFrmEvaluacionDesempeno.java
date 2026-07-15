package Vista;

import Clases.Sesion;
import DAO.EvaluacionDAO;
import Vista.Estilos.UIKit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * IFrmEvaluacionDesempeno — Módulo de RRHH para evaluación de desempeño.
 *
 * Cubre el requisito del profesor: "evaluación de desempeño".
 *
 * Criterios de evaluación (escala 0-10):
 *   - Puntualidad (calculada automáticamente desde asistencia)
 *   - Productividad
 *   - Trabajo en equipo
 *   - Actitud
 *   - Cumplimiento de metas
 *
 * La nota promedio se clasifica como:
 *   9-10: Excelente  |  7-8.9: Bueno  |  5-6.9: Regular  |  0-4.9: Deficiente
 */
public class IFrmEvaluacionDesempeno extends JInternalFrame {

    // ── TAB NUEVA EVALUACIÓN ──
    private JComboBox<String> cbEmpleado, cbPeriodo;
    private JLabel lblCargo, lblPuntAutoLabel, lblPuntAuto;
    private JSlider slPuntualidad, slProductividad, slTrabajo, slActitud, slCumplimiento;
    private JLabel lblV1, lblV2, lblV3, lblV4, lblV5;
    private JLabel lblPromedio, lblCalificacion;
    private JTextArea txtComentarios;
    private JButton btnCalcularAuto, btnGuardar, btnLimpiar;

    // ── TAB HISTORIAL ──
    private DefaultTableModel modelHistorial;
    private JComboBox<String> cbFiltroPeriodo;
    private JButton btnFiltrar;

    private final EvaluacionDAO dao = new EvaluacionDAO();
    private List<Object[]> listaEmpleados;

    public IFrmEvaluacionDesempeno() {
        super("Evaluación de Desempeño", true, true, true, true);
        initComponents();
        buildLayout();
        attachEvents();
        setSize(1050, 650);
    }

    private void initComponents() {
        listaEmpleados = dao.listarEmpleados();

        // Combo empleados
        cbEmpleado = new JComboBox<>();
        cbEmpleado.setFont(UIKit.BODY);
        cbEmpleado.addItem("-- Seleccione empleado --");
        for (Object[] e : listaEmpleados)
            cbEmpleado.addItem(e[1].toString());

        // Combo período (últimos 12 meses)
        cbPeriodo = new JComboBox<>();
        cbPeriodo.setFont(UIKit.BODY);
        LocalDate hoy = LocalDate.now();
        for (int i = 0; i < 12; i++) {
            LocalDate mes = hoy.minusMonths(i);
            cbPeriodo.addItem(mes.format(DateTimeFormatter.ofPattern("yyyy-MM")));
        }

        lblCargo = new JLabel("—"); lblCargo.setFont(UIKit.BODY_BOLD); lblCargo.setForeground(UIKit.TEXT_SECONDARY);
        lblPuntAutoLabel = new JLabel("Puntualidad calculada del mes:");
        lblPuntAuto = new JLabel("—"); lblPuntAuto.setFont(UIKit.BODY_BOLD); lblPuntAuto.setForeground(UIKit.ACCENT);

        // Sliders de criterios (0-10)
        slPuntualidad  = buildSlider(); lblV1 = new JLabel("5");
        slProductividad = buildSlider(); lblV2 = new JLabel("5");
        slTrabajo      = buildSlider(); lblV3 = new JLabel("5");
        slActitud      = buildSlider(); lblV4 = new JLabel("5");
        slCumplimiento = buildSlider(); lblV5 = new JLabel("5");

        for (JLabel l : new JLabel[]{lblV1,lblV2,lblV3,lblV4,lblV5}) {
            l.setFont(UIKit.H1); l.setForeground(UIKit.ACCENT);
            l.setHorizontalAlignment(SwingConstants.CENTER);
        }

        lblPromedio = new JLabel("5.0"); lblPromedio.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblPromedio.setHorizontalAlignment(SwingConstants.CENTER);
        lblCalificacion = new JLabel("REGULAR"); lblCalificacion.setFont(UIKit.BODY_BOLD);
        lblCalificacion.setHorizontalAlignment(SwingConstants.CENTER);

        txtComentarios = new JTextArea(3, 20);
        txtComentarios.setFont(UIKit.BODY); txtComentarios.setLineWrap(true);

        btnCalcularAuto = UIKit.secondaryButton("↺ Calcular Puntualidad Auto");
        btnGuardar = UIKit.primaryButton("Guardar Evaluación");
        btnLimpiar = UIKit.secondaryButton("Limpiar");

        // Historial
        String[] cols = {"#", "Empleado", "Cargo", "Período",
                         "Punt.", "Prod.", "Trabajo", "Actitud", "Cumpl.", "Promedio", "Comentarios"};
        modelHistorial = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        cbFiltroPeriodo = new JComboBox<>();
        cbFiltroPeriodo.setFont(UIKit.BODY);
        cbFiltroPeriodo.addItem("Todos los períodos");
        for (int i = 0; i < 12; i++) {
            cbFiltroPeriodo.addItem(hoy.minusMonths(i).format(DateTimeFormatter.ofPattern("yyyy-MM")));
        }
        btnFiltrar = UIKit.secondaryButton("Filtrar");
    }

    private JSlider buildSlider() {
        JSlider s = new JSlider(0, 10, 5);
        s.setMajorTickSpacing(1); s.setPaintTicks(true); s.setPaintLabels(true);
        s.setSnapToTicks(true); s.setOpaque(false);
        return s;
    }

    private void buildLayout() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UIKit.BG_APP);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));
        getContentPane().add(UIKit.screenHeader("Evaluación de Desempeño", "Personal  ›  RRHH  ›  Evaluación de Desempeño"), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane(); tabs.setFont(UIKit.BODY);
        tabs.addTab("Nueva Evaluación", buildTabNueva());
        tabs.addTab("Historial de Evaluaciones", buildTabHistorial());
        tabs.addChangeListener(e -> { if (tabs.getSelectedIndex() == 1) cargarHistorial(""); });
        getContentPane().add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildTabNueva() {
        JPanel tab = new JPanel(new BorderLayout(UIKit.SPACE_LG, 0));
        tab.setOpaque(false); tab.setBorder(new EmptyBorder(UIKit.SPACE_MD, 0, 0, 0));

        // ─── Panel izquierdo: empleado + criterios ───
        JPanel pnlIzq = new JPanel(new BorderLayout(0, UIKit.SPACE_MD)); pnlIzq.setOpaque(false);

        // Empleado y período
        JPanel pnlEmpleado = UIKit.card(); pnlEmpleado.setLayout(new GridLayout(0, 2, UIKit.SPACE_SM, UIKit.SPACE_SM));
        pnlEmpleado.add(UIKit.fieldLabel("Empleado")); pnlEmpleado.add(UIKit.fieldLabel("Período"));
        pnlEmpleado.add(cbEmpleado); pnlEmpleado.add(cbPeriodo);
        pnlEmpleado.add(UIKit.fieldLabel("Cargo")); pnlEmpleado.add(btnCalcularAuto);
        pnlEmpleado.add(lblCargo); pnlEmpleado.add(lblPuntAuto);
        pnlIzq.add(pnlEmpleado, BorderLayout.NORTH);

        // Criterios con sliders
        JPanel pnlCriterios = UIKit.card(); pnlCriterios.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints(); g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1;
        g.insets = new Insets(0, 0, UIKit.SPACE_MD, 0); g.gridx = 0;

        g.gridy = 0; g.gridwidth = 2;
        pnlCriterios.add(UIKit.sectionHeader("Criterios de Evaluación (0 = Deficiente  ▸  10 = Excelente)", null), g);
        g.gridwidth = 1;

        addCriterioRow(pnlCriterios, g, 1, "Puntualidad / Asistencia",   slPuntualidad,  lblV1);
        addCriterioRow(pnlCriterios, g, 3, "Productividad / Rendimiento", slProductividad, lblV2);
        addCriterioRow(pnlCriterios, g, 5, "Trabajo en Equipo",          slTrabajo,       lblV3);
        addCriterioRow(pnlCriterios, g, 7, "Actitud y Conducta",         slActitud,       lblV4);
        addCriterioRow(pnlCriterios, g, 9, "Cumplimiento de Metas",      slCumplimiento,  lblV5);

        g.gridy = 11; g.gridwidth = 2;
        pnlCriterios.add(UIKit.fieldLabel("Comentarios u Observaciones:"), g);
        g.gridy = 12;
        pnlCriterios.add(new JScrollPane(txtComentarios), g);

        pnlIzq.add(pnlCriterios, BorderLayout.CENTER);

        // ─── Panel derecho: resultado ───
        JPanel pnlResumen = UIKit.card(); pnlResumen.setPreferredSize(new Dimension(220, 0));
        pnlResumen.setLayout(new GridBagLayout());
        GridBagConstraints gr = new GridBagConstraints(); gr.fill = GridBagConstraints.HORIZONTAL; gr.weightx = 1; gr.gridx = 0;
        gr.gridy = 0; gr.insets = new Insets(0, 0, UIKit.SPACE_LG, 0);
        pnlResumen.add(UIKit.sectionHeader("Resultado", null), gr);
        gr.gridy = 1;
        JLabel lblPLabel = new JLabel("NOTA PROMEDIO"); lblPLabel.setFont(UIKit.BODY_BOLD); lblPLabel.setForeground(UIKit.TEXT_SECONDARY); lblPLabel.setHorizontalAlignment(SwingConstants.CENTER);
        pnlResumen.add(lblPLabel, gr);
        gr.gridy = 2; gr.insets = new Insets(0, 0, UIKit.SPACE_SM, 0);
        pnlResumen.add(lblPromedio, gr);
        gr.gridy = 3; gr.insets = new Insets(0, 0, UIKit.SPACE_LG, 0);
        pnlResumen.add(lblCalificacion, gr);

        // Escala de referencia
        JPanel pnlEscala = new JPanel(new GridLayout(4, 1, 0, 4)); pnlEscala.setOpaque(false);
        pnlEscala.add(buildBadge("9-10: EXCELENTE",  new Color(46, 125, 50)));
        pnlEscala.add(buildBadge("7-8.9: BUENO",     new Color(25, 118, 210)));
        pnlEscala.add(buildBadge("5-6.9: REGULAR",   new Color(245, 124, 0)));
        pnlEscala.add(buildBadge("0-4.9: DEFICIENTE",new Color(198, 40, 40)));
        gr.gridy = 4; gr.insets = new Insets(0, 0, UIKit.SPACE_LG, 0);
        pnlResumen.add(pnlEscala, gr);

        gr.gridy = 5; gr.insets = new Insets(0, 0, UIKit.SPACE_SM, 0);
        pnlResumen.add(btnGuardar, gr);
        gr.gridy = 6; gr.weighty = 1; gr.anchor = GridBagConstraints.NORTH;
        pnlResumen.add(btnLimpiar, gr);

        tab.add(pnlIzq, BorderLayout.CENTER);
        tab.add(pnlResumen, BorderLayout.EAST);
        return tab;
    }

    private void addCriterioRow(JPanel panel, GridBagConstraints g, int startRow, String label, JSlider slider, JLabel valLabel) {
        g.gridy = startRow; g.gridwidth = 2; g.insets = new Insets(0, 0, 2, 0);
        panel.add(UIKit.fieldLabel(label), g);
        g.gridy = startRow + 1; g.gridwidth = 1; g.insets = new Insets(0, 0, UIKit.SPACE_SM, 0);
        g.weightx = 0.9; panel.add(slider, g);
        g.gridx = 1; g.weightx = 0.1;
        valLabel.setPreferredSize(new Dimension(40, 36));
        panel.add(valLabel, g);
        g.gridx = 0; g.weightx = 1;
    }

    private JLabel buildBadge(String texto, Color color) {
        JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(Color.WHITE);
        lbl.setBackground(color);
        lbl.setOpaque(true);
        lbl.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        return lbl;
    }

    private JPanel buildTabHistorial() {
        JPanel tab = new JPanel(new BorderLayout(0, UIKit.SPACE_MD));
        tab.setOpaque(false); tab.setBorder(new EmptyBorder(UIKit.SPACE_MD, 0, 0, 0));

        JPanel pnlCard = UIKit.card(); pnlCard.setLayout(new BorderLayout(0, UIKit.SPACE_SM));

        JPanel pnlTop = new JPanel(new BorderLayout()); pnlTop.setOpaque(false);
        pnlTop.add(UIKit.sectionHeader("Historial de Evaluaciones", null), BorderLayout.NORTH);
        JPanel pnlFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, UIKit.SPACE_SM, 0)); pnlFiltro.setOpaque(false);
        pnlFiltro.add(new JLabel("Filtrar período:")); pnlFiltro.add(cbFiltroPeriodo); pnlFiltro.add(btnFiltrar);
        pnlTop.add(pnlFiltro, BorderLayout.CENTER);

        JTable tblH = UIKit.styledTable(modelHistorial);
        // Colorear promedio
        tblH.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel && v != null) {
                    try {
                        double p = Double.parseDouble(v.toString().split(" ")[0]);
                        if (p >= 9)        { comp.setForeground(new Color(46,125,50));   ((JLabel)comp).setFont(UIKit.BODY_BOLD); }
                        else if (p >= 7)   { comp.setForeground(new Color(25,118,210));  ((JLabel)comp).setFont(UIKit.BODY_BOLD); }
                        else if (p >= 5)   { comp.setForeground(new Color(245,124,0));   ((JLabel)comp).setFont(UIKit.BODY_BOLD); }
                        else               { comp.setForeground(new Color(198,40,40));   ((JLabel)comp).setFont(UIKit.BODY_BOLD); }
                    } catch (Exception ignored) {}
                }
                return comp;
            }
        });

        pnlCard.add(pnlTop, BorderLayout.NORTH);
        pnlCard.add(new JScrollPane(tblH), BorderLayout.CENTER);
        tab.add(pnlCard, BorderLayout.CENTER);
        return tab;
    }

    private void attachEvents() {
        // Selección de empleado → actualizar cargo
        cbEmpleado.addActionListener(e -> {
            int idx = cbEmpleado.getSelectedIndex() - 1;
            if (idx >= 0 && idx < listaEmpleados.size()) {
                lblCargo.setText(listaEmpleados.get(idx)[2].toString());
            } else {
                lblCargo.setText("—");
            }
        });

        // Sliders → actualizar valor y promedio en tiempo real
        for (JSlider[] pair : new JSlider[][]{{slPuntualidad},{slProductividad},{slTrabajo},{slActitud},{slCumplimiento}}) {
            pair[0].addChangeListener(ce -> actualizarPromedio());
        }
        // Actualizar labels individuales
        slPuntualidad.addChangeListener(e  -> lblV1.setText(String.valueOf(slPuntualidad.getValue())));
        slProductividad.addChangeListener(e -> lblV2.setText(String.valueOf(slProductividad.getValue())));
        slTrabajo.addChangeListener(e      -> lblV3.setText(String.valueOf(slTrabajo.getValue())));
        slActitud.addChangeListener(e      -> lblV4.setText(String.valueOf(slActitud.getValue())));
        slCumplimiento.addChangeListener(e -> lblV5.setText(String.valueOf(slCumplimiento.getValue())));

        // Calcular puntualidad automática desde asistencia
        btnCalcularAuto.addActionListener(e -> {
            int idx = cbEmpleado.getSelectedIndex() - 1;
            if (idx < 0) { JOptionPane.showMessageDialog(this, "Seleccione un empleado primero."); return; }
            int idEmpleado = (int) listaEmpleados.get(idx)[0];
            String periodo = cbPeriodo.getSelectedItem().toString();
            int puntAuto = dao.calcularPuntualidad(idEmpleado, periodo);
            slPuntualidad.setValue(puntAuto);
            lblPuntAuto.setText(puntAuto + " / 10  (calculado de asistencia)");
        });

        btnGuardar.addActionListener(e -> guardarEvaluacion());
        btnLimpiar.addActionListener(e -> limpiar());
        btnFiltrar.addActionListener(e -> {
            String p = cbFiltroPeriodo.getSelectedIndex() == 0 ? "" : cbFiltroPeriodo.getSelectedItem().toString();
            cargarHistorial(p);
        });
    }

    private void actualizarPromedio() {
        double prom = (slPuntualidad.getValue() + slProductividad.getValue() +
                       slTrabajo.getValue() + slActitud.getValue() +
                       slCumplimiento.getValue()) / 5.0;
        lblPromedio.setText(String.format("%.1f", prom));

        String cal; Color color;
        if (prom >= 9)      { cal = "✦ EXCELENTE";  color = new Color(46, 125, 50);  }
        else if (prom >= 7) { cal = "✔ BUENO";       color = new Color(25, 118, 210); }
        else if (prom >= 5) { cal = "⚠ REGULAR";     color = new Color(245, 124, 0);  }
        else                { cal = "✖ DEFICIENTE";  color = new Color(198, 40, 40);  }
        lblCalificacion.setText(cal);
        lblCalificacion.setForeground(color);
        lblPromedio.setForeground(color);
    }

    private void guardarEvaluacion() {
        int idx = cbEmpleado.getSelectedIndex() - 1;
        if (idx < 0) { JOptionPane.showMessageDialog(this, "Seleccione un empleado."); return; }

        int idEmpleado = (int) listaEmpleados.get(idx)[0];
        int idEvaluador = Sesion.getIdUsuario() > 0 ? Sesion.getIdUsuario() : 1;
        String periodo = cbPeriodo.getSelectedItem().toString();

        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Guardar evaluación de " + cbEmpleado.getSelectedItem() + "?\n" +
            "Período: " + periodo + "\n" +
            "Promedio: " + lblPromedio.getText() + " — " + lblCalificacion.getText(),
            "Confirmar Evaluación", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        int id = dao.insertar(idEmpleado, idEvaluador, periodo,
            slPuntualidad.getValue(), slProductividad.getValue(),
            slTrabajo.getValue(), slActitud.getValue(),
            slCumplimiento.getValue(), txtComentarios.getText().trim());

        if (id > 0) {
            JOptionPane.showMessageDialog(this,
                "Evaluación #" + id + " guardada correctamente.\n" +
                "Empleado: " + cbEmpleado.getSelectedItem() + "\n" +
                "Promedio: " + lblPromedio.getText() + " — " + lblCalificacion.getText(),
                "Evaluación Guardada", JOptionPane.INFORMATION_MESSAGE);
            limpiar();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar la evaluación.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar() {
        cbEmpleado.setSelectedIndex(0);
        lblCargo.setText("—"); lblPuntAuto.setText("—");
        slPuntualidad.setValue(5); slProductividad.setValue(5);
        slTrabajo.setValue(5); slActitud.setValue(5); slCumplimiento.setValue(5);
        txtComentarios.setText("");
        actualizarPromedio();
    }

    private void cargarHistorial(String filtroPeriodo) {
        modelHistorial.setRowCount(0);
        for (Object[] row : dao.listar(filtroPeriodo)) {
            modelHistorial.addRow(new Object[]{
                row[0], row[1], row[2], row[3],
                row[4], row[5], row[6], row[7], row[8], row[9], row[10]
            });
        }
    }
}
