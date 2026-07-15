//package ArchivosTXT;
//
//import Clases.Categoria;
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ArchivoCategoriaTXT {
//
//    private final String archivo = "categorias.txt";
//
//    // Guarda las categorias
//    public void guardar(List<Categoria> lista) {
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
//
//            for (Categoria c : lista) {
//                bw.write(
//                        c.getIdCategoria() + ","
//                        + c.getDescripcion() + ","
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
//    // Recorre el txt
//    public List<Categoria> leer() {
//        List<Categoria> lista = new ArrayList<>();
//
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
//                String[] datos = linea.split(",");
//
//                if (datos.length == 3) {
//
//                    Categoria c = new Categoria(
//                            Integer.parseInt(datos[0]),
//                            datos[1],
//                            Integer.parseInt(datos[2])
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
