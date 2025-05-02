package tn.esprit.controllers;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.layout.*;
import tn.esprit.controllers.ItemPUserController;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import tn.esprit.entities.Categorie;
import tn.esprit.entities.Produit;
import tn.esprit.service.ServiceProduit;
import javafx.scene.image.ImageView;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.ScrollPane;
import java.util.Comparator;

public class AfficherPUserController {

    @FXML
    private JFXButton sortPrice;
    @FXML
    private JFXButton defaultSortButton;

    @FXML
    private JFXButton sortQuantity;
    @FXML
    private AnchorPane AfficherProduitScene;

    @FXML
    GridPane grid;
    @FXML
    private JFXButton search;

    @FXML
    private JFXTextField searchField;
    @FXML
    private JFXButton BasketButton;
    @FXML
    private ScrollPane scroll;
    private final ServiceProduit ps = new ServiceProduit();
    private List<Produit> produits = new ArrayList<>();
    private List<Produit> originalProduits = new ArrayList<>(); // Added originalProduits list

    @FXML
    void initialize() throws SQLException, IOException {
        produits = ps.recuperer();
        originalProduits.addAll(produits);
        System.out.println(produits);
        intitialisationProduitList();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                // Champ vide → réinitialiser les produits affichés
                produits.clear();
                produits.addAll(originalProduits);
                intitialisationProduitList();
            } else {
                searchPrototype(newValue); // Appliquer la recherche
            }
        });

    }

    private void searchPrototype(String keyword) {
        try {
            grid.getChildren().clear();
            int column = 0;
            int row = 0;

            // Toujours chercher dans originalProduits
            for (Produit produit : originalProduits) {
                if (produit.getNom_prod().toLowerCase().contains(keyword.toLowerCase())) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/itemPUser.fxml"));
                    AnchorPane item = loader.load();

                    ItemPUserController itemCardController = loader.getController();
                    itemCardController.setData1(produit);

                    item.setStyle("-fx-background-color: transparent; -fx-border-color: #008152; -fx-border-width: 1px;");

                    grid.add(item, column, row);
                    GridPane.setMargin(item, new Insets(15));

                    column++;
                    if (column == 3) {
                        column = 0;
                        row++;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void resetToDefault(ActionEvent event) {
        produits.clear();
        produits.addAll(originalProduits);
        grid.getChildren().clear();
        intitialisationProduitList();
    }

    void intitialisationProduitList() {
        grid.getChildren().clear(); // clear any existing items
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();

        int column = 0;
        int row = 0;

        try {
            for (Produit produit : produits) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/itemPUser.fxml"));
                AnchorPane item = loader.load();

                ItemPUserController itemCardController = loader.getController();
                itemCardController.setData1(produit);

                item.setStyle("-fx-background-color: transparent; -fx-border-color: #008152; -fx-border-width: 1px;");

                grid.add(item, column++, row);
                GridPane.setMargin(item, new Insets(15));

                if (column == 3) { // max 3 items per row
                    column = 0;
                    row++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void sortByQuantity(ActionEvent event) {
        produits.sort(Comparator.comparing(Produit::getQuantite_prod));
        grid.getChildren().clear();
        intitialisationProduitList();
    }
    @FXML
    private void sortByPrice(ActionEvent event) {
        produits.sort(Comparator.comparing(Produit::getPrix_prod));
        grid.getChildren().clear();
        intitialisationProduitList();
    }



    @FXML
    void openBasket(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherPanier.fxml"));
            Parent root = loader.load(); // Load the FXML file

            // Get the scene and stage from the event source
            Scene scene = BasketButton.getScene();
            Stage stage = (Stage) scene.getWindow();

            // Set the scene to the AfficherPanier.fxml
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error occurred while loading the basket.");
            alert.showAndWait();
        }
    }


}
