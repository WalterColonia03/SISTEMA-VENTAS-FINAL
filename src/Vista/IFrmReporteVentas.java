package Vista;

import Conexion.Conexion;
import Vista.Estilos.UIKit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class IFrmReporteVentas extends JInternalFrame {

    private JTable tblVentas;
    private DefaultTableModel modelVentas;

    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JComboBox<String> cbMes;
    private JComboBox<String> cbAnio;
    private JButton btnBuscar;
    private JButton btnRefrescar;

    private JLabel lblTotalVentas;
    private JLabel lblTotalTransacciones;
    private JLabel lblPromedioVenta;
    private JLabel lblTotalIGV;

    public IFrmReporteVentas() {
        super("Reporte de Ventas", true, true, true, true);
        initComponents();
        buildLayout();
        attachEvents();
        setSize(1000, 620);
        cargarReporte();
    }

    private void initComponents() {
        txtFechaInicio = UIKit.textField();
        txtFechaInicio.setText(LocalDate.now().withDayOfMonth(1).toString());
        txtFechaInicio.setPreferredSize(new Dimension(130, 36));

        txtFechaFin = UIKit.textField();
        txtFechaFin.setText(LocalDate.now().toString());
        txtFechaFin.setPreferredSize(new Dimension(130, 36));

        cbMes = new JComboBox<>(new String[]{
            "Todos", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        });
        cbMes.setFont(UIKit.BODY);
        cbMes.setPreferredSize(new Dimension(130, 36));

        cbAnio = new JComboBox<>(new String[]{
            "2024", "2025", "2026", "2027"
        });
        cbAnio.setFont(UIKit.BODY);
        cbAnio.setSelectedItem(String.valueOf(LocalDate.now().getYear()));
        cbAnio.setPreferredSize(new Dimension(90, 36));

        btnBuscar    = UIKit.primaryButton("Buscar");
        btnRefrescar = UIKit.secondaryButton("Refrescar");

        lblTotalVentas       = new JLabel("S/ 0.00");
        lblTotalVentas.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotalVentas.setForeground(UIKit.ACCENT);

        lblTotalTransacciones = new JLabel("0");
        lblTotalTransacciones.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotalTransacciones.setForeground(UIKit.PRIMARY);

        lblPromedioVenta = new JLabel("S/ 0.00");
        lblPromedioVenta.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblPromedioVenta.setForeground(UIKit.SUCCESS);

        lblTotalIGV = new JLabel("S/ 0.00");
        lblTotalIGV.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotalIGV.setForeground(UIKit.WARNING);

        String[] columns = {"#Venta", "Cliente", "Subtotal", "IGV", "Total", "Método", "Fecha", "Estado"};
        modelVentas = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tblVentas = UIKit.styledTable(modelVentas);
    }

    private void buildLayout() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UIKit.BG_APP);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(
                UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));

        getContentPane().add(
                UIKit.screenHeader("Reporte de Ventas", "Finanzas  ›  Reporte de Ventas"),
                BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(0, UIKit.SPACE_MD));
        cuerpo.setOpaque(false);

        // KPIs
        JPanel pnlCards = new JPanel(new GridLayout(1, 4, UIKit.SPACE_MD, 0));
        pnlCards.setOpaque(false);
        pnlCards.setPreferredSize(new Dimension(0, 85));
        pnlCards.add(buildKpiCard("TOTAL VENTAS",        lblTotalVentas,        "del período"));
        pnlCards.add(buildKpiCard("TRANSACCIONES",       lblTotalTransacciones, "ventas realizadas"));
        pnlCards.add(buildKpiCard("PROMEDIO POR VENTA",  lblPromedioVenta,      "ticket promedio"));
        pnlCards.add(buildKpiCard("TOTAL IGV",           lblTotalIGV,           "18% sobre subtotal"));
        cuerpo.add(pnlCards, BorderLayout.NORTH);

        // Tabla
        JPanel pnlTabla = UIKit.card();
        pnlTabla.setLayout(new BorderLayout(0, UIKit.SPACE_SM));

        JPanel pnlFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, UIKit.SPACE_SM, 0));
        pnlFiltros.setOpaque(false);
        pnlFiltros.add(UIKit.fieldLabel("Desde:"));
        pnlFiltros.add(txtFechaInicio);
        pnlFiltros.add(UIKit.fieldLabel("Hasta:"));
        pnlFiltros.add(txtFechaFin);
        pnlFiltros.add(UIKit.fieldLabel("Mes:"));
        pnlFiltros.add(cbMes);
        pnlFiltros.add(UIKit.fieldLabel("Año:"));
        pnlFiltros.add(cbAnio);
        pnlFiltros.add(btnBuscar);
        pnlFiltros.add(btnRefrescar);

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        pnlHeader.add(UIKit.sectionHeader("Detalle de Ventas", null), BorderLayout.NORTH);
        pnlHeader.add(pnlFiltros, BorderLayout.CENTER);
        pnlTabla.add(pnlHeader, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tblVentas);
        scroll.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlTabla.add(scroll, BorderLayout.CENTER);

        cuerpo.add(pnlTabla, BorderLayout.CENTER);
        getContentPane().add(cuerpo, BorderLayout.CENTER);
    }

    private JPanel buildKpiCard(String titulo, JLabel lblValor, String subtitulo) {
        JPanel card = UIKit.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;

        JLabel lblT = new JLabel(titulo);
        lblT.setFont(UIKit.CAPTION);
        lblT.setForeground(UIKit.TEXT_SECONDARY);
        gbc.gridy = 0; gbc.insets = new Insets(10, 12, 2, 12);
        card.add(lblT, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 12, 2, 12);
        card.add(lblValor, gbc);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(UIKit.CAPTION);
        lblSub.setForeground(UIKit.TEXT_SECONDARY);
        gbc.gridy = 2; gbc.insets = new Insets(0, 12, 10, 12);
        card.add(lblSub, gbc);

        return card;
    }

    private void attachEvents() {
        btnBuscar.addActionListener(e -> cargarReporte());
        btnRefrescar.addActionListener(e -> {
            txtFechaInicio.setText(LocalDate.now().withDayOfMonth(1).toString());
            txtFechaFin.setText(LocalDate.now().toString());
            cbMes.setSelectedIndex(0);
            cbAnio.setSelectedItem(String.valueOf(LocalDate.now().getYear()));
            cargarReporte();
        });
    }

    private void cargarReporte() {
        modelVentas.setRowCount(0);
        double totalVentas = 0, totalIGV = 0;
        int transacciones = 0;

        String inicio = txtFechaInicio.getText().trim();
        String fin    = txtFechaFin.getText().trim();
        int mes       = cbMes.getSelectedIndex();
        String anio   = cbAnio.getSelectedItem().toString();

        StringBuilder sql = new StringBuilder(
            "SELECT v.idVenta, CONCAT(c.nombre,' ',c.apellido) as cliente, " +
            "v.subtotal, v.igv, v.total, v.metodoPago, v.fecha, v.estado " +
            "FROM Venta v JOIN cliente c ON v.idCliente = c.idCliente " +
            "WHERE v.estado = 1 ");

        if (mes > 0) {
            sql.append("AND MONTH(v.fecha) = ").append(mes).append(" ");
            sql.append("AND YEAR(v.fecha) = ").append(anio).append(" ");
        } else {
            sql.append("AND DATE(v.fecha) BETWEEN '").append(inicio)
               .append("' AND '").append(fin).append("' ");
        }
        sql.append("ORDER BY v.fecha DESC");

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString());
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                double total = rs.getDouble("total");
                double igv   = rs.getDouble("igv");
                totalVentas += total;
                totalIGV    += igv;
                transacciones++;
                modelVentas.addRow(new Object[]{
                    "#" + rs.getInt("idVenta"),
                    rs.getString("cliente"),
                    String.format("S/ %.2f", rs.getDouble("subtotal")),
                    String.format("S/ %.2f", igv),
                    String.format("S/ %.2f", total),
                    rs.getString("metodoPago"),
                    rs.getString("fecha"),
                    rs.getInt("estado") == 1 ? "Activa" : "Anulada"
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }

        lblTotalVentas.setText(String.format("S/ %.2f", totalVentas));
        lblTotalTransacciones.setText(String.valueOf(transacciones));
        lblPromedioVenta.setText(transacciones > 0 ?
            String.format("S/ %.2f", totalVentas / transacciones) : "S/ 0.00");
        lblTotalIGV.setText(String.format("S/ %.2f", totalIGV));
    }
}