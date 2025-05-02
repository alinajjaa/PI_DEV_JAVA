package com.example.AgritraceNes.Controllers;

import com.example.AgritraceNes.Services.StatistiqueService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;

public class AdminStatistiquesController {

    @FXML private BarChart<String, Number> barChartParticipants;
    @FXML private BarChart<String, Number> barChartEvenementsParLieu;
    @FXML private PieChart pieChartTopEvenements;
    @FXML private Label lblMoyenne, lblMediane, lblEcartType;
    @FXML private Label lblTestStatistique;
    @FXML private Label totalEvenementsLabel, totalParticipantsLabel;

    private final StatistiqueService service = new StatistiqueService();
    private final DecimalFormat df = new DecimalFormat("#.##");

    @FXML
    private void GoToEventPagee(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/AgritraceNes/tools1.fxml")); // <-- chemin corrigé
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        loadBasicStats();
        loadAdvancedStats();
        updateStatistics();
        updateCharts();
    }

    private void loadBasicStats() {
        // Participants par événement
        XYChart.Series<String, Number> seriesParticipants = new XYChart.Series<>();
        seriesParticipants.setName("Participants");

        service.getNombreParticipantsParEvenement().forEach((k, v) -> {
            XYChart.Data<String, Number> data = new XYChart.Data<>(k, v);
            seriesParticipants.getData().add(data);
        });
        barChartParticipants.getData().add(seriesParticipants);

        // Ajouter Tooltip après que les données soient rendues
        for (XYChart.Data<String, Number> data : seriesParticipants.getData()) {
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    Tooltip.install(newNode, new Tooltip(data.getXValue() + ": " + data.getYValue()));
                }
            });
        }

        // Événements par lieu
        XYChart.Series<String, Number> seriesLieux = new XYChart.Series<>();
        seriesLieux.setName("Événements");

        service.getNombreEvenementsParLieu().forEach((k, v) -> {
            XYChart.Data<String, Number> data = new XYChart.Data<>(k, v);
            seriesLieux.getData().add(data);
        });
        barChartEvenementsParLieu.getData().add(seriesLieux);

        for (XYChart.Data<String, Number> data : seriesLieux.getData()) {
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    Tooltip.install(newNode, new Tooltip(data.getXValue() + ": " + data.getYValue()));
                }
            });
        }

        // Top 5 événements (PieChart)
        service.getTopEvenementsParParticipants().forEach((k, v) ->
                pieChartTopEvenements.getData().add(new PieChart.Data(k, v))
        );

        // Mettre à jour les statistiques globales (totaux)
        int totalEvenements = service.getNombreParticipantsParEvenement().size();
        int totalParticipants = service.getNombreParticipantsParEvenement().values().stream().mapToInt(Integer::intValue).sum();

        totalEvenementsLabel.setText(String.valueOf(totalEvenements));
        totalParticipantsLabel.setText(String.valueOf(totalParticipants));
    }

    private void loadAdvancedStats() {
        Map<String, Double> stats = service.getStatistiquesDescriptives();
        lblMoyenne.setText(df.format(stats.getOrDefault("Moyenne", 0.0)));
        lblMediane.setText(df.format(stats.getOrDefault("Médiane", 0.0)));
        lblEcartType.setText(df.format(stats.getOrDefault("Écart-type", 0.0)));
    }

    private void updateStatistics() {
        // Mettre à jour les statistiques descriptives si nécessaire
        // Par exemple, mettre à jour d'autres labels si des valeurs supplémentaires sont nécessaires
    }

    private void updateCharts() {
        // Mettre à jour les graphiques si nécessaire (par exemple, recharger les données, appliquer des filtres, etc.)
    }
}
