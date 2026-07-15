package Vista;

import Clases.Producto;
import DAO.KardexDAO;
import DAO.ProductoDAO;
import Vista.Estilos.UIKit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class IFrmKardex extends JInternalFrame {

    private JComboBox<String> cbProducto;
    private JTable tblKardex;
    private DefaultTableModel modelKardex;
    private JLabel lblStockActual;
    private JLabel lblEntradas;
    private JLabel lblSalidas;
    private JButton btnRefrescar;

    private List<Producto> listaProductos;

    public IFrmKardex() {
        super("Kardex - Historial de Movimientos", true, true, true, true);
        initComponents();
        buildLayout();
        attachEvents();
        setSize(900, 580);
    }

    private void initComponents() {
        cbProducto = new JComboBox<>();
        cbProducto.setFont(UIKit.BODY);
        cbProducto.setPreferredSize(new Dimension(250, 36));
        cargarProductos();

        btnRefrescar = UIKit.secondaryButton("Refrescar");

        lblStockActual = new JLabel("0");
        lblStockActual.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblStockActual.setForeground(UIKit.PRIMARY);

        lblEntradas = new JLabel("0");
        lblEntradas.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblEntradas.setForeground(new Color(46, 125, 50));

        lblSalidas = new JLabel("0");
        lblSalidas.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblSalidas.setForeground(new Color(198, 40, 40));

        String[] columns = {"Fecha", "Tipo", "Cantidad", "Stock Resultante", "Referencia"};
        modelKardex = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tblKardex = UIKit.styledTable(modelKardex);
    }

    private void cargarProductos() {
        cbProducto.removeAllItems();
        cbProducto.addItem("-- Seleccione un producto --");
        ProductoDAO dao = new ProductoDAO();
        listaProductos = dao.listar();
        for (Producto p : listaProductos) {
            cbProducto.addItem(p.getIdProducto() + " - " + p.getNombre());
        }
    }

    private void buildLayout() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UIKit.BG_APP);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(
                UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));

        getContentPane().add(
                UIKit.screenHeader("Kardex", "Inventario  ›  Kardex"),
                BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(0, UIKit.SPACE_MD));
        cuerpo.setOpaque(false);

        // ── Panel superior: selector + KPIs ──
        JPanel pnlTop = UIKit.card();
        pnlTop.setLayout(new BorderLayout(UIKit.SPACE_LG, 0));

        // Selector de producto
        JPanel pnlSelector = new JPanel(new FlowLayout(FlowLayout.LEFT, UIKit.SPACE_SM, 0));
        pnlSelector.setOpaque(false);
        pnlSelector.add(UIKit.fieldLabel("Producto:"));
        pnlSelector.add(cbProducto);
        pnlSelector.add(btnRefrescar);
        pnlTop.add(pnlSelector, BorderLayout.WEST);

        // KPIs
        JPanel pnlKpis = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIKit.SPACE_MD, 0));
        pnlKpis.setOpaque(false);
        pnlKpis.add(crearKpi("Stock Actual", lblStockActual, new Color(232, 240, 254)));
        pnlKpis.add(crearKpi("Entradas", lblEntradas, new Color(232, 245, 233)));
        pnlKpis.add(crearKpi("Salidas", lblSalidas, new Color(255, 235, 238)));
        pnlTop.add(pnlKpis, BorderLayout.CENTER);

        cuerpo.add(pnlTop, BorderLayout.NORTH);

        // ── Tabla de movimientos ──
        JPanel pnlTabla = UIKit.card();
        pnlTabla.setLayout(new BorderLayout(0, UIKit.SPACE_SM));
        pnlTabla.add(UIKit.sectionHeader("Movimientos del Producto", null), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tblKardex);
        scroll.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlTabla.add(scroll, BorderLayout.CENTER);

        cuerpo.add(pnlTabla, BorderLayout.CENTER);
        getContentPane().add(cuerpo, BorderLayout.CENTER);
    }

    private JPanel crearKpi(String titulo, JLabel lblValor, Color bgColor) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createLineBorder(bgColor.darker()));
        panel.setPreferredSize(new Dimension(140, 65));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;

        gbc.gridy = 0;
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(UIKit.CAPTION);
        panel.add(lblTitulo, gbc);

        gbc.gridy = 1;
        panel.add(lblValor, gbc);

        return panel;
    }

    private void attachEvents() {

        // Al seleccionar producto cargar automáticamente
        cbProducto.addActionListener(e -> {
            if (cbProducto.getSelectedIndex() > 0) {
                cargarKardex();
            } else {
                modelKardex.setRowCount(0);
                lblStockActual.setText("0");
                lblEntradas.setText("0");
                lblSalidas.setText("0");
            }
        });

        // Refrescar
        btnRefrescar.addActionListener(e -> {
            cargarProductos();
            modelKardex.setRowCount(0);
            lblStockActual.setText("0");
            lblEntradas.setText("0");
            lblSalidas.setText("0");
        });
    }

    private void cargarKardex() {
        int idx = cbProducto.getSelectedIndex() - 1;
        if (idx < 0 || listaProductos == null) return;

        int idProducto = listaProductos.get(idx).getIdProducto();
        int stockActual = listaProductos.get(idx).getCantidad();

        // Cargar movimientos
        modelKardex.setRowCount(0);
        KardexDAO dao = new KardexDAO();
        List<Object[]> movimientos = dao.listarPorProducto(idProducto);

        for (Object[] mov : movimientos) {
            String tipo = mov[1].toString();
            modelKardex.addRow(new Object[]{
                mov[0],  // fecha
                tipo,
                mov[2],  // cantidad
                mov[3],  // stockActual
                mov[4]   // referencia
            });
        }

        // Actualizar KPIs
        int[] totales = dao.getTotales(idProducto);
        lblStockActual.setText(String.valueOf(stockActual));
        lblEntradas.setText(String.valueOf(totales[0]));
        lblSalidas.setText(String.valueOf(totales[1]));
    }
}