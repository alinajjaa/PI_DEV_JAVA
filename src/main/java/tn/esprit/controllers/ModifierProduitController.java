package tn.esprit.controllers;
import javafx.scene.Parent;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import tn.esprit.entities.Produit;
import tn.esprit.service.ServiceProduit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

public class ModifierProduitController {


    @FXML
    private JFXTextField prix_prod;

    @FXML
    private JFXTextField quantite_prod;

    @FXML
    private JFXTextField description_prod;

    private String imagePath1;



    @FXML
    private JFXTextField nom_prod;




    @FXML
    private ImageView image_prod;

    private Produit selectedProduit;
    private final ServiceProduit ps = new ServiceProduit();

    /**
     * Called automatically when FXML is loaded.
     * Leave it empty or just do null-checks here.
     */
    @FXML
    public void initialize() {
        // Avoid NullPointerException if selectedProduit is not yet set
        if (selectedProduit != null) {
            fillForm();
        }
    }
    @FXML
    public void importImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                Image image = new Image(fileInputStream);
                image_prod.setImage(image);
                imagePath1 = selectedFile.toURI().toString();
            } catch (FileNotFoundException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Failed to load image: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
    /**
     * This method is called by the controller who loads this FXML to pass the selected product.
     */
    public void setSelectedProduit(Produit produit) {
        this.selectedProduit = produit;
        fillForm();
    }

    /**
     * Fills the form fields with selectedProduit data.
     */
    private void fillForm() {
        if (selectedProduit != null) {
            nom_prod.setText(selectedProduit.getNom_prod());
            prix_prod.setText(String.valueOf(selectedProduit.getPrix_prod()));
            quantite_prod.setText(String.valueOf(selectedProduit.getQuantite_prod()));
            description_prod.setText(selectedProduit.getDescription_prod());

            // Optional: Load image if needed
            try {
                String imagePath = selectedProduit.getImage_prod().replace("file:/", ""); // Windows path
                image_prod.setImage(new javafx.scene.image.Image("file:" + imagePath));
            } catch (Exception e) {
                System.out.println("Image loading error: " + e.getMessage());
            }
        }
    }

    public void setProduit(Produit produit) {
    }
    @FXML
    public void modifierProduit(ActionEvent actionEvent) {
        String path;
        path = (imagePath1 != null) ? imagePath1 : selectedProduit.getImage_prod(); // Garde l'ancienne image si non modifiée

        try {
            if (nom_prod.getText().isEmpty() || prix_prod.getText().isEmpty() || description_prod.getText().isEmpty()
                    || quantite_prod.getText().isEmpty() || path == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Champs manquants");
                alert.setContentText("Veuillez remplir tous les champs.");
                alert.showAndWait();
                return;
            }

            double prix;
            int quantite;

            try {
                prix = Double.parseDouble(prix_prod.getText());
                quantite = Integer.parseInt(quantite_prod.getText());
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Erreur de saisie");
                alert.setContentText("Veuillez entrer des valeurs numériques valides pour le prix et la quantité.");
                alert.showAndWait();
                return;
            }

            if (prix <= 0 || quantite <= 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Valeurs incorrectes");
                alert.setContentText("Le prix et la quantité doivent être supérieurs à zéro.");
                alert.showAndWait();
                return;
            }

            // Mise à jour de l’objet produit sélectionné
            selectedProduit.setNom_prod(nom_prod.getText());
            selectedProduit.setPrix_prod(prix);
            selectedProduit.setDescription_prod(description_prod.getText());
            selectedProduit.setQuantite_prod(quantite);
            selectedProduit.setImage_prod(path);

            // Appel à la méthode de modification dans ton service ProduitService
            ps.modifier(selectedProduit); // Assure-toi que la méthode existe bien

            if (quantite < 20) {
                Notifications.create()
                        .title("Quantité faible")
                        .text("Le produit risque d'être épuisé.")
                        .showWarning();
            }

            // Redirection vers AfficherProduit.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProduit.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene((javafx.scene.Parent) root));

        } catch (IOException | SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Une erreur est survenue : " + e.getMessage());
            alert.showAndWait();
        }
    }

}


