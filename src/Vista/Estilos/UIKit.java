package Vista.Estilos;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Sistema de diseño centralizado del ERP Minimarket LAREDO.
 * Toda pantalla nueva o rediseñada debe construir su UI usando
 * estos tokens y fábricas — nunca colores/fuentes sueltos.
 */
public final class UIKit {

    private UIKit() {}

    // ===================== PALETA =====================
    public static final Color PRIMARY        = new Color(0x1B3B6F);
    public static final Color PRIMARY_DARK    = new Color(0x142B52);
    public static final Color ACCENT          = new Color(0x2D6CDF);
    public static final Color ACCENT_HOVER    = new Color(0x1E54B7);
    public static final Color BG_APP          = new Color(0xF5F6F8);
    public static final Color BG_CARD         = Color.WHITE;
    public static final Color BORDER          = new Color(0xE1E4E8);
    public static final Color TEXT_PRIMARY    = new Color(0x1F2733);
    public static final Color TEXT_SECONDARY  = new Color(0x5B6472);
    public static final Color SUCCESS         = new Color(0x1E8E5A);
    public static final Color WARNING         = new Color(0xF2994A);
    public static final Color DANGER          = new Color(0xD64550);
    public static final Color INFO            = ACCENT;

    // ===================== TIPOGRAFÍA =====================
    private static final String FAM = "Segoe UI";
    public static final Font H1        = new Font(FAM, Font.BOLD, 20);
    public static final Font H2        = new Font(FAM, Font.BOLD, 15);
    public static final Font SUBTITLE  = new Font(FAM, Font.PLAIN, 13);
    public static final Font BODY      = new Font(FAM, Font.PLAIN, 13);
    public static final Font BODY_BOLD = new Font(FAM, Font.BOLD, 13);
    public static final Font LABEL     = new Font(FAM, Font.BOLD, 11);
    public static final Font CAPTION   = new Font(FAM, Font.PLAIN, 11);
    public static final Font KPI_VALUE = new Font(FAM, Font.BOLD, 22);

    // ===================== ESPACIADO =====================
    public static final int SPACE_XS = 4, SPACE_SM = 8, SPACE_MD = 14, SPACE_LG = 20, SPACE_XL = 28;

    // ===================== BOTONES =====================
    public static JButton primaryButton(String text) {
        JButton b = baseButton(text);
        b.setBackground(ACCENT);
        b.setForeground(Color.WHITE);
        b.putClientProperty(FlatClientProperties.STYLE,
                "arc: 8; focusWidth: 0; hoverBackground: " + hex(ACCENT_HOVER) + ";");
        return b;
    }

    public static JButton secondaryButton(String text) {
        JButton b = baseButton(text);
        b.setBackground(BG_CARD);
        b.setForeground(TEXT_PRIMARY);
        b.putClientProperty(FlatClientProperties.STYLE,
                "arc: 8; borderWidth: 1; borderColor: " + hex(BORDER) + "; focusWidth: 0;");
        return b;
    }

    public static JButton dangerOutlineButton(String text) {
        JButton b = baseButton(text);
        b.setBackground(BG_CARD);
        b.setForeground(DANGER);
        b.putClientProperty(FlatClientProperties.STYLE,
                "arc: 8; borderWidth: 1; borderColor: " + hex(DANGER) + "; focusWidth: 0;");
        return b;
    }

    public static JButton dangerSolidButton(String text) {
        JButton b = baseButton(text);
        b.setBackground(DANGER);
        b.setForeground(Color.WHITE);
        b.putClientProperty(FlatClientProperties.STYLE, "arc: 8; focusWidth: 0;");
        return b;
    }

    private static JButton baseButton(String text) {
        JButton b = new JButton(text);
        b.setFont(BODY_BOLD);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMargin(new Insets(6, 14, 6, 14));
        b.setPreferredSize(new Dimension(b.getPreferredSize().width, 36));
        return b;
    }

    // ===================== CAMPOS DE FORMULARIO =====================
    public static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(LABEL);
        l.setForeground(TEXT_SECONDARY);
        return l;
    }

    public static JTextField textField() {
        JTextField tf = new JTextField();
        tf.setFont(BODY);
        tf.putClientProperty(FlatClientProperties.STYLE,
                "arc: 8; borderColor: " + hex(BORDER) + "; focusedBorderColor: " + hex(ACCENT) + ";");
        tf.setPreferredSize(new Dimension(0, 36));
        return tf;
    }

    /** Campo de solo lectura (reemplaza el patrón "JTextField deshabilitado gris" actual). */
    public static JTextField readOnlyField() {
        JTextField tf = textField();
        tf.setEditable(false);
        tf.setBackground(BG_APP);
        tf.setForeground(TEXT_SECONDARY);
        return tf;
    }

    // ===================== TARJETAS / SECCIONES =====================
    /** Tarjeta blanca con borde sutil, usada para envolver tablas, formularios y bloques. */
    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(BG_CARD);
        p.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(SPACE_MD, SPACE_MD, SPACE_MD, SPACE_MD)));
        return p;
    }

    /** Encabezado estándar de una tarjeta de sección (título + opcional acción a la derecha). */
    public static JPanel sectionHeader(String title, JComponent trailingAction) {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel lbl = new JLabel(title);
        lbl.setFont(H2);
        lbl.setForeground(TEXT_PRIMARY);
        header.add(lbl, BorderLayout.WEST);
        if (trailingAction != null) header.add(trailingAction, BorderLayout.EAST);
        header.setBorder(new EmptyBorder(0, 0, SPACE_SM, 0));
        return header;
    }

    /** Encabezado de pantalla: H1 + subtítulo/breadcrumb. */
    public static JPanel screenHeader(String titulo, String breadcrumb) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel lblBreadcrumb = new JLabel(breadcrumb);
        lblBreadcrumb.setFont(CAPTION);
        lblBreadcrumb.setForeground(TEXT_SECONDARY);
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(H1);
        lblTitulo.setForeground(TEXT_PRIMARY);
        p.add(lblBreadcrumb);
        p.add(lblTitulo);
        p.setBorder(new EmptyBorder(0, 0, SPACE_MD, 0));
        return p;
    }

    // ===================== TARJETA KPI =====================
    public static JPanel kpiCard(String label, String valor, String subLabel, Color acento) {
        JPanel card = card();
        card.setLayout(new BorderLayout(0, SPACE_XS));

        JPanel barra = new JPanel();
        barra.setBackground(acento);
        barra.setPreferredSize(new Dimension(4, 0));

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

        JLabel lblLabel = new JLabel(label.toUpperCase());
        lblLabel.setFont(LABEL);
        lblLabel.setForeground(TEXT_SECONDARY);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(KPI_VALUE);
        lblValor.setForeground(TEXT_PRIMARY);

        JLabel lblSub = new JLabel(subLabel);
        lblSub.setFont(CAPTION);
        lblSub.setForeground(TEXT_SECONDARY);

        contenido.add(lblLabel);
        contenido.add(Box.createVerticalStrut(SPACE_XS));
        contenido.add(lblValor);
        contenido.add(lblSub);

        card.add(barra, BorderLayout.WEST);
        card.add(contenido, BorderLayout.CENTER);
        return card;
    }

    // ===================== BADGE DE ESTADO =====================
    public static JLabel statusBadge(String texto, Color color) {
        JLabel badge = new JLabel(texto);
        badge.setFont(CAPTION);
        badge.setForeground(color);
        badge.setOpaque(false);
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        badge.setBorder(new EmptyBorder(3, 10, 3, 10));
        badge.putClientProperty(FlatClientProperties.STYLE,
                "arc: 999; background: " + hex(mezclarConBlanco(color, 0.85f)) + ";");
        badge.setOpaque(true);
        return badge;
    }

    // ===================== TABLAS =====================
    public static JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(BODY);
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(mezclarConBlanco(ACCENT, 0.88f));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(LABEL);
        header.setBackground(BG_APP);
        header.setForeground(TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(0, 38));
        header.setReorderingAllowed(false);

        // Zebra striping
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? BG_CARD : mezclarConBlanco(BG_APP, 0.4f));
                }
                setBorder(new EmptyBorder(0, SPACE_SM, 0, SPACE_SM));
                return c;
            }
        });
        return table;
    }

    // ===================== HELPERS =====================
    private static String hex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    private static Color mezclarConBlanco(Color c, float proporcionBlanco) {
        int r = (int) (c.getRed() + (255 - c.getRed()) * proporcionBlanco);
        int g = (int) (c.getGreen() + (255 - c.getGreen()) * proporcionBlanco);
        int b = (int) (c.getBlue() + (255 - c.getBlue()) * proporcionBlanco);
        return new Color(r, g, b);
    }
}
