package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.Comment;
import tn.esprit.entities.Produit;
import tn.esprit.service.CommentService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ReviewsController implements Initializable {

    @FXML
    private VBox commentListVBox;


    @FXML
    private TextArea commentInput;

    private Produit produit; // à recevoir depuis ItemPUserController

    private final CommentService commentService = new CommentService();

    public void setProduit(Produit produit) {
        this.produit = produit;
        loadComments();
    }

    private void loadComments() {
        try {
            commentListVBox.getChildren().clear();
            List<Comment> comments = commentService.getCommentsByProductId(produit.getId_prod());

            for (Comment c : comments) {
                // Affiche toujours l'utilisateur avec son ID
                Label userLabel = new Label("Utilisateur #" + c.getUserId());
                userLabel.setStyle("-fx-font-weight: bold;");

                // Contenu du commentaire
                Label content = new Label("📝 " + c.getContent());
                content.setWrapText(true);

                // Boîte contenant le commentaire
                VBox commentBox = new VBox(5, userLabel, content);
                commentBox.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 10; -fx-background-radius: 8;");

                commentListVBox.getChildren().add(commentBox);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement des commentaires : " + e.getMessage());
        }
    }

    @FXML
    private void closePopup() {
        // Récupérer la fenêtre (stage) actuelle via un composant de la vue
        Stage stage = (Stage) commentInput.getScene().getWindow();
        stage.close();
    }


    @FXML
    private void handleAddComment() {
        String content = commentInput.getText().trim();
        if (!content.isEmpty()) {
            int userId = 1;
            commentService.addComment(new Comment(content, userId, produit.getId_prod()));
            commentInput.clear();
            loadComments();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
}
