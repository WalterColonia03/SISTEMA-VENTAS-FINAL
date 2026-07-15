//package ArchivosTXT;
//
//import Clases.Usuario;
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ArchivoUsuarioTXT {
//
//    private final String archivo = "usuarios.txt";
//
//    // Guarda el usuario en la lista
//    public void guardar(List<Usuario> lista) {
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, false))) {
//
//            for (Usuario u : lista) {
//                String linea = u.getIdUsuario() + ","
//                        + u.getNombre() + ","
//                        + u.getApellido() + ","
//                        + u.getUsuario() + ","
//                        + u.getPassword() + ","
//                        + u.getTelefono() + ","
//                        + u.getRol() + ","
//                        + u.getEstado();
//
//                bw.write(linea);
//                bw.newLine();
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Esto lee los usuarios que existe en la lista
//    public List<Usuario> leer() {
//        List<Usuario> lista = new ArrayList<>();
//
//        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
//
//            String linea;
//
//            while ((linea = br.readLine()) != null) {
//
//                String[] datos = linea.split(",");
//
//                if (datos.length == 8) {
//
//                    Usuario u = new Usuario(
//                            Integer.parseInt(datos[0]),
//                            datos[1], //nombre
//                            datos[2], //apellido
//                            datos[3], //usuario
//                            datos[4], //contraseña
//                            datos[5], // telefono
//                            datos[6], //rol
//                            Integer.parseInt(datos[7]) //estado
//                    );
//
//                    lista.add(u);
//                }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        // si no existe archivo
//        return lista;
//    }
//}
