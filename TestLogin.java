import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;
import Conexion.Conexion;

public class TestLogin {
    public static void main(String[] args) throws Exception {
        String newHash = BCrypt.hashpw("1234", BCrypt.gensalt(10));
        System.out.println("New Hash for 1234: " + newHash);
        Connection con = Conexion.getConexion();
        PreparedStatement ps = con.prepareStatement("UPDATE usuario SET contrasena = ?");
        ps.setString(1, newHash);
        ps.executeUpdate();
        System.out.println("Passwords updated to 1234.");
    }
}
