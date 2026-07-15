//package ArchivosTXT;
//
//import Clases.Producto;
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ArchivoProductoTXT {
//
//    private final String archivo = "productos.txt";
//
//    //REUTILIZAMOS EL CODIGO DE ArchivoCategoriaTXT
//    // Guarda los productos
//    public void guardar(List<Producto> lista) {
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
//
//            for (Producto p : lista) {
//                bw.write(
//                        p.getIdProducto() + ","
//                        + p.getNombre() + ","
//                        + p.getCantidad() + ","
//                        + p.getPrecio() + ","
//                        + p.getDescripcion() + ","
//                        + p.getIdCategoria() + ","
//                        + //p.getIgv() + "," +
//                        p.getEstado()
//                );
//                bw.newLine();
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // GUARDAR TODA LA LISTA
//    // GUARDAR TODA LA LISTA
//    public void guardarLista(List<Producto> lista) {
//
//        try (BufferedWriter bw
//                = new BufferedWriter(new FileWriter(archivo))) {
//
//            for (Producto p : lista) {
//
//                bw.write(
//                        p.getIdProducto() + ","
//                        + p.getNombre() + ","
//                        + p.getCantidad() + ","
//                        + p.getPrecio() + ","
//                        + p.getDescripcion() + ","
//                        + p.getIdCategoria() + ","
//                        + p.getEstado()
//                );
//
//                bw.newLine();
//            }
//
//        } catch (IOException e) {
//
//            e.printStackTrace();
//        }
//    }
//
//    // 🔹 LEER PRODUCTOS (con IGV)
//    public List<Producto> leer() {
//
//        List<Producto> lista = new ArrayList<>();
//
//        File f = new File(archivo);
//        if (!f.exists()) {
//            return lista;
//        }
//
//        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
//
//            String linea;
//
//            while ((linea = br.readLine()) != null) {
//
//                String[] d = linea.split(",");
//
//                if (d.length == 7) {
//
//                    Producto p = new Producto(
//                            Integer.parseInt(d[0]), // id
//                            d[1], // nombre
//                            Integer.parseInt(d[2]), // cantidad
//                            Double.parseDouble(d[3]),// precio
//                            d[4], // descripcion
//                            Integer.parseInt(d[5]), // idCategoria
//                            //Integer.parseInt(d[6]),  
//                            Integer.parseInt(d[6]) // estado
//                    );
//
//                    lista.add(p);
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
