package tn.esprit.controllers;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import java.util.Properties;

import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import com.alibaba.fastjson2.JSONObject;
import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.entities.Panier;
import tn.esprit.service.MailService;
import tn.esprit.service.ServicePanier;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.*;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.awt.Desktop;
import java.util.List;

public class AfficherPanierController implements Initializable, ItemPanierController.QuantityUpdateListener {

    @FXML
    private GridPane PanierContainer;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private JFXButton ShopButton;

    private final ServicePanier servicePanier = ServicePanier.getInstance();
    private final MailService ms = new MailService();


    private final Map<Integer, Panier> panierMap = new HashMap<>();
    private double totalPrice = 0.0;
    String nomUtilisateur = "Aziz Msekni"; // À remplacer par la vraie source
    public AfficherPanierController() throws SQLException {
    }

    // -------------------- INITIALISATION --------------------

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadPanierItems();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        // Rafraîchissement automatique toutes les 2 secondes (facultatif)
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            try {
                loadPanierItems();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // -------------------- CHARGEMENT DES ARTICLES --------------------

    private void loadPanierItems() throws SQLException, IOException {
        PanierContainer.getChildren().clear();
        panierMap.clear();
        totalPrice = 0.0;

        List<Panier> panierList = servicePanier.recuperer();

        // Regrouper les produits par ID et additionner les quantités
        Map<Integer, Panier> groupedItems = new HashMap<>();
        for (Panier panier : panierList) {
            groupedItems.merge(panier.getId_prod(), panier, (p1, p2) -> {
                p1.setQuantite_panier(p1.getQuantite_panier() + p2.getQuantite_panier());
                return p1;
            });
        }

        int row = 0;
        for (Panier panier : groupedItems.values()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/itemPanier.fxml"));
            AnchorPane itemPane = loader.load();

            ItemPanierController controller = loader.getController();
            controller.setData1(panier);
            controller.setQuantityUpdateListener(this);

            itemPane.getProperties().put("controller", controller); // Pour suppression future

            PanierContainer.add(itemPane, 0, row++);
            panierMap.put(panier.getId_prod(), panier);

            totalPrice += panier.getPrix_prod() * panier.getQuantite_panier();
        }

        updateTotalPriceDisplay();
    }

    private void updateTotalPriceDisplay() {
        totalPriceLabel.setText(String.format("Total : %.2f TND", totalPrice));
    }


    @Override
    public void onQuantityUpdate(int productId, int newQuantity) {
        if (!panierMap.containsKey(productId)) return;

        Panier panier = panierMap.get(productId);

        if (newQuantity == 0) {
            // Supprimer le produit visuellement
            PanierContainer.getChildren().removeIf(node -> {
                if (node instanceof AnchorPane anchorPane) {
                    ItemPanierController controller = (ItemPanierController) anchorPane.getProperties().get("controller");
                    return controller != null && controller.currentPanier.getId_prod() == productId;
                }
                return false;
            });
            panierMap.remove(productId);
        } else {
            panier.setQuantite_panier(newQuantity);
        }

        // Recalculer le total
        totalPrice = panierMap.values().stream()
                .mapToDouble(p -> p.getPrix_prod() * p.getQuantite_panier())
                .sum();

        updateTotalPriceDisplay();
        System.out.println("Quantité actuelle : " + panier.getQuantite_panier());
        System.out.println("Incrémentation de : 1");

    }

    // -------------------- ACTIONS DES BOUTONS --------------------

    @FXML
    void openShop(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherPUser.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ShopButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur lors du chargement de la boutique.");
        }
    }

    @FXML
    void clearBasket(ActionEvent event) {
        try {
            List<Panier> panierList = servicePanier.recuperer(); // Récupérer tous les items du panier
            for (Panier panier : panierList) {
                servicePanier.supprimer(panier.getId_panier()); // Supprimer par ID du panier (et non du produit)
            }
            panierMap.clear();
            PanierContainer.getChildren().clear(); // Vider visuellement
            totalPrice = 0.0;
            updateTotalPriceDisplay();
        } catch (SQLException e) {
            showAlert("Erreur lors de la suppression du panier.");
            e.printStackTrace();
        }
    }

    // -------------------- AIDE --------------------

    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }








    @FXML
    public void handleBuy(ActionEvent event) {

        try {
            // 1) Créer la session Stripe Checkout

            long totalCents = (long)(totalPrice * 100);
            SessionCreateParams.LineItem item = SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("usd")               // ou "eur" si tu veux
                                    .setUnitAmount(totalCents)        // montant en plus petite unité
                                    .setProductData(
                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName("Total Panier")  // ou le nom utilisateur
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

// Créer la session Checkout en y ajoutant le line item
            SessionCreateParams params = SessionCreateParams.builder()
                    .addLineItem(item)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("https://example.com/success")
                    .setCancelUrl("https://example.com/cancel")
                    .build();
            Session session = Session.create(params);

            // 2) WebView JavaFX
            WebView webView = new WebView();
            webView.getEngine().load(session.getUrl());

            // **Optionnel** : désactiver le menu contextuel par défaut
            webView.setContextMenuEnabled(false);

            // 3) Affichage dans une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Paiement Stripe");
            stage.setScene(new Scene(webView, 800, 600));
            stage.show();
            webView.getEngine().locationProperty().addListener((obs, oldLocation, newLocation) -> {
                if (newLocation.contains("success")) {
                    stage.close(); // Fermer WebView

                    generateInvoicePDF(); // 📄 Générer le PDF
                    ms.sendConfirmationEmail(); // ✉️ Envoyer l'e-mail

                    clearBasket(null); // 🧹 Vider le panier (optionnel)
                    showAlert("Paiement réussi ! Une facture vous a été envoyée par email.");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    public void generateInvoicePDF() {
        Document document = new Document();
        try {
            String filePath = "facture_" + System.currentTimeMillis() + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            document.add(new Paragraph("Facture - Agritrace Shop"));
            document.add(new Paragraph("Client : " + nomUtilisateur));
            document.add(new Paragraph("Date : " + new Date().toString()));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(3);
            table.addCell("Produit");
            table.addCell("Quantité");
            table.addCell("Prix");

            for (Panier panier : panierMap.values()) {
                table.addCell(panier.getNom_prod());
                table.addCell(String.valueOf(panier.getQuantite_panier()));
                table.addCell(String.format("%.2f TND", panier.getPrix_prod()));
            }

            document.add(table);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total : " + String.format("%.2f TND", totalPrice)));

            document.close();

            // Ouvrir automatiquement
            Desktop.getDesktop().open(new java.io.File(filePath));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
