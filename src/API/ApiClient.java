package API;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {

    private static final String TOKEN_DECOLECTA = "sk_16843.jT4J5Qyf5W4QLADKeUx3hO7c2mL8kFbj";
    private static final String BASE_URL_DECOLECTA = "https://api.decolecta.com/v1";

    private static final String TOKEN_APISPERU = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6Indjb2xvbmlhaTFAdXBhby5lZHUucGUifQ.GpP-Cro4IcYR2NWeEthQyh8JqC-FL4mBbzeqzUnG2-U";
    private static final String BASE_URL_APISPERU = "https://dniruc.apisperu.com/api/v1";

    public static String[] consultarDni(String dni) {
        if (dni == null || dni.length() != 8) return null;
        try {
            URL url = new URL(BASE_URL_APISPERU + "/dni/" + dni);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + TOKEN_APISPERU);
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();
                String json = response.toString();
                // Extracción manual simple de JSON para Java 8 sin librerías externas
                String nombres = extractJsonValue(json, "nombres");
                String apePaterno = extractJsonValue(json, "apellidoPaterno");
                String apeMaterno = extractJsonValue(json, "apellidoMaterno");
                if (nombres != null && apePaterno != null) {
                    return new String[]{nombres, apePaterno + " " + apeMaterno};
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String[] consultarRuc(String ruc) {
        if (ruc == null || ruc.length() != 11) return null;
        try {
            URL url = new URL(BASE_URL_APISPERU + "/ruc/" + ruc);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + TOKEN_APISPERU);
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();
                String json = response.toString();
                String razonSocial = extractJsonValue(json, "razonSocial");
                String direccion = extractJsonValue(json, "direccion");
                if (razonSocial != null) {
                    return new String[]{razonSocial, direccion != null ? direccion : ""};
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Método utilitario para extraer valores de un JSON plano de forma nativa
    private static String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int index = json.indexOf(searchKey);
        if (index == -1) return null;
        int startIndex = json.indexOf("\"", index + searchKey.length());
        if (startIndex == -1) return null;
        int endIndex = json.indexOf("\"", startIndex + 1);
        if (endIndex == -1) return null;
        return json.substring(startIndex + 1, endIndex);
    }
}
