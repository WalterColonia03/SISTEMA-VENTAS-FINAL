//package ArchivosTXT;
//
//import Clases.Cliente;
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ArchivoClienteTXT {
//
//    private final String archivo = "clientes.txt";
//
//    // Guarda los cliente que se registran
//    public void guardar(List<Cliente> lista) {
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
//
//            for (Cliente c : lista) {
//                bw.write(
//                        c.getIdCliente() + ","
//                        + c.getNombre() + ","
//                        + c.getApellido() + ","
//                        + c.getDni() + ","
//                        + c.getTelefono() + ","
//                        + c.getDireccion() + ","
//                        + c.getEstado()
//                );
//                bw.newLine();
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Lee los clientes registrados
//    public List<Cliente> leer() {
//
//        List<Cliente> lista = new ArrayList<>();
//        // Verifica si el archivo existe para evitar errores al leer
//        // Si no existe, devuelve una lista vacía
//        File f = new File(archivo);
//        if (!f.exists()) {
//            return lista;
//        }
//        // El try catch evita que mi progama se rompa o se cierre si hay un error(el error solo se muentra en consola pero el progama sigue)
//        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
//
//            String linea;
//
//            while ((linea = br.readLine()) != null) {
//
//                String[] d = linea.split(",");
//
//                // Validamos que tenga 7 datos
//                if (d.length == 7) {
//                    Cliente c = new Cliente(
//                            Integer.parseInt(d[0]),
//                            d[1],
//                            d[2],
//                            d[3],
//                            d[4],
//                            d[5],
//                            Integer.parseInt(d[6])
//                    );
//
//                    lista.add(c);
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return lista;
//    }
//}
