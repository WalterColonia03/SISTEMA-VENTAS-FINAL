package Conexion;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Conexion — lee las credenciales de BD desde config.properties (no subido al
 * repositorio). Si el archivo no existe, usa valores por defecto de desarrollo.
 *
 * Referencia de buenas prácticas: minimarket usa .env + .env.example; acá
 * hacemos lo mismo con config.properties + config.properties.example.
 */
public class Conexion {

    private static final String URL;
    private static final String USUARIO;
    private static final String PASSWORD;

    static {
        Properties props = new Properties();

        // Busca config.properties en el directorio de trabajo (raíz del proyecto)
        try (InputStream is = new FileInputStream("config.properties")) {
            props.load(is);
            System.out.println("[Conexion] config.properties cargado correctamente.");
        } catch (IOException e) {
            System.err.println("[Conexion] AVISO: config.properties no encontrado. "
                + "Usando valores por defecto de desarrollo. "
                + "Copia config.properties.example → config.properties y edita tus credenciales.");
        }

        // Fallback a valores locales si el archivo no existe
        URL      = props.getProperty("db.url",
                   "jdbc:mysql://localhost:3306/sistemaventas?serverTimezone=UTC&useSSL=false");
        USUARIO  = props.getProperty("db.user",     "root");
        PASSWORD = props.getProperty("db.password", "root");
    }

    public static Connection getConexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("[Conexion] Error al conectar con la BD: " + e.getMessage());
            return null;
        }
    }
}