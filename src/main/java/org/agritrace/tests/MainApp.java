package org.agritrace.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ServiceIndex.fxml"));
            Scene scene = new Scene(root);

            // Add your stylesheet
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            primaryStage.setResizable(true);
            primaryStage.setWidth(1200); // Default width
            primaryStage.setHeight(800); // Default height
            primaryStage.setScene(scene);

            primaryStage.setTitle("Service Index"); // Optional: Add a title for the stage
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
