package com.example.agritrace.Controllers;

import com.example.agritrace.Models.Comment;
import com.example.agritrace.Services.CommentService;
import com.example.agritrace.utils.BadWordsFilter; // 🔥 import du BadWordsFilter
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.web.WebView;
import javafx.concurrent.Worker;



import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import org.json.JSONArray;


public class CommentController {

    @FXML private TextArea commentArea;
    @FXML private TextField gifUrlField;
    @FXML private CheckBox anonymousCheckBox;
    @FXML private Button submitButton;
    @FXML private VBox commentListVBox;
    @FXML private TextField gifSearchField;
    @FXML private HBox gifResultsBox;


    private final CommentService commentService = new CommentService();
    private int blogId;
    private Runnable refreshCallback;
    private boolean readOnlyMode = false;

    public void setBlogId(int blogId) {
        this.blogId = blogId;
    }

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    public void setReadOnlyMode(boolean readOnlyMode) {
        this.readOnlyMode = readOnlyMode;
    }

    @FXML
    public void initialize() {}

    public void initData() {
        if (readOnlyMode) {
            commentArea.setVisible(false);
            gifUrlField.setVisible(false);
            anonymousCheckBox.setVisible(false);
            submitButton.setVisible(false);
        }
        loadComments();
    }

    private void loadComments() {
        try {
            List<Comment> comments = commentService.getCommentsForBlog(blogId);
            commentListVBox.getChildren().clear();
            for (Comment c : comments) {
                Label userLabel = new Label(c.isAnonymous() ? "Utilisateur anonyme" : "Utilisateur #" + c.getUserId());
                Label content = new Label("📝 " + c.getContent());
                content.setWrapText(true);
                VBox commentBox = new VBox(5, userLabel, content);

                if (c.getGifUrl() != null) {
                    WebView gifView = new WebView();
                    gifView.setPrefSize(200, 200);
                    gifView.setMaxSize(200, 200);
                    gifView.setStyle("-fx-background-color: transparent;");

                    String html = "<html><body style='margin:0;padding:0;background:transparent;display:flex;align-items:center;justify-content:center;'>" +
                            "<img src='" + c.getGifUrl() + "' style='max-width:100%; max-height:100%;'/>" +
                            "</body></html>";

                    gifView.getEngine().loadContent(html);
                    commentBox.getChildren().add(gifView);
                }


                commentBox.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 10; -fx-background-radius: 8;");
                commentListVBox.getChildren().add(commentBox);
            }
        } catch (SQLException e) {
            showAlert("Erreur de base de données", e.getMessage());
        }
    }

    @FXML
    private void submitComment() {
        String content = commentArea.getText().trim();
        String gifUrl = gifUrlField.getText().trim();
        boolean isAnonymous = anonymousCheckBox.isSelected();

        if (content.isEmpty() && gifUrl.isEmpty()) {
            showAlert("Erreur", "Veuillez saisir un commentaire ou un GIF");
            return;
        }

        // 🔥🔥 Vérification de bad words ici
        if (BadWordsFilter.containsBadWord(content)) {
            showAlert("Erreur", "Impossible d'ajouter le commentaire : mot interdit détecté.");
            return; // 🔴 On arrête l'envoi
        }

        try {
            Comment comment = new Comment(
                    blogId,
                    isAnonymous ? null : 1, // Remplacer par l'ID utilisateur réel
                    content,
                    isAnonymous,
                    gifUrl.isEmpty() ? null : gifUrl
            );

            commentService.addComment(comment);

            if (refreshCallback != null) {
                refreshCallback.run();
            }
            closePopup();
        } catch (SQLException e) {
            showAlert("Erreur de base de données", e.getMessage());
        }
    }

    @FXML
    private void closePopup() {
        Stage stage = (Stage) commentArea.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

//    @FXML
//    private void searchGif() {
//        String keyword = gifSearchField.getText().trim();
//        if (keyword.isEmpty()) return;
//
//        String apiKey = "TTov4SQmBzITQCmvkFFMW4KMaHC9NT7R";
//
//        String urlStr = "https://api.giphy.com/v1/gifs/search?api_key=" + apiKey + "&q=" + keyword + "&limit=5&rating=g";
//
//        new Thread(() -> {
//            try {
//                URL url = new URL(urlStr);
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestMethod("GET");
//
//                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                StringBuilder response = new StringBuilder();
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    response.append(line);
//                }
//                reader.close();
//
//                JSONObject jsonResponse = new JSONObject(response.toString());
//                JSONArray dataArray = jsonResponse.getJSONArray("data");
//
//                List<String> gifUrls = new ArrayList<>();
//                for (int i = 0; i < dataArray.length(); i++) {
//                    JSONObject images = dataArray.getJSONObject(i).getJSONObject("images");
//                    String gifUrl = images.getJSONObject("fixed_height_small").getString("url");
//                    gifUrls.add(gifUrl);
//                }
//
//                Platform.runLater(() -> showGifResults(gifUrls));
//            } catch (Exception e) {
//                Platform.runLater(() -> showAlert("Erreur API", e.getMessage()));
//            }
//        }).start();
//    }

    @FXML
    private void searchGif() {
        String keyword = gifSearchField.getText().trim();
        if (keyword.isEmpty()) return;

        String apiKey = "TTov4SQmBzITQCmvkFFMW4KMaHC9NT7R";
        String urlStr = "https://api.giphy.com/v1/gifs/search?api_key=" + apiKey + "&q=" + keyword + "&limit=5&rating=g";

        new Thread(() -> {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                // Créer un BufferedReader pour lire la réponse
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder(); // Assure-toi que cette ligne est présente

                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line); // On ajoute chaque ligne à la réponse
                }
                reader.close();

                // Affiche la réponse brute dans la console pour vérifier
                System.out.println("Réponse Giphy : " + response.toString());

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray dataArray = jsonResponse.getJSONArray("data");

                List<String> gifUrls = new ArrayList<>();
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject images = dataArray.getJSONObject(i).getJSONObject("images");
                    String gifUrl = images.getJSONObject("fixed_height_small").getString("url");
                    gifUrls.add(gifUrl);
                }

                Platform.runLater(() -> showGifResults(gifUrls));
            } catch (Exception e) {
                Platform.runLater(() -> showAlert("Erreur API", e.getMessage()));
            }
        }).start();
    }


//    private void showGifResults(List<String> gifUrls) {
//        // Vide l'HBox avant d'ajouter les nouveaux résultats
//        gifResultsBox.getChildren().clear();
//
//        if (gifUrls == null || gifUrls.isEmpty()) {
//            System.out.println("Aucun GIF trouvé.");
//            return;
//        }
//
//        for (String gifUrl : gifUrls) {
//            try {
//                // Crée un ImageView pour chaque URL de GIF
//                Image gifImage = new Image(gifUrl);  // Charge l'image
//                if (gifImage.isError()) {
//                    System.out.println("Erreur de chargement pour l'URL: " + gifUrl);
//                    continue;
//                }
//
//                ImageView imageView = new ImageView(gifImage);  // Crée un ImageView avec l'image
//                imageView.setFitHeight(100);
//                imageView.setFitWidth(100);
//
//                // Ajoute l'ImageView à l'HBox
//                gifResultsBox.getChildren().add(imageView);
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("Erreur lors de l'ajout du GIF: " + gifUrl);
//            }
//        }
//    }
private void showGifResults(List<String> gifUrls) {
    gifResultsBox.getChildren().clear();

    if (gifUrls == null || gifUrls.isEmpty()) {
        Label noResultsLabel = new Label("Aucun GIF trouvé.");
        gifResultsBox.getChildren().add(noResultsLabel);
        return;
    }

    for (String gifUrl : gifUrls) {
        try {
            WebView webView = new WebView();
            webView.setPrefSize(200, 200);
            webView.setMaxSize(200, 200);
            webView.setStyle("-fx-background-color: transparent;");

            String html = "<html><body style='margin:0;padding:0;display:flex;align-items:center;justify-content:center;background:transparent;'>" +
                    "<img src='" + gifUrl + "' style='max-width:100%; max-height:100%;'/>" +
                    "</body></html>";

            webView.getEngine().loadContent(html);

            webView.setOnMouseClicked(event -> gifUrlField.setText(gifUrl));

            gifResultsBox.getChildren().add(webView);

        } catch (Exception e) {
            System.out.println("Erreur lors de l'affichage du GIF : " + gifUrl + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    if (gifResultsBox.getChildren().isEmpty()) {
        Label errorLabel = new Label("Aucun GIF n'a pu être chargé. Vérifiez votre connexion ou essayez un autre mot-clé.");
        gifResultsBox.getChildren().add(errorLabel);
    }
}

}
