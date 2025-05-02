package tn.esprit.controllers;

import com.jfoenix.controls.JFXTextField;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;
import tn.esprit.entities.ExcelGenerator;
import tn.esprit.service.ProduitListener;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import tn.esprit.entities.Categorie;
import tn.esprit.entities.Produit;
import tn.esprit.service.ServiceProduit;
import javafx.scene.image.ImageView;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.scene.control.ScrollPane;

public class AfficherProduitController implements Initializable, ProduitListener {
    private Timeline refreshTimeline;


    @FXML
    private HBox statsBox;

    @FXML
    private JFXTextField searchField;

    @FXML
    GridPane grid;


    @FXML
    private ScrollPane scroll;
    private final ServiceProduit ps = new ServiceProduit();
    private List<Produit> produits = new ArrayList<>();

    private List<Produit> originalProduits = new ArrayList<>();
    private void afficherStatistiques() {
        try {
            Map<String, Integer> stats = ps.getNombreProduitsParCategorie();
            statsBox.getChildren().clear();

            for (Map.Entry<String, Integer> entry : stats.entrySet()) {
                Label statLabel = new Label(entry.getKey() + " : " + entry.getValue() + " produit(s)");
                statLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #333;");
                statsBox.getChildren().add(statLabel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void exportToExcel(ActionEvent event) throws SQLException {
        onClose();
        ExcelGenerator excelGenerator = new ExcelGenerator();
        ServiceProduit produitService = new ServiceProduit();

        List<Produit> produits = produitService.recuperer();

        excelGenerator.generateExcel(produits);

        System.out.println("Export vers Excel terminé !");
    }

    void intitialisationProduitList() {
        int row = 0;
        try {
            for (Produit produit : produits) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/item.fxml"));
                AnchorPane item = loader.load();
                item.setMaxWidth(Double.MAX_VALUE);
                GridPane.setHgrow(item, Priority.ALWAYS);

                ItemController itemCardController = loader.getController();
                itemCardController.setData(produit);
                itemCardController.setProduitListener(this);
                itemCardController.setParentController(this);
                item.setStyle("-fx-background-color: transparent; -fx-border-color: #008152; -fx-border-width: 1px;");

                grid.add(item, 0, row);
                GridPane.setMargin(item, new Insets(20));



                // Set equal row heights
                RowConstraints rowConstraints = new RowConstraints();
                rowConstraints.setPercentHeight(100 / produits.size()); // Each row takes an equal percentage of the height
                grid.getRowConstraints().add(rowConstraints);

                row++;

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void searchPrototype(String keyword) {
        try {
            // Fetch the list of Défis from the database
            ServiceProduit ps = new ServiceProduit();
            List<Produit> produits = ps.recuperer();

            // Clear the existing content of the GridPane
            grid.getChildren().clear();

            int row = 0;

            // Iterate through the list of produits
            for (Produit produit : produits) {
                if (produit.getNom_prod().toLowerCase().contains(keyword.toLowerCase())) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/item.fxml"));
                    AnchorPane item = loader.load();
                    item.setMaxWidth(Double.MAX_VALUE);
                    GridPane.setHgrow(item, Priority.ALWAYS);

                    ItemController itemCardController = loader.getController();
                    itemCardController.setData(produit);
                    itemCardController.setProduitListener(this);
                    itemCardController.setParentController(this);
                    item.setStyle("-fx-background-color: transparent; -fx-border-color: #008152; -fx-border-width: 1px;");

                    grid.add(item, 0, row);
                    GridPane.setMargin(item, new Insets(20));



                    // Set equal row heights
                    RowConstraints rowConstraints = new RowConstraints();
                    rowConstraints.setPercentHeight(100 / produits.size()); // Each row takes an equal percentage of the height
                    grid.getRowConstraints().add(rowConstraints);

                    row++;
                }
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            // Handle exceptions appropriately
        }
    }



    @FXML
    private void ajouterProduitOnClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterProduit.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("add product");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            refreshPage();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error occurred while loading the add product window.");
            alert.showAndWait();
        }
    }

    @FXML
    private void viewcategory(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherCategorie.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("add product");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            refreshPage();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error occurred while loading the add product window.");
            alert.showAndWait();
        }
    }


    @FXML
    private void ajouterCategorieOnClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterCategorie.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("add category");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            refreshPage();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error occurred while loading the add product window.");
            alert.showAndWait();
        }
    }


    void refreshPage() {
        try {
            // Retrieve the latest list of products from the database
            produits = ps.recuperer();

            // Clear the original products list
            originalProduits.clear();

            // Add the retrieved products to the original products list
            originalProduits.addAll(produits);

            // Clear the grid to remove existing products
            grid.getChildren().clear();

            // Initialize the list of products again
            intitialisationProduitList();
            afficherStatistiques();

            onClose();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error occurred while retrieving products from the database.");
            alert.showAndWait();
        }
    }


    @Override
    public void onDelete(Produit produit) {

    }

    @Override
    public void refreshList() {

    }

    @Override
    public void OnModifier(Produit produit) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            produits = ps.recuperer();
            originalProduits.addAll(produits);
            afficherStatistiques();
// Store original products
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        intitialisationProduitList();

        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> refreshPage()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
        onClose();
        // Add listener to the search field textProperty
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                refreshPage(); // Reload content when search field is cleared
            } else {
                searchPrototype(newValue); // Filter products based on the search keyword
            }
        });

        // Perform search if the search field is not empty initially
        String query = searchField.getText().trim();
        if (!query.isEmpty()) {
            searchPrototype(query);
        }
    }


    private void stopRefreshTimeline() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
        }
    }
    @FXML
    private Label totalProductsLabel;

    @FXML
    private Label totalCategoriesLabel;

    public void updateStatistics(int productCount, int categoryCount) {
        totalProductsLabel.setText(String.valueOf(productCount));
        totalCategoriesLabel.setText(String.valueOf(categoryCount));
    }

    // Call this method when closing the scene or disposing of the controller
    public void onClose() {
        stopRefreshTimeline();
    }
}
