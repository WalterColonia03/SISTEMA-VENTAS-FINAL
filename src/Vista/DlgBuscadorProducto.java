package Vista;

import Conexion.Conexion;
import Vista.Estilos.UIKit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DlgBuscadorProducto extends JDialog {

    private JTextField txtBuscar;
    private JTable tblProductos;
    private DefaultTableModel model;
    private JButton btnSeleccionar;
    private JButton btnCancelar;

    // Resultado seleccionado
    private int idProductoSeleccionado = -1;
    private String nombreSeleccionado = "";
    private double precioSeleccionado = 0;
    private int stockSeleccionado = 0;

    // Lista completa para filtrar en memoria
    private List<Object[]> todosProductos = new ArrayList<>();

    public DlgBuscadorProducto(Component parent) {
        super((Frame) SwingUtilities.getWindowAncestor(parent), "Buscar Producto", true);
        initComponents();
        buildLayout();
        attachEvents();
        cargarProductos();
        setSize(650, 500);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        txtBuscar = UIKit.textField();
        txtBuscar.putClientProperty("JTextField.placeholderText", "Escriba el nombre del producto...");
        txtBuscar.setPreferredSize(new Dimension(0, 40));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        String[] columnas = {"ID", "Nombre", "Categoría", "Precio", "Stock"};
        model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblProductos = UIKit.styledTable(model);
        tblProductos.getColumnModel().getColumn(0).setPreferredWidth(40);   // ID
        tblProductos.getColumnModel().getColumn(1).setPreferredWidth(200);  // Nombre
        tblProductos.getColumnModel().getColumn(2).setPreferredWidth(120);  // Categoría
        tblProductos.getColumnModel().getColumn(3).setPreferredWidth(80);   // Precio
        tblProductos.getColumnModel().getColumn(4).setPreferredWidth(60);   // Stock

        btnSeleccionar = UIKit.primaryButton("Seleccionar");
        btnCancelar = UIKit.secondaryButton("Cancelar");
    }

    private void buildLayout() {
        getContentPane().setBackground(UIKit.BG_APP);
        setLayout(new BorderLayout(0, UIKit.SPACE_SM));
        ((JComponent) getContentPane()).setBorder(
                new EmptyBorder(UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));

        // Arriba: buscador
        JPanel pnlTop = new JPanel(new BorderLayout(UIKit.SPACE_SM, 0));
        pnlTop.setOpaque(false);
        JLabel lblIcono = new JLabel("🔍");
        lblIcono.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        pnlTop.add(lblIcono, BorderLayout.WEST);
        pnlTop.add(txtBuscar, BorderLayout.CENTER);
        add(pnlTop, BorderLayout.NORTH);

        // Centro: tabla
        JScrollPane scroll = new JScrollPane(tblProductos);
        scroll.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        add(scroll, BorderLayout.CENTER);

        // Abajo: botones
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIKit.SPACE_SM, 0));
        pnlBot.setOpaque(false);
        pnlBot.add(btnCancelar);
        pnlBot.add(btnSeleccionar);
        add(pnlBot, BorderLayout.SOUTH);
    }

    private void attachEvents() {
        // Filtro en tiempo real
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrar(); }
        });

        // Doble clic = seleccionar
        tblProductos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) seleccionar();
            }
        });

        btnSeleccionar.addActionListener(e -> seleccionar());
        btnCancelar.addActionListener(e -> {
            idProductoSeleccionado = -1;
            dispose();
        });
    }

    private void cargarProductos() {
        todosProductos.clear();
        String sql = "SELECT p.idProducto, p.nombre, " +
                     "COALESCE(c.descripcion, 'Sin categoría') AS categoria, " +
                     "p.precio, p.cantidad " +
                     "FROM producto p " +
                     "LEFT JOIN categoria c ON p.idCategoria = c.idCategoria " +
                     "WHERE p.estado = 1 ORDER BY p.nombre";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                todosProductos.add(new Object[]{
                    rs.getInt("idProducto"),
                    rs.getString("nombre"),
                    rs.getString("categoria"),
                    String.format("S/ %.2f", rs.getDouble("precio")),
                    rs.getInt("cantidad")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        filtrar();
    }

    private void filtrar() {
        String texto = txtBuscar.getText().trim().toLowerCase();
        model.setRowCount(0);
        for (Object[] fila : todosProductos) {
            String nombre = fila[1].toString().toLowerCase();
            String categoria = fila[2].toString().toLowerCase();
            if (texto.isEmpty() || nombre.contains(texto) || categoria.contains(texto)) {
                model.addRow(fila);
            }
        }
    }

    private void seleccionar() {
        int fila = tblProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto de la lista");
            return;
        }
        idProductoSeleccionado = Integer.parseInt(model.getValueAt(fila, 0).toString());
        nombreSeleccionado = model.getValueAt(fila, 1).toString();
        String precioStr = model.getValueAt(fila, 3).toString().replace("S/ ", "").replace(",", ".");
        precioSeleccionado = Double.parseDouble(precioStr);
        stockSeleccionado = Integer.parseInt(model.getValueAt(fila, 4).toString());
        dispose();
    }

    // ── Getters para obtener el resultado ──
    public int getIdProducto() { return idProductoSeleccionado; }
    public String getNombreProducto() { return nombreSeleccionado; }
    public double getPrecioProducto() { return precioSeleccionado; }
    public int getStockProducto() { return stockSeleccionado; }
    public boolean isSeleccionado() { return idProductoSeleccionado != -1; }
}