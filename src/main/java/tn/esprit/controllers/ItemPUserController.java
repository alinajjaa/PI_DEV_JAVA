package tn.esprit.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.esprit.entities.Panier;
import tn.esprit.entities.Produit;
import tn.esprit.service.ServicePanier;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ItemPUserController implements Initializable {

    @FXML
    private Label nom_prod;
    @FXML
    private JFXButton add;

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

    private Produit produit;
    public void setData1(Produit produit){
        this.produit=produit;
        System.out.println(produit);
        nom_prod.setText("Product name: " +produit.getNom_prod());
        description_prod.setText("Description: " + produit.getNom_prod());
        prix_prod.setText("Price: "+produit.getPrix_prod()+" TND");
        Image image = new Image(produit.getImage_prod());
        image_prod.setImage(image);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void AddToBasket(ActionEvent event) throws SQLException {
        ServicePanier servicePanier = ServicePanier.getInstance();
        int id_prod = produit.getId_prod();
        String nom_prod = produit.getNom_prod();
        double prix_prod = produit.getPrix_prod();
        String image_prod = produit.getImage_prod();

        if (servicePanier.existeDejaDansPanier(id_prod)) {
            servicePanier.incrementerQuantite(id_prod);
        } else {
            Panier panier = new Panier(prix_prod, id_prod, nom_prod, prix_prod, image_prod);
            servicePanier.ajouter(panier);
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherPanier.fxml"));
            Parent root = loader.load();
            Scene scene = add.getScene();
            Stage stage = (Stage) scene.getWindow();
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
    @FXML
    private void openReviewsWindow(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReviewsWindow.fxml"));
            Parent root = loader.load();
            ReviewsController controller = loader.getController();
            controller.setProduit(produit);

            Stage stage = new Stage();
            stage.setTitle("Reviews");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
