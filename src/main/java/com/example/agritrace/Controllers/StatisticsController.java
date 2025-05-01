package com.example.agritrace.Controllers;

import com.example.agritrace.Models.BlogPost;
import com.example.agritrace.Services.BlogService;
import com.example.agritrace.Services.CommentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class StatisticsController  implements Initializable {
    @FXML private Label totalBlogsLabel;
    @FXML private Label commentsPerBlogLabel;
    @FXML private PieChart commentsChart;

    private final BlogService blogService = new BlogService();
    private final CommentService commentService = new CommentService();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        commentsChart.getStylesheets().add(getClass().getResource("/com/example/agritrace/css/statistics.css").toExternalForm());
        loadStatistics();
    }

    private void loadStatistics() {
        try {
            int totalBlogs = blogService.afficherBlogs().size();
            int totalComments = commentService.getTotalCommentsCount();
            int blogsWithComments = blogService.getBlogsWithCommentsCount();
            int activeComments = commentService.getActiveCommentsCount();

            totalBlogsLabel.setText(String.valueOf(totalBlogs));

            // Ajout d'une vérification de division par zéro
            double average = totalBlogs > 0 ? (double) totalComments / totalBlogs : 0;
            commentsPerBlogLabel.setText(String.format("%.1f", average));

            setupCommentsChart(totalBlogs, blogsWithComments, activeComments);

        } catch (SQLException e) {
            handleError(e); // Appel corrigé
        }

    }

    private void handleError(SQLException e) {
        e.printStackTrace();
        totalBlogsLabel.setText("Erreur");
        commentsPerBlogLabel.setText("Données indisponibles");
        commentsChart.setTitle("Échec du chargement des données");
    }

    private void setupCommentsChart(int totalBlogs, int blogsWithComments, int activeComments) {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Blogs avec commentaires (" + blogsWithComments + ")", blogsWithComments),
                new PieChart.Data("Blogs sans commentaires (" + (totalBlogs - blogsWithComments) + ")", totalBlogs - blogsWithComments),
                new PieChart.Data("Commentaires actifs (" + activeComments + ")", activeComments)
        );

        commentsChart.setData(pieData);
        applyChartStyles();
    }

    private void applyChartStyles() {
        commentsChart.getData().forEach(data -> {
            String label = data.getName().toLowerCase();
            String color;

            if (label.contains("blogs avec")) {
                color = "#e74c3c"; // Rouge vif
            } else if (label.contains("blogs sans")) {
                color = "#f1c40f"; // Jaune
            } else if (label.contains("commentaires actifs")) {
                color = "#27ae60"; // Vert
            } else {
                color = "#95a5a6"; // Gris par défaut
            }

            data.getNode().setStyle("-fx-pie-color: " + color + ";");
        });
    }



    @FXML
    public void handleBack() {
        totalBlogsLabel.getScene().getWindow().hide();
    }
}