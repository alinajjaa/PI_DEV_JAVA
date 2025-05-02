package com.example.AgritraceNes.Controllers;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class MapViewController {
    @FXML
    private WebView mapWebView;

    public void initialize() {
        // Initialization code if needed
    }

    public void loadMap(String location) {
        String mapHTML = MapGenerator.generateHTML(location);
        WebEngine engine = mapWebView.getEngine();
        engine.loadContent(mapHTML);
    }
}