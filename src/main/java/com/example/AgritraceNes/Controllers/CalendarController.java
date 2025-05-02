package com.example.AgritraceNes.Controllers;

import com.example.AgritraceNes.Models.Evenement;
import com.example.AgritraceNes.Services.EvenementService;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarController {

    @FXML
    private GridPane calendarGrid;

    @FXML
    private Label monthLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Button prevMonthButton;

    @FXML
    private Button nextMonthButton;

    private YearMonth currentYearMonth;
    private EvenementService evenementService;

    @FXML
    private void initialize() {
        evenementService = new EvenementService(); // Accès aux événements
        currentYearMonth = YearMonth.now(); // Mois en cours
        updateCalendar();  // Affiche le calendrier du mois actuel
    }

    @FXML
    private void showPreviousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        updateCalendar();
    }

    @FXML
    private void showNextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        updateCalendar();
    }

    private void updateCalendar() {
        // Update month label
        monthLabel.setText(currentYearMonth.getMonth() + " " + currentYearMonth.getYear());

        // Clear previous calendar
        calendarGrid.getChildren().clear();

        // Add day headers (Lun, Mar, etc.)
        String[] days = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};
        for (int i = 0; i < days.length; i++) {
            Label dayLabel = new Label(days[i]);
            dayLabel.getStyleClass().add("day-header");
            calendarGrid.add(dayLabel, i, 0);
        }

        // Calcule le premier jour et le nombre de jours du mois :

        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int firstDayOfWeek = firstOfMonth.getDayOfWeek().getValue() - 1; // 0 = Monday, 6 = Sunday
        int daysInMonth = currentYearMonth.lengthOfMonth();

        // Fetch events
        List<Evenement> evenements = evenementService.getAllEvenements();

        // for (int day = 1; day <= daysInMonth; day++) {

        int row = 1;
        int col = firstDayOfWeek;
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentYearMonth.atDay(day);
            VBox dayBox = new VBox(5);
            dayBox.getStyleClass().add("day-box");

            // Crée chaque jour du calendrier
            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.getStyleClass().add("day-label");
            dayBox.getChildren().add(dayLabel);

            // Add events for this date
            for (Evenement event : evenements) {
                if (!event.getDateDebut().isAfter(date) && !event.getDateFin().isBefore(date)) {
                    Label eventMarker = new Label("✿"); // Flower icon as a placeholder
                    eventMarker.getStyleClass().add("event-marker");
                    Tooltip tooltip = new Tooltip(
                            "Événement: " + event.getNom() + "\n" +
                                    "Description: " + event.getDescription() + "\n" +
                                    "Lieu: " + event.getLieu() + "\n" +
                                    "Prix: " + event.getPrix() + "\n" +
                                    "Places: " + event.getPlacesDisponibles()
                    );
                    eventMarker.setTooltip(tooltip);
                    dayBox.getChildren().add(eventMarker);
                }
            }

            // Add click handler to show event details
            dayBox.setOnMouseClicked(event -> showEventDetails(date, evenements));

            calendarGrid.add(dayBox, col, row);
            col++;
            if (col == 7) {
                col = 0;
                row++;
            }
        }
    }

    private void showEventDetails(LocalDate date, List<Evenement> evenements) {
        // Filter events for the selected date
        List<Evenement> eventsOnDate = evenements.stream()
                .filter(e -> !e.getDateDebut().isAfter(date) && !e.getDateFin().isBefore(date))
                .collect(Collectors.toList());

        // Create a new Stage for the pop-up
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Événements du " + date.toString());

        // Create a VBox to hold event details
        VBox eventDetailsBox = new VBox(10);
        eventDetailsBox.getStyleClass().add("event-details-box");

        // Add a header
        Label headerLabel = new Label("Événements du " + date.getDayOfMonth() + " " + date.getMonth() + " " + date.getYear());
        headerLabel.getStyleClass().add("event-details-header");
        eventDetailsBox.getChildren().add(headerLabel);

        // Add event details or a "no events" message
        if (eventsOnDate.isEmpty()) {
            Label noEventsLabel = new Label("Aucun événement pour ce jour.");
            noEventsLabel.getStyleClass().add("no-events-label");
            eventDetailsBox.getChildren().add(noEventsLabel);
        } else {
            for (Evenement event : eventsOnDate) {
                VBox eventBox = new VBox(5);
                eventBox.getStyleClass().add("event-box");

                Label nameLabel = new Label("Événement: " + event.getNom());
                nameLabel.getStyleClass().add("event-detail-label");
                Label descLabel = new Label("Description: " + event.getDescription());
                descLabel.getStyleClass().add("event-detail-label");
                Label lieuLabel = new Label("Lieu: " + event.getLieu());
                lieuLabel.getStyleClass().add("event-detail-label");
                Label prixLabel = new Label("Prix: " + event.getPrix());
                prixLabel.getStyleClass().add("event-detail-label");
                Label placesLabel = new Label("Places disponibles: " + event.getPlacesDisponibles());
                placesLabel.getStyleClass().add("event-detail-label");

                eventBox.getChildren().addAll(nameLabel, descLabel, lieuLabel, prixLabel, placesLabel);
                eventDetailsBox.getChildren().add(eventBox);
            }
        }

        // Add a close button
        Button closeButton = new Button("Fermer");
        closeButton.getStyleClass().add("nav-button");
        closeButton.setOnAction(e -> dialogStage.close());
        eventDetailsBox.getChildren().add(closeButton);

        // Wrap the VBox in a ScrollPane
        ScrollPane scrollPane = new ScrollPane(eventDetailsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        // Create the scene and apply CSS
        Scene scene = new Scene(scrollPane, 300, 400);
        scene.getStylesheets().add(getClass().getResource("/calendar.css").toExternalForm());
        dialogStage.setScene(scene);
        dialogStage.show();
    }
}