package org.agritrace.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.agritrace.services.LanguageManager;

import java.io.IOException;
import java.util.ResourceBundle;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize language manager
            LanguageManager languageManager = LanguageManager.getInstance();
            
            // Load FXML with language resources
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LocationIndex.fxml"));
            loader.setResources(languageManager.getMessages());
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Add stylesheet
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            primaryStage.setResizable(true);
            primaryStage.setWidth(1200);
            primaryStage.setHeight(800);
            primaryStage.setScene(scene);

            primaryStage.setTitle("Service Index");
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
