package Vista;

import DAO.BitacoraDAO;
import Vista.Estilos.UIKit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class IFrmBitacoraAuditoria extends JInternalFrame {

    private JTable tblBitacora;
    private DefaultTableModel modelBitacora;

    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JTextField txtUsuario;
    private JComboBox<String> cbModulo;
    private JButton btnBuscar;
    private JButton btnRefrescar;

    private JLabel lblTotalRegistros;

    public IFrmBitacoraAuditoria() {
        super("Bitácora de Auditoría", true, true, true, true);
        initComponents();
        buildLayout();
        attachEvents();
        setSize(1000, 600);
        cargarBitacora();
    }

    private void initComponents() {
        txtFechaInicio = UIKit.textField();
        txtFechaInicio.setText(LocalDate.now().withDayOfMonth(1).toString());
        txtFechaInicio.setPreferredSize(new Dimension(130, 36));

        txtFechaFin = UIKit.textField();
        txtFechaFin.setText(LocalDate.now().toString());
        txtFechaFin.setPreferredSize(new Dimension(130, 36));

        txtUsuario = UIKit.textField();
        txtUsuario.setPreferredSize(new Dimension(130, 36));
        txtUsuario.putClientProperty("JTextField.placeholderText", "Usuario...");

        cbModulo = new JComboBox<>(new String[]{
            "Todos", "LOGIN", "VENTAS", "COMPRAS", "PRODUCTOS",
            "CLIENTES", "EMPLEADOS", "USUARIOS", "INVENTARIO",
            "FINANZAS", "DEVOLUCIONES", "FIDELIZACION"
        });
        cbModulo.setFont(UIKit.BODY);
        cbModulo.setPreferredSize(new Dimension(150, 36));

        btnBuscar = UIKit.primaryButton("Buscar");
        btnRefrescar = UIKit.secondaryButton("Refrescar");

        lblTotalRegistros = new JLabel("0 registros");
        lblTotalRegistros.setFont(UIKit.BODY_BOLD);
        lblTotalRegistros.setForeground(UIKit.TEXT_SECONDARY);

        String[] columns = {"ID", "Usuario", "Acción", "Módulo", "Detalle", "Fecha/Hora"};
        modelBitacora = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tblBitacora = UIKit.styledTable(modelBitacora);
        tblBitacora.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblBitacora.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblBitacora.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblBitacora.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblBitacora.getColumnModel().getColumn(4).setPreferredWidth(350);
        tblBitacora.getColumnModel().getColumn(5).setPreferredWidth(150);
    }

    private void buildLayout() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UIKit.BG_APP);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(
                UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));

        getContentPane().add(
                UIKit.screenHeader("Bitácora de Auditoría", "Administración  ›  Bitácora"),
                BorderLayout.NORTH);

        JPanel cuerpo = UIKit.card();
        cuerpo.setLayout(new BorderLayout(0, UIKit.SPACE_SM));

        // Filtros
        JPanel pnlFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, UIKit.SPACE_SM, 0));
        pnlFiltros.setOpaque(false);
        pnlFiltros.add(UIKit.fieldLabel("Desde:"));
        pnlFiltros.add(txtFechaInicio);
        pnlFiltros.add(UIKit.fieldLabel("Hasta:"));
        pnlFiltros.add(txtFechaFin);
        pnlFiltros.add(UIKit.fieldLabel("Usuario:"));
        pnlFiltros.add(txtUsuario);
        pnlFiltros.add(UIKit.fieldLabel("Módulo:"));
        pnlFiltros.add(cbModulo);
        pnlFiltros.add(btnBuscar);
        pnlFiltros.add(btnRefrescar);

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        pnlHeader.add(UIKit.sectionHeader("Registro de Actividades", null), BorderLayout.NORTH);
        pnlHeader.add(pnlFiltros, BorderLayout.CENTER);

        JPanel pnlSubHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSubHeader.setOpaque(false);
        pnlSubHeader.add(lblTotalRegistros);
        pnlHeader.add(pnlSubHeader, BorderLayout.SOUTH);

        cuerpo.add(pnlHeader, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tblBitacora);
        scroll.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        cuerpo.add(scroll, BorderLayout.CENTER);

        getContentPane().add(cuerpo, BorderLayout.CENTER);
    }

    private void attachEvents() {
        btnBuscar.addActionListener(e -> cargarBitacora());
        btnRefrescar.addActionListener(e -> {
            txtFechaInicio.setText(LocalDate.now().withDayOfMonth(1).toString());
            txtFechaFin.setText(LocalDate.now().toString());
            txtUsuario.setText("");
            cbModulo.setSelectedIndex(0);
            cargarBitacora();
        });
    }

    private void cargarBitacora() {
        modelBitacora.setRowCount(0);
        String inicio = txtFechaInicio.getText().trim();
        String fin = txtFechaFin.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String modulo = cbModulo.getSelectedItem().toString();

        if (inicio.isEmpty()) {
            inicio = "2000-01-01";
        }
        if (fin.isEmpty()) {
            fin = LocalDate.now().toString();
        }

        BitacoraDAO dao = new BitacoraDAO();
        java.util.List<Object[]> lista = dao.listar(inicio, fin, modulo, usuario);

        for (Object[] row : lista) {
            modelBitacora.addRow(row);
        }

        lblTotalRegistros.setText(lista.size() + " registros encontrados");
    }
}
