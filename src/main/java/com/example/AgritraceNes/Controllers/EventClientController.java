package com.example.AgritraceNes.Controllers;

import com.example.AgritraceNes.Models.Evenement;
import com.example.AgritraceNes.Models.Participant;
import com.example.AgritraceNes.Services.EvenementService;
import com.example.AgritraceNes.Services.ParticipantService;
import com.example.AgritraceNes.utils.PdfExporter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.stage.Stage;
import org.json.JSONObject;

public class EventClientController implements Initializable {

    @FXML private FlowPane eventCardContainer;
    @FXML private TextField txtRecherche;
    @FXML private DatePicker dateDebutFiltre;
    @FXML private DatePicker dateFinFiltre;
    @FXML private ComboBox<String> comboBoxStatut;
    @FXML private Button btnPrecedent;
    @FXML private Button btnSuivant;
    @FXML private Button btnCalendrier;
    @FXML private Label lblPage;
    @FXML private HBox weatherBox;
    @FXML private Label weatherLocation;
    @FXML private ImageView weatherIcon;
    @FXML private Label weatherTemp;
    @FXML private Label weatherDesc;

    private final EvenementService evenementService = new EvenementService();
    private final ParticipantService participantService = new ParticipantService();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String WEATHER_API_KEY = "cb8573f65bacabd1c58aff1bb5c18b92";
    private static final String WEATHER_API_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric";
    private static final int EVENTS_PAR_PAGE = 5;
    private int pageActuelle = 0;
    private List<Evenement> tousLesEvenements;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tousLesEvenements = evenementService.getAllEvenements();
        afficherPage(pageActuelle);

        btnPrecedent.setOnAction(e -> {
            if (pageActuelle > 0) {
                pageActuelle--;
                afficherPage(pageActuelle);
            }
        });

        btnSuivant.setOnAction(e -> {
            if ((pageActuelle + 1) * EVENTS_PAR_PAGE < tousLesEvenements.size()) {
                pageActuelle++;
                afficherPage(pageActuelle);
            }
        });

        txtRecherche.textProperty().addListener((observable, oldValue, newValue) -> rechercherEvenements());

        comboBoxStatut.getItems().addAll("Tous", "À venir", "Passés");
        comboBoxStatut.getSelectionModel().select("Tous");

        dateDebutFiltre.valueProperty().addListener((obs, oldVal, newVal) -> appliquerFiltrage());
        dateFinFiltre.valueProperty().addListener((obs, oldVal, newVal) -> appliquerFiltrage());
        comboBoxStatut.valueProperty().addListener((obs, oldVal, newVal) -> appliquerFiltrage());
    }

    private void afficherPage(int numeroPage) {
        int start = numeroPage * EVENTS_PAR_PAGE;
        int end = Math.min(start + EVENTS_PAR_PAGE, tousLesEvenements.size());

        List<Evenement> page = tousLesEvenements.subList(start, end);
        afficherEvenements(page);

        lblPage.setText("Page " + (numeroPage + 1));
        btnPrecedent.setDisable(numeroPage == 0);
        btnSuivant.setDisable(end >= tousLesEvenements.size());
    }

    private void afficherTousLesEvenements() {
        tousLesEvenements = evenementService.getAllEvenements();
        pageActuelle = 0;
        afficherPage(pageActuelle);
    }

    private void afficherEvenements(List<Evenement> evenements) {
        eventCardContainer.getChildren().clear();
        evenements.forEach(ev -> eventCardContainer.getChildren().add(createEventCard(ev)));
    }

    private VBox createEventCard(Evenement evenement) {
        VBox card = new VBox(8);
        card.setPrefWidth(240);

        ImageView imageView = new ImageView();
        try {
            String imagePath = evenement.getImage() != null ? evenement.getImage() : "/images/placeholder.png";
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            imageView.setImage(image);
        } catch (Exception e) {
            imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
        }
        configurerImageView(imageView);

        Label nom = new Label(evenement.getNom());
        Label lieu = new Label(evenement.getLieu());
        lieu.setWrapText(true);

        Label prix = new Label("Prix: " + evenement.getPrix() + " DT");
        prix.setWrapText(true);

        Label placesLabel = new Label("Places disponibles: ");
        Label placesDisponibles = new Label(String.valueOf(evenement.getPlacesDisponibles()));
        HBox placesBox = new HBox(5, placesLabel, placesDisponibles);

        Label date = new Label(String.format("Du %s au %s",
                evenement.getDateDebut().format(DATE_FORMATTER),
                evenement.getDateFin().format(DATE_FORMATTER)));

        Button participateBtn = new Button();
        if (evenement.getPlacesDisponibles() > 0) {
            participateBtn.setText("Participer");
            participateBtn.setOnAction(e -> gererParticipation(evenement));
        } else {
            participateBtn.setText("Complet");
            participateBtn.setDisable(true);
        }

        Button mapButton = new Button("Voir sur la carte");
        mapButton.setOnAction(e -> afficherCarte(evenement.getLieu())); // Passer le lieu de l'événement


        // Ajout des composants à la carte
        card.getChildren().addAll(imageView, nom, lieu, date, prix, placesBox, participateBtn, mapButton);

        card.setOnMouseClicked(e -> fetchWeatherData(evenement.getLieu()));

        if (evenement.getPlacesDisponibles() <= 0) {
            afficherSuggestions(evenement, card);
        }

        return card;
    }



    private void afficherSuggestions(Evenement evenement, VBox card) {
        List<Evenement> suggestions = evenementService.getEvenementsSimilaires(evenement.getCategorieId(), evenement.getId());
        if (!suggestions.isEmpty()) {
            Label suggestionsLabel = new Label("Suggestions d'événements :");
            card.getChildren().add(suggestionsLabel);

            for (Evenement suggestion : suggestions) {
                HBox suggestionBox = new HBox(5);

                Label suggestionLabel = new Label(suggestion.getNom() + " - " + suggestion.getDateDebut().format(DATE_FORMATTER));
                suggestionLabel.getStyleClass().add("suggestion-label");

                Button voirBtn = new Button("Voir");
                voirBtn.getStyleClass().add("btn-suggestion");

                voirBtn.setOnAction(e -> {
                    eventCardContainer.getChildren().clear();
                    eventCardContainer.getChildren().add(createEventCard(suggestion));
                });

                suggestionBox.getChildren().addAll(suggestionLabel, voirBtn);
                card.getChildren().add(suggestionBox);
            }
        }
    }

    private void configurerImageView(ImageView imageView) {
        imageView.setFitWidth(220);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(false);
    }

    private void gererParticipation(Evenement evenement) {
        boolean placesUpdated = evenementService.decrementerPlacesDisponibles(evenement.getId());
        if (placesUpdated) {
            Participant p = new Participant();
            p.setDateParticipation(LocalDateTime.now());
            p.setNombrePersonnes(1);
            p.setClientId(1); // À remplacer avec l'ID du client connecté
            p.setEvenementId(evenement.getId());

            boolean participationSaved = participantService.addParticipant(p);

            if (participationSaved) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Participation réussie");
                alert.setHeaderText("Votre participation a été enregistrée !");
                alert.setContentText("Souhaitez-vous télécharger votre confirmation de participation (PDF) ?");

                ButtonType btnOui = new ButtonType("Oui", ButtonBar.ButtonData.YES);
                ButtonType btnNon = new ButtonType("Non", ButtonBar.ButtonData.NO);
                alert.getButtonTypes().setAll(btnOui, btnNon);

                alert.showAndWait().ifPresent(response -> {
                    if (response == btnOui) {
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Enregistrer la confirmation de participation");
                        fileChooser.setInitialFileName("Confirmation_Participation_" + evenement.getNom().replaceAll(" ", "_") + ".pdf");
                        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
                        File selectedFile = fileChooser.showSaveDialog(null);

                        if (selectedFile != null) {
                            PdfExporter.exporterConfirmationParticipation(evenement, p, selectedFile.getAbsolutePath());
                            try {
                                if (selectedFile.exists() && Desktop.isDesktopSupported()) {
                                    Desktop.getDesktop().open(selectedFile);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                showAlert("Erreur", "Impossible d'ouvrir le fichier PDF.", Alert.AlertType.ERROR);
                            }
                        }
                    }
                });

                afficherTousLesEvenements();
            } else {
                showAlert("Erreur", "Erreur lors de l'enregistrement de la participation.", Alert.AlertType.ERROR);
            }
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void rechercherEvenements() {
        String recherche = txtRecherche.getText().trim(); //Récupère le texte saisi dans le champ de recherche txtRecherche.
        List<Evenement> resultats = evenementService.rechercherEvenements(recherche);//Appelle la méthode rechercherEvenements() du service, qui exécute une requête SQL sur les événements dont le nom ou le lieu correspond au mot-clé.
        tousLesEvenements = resultats;//Remplace la liste principale des événements par cette liste filtrée.
        pageActuelle = 0;
        afficherPage(pageActuelle);
    }

    @FXML
    private void appliquerFiltrage() {
        String recherche = txtRecherche.getText().trim();
        LocalDate dateDebut = dateDebutFiltre.getValue();
        LocalDate dateFin = dateFinFiltre.getValue();
        String statut = comboBoxStatut.getValue();

        List<Evenement> resultats = evenementService.filtrerEvenementsAvances(
                recherche, "", null, null, null, dateDebut, dateFin);

        LocalDate today = LocalDate.now();
        switch (statut) {
            case "À venir":
                resultats = resultats.stream()
                        .filter(e -> e.getDateDebut().isAfter(today))
                        .collect(Collectors.toList());
                break;
            case "Passés":
                resultats = resultats.stream()
                        .filter(e -> e.getDateDebut().isBefore(today))
                        .collect(Collectors.toList());
                break;
        }

        tousLesEvenements = resultats;
        pageActuelle = 0;
        afficherPage(pageActuelle);
    }

    private void fetchWeatherData(String location) {
        try {
            String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);
            String url = String.format(WEATHER_API_URL, encodedLocation, WEATHER_API_KEY);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());

            if (json.has("cod") && json.getInt("cod") != 200) {
                updateWeatherUIError("Erreur: " + json.optString("message", "Unknown error"));
                return;
            }

            String city = json.optString("name", location);
            double temp = json.getJSONObject("main").getDouble("temp");
            String desc = "", iconCode = "";

            if (json.has("weather") && json.getJSONArray("weather").length() > 0) {
                JSONObject weather = json.getJSONArray("weather").getJSONObject(0);
                desc = weather.optString("description", "No description");
                iconCode = weather.optString("icon", "");
            }

            weatherLocation.setText("Météo: " + city);
            weatherTemp.setText(String.format("%.1f°C", temp));
            weatherDesc.setText(desc);

            if (!iconCode.isEmpty()) {
                String iconUrl = "http://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                weatherIcon.setImage(new Image(iconUrl));
            } else {
                weatherIcon.setImage(null);
            }

        } catch (Exception e) {
            updateWeatherUIError("Impossible de récupérer la météo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateWeatherUIError(String message) {
        weatherLocation.setText("Météo: Erreur");
        weatherTemp.setText("");
        weatherDesc.setText(message);
        weatherIcon.setImage(null);
    }
    @FXML
    private void ouvrirCalendrier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/AgritraceNes/calendar.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Calendrier des Événements");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible d'ouvrir le calendrier");
            alert.setContentText("Une erreur s'est produite lors du chargement de la vue calendrier.");
            alert.showAndWait();
        }
    }
    private void afficherCarte(String lieu) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/AgritraceNes/mapview.fxml"));
            Parent root = loader.load();

            MapViewController controller = loader.getController();
            controller.loadMap(lieu);

            Stage stage = new Stage();
            stage.setTitle("Carte - " + lieu);
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible d'ouvrir la carte");
            alert.setContentText("Le fichier mapview.fxml est introuvable ou corrompu.");
            alert.showAndWait();
        }
    }
}
