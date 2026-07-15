package Vista;

import Clases.Cliente;
import Clases.Producto;
import DAO.ClienteDAO;
import DAO.KardexDAO;
import DAO.ProductoDAO;
import DAO.VentaDAO;
import Vista.Estilos.UIKit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class IFrmPuntoVenta extends JInternalFrame {

    private JTextField txtDni;
    private JLabel lblNombreCliente;
    private JTextField txtCodProducto;
    private JTextField txtCantidad;
    private JTable tblCarrito;
    private DefaultTableModel modelCarrito;

    private JComboBox<String> cbMetodoPago;
    private JLabel lblSubtotal;
    private JLabel lblIgv;
    private JLabel lblTotal;
    private JButton btnAgregar;
    private JButton btnBuscarCliente;
    private JButton btnQuitarItem;
    private JButton btnRegistrar;
    private JButton btnCancelar;

    // NUEVOS CAMPOS
    private JTextField txtMontoCliente;
    private JLabel lblVuelto;
    private JLabel lblVueltoValor;

    private int idClienteActivo = 0;
    private JTextField txtNroOperacion;
    private JLabel lblNroOperacion;

    public IFrmPuntoVenta() {
        super("Punto de Venta (POS)", true, true, true, true);
        initComponents();
        buildLayout();
        attachEvents();
        setSize(1000, 650);
    }

    private void initComponents() {
        txtDni = UIKit.textField();
        txtDni.setPreferredSize(new Dimension(150, 36));

        lblNombreCliente = new JLabel("Consumidor Final");
        lblNombreCliente.setFont(UIKit.BODY_BOLD);
        lblNombreCliente.setForeground(UIKit.PRIMARY);

        txtCodProducto = UIKit.textField();
        txtCodProducto.setPreferredSize(new Dimension(180, 36));

        txtCantidad = UIKit.textField();
        txtCantidad.setText("1");
        txtCantidad.setHorizontalAlignment(JTextField.CENTER);
        txtCantidad.setPreferredSize(new Dimension(80, 36));

        String[] columns = {"ID", "Producto", "P. Unit", "Cant.", "Subtotal"};
        modelCarrito = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tblCarrito = UIKit.styledTable(modelCarrito);

        cbMetodoPago = new JComboBox<>(
            
                new String[]{"Efectivo", "Yape", "Plin", "Tarjeta"});
        cbMetodoPago.setFont(UIKit.BODY);
        cbMetodoPago.setPreferredSize(new Dimension(0, 36));

        lblSubtotal = new JLabel("S/ 0.00");
        lblSubtotal.setFont(UIKit.BODY_BOLD);

        lblIgv = new JLabel("S/ 0.00");
        lblIgv.setFont(UIKit.BODY_BOLD);

        lblTotal = new JLabel("S/ 0.00");
        lblTotal.setFont(UIKit.H1);
        lblTotal.setForeground(UIKit.ACCENT);

        // Monto del cliente
        txtMontoCliente = UIKit.textField();
        txtMontoCliente.setHorizontalAlignment(JTextField.RIGHT);
        txtMontoCliente.setPreferredSize(new Dimension(0, 36));

        lblVuelto = new JLabel("VUELTO");
        lblVuelto.setFont(UIKit.BODY_BOLD);
        lblVuelto.setForeground(UIKit.TEXT_SECONDARY);

        lblVueltoValor = new JLabel("S/ 0.00");
        lblVueltoValor.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblVueltoValor.setForeground(UIKit.SUCCESS);

        // N° de operación para pagos digitales
        txtNroOperacion = UIKit.textField();
        txtNroOperacion.setHorizontalAlignment(JTextField.CENTER);
        txtNroOperacion.setPreferredSize(new Dimension(0, 36));
        txtNroOperacion.setEnabled(false);
        lblNroOperacion = UIKit.fieldLabel("N° de Operación");
        lblNroOperacion.setEnabled(false);
        
        btnAgregar = UIKit.secondaryButton("Agregar");
        btnBuscarCliente = UIKit.secondaryButton("Buscar");
        btnQuitarItem = UIKit.dangerOutlineButton("Quitar");

        btnRegistrar = UIKit.primaryButton("Cobrar y Registrar");
        btnRegistrar.setPreferredSize(new Dimension(0, 44));

        btnCancelar = UIKit.dangerOutlineButton("Cancelar Venta");
    }

    private void buildLayout() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UIKit.BG_APP);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(
                UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG, UIKit.SPACE_LG));

        getContentPane().add(
                UIKit.screenHeader("Punto de Venta (POS)", "Ventas  ›  Punto de Venta"),
                BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(UIKit.SPACE_LG, 0));
        cuerpo.setOpaque(false);

        // ── Izquierda ──
        JPanel pnlIzquierda = new JPanel(new BorderLayout(0, UIKit.SPACE_MD));
        pnlIzquierda.setOpaque(false);

        // Cliente
        JPanel pnlCliente = UIKit.card();
        pnlCliente.setLayout(new BorderLayout(0, UIKit.SPACE_SM));
        pnlCliente.add(UIKit.sectionHeader("Información del Cliente", null), BorderLayout.NORTH);

        JPanel pnlBusquedaC = new JPanel(new FlowLayout(FlowLayout.LEFT, UIKit.SPACE_SM, 0));
        pnlBusquedaC.setOpaque(false);
        pnlBusquedaC.add(UIKit.fieldLabel("DNI / RUC:"));
        pnlBusquedaC.add(txtDni);
        pnlBusquedaC.add(btnBuscarCliente);

        JPanel pnlNombreC = new JPanel(new FlowLayout(FlowLayout.LEFT, UIKit.SPACE_SM, 0));
        pnlNombreC.setOpaque(false);
        pnlNombreC.add(UIKit.fieldLabel("Cliente:"));
        pnlNombreC.add(lblNombreCliente);

        JPanel pnlClienteBody = new JPanel(new GridLayout(2, 1, 0, UIKit.SPACE_XS));
        pnlClienteBody.setOpaque(false);
        pnlClienteBody.add(pnlBusquedaC);
        pnlClienteBody.add(pnlNombreC);
        pnlCliente.add(pnlClienteBody, BorderLayout.CENTER);
        pnlIzquierda.add(pnlCliente, BorderLayout.NORTH);

        // Carrito
        JPanel pnlCarrito = UIKit.card();
        pnlCarrito.setLayout(new BorderLayout(0, UIKit.SPACE_SM));
        pnlCarrito.add(UIKit.sectionHeader("Detalle de Venta", btnQuitarItem), BorderLayout.NORTH);

        JPanel pnlAgregarProd = new JPanel(new FlowLayout(FlowLayout.LEFT, UIKit.SPACE_SM, 0));
        pnlAgregarProd.setOpaque(false);
        JButton btnBuscarProd = UIKit.secondaryButton("Buscar Producto");
        btnBuscarProd.addActionListener(e -> {
            DlgBuscadorProducto dlg = new DlgBuscadorProducto(this);
            dlg.setVisible(true);
            if (dlg.isSeleccionado()) {
                txtCodProducto.setText(String.valueOf(dlg.getIdProducto()));
            }
        });

        pnlAgregarProd.add(UIKit.fieldLabel("ID:"));
        pnlAgregarProd.add(txtCodProducto);
        pnlAgregarProd.add(btnBuscarProd);
        pnlAgregarProd.add(UIKit.fieldLabel("Cant:"));
        pnlAgregarProd.add(txtCantidad);
        pnlAgregarProd.add(btnAgregar);

        JPanel pnlCarritoBody = new JPanel(new BorderLayout(0, UIKit.SPACE_SM));
        pnlCarritoBody.setOpaque(false);
        pnlCarritoBody.add(pnlAgregarProd, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tblCarrito);
        scroll.setBorder(BorderFactory.createLineBorder(UIKit.BORDER));
        pnlCarritoBody.add(scroll, BorderLayout.CENTER);

        pnlCarrito.add(pnlCarritoBody, BorderLayout.CENTER);
        pnlIzquierda.add(pnlCarrito, BorderLayout.CENTER);
        cuerpo.add(pnlIzquierda, BorderLayout.CENTER);

        // ── Derecha: Resumen ──
        JPanel pnlDerecha = UIKit.card();
        pnlDerecha.setPreferredSize(new Dimension(280, 0));
        pnlDerecha.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_LG, 0);
        pnlDerecha.add(UIKit.sectionHeader("Resumen de Pago", null), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlDerecha.add(UIKit.fieldLabel("Método de Pago"), gbc);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_LG, 0);
        pnlDerecha.add(cbMetodoPago, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlDerecha.add(UIKit.fieldLabel("Subtotal"), gbc);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlDerecha.add(lblSubtotal, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlDerecha.add(UIKit.fieldLabel("IGV (18%)"), gbc);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_LG, 0);
        pnlDerecha.add(lblIgv, gbc);

        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        JLabel lblTotalTitle = new JLabel("TOTAL A PAGAR");
        lblTotalTitle.setFont(UIKit.BODY_BOLD);
        lblTotalTitle.setForeground(UIKit.TEXT_SECONDARY);
        pnlDerecha.add(lblTotalTitle, gbc);

        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_LG, 0);
        pnlDerecha.add(lblTotal, gbc);

        // Monto del cliente
        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlDerecha.add(UIKit.fieldLabel("Monto del Cliente (S/)"), gbc);
        gbc.gridy = 10;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlDerecha.add(txtMontoCliente, gbc);

       gbc.gridy = 11;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlDerecha.add(lblVuelto, gbc);
        gbc.gridy = 12;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlDerecha.add(lblVueltoValor, gbc);

        gbc.gridy = 13;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_XS, 0);
        pnlDerecha.add(lblNroOperacion, gbc);
        gbc.gridy = 14;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_LG, 0);
        pnlDerecha.add(txtNroOperacion, gbc);

        
        gbc.gridy = 15;
        gbc.insets = new Insets(0, 0, UIKit.SPACE_MD, 0);
        pnlDerecha.add(btnRegistrar, gbc);

        gbc.gridy = 16;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        pnlDerecha.add(btnCancelar, gbc);

        cuerpo.add(pnlDerecha, BorderLayout.EAST);
        getContentPane().add(cuerpo, BorderLayout.CENTER);
    }

    private void attachEvents() {

        // BUSCAR CLIENTE
        btnBuscarCliente.addActionListener(e -> buscarCliente());
        txtDni.addActionListener(e -> buscarCliente());

        // AGREGAR PRODUCTO
        btnAgregar.addActionListener(e -> agregarProducto());
        txtCodProducto.addActionListener(e -> agregarProducto());

        // QUITAR ITEM
        btnQuitarItem.addActionListener(e -> {
            int row = tblCarrito.getSelectedRow();
            if (row != -1) {
                modelCarrito.removeRow(row);
                recalcularTotal();
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla");
            }
        });

        // MÉTODO DE PAGO — activar/desactivar monto cliente y vuelto
       cbMetodoPago.addActionListener(e -> {
            String metodo = cbMetodoPago.getSelectedItem().toString();
            boolean esEfectivo = metodo.equals("Efectivo");

            // Efectivo: monto cliente + vuelto visible, N° operación oculto
            txtMontoCliente.setEnabled(esEfectivo);
            lblVuelto.setEnabled(esEfectivo);
            lblVueltoValor.setEnabled(esEfectivo);

            // Digital: N° operación visible, monto/vuelto oculto
            txtNroOperacion.setEnabled(!esEfectivo);
            lblNroOperacion.setEnabled(!esEfectivo);

            if (esEfectivo) {
                txtNroOperacion.setText("");
                lblVuelto.setForeground(UIKit.TEXT_SECONDARY);
                lblVueltoValor.setForeground(UIKit.SUCCESS);
            } else {
                txtMontoCliente.setText("");
                lblVueltoValor.setText("S/ 0.00");
                lblVuelto.setForeground(Color.LIGHT_GRAY);
                lblVueltoValor.setForeground(Color.LIGHT_GRAY);
            }
        });
       
        // CALCULAR VUELTO al escribir monto
        txtMontoCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                calcularVuelto();
            }
        });

        // REGISTRAR VENTA
        btnRegistrar.addActionListener(e -> registrarVenta());

        // CANCELAR
        btnCancelar.addActionListener(e -> cancelarVenta());
    }

    private void calcularVuelto() {
        try {
            String montoStr = txtMontoCliente.getText().trim().replace(",", ".");
            if (montoStr.isEmpty()) {
                lblVueltoValor.setText("S/ 0.00");
                lblVueltoValor.setForeground(UIKit.SUCCESS);
                return;
            }
            double monto = Double.parseDouble(montoStr);
            double total = calcularTotalConIgv();
            double vuelto = monto - total;

            if (vuelto < 0) {
                lblVueltoValor.setText("S/ " + String.format("%.2f", Math.abs(vuelto)) + " (falta)");
                lblVueltoValor.setForeground(UIKit.DANGER);
            } else {
                lblVueltoValor.setText("S/ " + String.format("%.2f", vuelto));
                lblVueltoValor.setForeground(UIKit.SUCCESS);
            }
        } catch (NumberFormatException ex) {
            lblVueltoValor.setText("S/ 0.00");
        }
    }

    private void buscarCliente() {
        String dni = txtDni.getText().trim();
        if (dni.isEmpty()) {
            return;
        }

        ClienteDAO dao = new ClienteDAO();
        Cliente c = dao.buscarPorDni(dni);

        if (c != null) {
            idClienteActivo = c.getIdCliente();
            lblNombreCliente.setText(c.getNombre() + " " + c.getApellido());
        } else {
            idClienteActivo = 0;
            lblNombreCliente.setText("Consumidor Final");
            JOptionPane.showMessageDialog(this, "Cliente no encontrado");
        }
    }

    private void agregarProducto() {
        String codStr = txtCodProducto.getText().trim();
        String cantStr = txtCantidad.getText().trim();

        if (codStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID del producto");
            return;
        }

        try {
            int id = Integer.parseInt(codStr);
            int cantidad = Integer.parseInt(cantStr);

            ProductoDAO dao = new ProductoDAO();
            Producto p = dao.listar().stream()
                    .filter(prod -> prod.getIdProducto() == id)
                    .findFirst().orElse(null);

            if (p == null) {
                JOptionPane.showMessageDialog(this, "Producto no encontrado");
                return;
            }

            // Buscar si el producto ya está en el carrito
            int filaExistente = -1;
            int cantidadExistente = 0;
            for (int i = 0; i < modelCarrito.getRowCount(); i++) {
                int idFila = Integer.parseInt(modelCarrito.getValueAt(i, 0).toString());
                if (idFila == id) {
                    filaExistente = i;
                    cantidadExistente = Integer.parseInt(modelCarrito.getValueAt(i, 3).toString());
                    break;
                }
            }

            int cantidadTotal = cantidadExistente + cantidad;

            if (p.getCantidad() < cantidadTotal) {
                JOptionPane.showMessageDialog(this,
                        "Stock insuficiente. Stock disponible: " + p.getCantidad()
                        + (cantidadExistente > 0 ? " (ya tienes " + cantidadExistente + " en el carrito)" : ""));
                return;
            }

            double subtotal = cantidadTotal * p.getPrecio();

            if (filaExistente != -1) {
                // Actualizar fila existente
                modelCarrito.setValueAt(cantidadTotal, filaExistente, 3);
                modelCarrito.setValueAt(String.format("%.2f", subtotal), filaExistente, 4);
            } else {
                // Nueva fila
                modelCarrito.addRow(new Object[]{
                    p.getIdProducto(),
                    p.getNombre(),
                    String.format("%.2f", p.getPrecio()),
                    cantidad,
                    String.format("%.2f", cantidad * p.getPrecio())
                });
            }

            recalcularTotal();
            txtCodProducto.setText("");
            txtCantidad.setText("1");
            txtCodProducto.requestFocus();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID y cantidad deben ser números válidos");
        }
    }

    private void registrarVenta() {
        if (modelCarrito.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Agregue al menos un producto");
            return;
        }

        String metodo = cbMetodoPago.getSelectedItem().toString();
        double total = calcularSubtotal();
        double subtotal = total / 1.18;
        double igv = total / 1.18 * 0.18;

        
        // Validar según método de pago
        if (metodo.equals("Efectivo")) {
            String montoStr = txtMontoCliente.getText().trim().replace(",", ".");
            if (!montoStr.isEmpty()) {
                try {
                    double monto = Double.parseDouble(montoStr);
                    if (monto < total) {
                        JOptionPane.showMessageDialog(this,
                                "El monto recibido es insuficiente.\n"
                                + "Total: S/ " + String.format("%.2f", total) + "\n"
                                + "Monto ingresado: S/ " + String.format("%.2f", monto),
                                "Monto Insuficiente", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Ingrese un monto válido");
                    return;
                }
            }
        } else {
            // Yape, Plin, Tarjeta — validar N° operación
            String nroOp = txtNroOperacion.getText().trim();
            if (nroOp.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Ingrese el N° de operación para pago con " + metodo,
                    "N° Operación Requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        String vueltoStr = "";
        String nroOpStr = "";
        if (metodo.equals("Efectivo")) {
            String montoStr = txtMontoCliente.getText().trim().replace(",", ".");
            if (!montoStr.isEmpty()) {
                try {
                    double monto = Double.parseDouble(montoStr);
                    double vuelto = monto - total;
                    vueltoStr = "\nVuelto: S/ " + String.format("%.2f", vuelto);
                } catch (NumberFormatException ignored) {
                }
            }
        } else {
            nroOpStr = "\nN° Operación: " + txtNroOperacion.getText().trim();
        }

        // CONFIRMACIÓN antes de cobrar
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Confirmar la venta?\n\n"
                + "Cliente: " + lblNombreCliente.getText() + "\n"
                + "Subtotal (sin IGV): S/ " + String.format("%.2f", subtotal) + "\n"
                + "IGV incluido (18%): S/ " + String.format("%.2f", igv) + "\n"
                + "Total: S/ " + String.format("%.2f", total) + "\n"
                + "Método: " + metodo   
                + vueltoStr
                + nroOpStr,
                "Confirmar Venta",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // REGISTRAR
        int idCliente = idClienteActivo > 0 ? idClienteActivo : 1;
        int idUsuario = 1;

        VentaDAO ventaDAO = new VentaDAO();
        int idVenta = ventaDAO.insertar(idCliente, idUsuario, subtotal, igv, total, metodo);

        if (idVenta == -1) {
            JOptionPane.showMessageDialog(this, "Error al registrar la venta");
            return;
        }

        ProductoDAO productoDAO = new ProductoDAO();
        KardexDAO kardexDAO = new KardexDAO();

        for (int i = 0; i < modelCarrito.getRowCount(); i++) {
            int idProducto = Integer.parseInt(modelCarrito.getValueAt(i, 0).toString());
            int cantidad = Integer.parseInt(modelCarrito.getValueAt(i, 3).toString());
            double precio = Double.parseDouble(modelCarrito.getValueAt(i, 2).toString().replace(",", "."));
            double sub = Double.parseDouble(modelCarrito.getValueAt(i, 4).toString().replace(",", "."));

            ventaDAO.insertarDetalle(idVenta, idProducto, cantidad, precio, 0, sub);

            Producto p = productoDAO.listar().stream()
                    .filter(prod -> prod.getIdProducto() == idProducto)
                    .findFirst().orElse(null);

            if (p != null) {
                int stockAnterior = p.getCantidad();
                int stockNuevo = stockAnterior - cantidad;
                productoDAO.actualizarStock(idProducto, stockNuevo);
                kardexDAO.registrar(idProducto, "SALIDA", cantidad,
                        stockAnterior, stockNuevo, "VENTA #" + idVenta, idUsuario);
            }
        }

        JOptionPane.showMessageDialog(this,
                "Venta registrada correctamente\n"
                + "N° Venta: #" + idVenta + "\n"
                + "Total: S/ " + String.format("%.2f", total) + "\n"
                + "Método: " + metodo
                + vueltoStr
                + nroOpStr,
                "Venta Exitosa", JOptionPane.INFORMATION_MESSAGE);

        cancelarVenta();
    }

    private void recalcularTotal() {
        double total = calcularSubtotal(); // El total ES la suma de precios
        double igv = total / 1.18 * 0.18; // IGV extraído (informativo)
        double subtotal = total / 1.18; // Base imponible sin IGV

        lblSubtotal.setText(String.format("S/ %.2f", subtotal));
        lblIgv.setText(String.format("S/ %.2f", igv));
        lblTotal.setText(String.format("S/ %.2f", total));
        calcularVuelto();
    }

    private double calcularSubtotal() {
        double total = 0;
        for (int i = 0; i < modelCarrito.getRowCount(); i++) {
            total += Double.parseDouble(
                    modelCarrito.getValueAt(i, 4).toString().replace(",", "."));
        }
        return total;
    }

    private double calcularTotalConIgv() {
        return calcularSubtotal(); // El total ya incluye IGV
    }

    private void cancelarVenta() {
        modelCarrito.setRowCount(0);
        txtDni.setText("");
        txtCodProducto.setText("");
        txtCantidad.setText("1");
        txtMontoCliente.setText("");
        lblNombreCliente.setText("Consumidor Final");
        lblSubtotal.setText("S/ 0.00");
        lblIgv.setText("S/ 0.00");
        lblTotal.setText("S/ 0.00");
        lblVueltoValor.setText("S/ 0.00");
        idClienteActivo = 0;
        txtNroOperacion.setText("");
    }
}
