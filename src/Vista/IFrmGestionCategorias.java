package Vista;

import Clases.Categoria;
import DAO.CategoriaDAO;
import Vista.Estilos.UIKit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class IFrmGestionCategorias extends JInternalFrame {

    private JTable tblCategorias;
    private DefaultTableModel modelCategorias;
    private JTextField txtBuscar;
    private JLabel lblEmptyState;

    private JTextField txtId;
    private JTextField txtDescripcion;
    private JComboBox<String> cbEstado;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnLimpiar;
    private JButton btnReactivar;

    public IFrmGestionCategorias() {
        super("Gestión de Categorías", true, true, true, true);
        initComponents();
        buildLayout();
        attachEvents();
        setSize(960, 600);
        cargarTabla();
    }

    private void initComponents() {
        // Sección 5 del prompt: searchField con ícono incrustado, max 260px
        txtBuscar = UIKit.searchField("Buscar categoría...", null);

        String[] columns = {"ID", "Descripción", "Estado"};
        modelCategorias = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tblCategorias = UIKit.styledTable(modelCategorias);

        // Estado vacío
        lblEmptyState = new JLabel("No hay categorías registradas", SwingConstants.CENTER);
        lblEmptyState.setFont(UIKit.BODY);
        lblEmptyState.setForeground(UIKit.TEXT_SECONDARY);
        lblEmptyState.setVisible(false);

        txtId          = UIKit.readOnlyField();
        txtId.setEditable(false);
        txtId.setFocusable(false);
        txtDescripcion = UIKit.textField();

        cbEstado = new JComboBox<>(new String[]{"Activo", "Inactivo"});
        cbEstado.setFont(UIKit.BODY);

        // Sección 5 del prompt: botón primario "+ Nueva Categoría"
        btnNuevo   = UIKit.primaryButton("+ Nueva Categoría");
        btnGuardar   = UIKit.primaryButton("Guardar / Actualizar");
        btnLimpiar   = UIKit.secondaryButton("Limpiar / Nuevo");
        btnReactivar = UIKit.secondaryButton("Reactivar / Desactivar");
    }

    private void buildLayout() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UIKit.BG_APP);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(
                UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));

        // Sección 5 del prompt: título izquierda + botón primario derecha
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        pnlTop.add(UIKit.screenHeader("Gestión de Categorías", "Inventario  ›  Categorías"), BorderLayout.WEST);
        pnlTop.add(btnNuevo, BorderLayout.EAST);
        getContentPane().add(pnlTop, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(UIKit.SPACE_LG, 0));
        cuerpo.setOpaque(false);

        // Tabla
        JPanel pnlTabla = UIKit.card();
        pnlTabla.setLayout(new BorderLayout(0, UIKit.SPACE_SM));

        // Sección 5: buscador angosto a la izquierda, no estirado
        JPanel pnlBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlBusqueda.setOpaque(false);
        pnlBusqueda.add(txtBuscar);

        pnlTabla.add(UIKit.sectionHeader("Listado de Categorías", null), BorderLayout.NORTH);
        pnlTabla.add(pnlBusqueda, BorderLayout.BEFORE_FIRST_LINE);

        JPanel pnlTableWrapper = new JPanel(new BorderLayout());
        pnlTableWrapper.setOpaque(false);
        JScrollPane scroll = new JScrollPane(tblCategorias);
        scroll.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlTableWrapper.add(scroll, BorderLayout.CENTER);
        pnlTableWrapper.add(lblEmptyState, BorderLayout.SOUTH);
        pnlTabla.add(pnlTableWrapper, BorderLayout.CENTER);
        cuerpo.add(pnlTabla, BorderLayout.CENTER);

        // Formulario
        JPanel pnlForm = UIKit.card();
        pnlForm.setPreferredSize(new Dimension(320, 0));
        pnlForm.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlForm.add(UIKit.sectionHeader("Detalle de Categoría", null), gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("ID Categoría"), gbc);
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlForm.add(txtId, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("Descripción"), gbc);
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlForm.add(txtDescripcion, gbc);

        gbc.gridy = 5; gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlForm.add(UIKit.fieldLabel("Estado"), gbc);
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, UIKit.SPACE_LG, 0);
        pnlForm.add(cbEstado, gbc);

        JPanel pnlBotones = new JPanel(new GridLayout(3, 1, 0, UIKit.SPACE_SM));
        pnlBotones.setOpaque(false);
        pnlBotones.add(btnGuardar);
        pnlBotones.add(btnLimpiar);
        pnlBotones.add(btnReactivar);

        gbc.gridy = 7; gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        pnlForm.add(pnlBotones, gbc);

        cuerpo.add(pnlForm, BorderLayout.EAST);
        getContentPane().add(cuerpo, BorderLayout.CENTER);
    }

    private void attachEvents() {

        // BUSCAR en tiempo real
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) {
                filtrarTabla(txtBuscar.getText().trim().toLowerCase());
            }
        });

        // Botón Nuevo: limpia el formulario para alta
        btnNuevo.addActionListener(e -> limpiar());

        // GUARDAR / ACTUALIZAR
        btnGuardar.addActionListener(e -> {
            String descripcion = txtDescripcion.getText().trim();
            if (descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese una descripción");
                return;
            }
            CategoriaDAO dao = new CategoriaDAO();

            if (txtId.getText().isEmpty()) {
                // NUEVO — siempre activo
                if (dao.insertar(new Categoria(0, descripcion, 1))) {
                    JOptionPane.showMessageDialog(this, "Categoría agregada correctamente");
                    cargarTabla();
                    limpiar();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al agregar");
                }
            } else {
                // ACTUALIZAR — solo si está activa
                int id = Integer.parseInt(txtId.getText());
                String estadoActual = cbEstado.getSelectedItem().toString();

                if (estadoActual.equals("Inactivo")) {
                    JOptionPane.showMessageDialog(this,
                        "No puedes editar una categoría inactiva.\nUsa Reactivar primero.");
                    return;
                }

                if (dao.actualizar(new Categoria(id, descripcion, 1))) {
                    JOptionPane.showMessageDialog(this, "Categoría actualizada");
                    cargarTabla();
                    limpiar();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar");
                }
            }
        });

        // REACTIVAR / DESACTIVAR
        btnReactivar.addActionListener(e -> {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione una categoría");
                return;
            }
            int id = Integer.parseInt(txtId.getText());
            String estadoActual = cbEstado.getSelectedItem().toString();
            CategoriaDAO dao = new CategoriaDAO();

            if (estadoActual.equals("Activo")) {
                // DESACTIVAR
                int op = JOptionPane.showConfirmDialog(this,
                    "¿Desactivar esta categoría?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (op != JOptionPane.YES_OPTION) return;

                if (dao.eliminar(id)) {
                    JOptionPane.showMessageDialog(this, "Categoría desactivada");
                    cargarTabla();
                    limpiar();
                    btnReactivar.setText("Reactivar / Desactivar");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al desactivar");
                }
            } else {
                // REACTIVAR
                if (dao.reactivar(id)) {
                    JOptionPane.showMessageDialog(this, "Categoría reactivada");
                    cargarTabla();
                    limpiar();
                    btnReactivar.setText("Reactivar / Desactivar");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al reactivar");
                }
            }
        });

        // LIMPIAR
        btnLimpiar.addActionListener(e -> limpiar());

        // SELECCIONAR FILA — cambia texto del botón según estado
        tblCategorias.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblCategorias.getSelectedRow() != -1) {
                int row = tblCategorias.getSelectedRow();
                txtId.setText(modelCategorias.getValueAt(row, 0).toString());
                txtDescripcion.setText(modelCategorias.getValueAt(row, 1).toString());
                String estado = modelCategorias.getValueAt(row, 2).toString();
                cbEstado.setSelectedItem(estado);

                // Cambia el texto del botón según el estado
                if (estado.equals("Activo")) {
                    btnReactivar.setText("Desactivar");
                } else {
                    btnReactivar.setText("Reactivar");
                }
            }
        });
    }

    private void cargarTabla() {
        modelCategorias.setRowCount(0);
        CategoriaDAO dao = new CategoriaDAO();
        for (Categoria c : dao.listarTodas()) {
            modelCategorias.addRow(new Object[]{
                c.getIdCategoria(),
                c.getDescripcion(),
                c.getEstado() == 1 ? "Activo" : "Inactivo"
            });
        }
        actualizarEstadoVacio();
    }

    private void filtrarTabla(String filtro) {
        modelCategorias.setRowCount(0);
        CategoriaDAO dao = new CategoriaDAO();
        for (Categoria c : dao.listarTodas()) {
            if (c.getDescripcion().toLowerCase().contains(filtro)) {
                modelCategorias.addRow(new Object[]{
                    c.getIdCategoria(),
                    c.getDescripcion(),
                    c.getEstado() == 1 ? "Activo" : "Inactivo"
                });
            }
        }
        actualizarEstadoVacio();
    }

    private void actualizarEstadoVacio() {
        boolean vacio = modelCategorias.getRowCount() == 0;
        lblEmptyState.setVisible(vacio);
    }

    private void limpiar() {
        txtId.setText("");
        txtDescripcion.setText("");
        cbEstado.setSelectedIndex(0);
        tblCategorias.clearSelection();
        btnReactivar.setText("Reactivar / Desactivar");
    }
}