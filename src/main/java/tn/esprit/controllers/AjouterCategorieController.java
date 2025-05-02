package tn.esprit.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import tn.esprit.entities.Categorie;
import tn.esprit.service.ServiceCategorie;

import java.io.IOException;
import java.sql.SQLException;


public class AjouterCategorieController {
    private final ServiceCategorie cs = new ServiceCategorie();

    @FXML
    private JFXButton Afficher;

    @FXML
    private JFXButton Ajouter;

    @FXML
    private JFXTextField description_categorie;

    @FXML
    private JFXTextField nom_categorie;

    @FXML
    private JFXTextField type_categorie;

    @FXML
    public void ajouterCategorie(javafx.event.ActionEvent actionEvent) throws IOException {
        try {
            // Contrôle des champs vides
            String nom = nom_categorie.getText().trim();
            String description = description_categorie.getText().trim();
            String type = type_categorie.getText().trim();

            if (nom.isEmpty() || description.isEmpty() || type.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Champs obligatoires", "Veuillez remplir tous les champs.");
                return;
            }

            // Vérifier si une catégorie avec le même nom ou type existe déjà
            if (cs.existsByNomOrType(nom, type)) {
                showAlert(Alert.AlertType.ERROR, "Doublon", "Une catégorie avec ce nom ou ce type existe déjà.");
                return;
            }

            // Ajouter la catégorie
            Categorie categorie = new Categorie(nom, description, type);
            cs.ajouter(categorie);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Catégorie ajoutée avec succès.");

            // Fermer la scène actuelle
            Stage stage = (Stage) ((JFXButton) actionEvent.getSource()).getScene().getWindow();
            stage.close();

            // Ouvrir AfficherCategorie.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherCategorie.fxml"));
            Parent root = loader.load();
            Stage afficherStage = new Stage();
            afficherStage.setTitle("Afficher catégories");
            afficherStage.getIcons().add(new Image("logo.png"));
            afficherStage.setScene(new Scene(root));
            afficherStage.show();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}



