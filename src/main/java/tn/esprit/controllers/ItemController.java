package tn.esprit.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.esprit.entities.Categorie;
import tn.esprit.entities.Produit;
import tn.esprit.service.ProduitListener;
import tn.esprit.service.ServiceProduit;
import tn.esprit.service.ServiceCategorie;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ItemController implements Initializable {

    private ProduitListener listener;
    private Produit produit;

    @FXML
    private JFXButton Modifier;

    @FXML
    private Label nom_prod;

    @FXML
    private Label description_prod;

    @FXML
    private ImageView image_prod;

    @FXML
    private Label id_categorie;

    @FXML
    private Label prix_prod;

    @FXML
    private Label quantite_prod;

    private AfficherProduitController parentController;

    public void setParentController(AfficherProduitController controller) {
        this.parentController = controller;
    }



    public void setData(Produit produit) throws SQLException {
        this.produit = produit;
        nom_prod.setText("Product name: " +produit.getNom_prod());
        description_prod.setText("Description: " + produit.getDescription_prod());
        prix_prod.setText("Price: "+produit.getPrix_prod() + " TND");
        quantite_prod.setText("Quantity: " +String.valueOf(produit.getQuantite_prod()));
        ServiceCategorie serviceCategorie = new ServiceCategorie();
        Categorie categorie = serviceCategorie.getCategorieByCategorieId(produit.getId_categorie());
        assert categorie != null;
        id_categorie.setText("Category: " +categorie.getNom_categorie());
        try {
            Image image = new Image(produit.getImage_prod());
            image_prod.setImage(image);
        } catch (Exception e) {
            System.err.println("Invalid image path: " + produit.getImage_prod());
        }
    }

    /**
     * Sets the listener for product-related events.
     */
    public void setProduitListener(ProduitListener listener) {
        this.listener = listener;
    }

    /**
     * Handles deletion of the current product.
     */
    @FXML
    private void handleDeletion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this product?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    ServiceProduit serviceProduit = new ServiceProduit();
                    serviceProduit.supprimer(produit.getId_prod());

                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Deletion Successful");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Product deleted successfully.");
                    successAlert.showAndWait();

                    // Notify parent controller to update the UI
                    parentController.refreshPage();

                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Deletion Error");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("An error occurred while deleting the product.");
                    errorAlert.showAndWait();
                }
            }
        });
    }


    @FXML
    void handlemodif(ActionEvent event) {
        if (listener != null) {
            listener.OnModifier(produit);
        }
    }

    /**
     * Opens a new window for modifying the product.
     */
    @FXML
    private void handleModification() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierProduit.fxml"));
            Parent root = loader.load();

            ModifierProduitController modifierController = loader.getController();
            modifierController.setSelectedProduit(produit);

            Stage stage = new Stage();
            stage.setTitle("Modifier le produit");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error occurred while loading the product modification window.");
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // You can initialize button actions or style setup here if needed
    }
}
