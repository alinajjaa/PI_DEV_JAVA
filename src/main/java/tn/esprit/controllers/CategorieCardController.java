package tn.esprit.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import tn.esprit.entities.Categorie;
import tn.esprit.service.ServiceCategorie;

import java.io.IOException;

public class CategorieCardController {

    @FXML
    private Label nom_categorie;

    @FXML
    private Label description_categorie;

    @FXML
    private Label type_categorie;

    @FXML
    private JFXButton Modifier;

    @FXML
    private JFXButton Delete;

    private Categorie categorie;

    private Runnable onRefreshCallback;

    private final ServiceCategorie cs = new ServiceCategorie();

    private AfficherCategorieController parentController;

    public void setData(Categorie cat, Runnable onRefreshCallback) {
        this.categorie = cat;
        this.onRefreshCallback = onRefreshCallback;

        nom_categorie.setText("Nom: " + cat.getNom_categorie());
        type_categorie.setText("Type: " + cat.getType_categorie());
        description_categorie.setText("Description: " + cat.getDescription_Categorie());
    }



    @FXML
    void handleModification(ActionEvent event) {
        try {
            // Charger la vue de modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierCategorie.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la vue chargée
            ModifierCategorieController controller = loader.getController();

            // Passer la catégorie sélectionnée à ce contrôleur
            controller.setCategorie(categorie);

            // Afficher la nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Modifier Categorie");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Rafraîchir la vue si nécessaire
            if (onRefreshCallback != null) onRefreshCallback.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void handleDeletion(ActionEvent event) {
        try {
            cs.supprimer(categorie.getId_categorie());
            if (onRefreshCallback != null) onRefreshCallback.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
