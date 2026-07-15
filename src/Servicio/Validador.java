package Servicio;

import java.util.Arrays;

/**
 * Validador — clase utilitaria de validaciones de negocio.
 *
 * Centraliza todas las reglas de validación del ERP para que las vistas
 * no repitan lógica. Equivalente al validationMiddleware del minimarket.
 *
 * Uso:
 *   if (!Validador.esDniValido(txt)) { mostrarError("DNI inválido"); return; }
 */
public final class Validador {

    private Validador() {} // No instanciar

    // ─── DOCUMENTOS ──────────────────────────────────────────────────────────────

    /**
     * DNI peruano: exactamente 8 dígitos numéricos.
     */
    public static boolean esDniValido(String dni) {
        return dni != null && dni.matches("^\\d{8}$");
    }

    /**
     * RUC peruano: exactamente 11 dígitos, empieza con 10 o 20.
     */
    public static boolean esRucValido(String ruc) {
        return ruc != null && ruc.matches("^(10|20)\\d{9}$");
    }

    /**
     * RUC simple: solo 11 dígitos (sin validar prefijo).
     */
    public static boolean esRucSimple(String ruc) {
        return ruc != null && ruc.matches("^\\d{11}$");
    }

    // ─── MONTOS ──────────────────────────────────────────────────────────────────

    /**
     * Monto válido: número > 0. Acepta comas como separador decimal.
     */
    public static boolean esMontoValido(String monto) {
        if (monto == null || monto.trim().isEmpty()) return false;
        try {
            return Double.parseDouble(monto.trim().replace(",", ".")) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Monto >= 0 (permite cero, p.ej. apertura de caja).
     */
    public static boolean esMontoNoNegativo(String monto) {
        if (monto == null || monto.trim().isEmpty()) return false;
        try {
            return Double.parseDouble(monto.trim().replace(",", ".")) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ─── NÚMEROS ENTEROS ──────────────────────────────────────────────────────────

    /**
     * Cantidad: entero > 0.
     */
    public static boolean esCantidadValida(String cantidad) {
        if (cantidad == null || cantidad.trim().isEmpty()) return false;
        try {
            return Integer.parseInt(cantidad.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Entero >= 0.
     */
    public static boolean esEnteroNoNegativo(String valor) {
        if (valor == null || valor.trim().isEmpty()) return false;
        try {
            return Integer.parseInt(valor.trim()) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ─── TEXTO ───────────────────────────────────────────────────────────────────

    /**
     * Verifica que todos los campos no estén vacíos ni en blanco.
     */
    public static boolean noVacio(String... campos) {
        return Arrays.stream(campos)
                .allMatch(c -> c != null && !c.trim().isEmpty());
    }

    /**
     * Longitud mínima.
     */
    public static boolean longitudMinima(String texto, int min) {
        return texto != null && texto.trim().length() >= min;
    }

    /**
     * Longitud máxima.
     */
    public static boolean longitudMaxima(String texto, int max) {
        return texto != null && texto.trim().length() <= max;
    }

    // ─── FECHAS ──────────────────────────────────────────────────────────────────

    /**
     * Formato de fecha ISO: YYYY-MM-DD.
     */
    public static boolean esFechaValida(String fecha) {
        if (fecha == null) return false;
        if (!fecha.matches("^\\d{4}-\\d{2}-\\d{2}$")) return false;
        try {
            java.time.LocalDate.parse(fecha);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ─── MÉTODOS COMPUESTOS ───────────────────────────────────────────────────────

    /**
     * Valida los datos básicos de un cliente.
     * @return null si OK, o mensaje de error si falla.
     */
    public static String validarCliente(String nombre, String apellido, String dni) {
        if (!noVacio(nombre, apellido))       return "El nombre y apellido son obligatorios.";
        if (!longitudMaxima(nombre, 100))     return "El nombre no puede tener más de 100 caracteres.";
        if (!dni.trim().isEmpty() && !esDniValido(dni.trim()))
                                              return "El DNI debe tener exactamente 8 dígitos.";
        return null; // OK
    }

    /**
     * Valida los datos básicos de un proveedor.
     * @return null si OK, o mensaje de error si falla.
     */
    public static String validarProveedor(String razonSocial, String ruc) {
        if (!noVacio(razonSocial))        return "La razón social es obligatoria.";
        if (!ruc.trim().isEmpty() && !esRucSimple(ruc.trim()))
                                          return "El RUC debe tener exactamente 11 dígitos.";
        return null; // OK
    }

    /**
     * Valida un producto.
     * @return null si OK, o mensaje de error si falla.
     */
    public static String validarProducto(String nombre, String precioStr, String cantidadStr) {
        if (!noVacio(nombre))             return "El nombre del producto es obligatorio.";
        if (!esMontoValido(precioStr))    return "El precio debe ser un número mayor a 0.";
        if (!esEnteroNoNegativo(cantidadStr)) return "La cantidad debe ser un número >= 0.";
        return null; // OK
    }

    /**
     * Valida una apertura de caja.
     * @return null si OK, o mensaje de error si falla.
     */
    public static String validarAperturaCaja(String montoStr) {
        if (!esMontoNoNegativo(montoStr)) return "El monto de apertura debe ser un número >= 0.";
        return null; // OK
    }
}
