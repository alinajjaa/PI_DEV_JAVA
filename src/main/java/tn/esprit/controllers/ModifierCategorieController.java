package tn.esprit.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import tn.esprit.entities.Categorie;
import tn.esprit.service.ServiceCategorie;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;



public class ModifierCategorieController implements Initializable {

    @FXML
    private JFXTextField description_categorie;
    @FXML
    private JFXTextField nom_categorie;
    @FXML
    private JFXTextField type_categorie;
    @FXML
    private JFXButton Modifier;

    private Categorie selectedCategorie;

    // Méthode pour initialiser la catégorie
    public void setCategorie(Categorie selectedCategorie) {
        System.out.println("setCategorie called with category: " + selectedCategorie);
        this.selectedCategorie = selectedCategorie;
        if (selectedCategorie != null) {
            fillInputs(selectedCategorie);
        }
    }

    // Méthode de modification de la catégorie
    @FXML
    public void modifierCategorie(ActionEvent event) throws SQLException {
        String nom_categorie1 = nom_categorie.getText();
        String description_categorie1 = description_categorie.getText();
        String type_categorie1 = type_categorie.getText();

        if (nom_categorie1.isEmpty() || description_categorie1.isEmpty() || type_categorie1.isEmpty()) {
            AfficherAlerte("Warning", "Please fill in all the fields.");
            return;
        }
        Categorie categorie = new Categorie(selectedCategorie.getId_categorie(), nom_categorie1, description_categorie1, type_categorie1);
        ServiceCategorie cs = new ServiceCategorie();
        cs.modifier(categorie);

        Stage stage = (Stage) Modifier.getScene().getWindow();
        stage.close();
    }

    // Remplir les champs avec les données de la catégorie
    private void fillInputs(Categorie categorie) {
        if (categorie != null) {
            nom_categorie.setText(categorie.getNom_categorie());
            description_categorie.setText(categorie.getDescription_Categorie());
            type_categorie.setText(categorie.getType_categorie());
        } else {
            // Optionnel : Afficher un message ou mettre des valeurs par défaut
            System.out.println("Categorie is null, unable to fill inputs.");
        }
    }

    // Afficher une alerte
    private void AfficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Initialiser avec des données
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ServiceCategorie serviceCategorie = new ServiceCategorie();
            // Si tu veux récupérer une catégorie à partir d'un ID, il faudrait utiliser un ID valide.
            selectedCategorie = serviceCategorie.getCategorieByCategorieId(Categorie.getFakeId());

            if (selectedCategorie != null) {
                fillInputs(selectedCategorie);
            } else {
                System.out.println("Erreur: La catégorie est nulle.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
