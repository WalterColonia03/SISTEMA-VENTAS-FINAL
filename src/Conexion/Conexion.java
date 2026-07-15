package Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL      = "jdbc:mysql://localhost:3306/sistemaventas?serverTimezone=UTC&useSSL=false";
    private static final String USUARIO  = "root";
    private static final String PASSWORD = "root"; // ← tu contraseña de MySQL aquí

    public static Connection getConexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            return null;
        }
    }
}