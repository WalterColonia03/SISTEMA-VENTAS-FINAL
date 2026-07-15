import java.sql.*;
public class CheckUsers {
    public static void main(String[] args) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/sistemaventas?serverTimezone=UTC&useSSL=false", "root", "root");
            ResultSet rs = con.createStatement().executeQuery("SELECT usuario, contrasena, estado FROM usuario");
            while (rs.next()) {
                System.out.println("User: " + rs.getString("usuario") + ", Pass: " + rs.getString("contrasena") + ", Estado: " + rs.getInt("estado"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
