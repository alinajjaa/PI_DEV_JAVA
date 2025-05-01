package com.example.agritrace.Controllers;

import com.example.agritrace.Models.BlogPost;
import com.example.agritrace.Models.Comment;
import com.example.agritrace.Models.Report;
import com.example.agritrace.Services.BlogService;
import com.example.agritrace.Services.CommentService;
import com.example.agritrace.Services.ReportService;
import com.example.agritrace.utils.DualButtonCell;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

public class AdminPanelController {

    @FXML private TableView<BlogPost> blogTableView;
    @FXML private TableColumn<BlogPost, Integer> blogIdColumn;
    @FXML private TableColumn<BlogPost, String> blogTitleColumn;
    @FXML private TableColumn<BlogPost, String> blogContentColumn;
    @FXML private TableColumn<BlogPost, Integer> blogLikesColumn;
    @FXML private TableColumn<BlogPost, Void> blogActionsColumn;

    @FXML private TableView<Comment> commentTableView;
    @FXML private TableColumn<Comment, Integer> commentIdColumn;
    @FXML private TableColumn<Comment, Integer> commentBlogIdColumn;
    @FXML private TableColumn<Comment, String> commentBlogTitleColumn;
    @FXML private TableColumn<Comment, String> commentContentColumn;
    @FXML private TableColumn<Comment, Void> commentActionsColumn;

    @FXML private TableView<Report> reportTableView;
    @FXML private TableColumn<Report, Integer> reportIdColumn;
    @FXML private TableColumn<Report, Integer> reportBlogIdColumn;
    @FXML private TableColumn<Report, String> reportBlogTitleColumn;
    @FXML private TableColumn<Report, String> reportDescriptionColumn;
    @FXML private TableColumn<Report, Void> reportActionsColumn;
    @FXML private Label dialogTitle;
    @FXML private TextField titleField;
    @FXML private TextArea contentArea;


    private final BlogService blogService = new BlogService();
    private final CommentService commentService = new CommentService();
    private final ReportService reportService = new ReportService();


    private ObservableList<BlogPost> blogList = FXCollections.observableArrayList();
    private ObservableList<Comment> commentList = FXCollections.observableArrayList();
    private ObservableList<Report> reportList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        initBlogTable();
        initCommentTable();
        initReportTable();
        loadBlogDataFromDB();
        loadCommentDataFromDB();
        loadReportDataFromDB();
    }

    private void initBlogTable() {
        blogIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        blogTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        blogContentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));
        blogLikesColumn.setCellValueFactory(new PropertyValueFactory<>("likes"));

        blogActionsColumn.setCellFactory(param -> new DualButtonCell<>(
                this::editBlogPost,
                this::deleteBlogPost
        ));
    }

    private void initCommentTable() {
        commentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        commentBlogIdColumn.setCellValueFactory(new PropertyValueFactory<>("blogId"));
        commentBlogTitleColumn.setCellValueFactory(cellData -> {
            String title = getBlogTitleById(cellData.getValue().getBlogId());
            return new ReadOnlyStringWrapper(title);
        });
        commentContentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));

        commentActionsColumn.setCellFactory(param -> new DualButtonCell<>(
                this::editComment,
                this::deleteComment
        ));
    }

    private void initReportTable() {
        reportIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        reportBlogIdColumn.setCellValueFactory(new PropertyValueFactory<>("blogId"));
        reportBlogTitleColumn.setCellValueFactory(cellData -> {
            String title = getBlogTitleById(cellData.getValue().getBlogId());
            return new ReadOnlyStringWrapper(title);
        });
        reportDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        reportActionsColumn.setCellFactory(param -> new DualButtonCell<>(
                this::editReport,
                this::deleteReport
        ));
    }

    private void loadBlogDataFromDB() {
        try {
            blogList.clear();
            blogList.addAll(blogService.afficherBlogs());
            blogTableView.setItems(blogList);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les blogs depuis la base.");
        }
    }

    private void loadCommentDataFromDB() {
        try {
            commentList.clear();
            for (BlogPost blog : blogList) {
                commentList.addAll(commentService.getCommentsForBlog(blog.getId()));
            }
            commentTableView.setItems(commentList);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les commentaires depuis la base.");
        }
    }

    private void loadReportDataFromDB() {
        try {
            reportList.clear();
            for (BlogPost blog : blogList) {
                List<Report> reports = reportService.getReportsForBlog(blog.getId());
                reportList.addAll(reports);
            }
            reportTableView.setItems(reportList);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les signalements depuis la base.");
        }
    }

    private String getBlogTitleById(int blogId) {
        for (BlogPost blog : blogList) {
            if (blog.getId() == blogId) {
                return blog.getTitle();
            }
        }
        return "N/A";
    }

    private void deleteBlogPost(BlogPost blogPost) {
        try {
            blogService.supprimerBlog(blogPost.getId());
            blogList.remove(blogPost);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de supprimer le blog.");
        }
    }

    private void deleteComment(Comment comment) {
        try {
            commentService.deleteComment(comment.getId());
            commentList.remove(comment);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de supprimer le commentaire.");
        }
    }

    private void deleteReport(Report report) {
        // Tu peux ajouter ici un appel à un service pour supprimer de la base
        reportList.remove(report);
    }

    private void editBlogPost(BlogPost blogPost) {
        showEditDialog("Modifier Blog", blogPost, updatedBlog -> {
            blogTableView.refresh();
        });
    }

    private void editComment(Comment comment) {
        showEditDialog("Modifier Commentaire", comment, updatedComment -> {
            commentTableView.refresh();
        });
    }

    private void editReport(Report report) {
        showEditDialog("Modifier Signalement", report, updatedReport -> {
            reportTableView.refresh();
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private <T> void showEditDialog(String title, T item, Consumer<T> updateAction) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/agritrace/EditDialog.fxml"));
            Parent root = loader.load();

            Label dialogTitle = (Label) loader.getNamespace().get("dialogTitle");
            TextField titleField = (TextField) loader.getNamespace().get("titleField");
            TextArea contentArea = (TextArea) loader.getNamespace().get("contentArea");

            dialogTitle.setText(title);

            // Pré-remplir les champs selon le type
            if (item instanceof BlogPost blog) {
                titleField.setText(blog.getTitle());
                contentArea.setText(blog.getContent());
            } else if (item instanceof Comment comment) {
                titleField.setVisible(false); // Pas besoin de titre pour commentaire
                contentArea.setText(comment.getContent());
            } else if (item instanceof Report report) {
                titleField.setVisible(false); // Pas besoin de titre pour report
                contentArea.setText(report.getDescription());
            }

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));

            Button saveBtn = (Button) loader.getNamespace().get("saveButton");
            saveBtn.setOnAction(e -> {
                try {
                    if (item instanceof BlogPost blog) {
                        blog.setTitle(titleField.getText());
                        blog.setContent(contentArea.getText());
                        blogService.modifierBlog(blog);
                        blogTableView.refresh();
                    } else if (item instanceof Comment comment) {
                        comment.setContent(contentArea.getText());
                        commentService.updateComment(comment);
                        commentTableView.refresh();
                    } else if (item instanceof Report report) {
                        report.setDescription(contentArea.getText());
                        reportService.updateReport(report);
                        reportTableView.refresh();
                    }

                    updateAction.accept(item);
                    dialogStage.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showAlert("Erreur", "Erreur lors de la sauvegarde.");
                }
            });
            Button cancelBtn = (Button) loader.getNamespace().get("cancelButton");
            cancelBtn.setOnAction(e -> dialogStage.close());


            dialogStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d’ouvrir le formulaire de modification.");
        }
    }

    @FXML
    private void handleViewStatistics() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/agritrace/StatisticsView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Statistiques");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir les statistiques");
        }
    }
}
