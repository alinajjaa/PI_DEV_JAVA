package tn.esprit.controllers;
import javafx.scene.Node;

import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.entities.Categorie;
import tn.esprit.service.ServiceCategorie;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherCategorieController {
    @FXML
    private AnchorPane AfficherCategorieScene;

    @FXML
    private GridPane CategoriesContainer;

    private Timeline refreshTimeline;
    private final ServiceCategorie cs = new ServiceCategorie();

    @FXML
    void initialize() throws SQLException {
        refreshPage();
        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> refreshPage()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }
    @FXML
    private void ajouterCategorieOnClick(ActionEvent event) {
        try {
            // Close the current window
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            // Load the new scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterCategorie.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add Category");
            stage.setScene(new Scene(root));
            stage.show(); // You can use show() instead of showAndWait() if you don't need to wait for it to close
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error occurred while loading the add category window.");
            alert.showAndWait();
        }
    }

    private void refreshPage() {
        try {
            List<Categorie> categories = cs.recuperer();
            CategoriesContainer.getChildren().clear();

            if (!categories.isEmpty()) {
                int columnIndex = 0;
                int rowIndex = 0;
                for (Categorie categorie : categories) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/CategoryCard.fxml"));
                    Parent card = loader.load();

                    CategorieCardController controller = loader.getController();
                    controller.setData(categorie, () -> refreshPage());

                    CategoriesContainer.add(card, columnIndex, rowIndex);
                    columnIndex++;
                    if (columnIndex == 1) {
                        columnIndex = 0;
                        rowIndex++;
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("No Categories Found");
                alert.setHeaderText(null);
                alert.setContentText("No categories found in the database.");
                alert.showAndWait();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error occurred while retrieving categories from the database.");
            alert.showAndWait();
        }
    }

    public void deleteCategorieClicked(int categorieId) {
        try {
            cs.supprimer(categorieId);
            refreshPage();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Delete Error");
            alert.setHeaderText(null);
            alert.setContentText("Error occurred while deleting the category.");
            alert.showAndWait();
        }
    }

    public void modifierCategorieOnClicked(Categorie cat) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherCategorie.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Modifier la catégorie");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            refreshPage();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error occurred while loading the category modification window.");
            alert.showAndWait();
        }
    }
}
