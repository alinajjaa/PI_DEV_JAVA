package com.example.agritrace.Controllers;

import com.example.agritrace.Models.BlogPost;
import com.example.agritrace.Services.BlogService;
import com.restfb.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import facebook4j.Facebook;
import facebook4j.FacebookFactory;
import facebook4j.auth.AccessToken;
import facebook4j.FacebookException;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import com.example.agritrace.Models.Comment;
import com.example.agritrace.Services.CommentService;
import com.restfb.types.FacebookType;

public class BlogController {

    @FXML private VBox blogListSection;
    @FXML private VBox blogFormSection;
    @FXML private VBox blogContainer;
    @FXML private TextField titleField;
    @FXML private TextArea contentArea;
    @FXML private TextField searchField;
    @FXML private VBox editFormSection;
    @FXML private TextField editTitleField;
    @FXML private TextArea editContentArea;
    @FXML
    private ComboBox<String> filterComboBox;


    @FXML private Button previousButton, nextButton;

    @FXML private Label pageInfoLabel;

    private final BlogService blogService = new BlogService();
    private BlogPost selectedBlog = null;
    private int currentPage = 1;     // page actuelle
    private int itemsPerPage = 3;    // nombre de blogs par page
    private List<BlogPost> blogs = new ArrayList<>(); // liste de tous les blogs
    private List<BlogPost> allBlogs = new ArrayList<>(); // liste complète

    @FXML
    public void initialize() {
        try {
            allBlogs = blogService.afficherBlogs(); // Charger tous les blogs au début
            blogs = new ArrayList<>(allBlogs); // Copier pour affichage initial
        } catch (SQLException e) {
            e.printStackTrace();
        }
        updateBlogDisplay();
        // Ajouter un écouteur pour la recherche dynamique
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterBlogsByTitle(newValue);
        });
    }

    @FXML
    private void nextPage() {
        if ((currentPage * itemsPerPage) < blogs.size()) {
            currentPage++;
            updateBlogDisplay();
        }
    }

    @FXML
    private void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            updateBlogDisplay();
        }
    }

    public void showAddForm() {
        selectedBlog = null;
        blogFormSection.setVisible(true);
        blogFormSection.setManaged(true);
        blogListSection.setVisible(false);
        blogListSection.setManaged(false);
        titleField.clear();
        contentArea.clear();
    }

    public void cancelAddForm() {
        blogFormSection.setVisible(false);
        blogFormSection.setManaged(false);
        blogListSection.setVisible(true);
        blogListSection.setManaged(true);
    }

    public void saveBlog() {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();

        if (title.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Le titre est obligatoire.").show();
            return;
        }

        if (content.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Le contenu est obligatoire.").show();
            return;
        }

        try {
            if (selectedBlog == null) {
                BlogPost newBlog = new BlogPost(0, 1, title, content, 0, 0, LocalDateTime.now(), null, null);
                blogService.ajouterBlog(newBlog);
            } else {
                selectedBlog.setTitle(title);
                selectedBlog.setContent(content);
                blogService.modifierBlog(selectedBlog);
            }
            cancelAddForm();
            reloadBlogsFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de l'ajout du blog.");
        }
    }

    private void reloadBlogsFromDatabase() {
        try {
            blogs = blogService.afficherBlogs(); // Recharger tous les blogs en mémoire
        } catch (SQLException e) {
            e.printStackTrace();
        }
        updateBlogDisplay(); // Puis mettre à jour l'affichage avec pagination
    }

    private void refreshBlogList() {
        updateBlogDisplay(); // Ne recharge plus depuis la base
    }

    private void updateBlogDisplay() {
        blogContainer.getChildren().clear(); // Nettoyer l'ancien contenu

        int start = (currentPage - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, blogs.size());

        List<BlogPost> blogsToShow = blogs.subList(start, end);

        for (BlogPost blog : blogsToShow) {
            VBox blogCard = createBlogItem(blog);
            blogContainer.getChildren().add(blogCard);
        }

        pageInfoLabel.setText("Page " + currentPage);
        previousButton.setDisable(currentPage == 1);
        nextButton.setDisable(end >= blogs.size());
    }

    public void showEditForm(BlogPost blog) {
        selectedBlog = blog;

        editTitleField.setText(blog.getTitle());
        editContentArea.setText(blog.getContent());

        editFormSection.setVisible(true);
        editFormSection.setManaged(true);

        blogListSection.setVisible(false);
        blogListSection.setManaged(false);
    }

    public void cancelEditForm() {
        selectedBlog = null;
        editFormSection.setVisible(false);
        editFormSection.setManaged(false);
        blogListSection.setVisible(true);
        blogListSection.setManaged(true);
    }

    public void updateBlog() {
        String newTitle = editTitleField.getText();
        String newContent = editContentArea.getText();

        if (newTitle.isEmpty() || newContent.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Titre ou contenu manquant.").show();
            return;
        }

        try {
            selectedBlog.setTitle(newTitle);
            selectedBlog.setContent(newContent);
            blogService.modifierBlog(selectedBlog);

            cancelEditForm();
            reloadBlogsFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createBlogItem(BlogPost blog) {
        VBox blogBox = new VBox(10);
        blogBox.setStyle("-fx-border-color: #ddd; -fx-padding: 15; -fx-background-color: #ffffff; -fx-background-radius: 15; -fx-border-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 5, 5, 5);");

        Text title = new Text(blog.getTitle());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-padding: 5 0;");

        Text content = new Text(blog.getContent());
        content.setStyle("-fx-font-size: 14px; -fx-text-fill: #555; -fx-line-spacing: 1.5;");

        Label likes = new Label("👍 " + blog.getLikes());
        likes.setStyle("-fx-font-size: 14px; -fx-text-fill: #28a745;");

        Label reports = new Label("🚩 " + blog.getReports());
        reports.setStyle("-fx-font-size: 14px; -fx-text-fill: #dc3545;");

        Label commentsLabel = new Label();
        CommentService commentService = new CommentService();
        int commentCount;
        try {
            commentCount = commentService.countCommentsByBlogId(blog.getId());
        } catch (SQLException e) {
            commentCount = -1;
            e.printStackTrace();
        }

        commentsLabel.setText("💬 " + (commentCount >= 0 ? commentCount : "?"));
        commentsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #17a2b8;");
        commentsLabel.setCursor(Cursor.HAND);
        commentsLabel.setOnMouseClicked(e -> openCommentViewer(blog.getId()));

        HBox statsBox = new HBox(10, likes, reports, commentsLabel);
        statsBox.setAlignment(Pos.CENTER_LEFT);

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER_LEFT);
        buttons.setStyle("-fx-padding: 10 0 0 0;");

        Button likeBtn = createStyledButton("Like", "#28a745");
        Button editBtn = createStyledButton("Edit", "#007bff");
        Button deleteBtn = createStyledButton("Delete", "#dc3545");
        Button reportBtn = createStyledButton("Report", "#ffc107");
        Button commentBtn = createStyledButton("Commentaire", "#17a2b8");
        Button shareFbBtn = createStyledButton("Partager sur FB", "#3b5998"); // Nouveau bouton FB

        likeBtn.setOnAction(e -> {
            try {
                blogService.likeBlog(blog.getId());
                reloadBlogsFromDatabase();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        reportBtn.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/agritrace/ReportForm.fxml"));
                Parent root = loader.load();
                ReportController controller = loader.getController();
                controller.setContext(blog.getId(), blog.getTitle());
                Stage stage = new Stage();
                stage.setTitle("Signaler une publication");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        editBtn.setOnAction(e -> showEditForm(blog));
        deleteBtn.setOnAction(e -> {
            try {
                blogService.supprimerBlog(blog.getId());
                reloadBlogsFromDatabase();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        commentBtn.setOnAction(e -> openCommentWindow(blog.getId(), false));
        shareFbBtn.setOnAction(e -> shareOnFacebook(blog)); // Action pour le partage FB

        buttons.getChildren().addAll(likeBtn, reportBtn, editBtn, deleteBtn, commentBtn, shareFbBtn);

        blogBox.getChildren().addAll(title, content, statsBox, buttons);
        return blogBox;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 14px; -fx-border-radius: 10px; -fx-padding: 8 20; -fx-cursor: hand;", color));
        button.setOnMouseEntered(e -> button.setStyle(String.format("-fx-background-color: darken(%s, 10%); -fx-text-fill: white; -fx-font-size: 14px; -fx-border-radius: 10px; -fx-padding: 8 20;", color)));
        button.setOnMouseExited(e -> button.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 14px; -fx-border-radius: 10px; -fx-padding: 8 20;", color)));
        return button;
    }


    private void shareOnFacebook(BlogPost blog) {
        String pageAccessToken = "EAAJ2bQRZArdABOwZC5DgZBVKW6xnxSI8EZCZBLZAdk8jZB3Pq8PM1JpqDwXB93CaXdpXk0EOQAZBh12OBz5ixJ8rVcxi5sXzPRo3QcrZCTEZBxoHBfcE8mtbOH7nATs2xY0BiT9CaF70ki0J55U5NEEKMxARy7egsQCnoNSYU8nOWmZAQ7WArZCcCIZCOI5heTXeT7CPJl1JIWMjvVogkWFIyRotfOpY2";

        Facebook facebook = new FacebookFactory().getInstance();
        facebook.setOAuthAppId("693160549920208", "4d8f306472331be67801bbfcbfdcb9c1");
        facebook.setOAuthAccessToken(new AccessToken(pageAccessToken, null));

        String msg = "New Blog is available now\n"
                + "*** Title: " + blog.getTitle()
                + "\n*** Content: " + blog.getContent();

        try {
            facebook.postStatusMessage(msg);

            // ✅ Affiche une alerte dans l'interface JavaFX
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Partage Facebook");
                alert.setHeaderText(null);
                alert.setContentText("✅ Post partagé avec succès sur la page Facebook.");
                alert.showAndWait();
            });

        } catch (FacebookException e) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur Facebook");
                alert.setHeaderText("Échec du partage");
                alert.setContentText("❌ Erreur lors du partage sur Facebook : " + e.getErrorMessage());
                alert.showAndWait();
            });
            e.printStackTrace();
        }
    }



    private void openCommentWindow(int blogId, boolean readOnly) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/agritrace/comment_popup.fxml"));
            Parent root = loader.load();

            CommentController controller = loader.getController();
            controller.setBlogId(blogId);
            controller.setReadOnlyMode(readOnly);
            controller.setRefreshCallback(this::reloadBlogsFromDatabase);
            controller.initData();

            Stage stage = new Stage();
            stage.setTitle(readOnly ? "Commentaires" : "Ajouter un commentaire");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre des commentaires.");
        }
    }

    private void openCommentViewer(int blogId) {
        openCommentWindow(blogId, true);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onFilterChanged() {
        String selectedFilter = filterComboBox.getValue();

        if (selectedFilter == null || selectedFilter.equals("Tous")) {
            // Affiche tous les blogs sans tri
            blogs.sort(Comparator.comparing(BlogPost::getId)); // ou ton ordre par défaut
        } else if (selectedFilter.equals("Most Liked")) {
            blogs.sort((b1, b2) -> Integer.compare(b2.getLikes(), b1.getLikes()));
        } else if (selectedFilter.equals("Least Liked")) {
            blogs.sort((b1, b2) -> Integer.compare(b1.getLikes(), b2.getLikes()));
        }

        refreshBlogList();
    }


    private void filterBlogsByTitle(String query) {
        if (query == null || query.isEmpty()) {
            blogs = new ArrayList<>(allBlogs);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            blogs = allBlogs.stream()
                    .filter(blog -> blog.getTitle().toLowerCase().contains(lowerCaseQuery))
                    .collect(Collectors.toList());
        }
        updateBlogDisplay();
    }
}