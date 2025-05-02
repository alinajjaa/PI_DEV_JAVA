package com.example.AgritraceNes.Controllers;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapGenerator {
    public static String getCoordinates(String location) throws Exception {
        String url = "https://nominatim.openstreetmap.org/search?q=" +
                location.replace(" ", "+") +
                "&format=json&addressdetails=1&limit=1";

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Java client");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            String responseStr = response.toString();
            if (responseStr.isEmpty() || !responseStr.contains("\"lat\":")) {
                throw new Exception("No coordinates found for location: " + location);
            }

            String lat = responseStr.split("\"lat\":\"")[1].split("\"")[0];
            String lon = responseStr.split("\"lon\":\"")[1].split("\"")[0];
            return lat + "," + lon;
        } finally {
            connection.disconnect();
        }
    }

    public static String generateHTML(String location) {
        try {
            String coordinates = getCoordinates(location); // On appelle la méthode getCoordinates pour obtenir les coordonnées de la localisation.
            return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="utf-8">
                    <title>Carte</title>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css" />
                    <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
                    <style>
                        #map { height: 100%%; width: 100%%; }
                        body { margin: 0; padding: 0; }
                        html, body { height: 100%%; }
                    </style>
                </head>
                <body>
                    <div id="map"></div>
                    <script>
                        var map = L.map('map').setView([%s], 13);
                        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
                            maxZoom: 19
                        }).addTo(map);
                        
                        var marker = L.marker([%s]).addTo(map);
                        marker.bindPopup("<b>%s</b>").openPopup();
                    </script>
                </body>
                </html>
                """, coordinates, coordinates, location);
        } catch (Exception e) {
            e.printStackTrace();
            return """
                <html>
                <body>
                    <h3>Erreur lors de la récupération des coordonnées.</h3>
                    <p>%s</p>
                </body>
                </html>
                """.formatted(e.getMessage());
        }
    }
}