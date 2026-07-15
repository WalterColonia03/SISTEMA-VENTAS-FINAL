package Clases;

public class Sesion {
    private static String rolActual = "";
    private static String usuarioActual = "";
    private static int idUsuario = 0;
    private static int idEmpleado = 0;

    public static String getRol() { return rolActual; }
    public static void setRol(String rol) { rolActual = rol; }

    public static String getUsuario() { return usuarioActual; }
    public static void setUsuario(String usuario) { usuarioActual = usuario; }

    public static int getIdUsuario() { return idUsuario; }
    public static void setIdUsuario(int id) { idUsuario = id; }

    public static int getIdEmpleado() { return idEmpleado; }
    public static void setIdEmpleado(int id) { idEmpleado = id; }
}