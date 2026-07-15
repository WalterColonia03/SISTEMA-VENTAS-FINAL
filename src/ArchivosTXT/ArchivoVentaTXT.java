//package ArchivosTXT;
//
//import Clases.Venta;
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ArchivoVentaTXT {
//
//    private final String RUTA = "ventas.txt";
//
//    // GUARDAR VENTA
//    public void guardar(Venta venta) {
//
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA, true))) {
//
//            bw.write(
//                    venta.getIdVenta() + ";"
//                    + venta.getCliente() + ";"
//                    + venta.getTotal() + ";"
//                    + venta.getFecha()
//            );
//
//            bw.newLine();
//
//        } catch (IOException e) {
//
//            e.printStackTrace();
//        }
//    }
//
//    public void guardar(List<Venta> lista) {
//
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA))) {
//
//            for (Venta venta : lista) {
//
//                bw.write(
//                        venta.getIdVenta() + ";"
//                        + venta.getCliente() + ";"
//                        + venta.getTotal() + ";"
//                        + venta.getFecha()
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
//    // LEER VENTAS
//    public List<Venta> leer() {
//
//        List<Venta> lista = new ArrayList<>();
//
//        File archivo = new File(RUTA);
//
//        // SI EL ARCHIVO NO EXISTE
//        if (!archivo.exists()) {
//
//            return lista;
//        }
//
//        try (BufferedReader br
//                = new BufferedReader(new FileReader(archivo))) {
//
//            String linea;
//
//            while ((linea = br.readLine()) != null) {
//
//                String[] datos = linea.split(";");
//
//                Venta v = new Venta();
//
//                v.setIdVenta(Integer.parseInt(datos[0]));
//                v.setCliente(datos[1]);
//                v.setTotal(Double.parseDouble(datos[2]));
//                v.setFecha(datos[3]);
//
//                lista.add(v);
//            }
//
//        } catch (IOException e) {
//
//            e.printStackTrace();
//        }
//
//        return lista;
//    }
//}
