package tn.esprit.entities;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class StripeCheckoutApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Configuration Stripe
        Stripe.apiKey = "sk_test_..."; // 🔒 Mets ta clé secrète ici

        // Créer une session Stripe Checkout
        SessionCreateParams.LineItem item = SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("eur")
                                .setUnitAmount(1500L) // 💶 Montant en centimes = 15 €
                                .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName("Test Produit JavaFX")
                                                .build()
                                )
                                .build()
                )
                .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .addLineItem(item)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("https://example.com/success")
                .setCancelUrl("https://example.com/cancel")
                .build();

        Session session = Session.create(params);
        String checkoutUrl = session.getUrl(); // 🔗 URL vers l’interface de paiement Stripe

        // Affichage WebView avec Stripe Checkout
        WebView webView = new WebView();
        webView.getEngine().load(checkoutUrl);

        primaryStage.setTitle("Paiement Stripe JavaFX");
        primaryStage.setScene(new Scene(webView, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

