package DAO;

import Clases.Usuario;
import Conexion.Conexion;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

/**
 * UsuarioDAO — acceso a datos de usuario con seguridad reforzada.
 *
 * Mejoras tomadas del minimarket (auth.controller.js):
 *   - Contraseñas hasheadas con BCrypt (nunca texto plano en BD)
 *   - Bloqueo temporal tras 5 intentos fallidos (15 minutos)
 *   - Validación de cuenta activa antes de verificar contraseña
 */
public class UsuarioDAO {

    private static final int  INTENTOS_MAX     = 5;
    private static final int  BLOQUEO_MINUTOS  = 15;

    // ─── LOGIN ───────────────────────────────────────────────────────────────────
    /**
     * Autentica al usuario.
     * @return Usuario si las credenciales son válidas; null en caso contrario.
     * @throws CuentaBloqueadaException si la cuenta está temporalmente bloqueada.
     */
    public Usuario login(String usuario, String contrasena) {
        String sql =
            "SELECT u.idUsuario, u.usuario, u.contrasena, u.idEmpleado, u.estado, "
          + "u.intentos_fallidos, u.bloqueo_hasta, "
          + "e.nombres, e.apellidos, c.nombreCargo "
          + "FROM usuario u "
          + "JOIN empleado e ON u.idEmpleado = e.idEmpleado "
          + "JOIN cargo c ON e.idCargo = c.idCargo "
          + "WHERE u.usuario = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                // Usuario no existe — mismo mensaje genérico que minimarket
                // para no revelar si el usuario existe o no
                return null;
            }

            // Cuenta desactivada
            if (rs.getInt("estado") == 0) {
                return null;
            }

            // Cuenta bloqueada temporalmente
            Timestamp bloqueoHasta = rs.getTimestamp("bloqueo_hasta");
            if (bloqueoHasta != null && new java.util.Date().before(bloqueoHasta)) {
                long msRestantes = bloqueoHasta.getTime() - System.currentTimeMillis();
                int minutosRestantes = (int) Math.ceil(msRestantes / 60000.0);
                throw new CuentaBloqueadaException(minutosRestantes);
            }

            // Verificar contraseña con BCrypt
            String hashAlmacenado = rs.getString("contrasena");
            boolean passwordValida;
            try {
                passwordValida = BCrypt.checkpw(contrasena, hashAlmacenado);
            } catch (IllegalArgumentException e) {
                // El hash no es válido — contraseña probablemente aún en texto plano
                // (base de datos no migrada). Comparar como fallback temporal.
                System.err.println("[UsuarioDAO] AVISO: contraseña no hasheada para usuario '"
                    + usuario + "'. Ejecuta migracion_fase1_seguridad.sql");
                passwordValida = contrasena.equals(hashAlmacenado);
            }

            if (!passwordValida) {
                registrarIntentoFallido(con, rs.getInt("idUsuario"),
                                        rs.getInt("intentos_fallidos"));
                return null;
            }

            // Login exitoso → resetear intentos
            resetearIntentos(con, rs.getInt("idUsuario"));

            return new Usuario(
                rs.getInt("idUsuario"),
                rs.getString("nombres"),
                rs.getString("apellidos"),
                rs.getString("usuario"),
                rs.getString("contrasena"),
                "",
                rs.getString("nombreCargo"),
                rs.getInt("estado")
            );

        } catch (CuentaBloqueadaException e) {
            throw e; // re-lanzar para que FrmLogin la muestre
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ─── INSERTAR ────────────────────────────────────────────────────────────────
    /**
     * Crea un nuevo usuario. La contraseña se hashea automáticamente.
     */
    public boolean insertar(String usuario, String contrasenaTextoPlano, int idEmpleado) {
        String hash = BCrypt.hashpw(contrasenaTextoPlano, BCrypt.gensalt(10));
        String sql  = "INSERT INTO usuario (usuario, contrasena, idEmpleado) VALUES (?,?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, hash);
            ps.setInt(3, idEmpleado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ─── ELIMINAR ────────────────────────────────────────────────────────────────
    public boolean eliminar(int idUsuario) {
        String sql = "DELETE FROM usuario WHERE idUsuario=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ─── ACTUALIZAR CONTRASEÑA ───────────────────────────────────────────────────
    /**
     * Cambia la contraseña de un usuario. El nuevo valor se hashea antes de guardar.
     */
    public boolean actualizarContrasena(int idUsuario, String nuevaContrasenaTextoPlano) {
        String hash = BCrypt.hashpw(nuevaContrasenaTextoPlano, BCrypt.gensalt(10));
        String sql  = "UPDATE usuario SET contrasena=?, intentos_fallidos=0, bloqueo_hasta=NULL "
                    + "WHERE idUsuario=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hash);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ─── DESACTIVAR / REACTIVAR ──────────────────────────────────────────────────
    public boolean desactivar(int idUsuario) {
        return cambiarEstado(idUsuario, 0);
    }

    public boolean reactivar(int idUsuario) {
        // Al reactivar también limpiamos el bloqueo por si quedó pendiente
        String sql = "UPDATE usuario SET estado=1, intentos_fallidos=0, bloqueo_hasta=NULL "
                   + "WHERE idUsuario=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ─── HELPERS PRIVADOS ────────────────────────────────────────────────────────

    private boolean cambiarEstado(int idUsuario, int estado) {
        String sql = "UPDATE usuario SET estado=? WHERE idUsuario=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, estado);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Incrementa intentos_fallidos. Si llega a INTENTOS_MAX, bloquea la cuenta
     * por BLOQUEO_MINUTOS minutos — mismo comportamiento que minimarket.
     */
    private void registrarIntentoFallido(Connection con, int idUsuario, int intentosActuales)
            throws SQLException {
        int nuevosIntentos = intentosActuales + 1;
        String sql;
        PreparedStatement ps;

        if (nuevosIntentos >= INTENTOS_MAX) {
            // Bloquear cuenta
            sql = "UPDATE usuario SET intentos_fallidos=0, bloqueo_hasta=DATE_ADD(NOW(), INTERVAL ? MINUTE) "
                + "WHERE idUsuario=?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, BLOQUEO_MINUTOS);
            ps.setInt(2, idUsuario);
        } else {
            // Solo incrementar contador
            sql = "UPDATE usuario SET intentos_fallidos=? WHERE idUsuario=?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, nuevosIntentos);
            ps.setInt(2, idUsuario);
        }
        ps.executeUpdate();
        ps.close();
    }

    /** Resetea el contador de intentos tras un login exitoso. */
    private void resetearIntentos(Connection con, int idUsuario) throws SQLException {
        String sql = "UPDATE usuario SET intentos_fallidos=0, bloqueo_hasta=NULL WHERE idUsuario=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
    }

    // ─── EXCEPCIÓN PERSONALIZADA ─────────────────────────────────────────────────
    /**
     * Lanzada cuando la cuenta está bloqueada por intentos fallidos.
     * FrmLogin la captura para mostrar el mensaje con el tiempo restante.
     */
    public static class CuentaBloqueadaException extends RuntimeException {
        private final int minutosRestantes;
        public CuentaBloqueadaException(int minutosRestantes) {
            super("Cuenta bloqueada");
            this.minutosRestantes = minutosRestantes;
        }
        public int getMinutosRestantes() { return minutosRestantes; }
    }
}


