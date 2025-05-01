package com.example.agritrace.Controllers;

import com.example.agritrace.Models.Report;
import com.example.agritrace.Services.ReportService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class ReportController {
    @FXML
    private TextArea reasonTextArea;

    private int blogId;
    private String description;

    // Ajout d'un callback pour signaler l'envoi du signalement
    private Runnable onReportSubmitted;

    public void setContext(int blogId, String description) {
        this.blogId = blogId;
        this.description = description;
    }

    // Méthode pour définir un callback lorsque le signalement est soumis
    public void setOnReportSubmitted(Runnable onReportSubmitted) {
        this.onReportSubmitted = onReportSubmitted;
    }

    @FXML
    private void handleSend() {
        String description = reasonTextArea.getText().trim();

        // Validation : vérifier si la description est vide
        if (description.isEmpty()) {
            showAlert(AlertType.WARNING, "Erreur", "Veuillez fournir une raison pour le signalement.");
            return;
        }

        // Créer le signalement
        Report report = new Report(blogId, description);
        ReportService service = new ReportService();

        try {
            // Ajouter le signalement via le service
            service.addReport(report);

            // Afficher une alerte de confirmation
            showAlert(AlertType.INFORMATION, "Signalement envoyé", "Votre signalement a été envoyé avec succès.");

            // Appeler le callback si défini
            if (onReportSubmitted != null) {
                onReportSubmitted.run();
            }

        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Une erreur est survenue lors de l'envoi du signalement.");
        }

        // Fermer la fenêtre après l'envoi
        Stage stage = (Stage) reasonTextArea.getScene().getWindow();
        stage.close();
    }

    // Méthode utilitaire pour afficher des alertes
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
