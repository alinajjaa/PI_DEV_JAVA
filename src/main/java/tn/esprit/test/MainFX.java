package tn.esprit.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MainFX extends Application {
//--module-path D:/javafx-sdk-17.0.15/lib --add-modules javafx.controls,javafx.fxml --add-exports=javafx.graphics/com.sun.javafx.sg.prism.web=ALL-UNNAMED --add-opens javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAMED --add-opens javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED
public static final String CURRENCY = "TND";

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProduit.fxml"));

        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("Shop");
            stage.setWidth(1325);
            stage.setHeight(1080);
            stage.setFullScreen(false);
            stage.setFullScreenExitHint("Press F to exit!");
            stage.setFullScreenExitKeyCombination(KeyCombination.valueOf("f"));
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
