package cal335.projet.mes_chums.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OpenWeatherMapService {

    private final String BASE_URL = "http://api.openweathermap.org/geo/1.0/";
    private final String API_KEY = "ac8f194429e89a33b11ba52c734ff9ad";

    public double[] getCoordoneByCity(String city) {
        double[] coordinates = new double[2]; // Index 0: lat, Index 1: lon
        try {
            // Construire l'URL
            String urlStr = BASE_URL + "direct?q=" + city + "&limit=1&appid=" + API_KEY;
            URL url = new URL(urlStr);

            // Ouvrir la connexion
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Vérifier la réponse
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Utiliser Jackson pour analyser le JSON sans créer de classe
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(response.toString());

                // Vérifier si le tableau contient au moins un élément
                if (rootNode.isArray() && rootNode.size() > 0) {
                    JsonNode firstNode = rootNode.get(0);
                    coordinates[0] = firstNode.get("lat").asDouble();
                    coordinates[1] = firstNode.get("lon").asDouble();
                } else {
                    System.out.println("Aucune donnée trouvée pour la ville : " + city);
                }
            } else {
                System.out.println("Échec de la requête : code " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return coordinates;
    }

/*
    // Test de la méthode getCoordoneByCity
    public static void main(String[] args) {
        OpenWeatherMapService service = new OpenWeatherMapService();
        double[] coords = service.getCoordoneByCity("Montréal");
        if (coords[0] != 0 && coords[1] != 0) {
            System.out.println("Latitude: " + coords[0] + ", Longitude: " + coords[1]);
        } else {
            System.out.println("Impossible d'obtenir les coordonnées.");
        }
    }

 */
}


