package Vista;

import Conexion.Conexion;
import Vista.Estilos.UIKit;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileOutputStream;
import java.sql.*;
import java.time.LocalDate;

/**
 * IFrmEstadosFinancieros — genera Balance General y Estado de Resultados
 * a partir del Libro Mayor (partida doble ya registrada por LibroMayorDAO).
 *
 * Cuentas reconocidas del Libro Mayor:
 *   ACTIVO:   101 Efectivo, 201 Mercaderías
 *   PASIVO:   421 Cuentas por Pagar, 4011 IGV por Pagar
 *   INGRESOS: 701 Ventas
 *   GASTOS:   (egresos de flujo de caja = costo aproximado)
 */
public class IFrmEstadosFinancieros extends JInternalFrame {

    private JTabbedPane tabs;
    private JComboBox<String> cbPeriodo;
    private JButton btnGenerar, btnExportarPDF;

    // Balance General
    private DefaultTableModel modelActivo, modelPasivo, modelPatrimonio;

    // Estado de Resultados
    private DefaultTableModel modelResultados;
    private JLabel lblUtilidad;

    // Totales resumen
    private JLabel lblTotalActivo, lblTotalPasivo, lblTotalPatrimonio;

    public IFrmEstadosFinancieros() {
        super("Estados Financieros", true, true, true, true);
        initComponents();
        buildLayout();
        attachEvents();
        setSize(1000, 620);
    }

    private void initComponents() {
        String[] periodos = new String[13];
        periodos[0] = "Todo el período";
        LocalDate hoy = LocalDate.now();
        for (int i = 0; i < 12; i++) {
            LocalDate mes = hoy.minusMonths(i);
            periodos[i + 1] = mes.getYear() + "-" + String.format("%02d", mes.getMonthValue());
        }
        cbPeriodo = new JComboBox<>(periodos);
        cbPeriodo.setFont(UIKit.BODY);
        cbPeriodo.setPreferredSize(new Dimension(160, 36));

        btnGenerar     = UIKit.primaryButton("Generar");
        btnExportarPDF = UIKit.secondaryButton("Exportar PDF");

        String[] colsAct = {"Cuenta", "Descripción", "S/ Monto"};
        modelActivo     = new DefaultTableModel(colsAct, 0) { @Override public boolean isCellEditable(int r,int c){return false;} };
        modelPasivo     = new DefaultTableModel(colsAct, 0) { @Override public boolean isCellEditable(int r,int c){return false;} };
        modelPatrimonio = new DefaultTableModel(colsAct, 0) { @Override public boolean isCellEditable(int r,int c){return false;} };

        String[] colsRes = {"Concepto", "S/ Monto"};
        modelResultados = new DefaultTableModel(colsRes, 0) { @Override public boolean isCellEditable(int r,int c){return false;} };

        lblTotalActivo     = new JLabel("S/ 0.00"); lblTotalActivo.setFont(UIKit.H1); lblTotalActivo.setForeground(UIKit.SUCCESS);
        lblTotalPasivo     = new JLabel("S/ 0.00"); lblTotalPasivo.setFont(UIKit.H1); lblTotalPasivo.setForeground(UIKit.DANGER);
        lblTotalPatrimonio = new JLabel("S/ 0.00"); lblTotalPatrimonio.setFont(UIKit.H1); lblTotalPatrimonio.setForeground(UIKit.ACCENT);
        lblUtilidad        = new JLabel("S/ 0.00"); lblUtilidad.setFont(UIKit.H1);
    }

    private void buildLayout() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UIKit.BG_APP);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));

        // Header
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        pnlTop.add(UIKit.screenHeader("Estados Financieros", "Finanzas  ›  Balance General y Estado de Resultados"), BorderLayout.CENTER);

        JPanel pnlToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIKit.SPACE_SM, 0));
        pnlToolbar.setOpaque(false);
        pnlToolbar.add(new JLabel("Período:"));
        pnlToolbar.add(cbPeriodo);
        pnlToolbar.add(btnGenerar);
        pnlToolbar.add(btnExportarPDF);
        pnlTop.add(pnlToolbar, BorderLayout.SOUTH);
        getContentPane().add(pnlTop, BorderLayout.NORTH);

        // Tabs
        tabs = new JTabbedPane();
        tabs.setFont(UIKit.BODY);
        tabs.addTab("Balance General", buildTabBalance());
        tabs.addTab("Estado de Resultados", buildTabResultados());
        getContentPane().add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildTabBalance() {
        JPanel tab = new JPanel(new GridLayout(1, 2, UIKit.SPACE_MD, 0));
        tab.setOpaque(false);
        tab.setBorder(new EmptyBorder(UIKit.SPACE_MD, 0, 0, 0));

        // ACTIVO
        JPanel pnlActivo = UIKit.card();
        pnlActivo.setLayout(new BorderLayout(0, UIKit.SPACE_SM));
        pnlActivo.add(UIKit.sectionHeader("ACTIVO", null), BorderLayout.NORTH);
        JTable tblActivo = UIKit.styledTable(modelActivo);
        alignMonto(tblActivo, 2);
        pnlActivo.add(new JScrollPane(tblActivo), BorderLayout.CENTER);
        JPanel pnlTotAct = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlTotAct.setOpaque(false);
        pnlTotAct.add(new JLabel("TOTAL ACTIVO: ")); pnlTotAct.add(lblTotalActivo);
        pnlActivo.add(pnlTotAct, BorderLayout.SOUTH);

        // PASIVO + PATRIMONIO
        JPanel pnlDerecha = new JPanel(new GridLayout(2, 1, 0, UIKit.SPACE_SM));
        pnlDerecha.setOpaque(false);

        JPanel pnlPasivo = UIKit.card();
        pnlPasivo.setLayout(new BorderLayout(0, UIKit.SPACE_SM));
        pnlPasivo.add(UIKit.sectionHeader("PASIVO", null), BorderLayout.NORTH);
        JTable tblPasivo = UIKit.styledTable(modelPasivo);
        alignMonto(tblPasivo, 2);
        pnlPasivo.add(new JScrollPane(tblPasivo), BorderLayout.CENTER);
        JPanel pnlTotPas = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlTotPas.setOpaque(false);
        pnlTotPas.add(new JLabel("TOTAL PASIVO: ")); pnlTotPas.add(lblTotalPasivo);
        pnlPasivo.add(pnlTotPas, BorderLayout.SOUTH);

        JPanel pnlPatrimonio = UIKit.card();
        pnlPatrimonio.setLayout(new BorderLayout(0, UIKit.SPACE_SM));
        pnlPatrimonio.add(UIKit.sectionHeader("PATRIMONIO", null), BorderLayout.NORTH);
        JTable tblPatrimonio = UIKit.styledTable(modelPatrimonio);
        alignMonto(tblPatrimonio, 2);
        pnlPatrimonio.add(new JScrollPane(tblPatrimonio), BorderLayout.CENTER);
        JPanel pnlTotPat = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlTotPat.setOpaque(false);
        pnlTotPat.add(new JLabel("TOTAL PATRIMONIO: ")); pnlTotPat.add(lblTotalPatrimonio);
        pnlPatrimonio.add(pnlTotPat, BorderLayout.SOUTH);

        pnlDerecha.add(pnlPasivo);
        pnlDerecha.add(pnlPatrimonio);

        tab.add(pnlActivo);
        tab.add(pnlDerecha);
        return tab;
    }

    private JPanel buildTabResultados() {
        JPanel tab = new JPanel(new BorderLayout(0, UIKit.SPACE_MD));
        tab.setOpaque(false);
        tab.setBorder(new EmptyBorder(UIKit.SPACE_MD, 0, 0, 0));

        JPanel pnlCard = UIKit.card();
        pnlCard.setLayout(new BorderLayout(0, UIKit.SPACE_SM));
        pnlCard.add(UIKit.sectionHeader("Estado de Resultados", null), BorderLayout.NORTH);

        JTable tblRes = UIKit.styledTable(modelResultados);
        alignMonto(tblRes, 1);
        pnlCard.add(new JScrollPane(tblRes), BorderLayout.CENTER);

        JPanel pnlUtilidad = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlUtilidad.setOpaque(false);
        JLabel lbl = new JLabel("UTILIDAD NETA: "); lbl.setFont(UIKit.BODY_BOLD);
        pnlUtilidad.add(lbl); pnlUtilidad.add(lblUtilidad);
        pnlCard.add(pnlUtilidad, BorderLayout.SOUTH);

        tab.add(pnlCard, BorderLayout.CENTER);
        return tab;
    }

    private void alignMonto(JTable tbl, int col) {
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        tbl.getColumnModel().getColumn(col).setCellRenderer(right);
    }

    private void attachEvents() {
        btnGenerar.addActionListener(e -> generarEstados());
        btnExportarPDF.addActionListener(e -> exportarPDF());
    }

    // ─── LÓGICA PRINCIPAL ────────────────────────────────────────────────────────

    private void generarEstados() {
        modelActivo.setRowCount(0);
        modelPasivo.setRowCount(0);
        modelPatrimonio.setRowCount(0);
        modelResultados.setRowCount(0);

        String filtroFecha = "";
        String periodo = cbPeriodo.getSelectedItem().toString();
        if (!periodo.equals("Todo el período")) {
            filtroFecha = " AND DATE_FORMAT(fecha, '%Y-%m') = '" + periodo + "'";
        }

        try (Connection con = Conexion.getConexion()) {
            if (con == null) { JOptionPane.showMessageDialog(this, "Sin conexión a la base de datos"); return; }

            // ── ACTIVOS ─────────────────────────────────────────────────────────
            // 101 Efectivo = SUM(debe) - SUM(haber) donde cuentaDebe='101 Efectivo' o cuentaHaber='101 Efectivo'
            double efectivo = getSaldo(con, "101 Efectivo", filtroFecha);
            double mercancias = getSaldo(con, "201 Mercaderías", filtroFecha);
            double totalActivo = efectivo + mercancias;

            modelActivo.addRow(new Object[]{"101", "Efectivo y Equivalentes", fmt(efectivo)});
            modelActivo.addRow(new Object[]{"201", "Mercaderías (Inventario)", fmt(mercancias)});
            lblTotalActivo.setText(fmt(totalActivo));

            // ── PASIVOS ──────────────────────────────────────────────────────────
            double cxp  = getSaldoPasivo(con, "421 Cuentas por Pagar", filtroFecha);
            double igvP = getSaldoPasivo(con, "4011 IGV por Pagar", filtroFecha);
            double totalPasivo = cxp + igvP;

            modelPasivo.addRow(new Object[]{"421", "Cuentas por Pagar Comerciales", fmt(cxp)});
            modelPasivo.addRow(new Object[]{"4011", "IGV por Pagar (Tributos)", fmt(igvP)});
            lblTotalPasivo.setText(fmt(totalPasivo));

            // ── PATRIMONIO = ACTIVO - PASIVO ─────────────────────────────────────
            double patrimonio = totalActivo - totalPasivo;
            modelPatrimonio.addRow(new Object[]{"301", "Capital Social", fmt(Math.max(patrimonio, 0))});
            lblTotalPatrimonio.setText(fmt(patrimonio));

            // ── ESTADO DE RESULTADOS ─────────────────────────────────────────────
            double ventas        = getHaber(con, "701 Ventas", filtroFecha);
            double igvCreditoFiscal = getDebe(con, "4011 IGV Crédito Fiscal", filtroFecha);
            double costoMercancias = getDebe(con, "201 Mercaderías", filtroFecha); // compras
            double utilidadBruta = ventas - costoMercancias;
            double utilidadNeta  = utilidadBruta - igvCreditoFiscal;

            modelResultados.addRow(new Object[]{"(+) Ventas Netas",               fmt(ventas)});
            modelResultados.addRow(new Object[]{"(-) Costo de Mercaderías",        fmt(costoMercancias)});
            modelResultados.addRow(new Object[]{"= UTILIDAD BRUTA",               fmt(utilidadBruta)});
            modelResultados.addRow(new Object[]{"(-) IGV Crédito Fiscal (gastos)", fmt(igvCreditoFiscal)});
            modelResultados.addRow(new Object[]{"= UTILIDAD OPERATIVA",           fmt(utilidadNeta)});

            lblUtilidad.setText(fmt(utilidadNeta));
            lblUtilidad.setForeground(utilidadNeta >= 0 ? UIKit.SUCCESS : UIKit.DANGER);

        } catch (SQLException ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }

    // Saldo de cuenta ACTIVO: débitos - créditos
    private double getSaldo(Connection con, String cuenta, String filtro) throws SQLException {
        String sql = "SELECT COALESCE(SUM(debe),0)-COALESCE(SUM(haber),0) as saldo FROM LibroMayor WHERE (cuentaDebe=? OR cuentaHaber=?) " + filtro.replace("fecha","fecha");
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT COALESCE(SUM(CASE WHEN cuentaDebe=? THEN debe ELSE 0 END),0) - " +
                "COALESCE(SUM(CASE WHEN cuentaHaber=? THEN haber ELSE 0 END),0) as saldo " +
                "FROM LibroMayor WHERE 1=1" + filtro)) {
            ps.setString(1, cuenta); ps.setString(2, cuenta);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getDouble("saldo") : 0;
        }
    }

    // Saldo de cuenta PASIVO: créditos - débitos
    private double getSaldoPasivo(Connection con, String cuenta, String filtro) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT COALESCE(SUM(CASE WHEN cuentaHaber=? THEN haber ELSE 0 END),0) - " +
                "COALESCE(SUM(CASE WHEN cuentaDebe=? THEN debe ELSE 0 END),0) as saldo " +
                "FROM LibroMayor WHERE 1=1" + filtro)) {
            ps.setString(1, cuenta); ps.setString(2, cuenta);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? Math.max(rs.getDouble("saldo"), 0) : 0;
        }
    }

    private double getDebe(Connection con, String cuenta, String filtro) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT COALESCE(SUM(debe),0) as total FROM LibroMayor WHERE cuentaDebe=?" + filtro)) {
            ps.setString(1, cuenta);
            ResultSet rs = ps.executeQuery(); return rs.next() ? rs.getDouble("total") : 0;
        }
    }

    private double getHaber(Connection con, String cuenta, String filtro) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT COALESCE(SUM(haber),0) as total FROM LibroMayor WHERE cuentaHaber=?" + filtro)) {
            ps.setString(1, cuenta);
            ResultSet rs = ps.executeQuery(); return rs.next() ? rs.getDouble("total") : 0;
        }
    }

    private String fmt(double v) { return String.format("S/ %.2f", v); }

    // ─── EXPORTAR PDF ────────────────────────────────────────────────────────────
    private void exportarPDF() {
        if (modelActivo.getRowCount() == 0) { JOptionPane.showMessageDialog(this, "Primero genere los estados financieros."); return; }

        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("Estados_Financieros_" + LocalDate.now() + ".pdf"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        String ruta = fc.getSelectedFile().getAbsolutePath();
        if (!ruta.endsWith(".pdf")) ruta += ".pdf";

        try {
            Document doc = new Document(PageSize.A4, 40, 40, 60, 60);
            PdfWriter.getInstance(doc, new FileOutputStream(ruta));
            doc.open();

            BaseColor AZUL = new BaseColor(25, 118, 210);
            BaseColor GRIS = new BaseColor(245, 245, 245);
            com.itextpdf.text.Font fTitulo  = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD, BaseColor.WHITE);
            com.itextpdf.text.Font fSub     = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.NORMAL, BaseColor.WHITE);
            com.itextpdf.text.Font fHead    = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,  9, com.itextpdf.text.Font.BOLD, BaseColor.WHITE);
            com.itextpdf.text.Font fCelda   = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,  9, com.itextpdf.text.Font.NORMAL, new BaseColor(33,33,33));
            com.itextpdf.text.Font fTotal   = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.BOLD, AZUL);
            com.itextpdf.text.Font fSeccion = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 11, com.itextpdf.text.Font.BOLD, AZUL);

            // Encabezado
            PdfPTable hdr = new PdfPTable(1); hdr.setWidthPercentage(100); hdr.setSpacingAfter(20);
            PdfPCell hCell = new PdfPCell(); hCell.setBackgroundColor(AZUL); hCell.setPadding(14); hCell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
            hCell.addElement(new Paragraph("Minimarket LAREDO", fTitulo));
            hCell.addElement(new Paragraph("Estados Financieros — Período: " + cbPeriodo.getSelectedItem() + "   |   Fecha: " + LocalDate.now(), fSub));
            hdr.addCell(hCell); doc.add(hdr);

            // Balance General
            doc.add(new Paragraph("BALANCE GENERAL", fSeccion)); doc.add(Chunk.NEWLINE);
            agregarTablaEstado(doc, "ACTIVO", modelActivo, fHead, fCelda, fTotal, AZUL, GRIS, "TOTAL ACTIVO: " + lblTotalActivo.getText());
            doc.add(Chunk.NEWLINE);
            agregarTablaEstado(doc, "PASIVO", modelPasivo, fHead, fCelda, fTotal, AZUL, GRIS, "TOTAL PASIVO: " + lblTotalPasivo.getText());
            doc.add(Chunk.NEWLINE);
            agregarTablaEstado(doc, "PATRIMONIO", modelPatrimonio, fHead, fCelda, fTotal, AZUL, GRIS, "TOTAL PATRIMONIO: " + lblTotalPatrimonio.getText());
            doc.add(Chunk.NEWLINE);

            // Estado de Resultados
            doc.add(new Paragraph("ESTADO DE RESULTADOS", fSeccion)); doc.add(Chunk.NEWLINE);
            agregarTablaEstado(doc, "ESTADO DE RESULTADOS", modelResultados, fHead, fCelda, fTotal, AZUL, GRIS, "UTILIDAD NETA: " + lblUtilidad.getText());

            doc.close();
            int op = JOptionPane.showConfirmDialog(this, "PDF generado. ¿Abrir ahora?", "Listo", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) java.awt.Desktop.getDesktop().open(new java.io.File(ruta));
        } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage()); }
    }

    private void agregarTablaEstado(Document doc, String titulo, DefaultTableModel model,
            com.itextpdf.text.Font fHead, com.itextpdf.text.Font fCelda,
            com.itextpdf.text.Font fTotal, BaseColor azul, BaseColor gris, String totalStr) throws DocumentException {
        int cols = model.getColumnCount();
        PdfPTable tabla = new PdfPTable(cols); tabla.setWidthPercentage(100); tabla.setSpacingAfter(8);
        if (cols == 3) tabla.setWidths(new float[]{15f, 55f, 30f});
        else           tabla.setWidths(new float[]{70f, 30f});

        for (int c = 0; c < cols; c++) {
            PdfPCell cell = new PdfPCell(new Phrase(model.getColumnName(c), fHead));
            cell.setBackgroundColor(azul); cell.setPadding(7); cell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
            tabla.addCell(cell);
        }
        for (int r = 0; r < model.getRowCount(); r++) {
            BaseColor bg = r % 2 == 0 ? BaseColor.WHITE : gris;
            for (int c = 0; c < cols; c++) {
                String val = model.getValueAt(r, c).toString();
                PdfPCell cell = new PdfPCell(new Phrase(val, fCelda));
                cell.setBackgroundColor(bg); cell.setPadding(6);
                cell.setHorizontalAlignment(c == cols-1 ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT);
                tabla.addCell(cell);
            }
        }
        // Fila de total
        PdfPCell cTotal = new PdfPCell(new Phrase(totalStr, fTotal));
        cTotal.setColspan(cols); cTotal.setBackgroundColor(new BaseColor(227,242,253));
        cTotal.setPadding(8); cTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tabla.addCell(cTotal);
        doc.add(tabla);
    }
}
