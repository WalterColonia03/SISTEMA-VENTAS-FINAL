package Vista;

import Clases.Categoria;
import Clases.Producto;
import DAO.CategoriaDAO;
import DAO.ProductoDAO;
import Servicio.Validador;
import Vista.Estilos.UIKit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class IFrmGestionProductos extends JInternalFrame {

    private JTable tblProductos;
    private DefaultTableModel modelProductos;
    private JTextField txtBuscar;
    private JComboBox<String> cbFiltroCategoria;

    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtDescripcion;
    private JTextField txtCantidad;
    private JTextField txtPrecio;
    private JComboBox<String> cbCategoria;

    private JLabel lblEmptyState;
    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnDesactivar;
    private JButton btnLimpiar;

    private List<Categoria> listaCategorias;

    public IFrmGestionProductos() {
        super("Gestión de Productos", true, true, true, true);
        initComponents();
        buildLayout();
        attachEvents();
        setSize(1000, 620);
        cargarTabla();
    }

    private void initComponents() {
        // Sección 5: searchField con 260px, sin botón separado
        txtBuscar = UIKit.searchField("Buscar producto...", null);

        String[] columns = {"ID", "Nombre", "Cantidad", "Precio (S/)", "Categoría", "Estado"};
        modelProductos = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblProductos = UIKit.styledTable(modelProductos);

        lblEmptyState = new JLabel("No hay productos registrados", SwingConstants.CENTER);
        lblEmptyState.setFont(UIKit.BODY);
        lblEmptyState.setForeground(UIKit.TEXT_SECONDARY);
        lblEmptyState.setVisible(false);

        txtId = UIKit.readOnlyField(); txtId.setEditable(false); txtId.setFocusable(false);
        txtNombre = UIKit.textField();
        txtDescripcion = UIKit.textField();
        txtCantidad = UIKit.textField(); txtCantidad.setHorizontalAlignment(JTextField.RIGHT);
        txtPrecio   = UIKit.textField(); txtPrecio.setHorizontalAlignment(JTextField.RIGHT);

        cbCategoria = new JComboBox<>();
        cbCategoria.setFont(UIKit.BODY);
        cbCategoria.setPreferredSize(new Dimension(0, 36));

        cbFiltroCategoria = new JComboBox<>();
        cbFiltroCategoria.setFont(UIKit.BODY);
        cbFiltroCategoria.setPreferredSize(new Dimension(180, 36));

        btnNuevo      = UIKit.primaryButton("+ Nuevo Producto");
        btnGuardar    = UIKit.primaryButton("Guardar / Actualizar");
        btnLimpiar    = UIKit.secondaryButton("Limpiar / Nuevo");
        btnDesactivar = UIKit.secondaryButton("Desactivar / Activar");

        cargarCategorias();
    }

    private void cargarCategorias() {
        cbCategoria.removeAllItems();
        cbFiltroCategoria.removeAllItems();
        cbFiltroCategoria.addItem("Todas las Categorías");

        CategoriaDAO dao = new CategoriaDAO();
        listaCategorias = dao.listar();
        for (Categoria c : listaCategorias) {
            cbCategoria.addItem(c.getDescripcion());
            cbFiltroCategoria.addItem(c.getDescripcion());
        }
    }

    private void buildLayout() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UIKit.BG_APP);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(
                UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));

        // Sección 5: título + botón primario en BorderLayout
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        pnlTop.add(UIKit.screenHeader("Gestión de Productos", "Inventario  ›  Productos"), BorderLayout.WEST);
        pnlTop.add(btnNuevo, BorderLayout.EAST);
        getContentPane().add(pnlTop, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(UIKit.SPACE_LG, 0));
        cuerpo.setOpaque(false);

        JPanel pnlTabla = UIKit.card();
        pnlTabla.setLayout(new BorderLayout(0, UIKit.SPACE_SM));

        // searchField + cbFiltroCategoria, ambos angostos (sin botón)
        JPanel pnlBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, UIKit.SPACE_SM, 0));
        pnlBusqueda.setOpaque(false);
        pnlBusqueda.add(txtBuscar);
        pnlBusqueda.add(cbFiltroCategoria);

        pnlTabla.add(UIKit.sectionHeader("Listado de Productos", null), BorderLayout.NORTH);
        pnlTabla.add(pnlBusqueda, BorderLayout.BEFORE_FIRST_LINE);

        JPanel pnlWrapper = new JPanel(new BorderLayout()); pnlWrapper.setOpaque(false);
        JScrollPane scroll = new JScrollPane(tblProductos);
        scroll.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlWrapper.add(scroll, BorderLayout.CENTER);
        pnlWrapper.add(lblEmptyState, BorderLayout.SOUTH);
        pnlTabla.add(pnlWrapper, BorderLayout.CENTER);
        cuerpo.add(pnlTabla, BorderLayout.CENTER);

        // Formulario
        JPanel pnlForm = UIKit.card();
        pnlForm.setPreferredSize(new Dimension(340, 0));
        pnlForm.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlForm.add(UIKit.sectionHeader("Detalle del Producto", null), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("ID Producto"), gbc);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlForm.add(txtId, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("Nombre"), gbc);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlForm.add(txtNombre, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("Descripción"), gbc);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlForm.add(txtDescripcion, gbc);

        // Categoría y Precio
        gbc.gridwidth = 1;
        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, UIKit.SPACE_SM);
        pnlForm.add(UIKit.fieldLabel("Categoría"), gbc);
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("Precio (S/)"), gbc);

        gbc.gridy = 8;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, UIKit.SPACE_SM);
        pnlForm.add(cbCategoria, gbc);
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlForm.add(txtPrecio, gbc);

        // Cantidad
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("Cantidad"), gbc);
        gbc.gridy = 10;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_LG, 0);
        pnlForm.add(txtCantidad, gbc);

        // Botones
        JPanel pnlBotones = new JPanel(new GridLayout(3, 1, 0, UIKit.SPACE_SM));
        pnlBotones.setOpaque(false);
        pnlBotones.add(btnGuardar);
        pnlBotones.add(btnLimpiar);
        pnlBotones.add(btnDesactivar);

        gbc.gridy = 11;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        pnlForm.add(pnlBotones, gbc);

        cuerpo.add(pnlForm, BorderLayout.EAST);
        getContentPane().add(cuerpo, BorderLayout.CENTER);
    }

    private void attachEvents() {

        // BUSCAR en tiempo real (nombre + filtro de categoría)
        java.awt.event.KeyAdapter buscarListener = new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) { filtrarTabla(); }
        };
        txtBuscar.addKeyListener(buscarListener);
        cbFiltroCategoria.addActionListener(e -> filtrarTabla());

        // Botón Nuevo
        btnNuevo.addActionListener(e -> limpiar());

        // GUARDAR / ACTUALIZAR
        btnGuardar.addActionListener(e -> {
            String nombre      = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            String cantidadStr = txtCantidad.getText().trim();
            String precioStr   = txtPrecio.getText().trim();

            // Validación centralizada
            String error = Validador.validarProducto(nombre, precioStr, cantidadStr);
            if (error != null) {
                JOptionPane.showMessageDialog(this, error, "Campo inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validar que no sea inactivo
            if (!txtId.getText().isEmpty()) {
                int row = tblProductos.getSelectedRow();
                if (row != -1) {
                    String estado = modelProductos.getValueAt(row, 5).toString();
                    if (estado.equals("Inactivo")) {
                        JOptionPane.showMessageDialog(this,
                                "No puedes editar un producto inactivo.\nActívalo primero.");
                        return;
                    }
                }
            }

            int cantidad    = Integer.parseInt(cantidadStr);
            double precio   = Double.parseDouble(precioStr.replace(",", "."));
            int idCategoria = obtenerIdCategoria(cbCategoria.getSelectedItem().toString());

            ProductoDAO dao = new ProductoDAO();

            if (txtId.getText().isEmpty()) {
                Producto p = new Producto(0, nombre, cantidad, precio, descripcion, idCategoria, 1);
                if (dao.insertar(p)) {
                    JOptionPane.showMessageDialog(this, "Producto agregado correctamente");
                    cargarTabla();
                    limpiar();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al agregar producto");
                }
            } else {
                int id = Integer.parseInt(txtId.getText());
                Producto p = new Producto(id, nombre, cantidad, precio, descripcion, idCategoria, 1);
                if (dao.actualizar(p)) {
                    JOptionPane.showMessageDialog(this, "Producto actualizado");
                    cargarTabla();
                    limpiar();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar");
                }
            }
        });

        // DESACTIVAR / ACTIVAR
        btnDesactivar.addActionListener(e -> {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione un producto");
                return;
            }
            int id = Integer.parseInt(txtId.getText());
            int row = tblProductos.getSelectedRow();
            String estadoActual = modelProductos.getValueAt(row, 5).toString();
            ProductoDAO dao = new ProductoDAO();

            if (estadoActual.equals("Activo")) {
                int op = JOptionPane.showConfirmDialog(this,
                        "¿Desactivar este producto?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (op != JOptionPane.YES_OPTION) {
                    return;
                }
                if (dao.eliminar(id)) {
                    JOptionPane.showMessageDialog(this, "Producto desactivado");
                    cargarTabla();
                    limpiar();
                    btnDesactivar.setText("Desactivar / Activar");
                }
            } else {
                if (dao.reactivar(id)) {
                    JOptionPane.showMessageDialog(this, "Producto reactivado");
                    cargarTabla();
                    limpiar();
                    btnDesactivar.setText("Desactivar / Activar");
                }
            }
        });

        // LIMPIAR
        btnLimpiar.addActionListener(e -> limpiar());

        // SELECCIONAR FILA
        // SELECCIONAR FILA
        tblProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblProductos.getSelectedRow() != -1) {
                int row = tblProductos.getSelectedRow();
                txtId.setText(modelProductos.getValueAt(row, 0).toString());
                txtNombre.setText(modelProductos.getValueAt(row, 1).toString());
                txtCantidad.setText(modelProductos.getValueAt(row, 2).toString());
                txtPrecio.setText(modelProductos.getValueAt(row, 3).toString());

                // Cargar descripción desde BD
                int idProd = Integer.parseInt(modelProductos.getValueAt(row, 0).toString());
                new ProductoDAO().listarTodos().stream()
                        .filter(p -> p.getIdProducto() == idProd)
                        .findFirst()
                        .ifPresent(p -> txtDescripcion.setText(
                        p.getDescripcion() != null ? p.getDescripcion() : ""));

                String cat = modelProductos.getValueAt(row, 4).toString();
                for (int i = 0; i < cbCategoria.getItemCount(); i++) {
                    if (cbCategoria.getItemAt(i).equals(cat)) {
                        cbCategoria.setSelectedIndex(i);
                        break;
                    }
                }
                String estado = modelProductos.getValueAt(row, 5).toString();
                btnDesactivar.setText(estado.equals("Activo") ? "Desactivar" : "Activar");
            }
        });
    }

    private void cargarTabla() {
        modelProductos.setRowCount(0);
        for (Producto p : new ProductoDAO().listarTodos()) {
            modelProductos.addRow(new Object[]{
                p.getIdProducto(), p.getNombre(), p.getCantidad(),
                String.format("%.2f", p.getPrecio()),
                obtenerNombreCategoria(p.getIdCategoria()),
                p.getEstado() == 1 ? "Activo" : "Inactivo"
            });
        }
        lblEmptyState.setVisible(modelProductos.getRowCount() == 0);
    }

    private void filtrarTabla() {
        String texto  = txtBuscar.getText().trim().toLowerCase();
        String filtro = cbFiltroCategoria.getSelectedItem() != null
                        ? cbFiltroCategoria.getSelectedItem().toString() : "Todas las Categorías";
        modelProductos.setRowCount(0);
        for (Producto p : new ProductoDAO().listarTodos()) {
            String cat = obtenerNombreCategoria(p.getIdCategoria());
            boolean okNombre = p.getNombre().toLowerCase().contains(texto);
            boolean okCat    = filtro.equals("Todas las Categorías") || cat.equals(filtro);
            if (okNombre && okCat) {
                modelProductos.addRow(new Object[]{
                    p.getIdProducto(), p.getNombre(), p.getCantidad(),
                    String.format("%.2f", p.getPrecio()), cat,
                    p.getEstado() == 1 ? "Activo" : "Inactivo"
                });
            }
        }
        lblEmptyState.setVisible(modelProductos.getRowCount() == 0);
    }

    private String obtenerNombreCategoria(int idCategoria) {
        if (listaCategorias != null) {
            for (Categoria c : listaCategorias) {
                if (c.getIdCategoria() == idCategoria) {
                    return c.getDescripcion();
                }
            }
        }
        return "Sin categoría";
    }

    private int obtenerIdCategoria(String nombre) {
        if (listaCategorias != null) {
            for (Categoria c : listaCategorias) {
                if (c.getDescripcion().equals(nombre)) {
                    return c.getIdCategoria();
                }
            }
        }
        return 0;
    }

    private void limpiar() {
        txtId.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtCantidad.setText("");
        txtPrecio.setText("");
        cbCategoria.setSelectedIndex(0);
        tblProductos.clearSelection();
        btnDesactivar.setText("Desactivar / Activar");
    }
}
