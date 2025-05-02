package com.example.AgritraceNes.Controllers;

import com.example.AgritraceNes.Models.Categorie;
import com.example.AgritraceNes.Models.Evenement;
import com.example.AgritraceNes.Models.Participant;
import com.example.AgritraceNes.Services.CategorieEvenementService;
import com.example.AgritraceNes.Services.EvenementService;
import com.example.AgritraceNes.Services.StatistiqueService;
import com.example.AgritraceNes.Services.ParticipantService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.util.StringConverter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import com.example.AgritraceNes.Models.Evenement; // ton modèle Evenement


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.scene.chart.BarChart;
import javafx.stage.FileChooser;


public class EvennementAdminController implements Initializable {
    @FXML
    private TableView<Categorie> CategoriesTableView;

    @FXML
    private TableColumn<Categorie, Integer> idAvisColumn;

    @FXML
    private TableColumn<Categorie, Void> ideventcolumnnA;

    @FXML
    private TableColumn<Categorie, Void> actionsCategorieColumn;

    @FXML
    private TableColumn<Participant, Void> actionsColumnP;
    @FXML
    private Button btnStatistiques;
    private CategorieEvenementService categorieService = new CategorieEvenementService();
    @FXML
    private TableView<Participant> participantsTableView;

    @FXML
    private TableColumn<Participant, Integer> idColumnParticipants;

    @FXML
    private TableColumn<Participant, String> ideventcolumnn;

    @FXML
    private TableColumn<Participant, String> iduserrColumn;

    @FXML
    private TableColumn<Participant, String> dateColumnP;
    private ParticipantService participantService;
    //pages
    @FXML
    VBox ActivitesPage;
    @FXML
    Pane AddEventPage;
    @FXML
    VBox EventsInterface;
    @FXML
    VBox ParticipantsPage;
    @FXML private TextField nomEventInput;
    @FXML private TextField descEventInput;
    @FXML private DatePicker dateDebutInput;
    @FXML private DatePicker dateFinInput;
    @FXML private TextField prixEventInput;
    @FXML private TextField lieuEventInput;
    @FXML private TextField placesEventInput;
    @FXML private ComboBox<Categorie> categorieComboBox;
    @FXML private TextField imageEventInput;
    @FXML private TextField nomEventUpdate;
    @FXML private TextArea descEventUpdate;
    @FXML private DatePicker dateDebutUpdate;
    @FXML private DatePicker dateFinUpdate;
    @FXML private TextField prixEventUpdate;
    @FXML private TextField lieuEventUpdate;
    @FXML private TextField placesEventUpdate;
    @FXML private ComboBox<Categorie> categorieComboUpdate;
    @FXML private TextField imageEventUpdate;
    @FXML  TextField nomCategorieInputU;
    private Evenement evenementSelectionne;
    @FXML
    Pane UpdateEventPage,ADDAcitivitesPage,UpdateAcitivitesPage;
//    @FXML private GridPane eventsGrid;
@FXML
private TableView<Evenement> eventsTable;

    @FXML
    private TableColumn<Evenement, String> nameColumn;
    @FXML
    private TableColumn<Evenement, String> descriptionColumn;
    @FXML
    private TableColumn<Evenement, LocalDate> dateDColumn;
    @FXML
    private TableColumn<Evenement, LocalDate> dateFColumn;
    @FXML
    private TableColumn<Evenement, String> locationColumn;
    @FXML
    private TableColumn<Evenement, Double> prixColumn;
    @FXML
    private TableColumn<Evenement, Integer> placeColumn;
    @FXML
    private TableColumn<Evenement, Void> actionsColumn;
    @FXML
    private TableColumn<Evenement, String> imageColumn;

    private EvenementService evenementService;
//    private final EvenementService evenementService = new EvenementService();






    //Crud Actions Button
    @FXML
    Button AddEventBtn;
    @FXML
    Button UpdateEvent;


    @FXML
    Button AddImageBtn;

    @FXML
    private void handleAfficherStatistiques(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/AgritraceNes/AdminStatistiques.fxml"));
            Parent root = loader.load();
            ((Button) event.getSource()).getScene().setRoot(root);  // Change la scène
        } catch (IOException e) {
            e.printStackTrace();
        }
    }














    @FXML
    TextField ImageEventInput;

    @FXML
    TextField LieuIEventnput;
    @FXML
    ChoiceBox<?> TypeEventInput;


    @FXML
    DatePicker dateEvent1;

    Categorie selectedCategprie;
    @FXML
    DatePicker dateEventInput;

    @FXML
    DatePicker dateEventInput2;

    @FXML
    TextArea desEvent1;

    @FXML
    TextField imageEvent1;

    @FXML
    TextField localisation1;

    @FXML
    TextField nomEvent1;

    @FXML
    TextField nomEventIInput;

    @FXML
    TextField typeEvent1;
    @FXML
    private TextField nomCategorieInput;

    @FXML
    private Button AddEventBtn11;

    @FXML
    private void handleAddCategorie() {
        String nomCategorie = nomCategorieInput.getText().trim();

        if (nomCategorie.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ vide", "Veuillez entrer un nom pour la catégorie.");
            return;
        }

        // Appel au service pour ajouter la catégorie (tu devras créer ce service si ce n’est pas encore fait)
        CategorieEvenementService service = new CategorieEvenementService();
        boolean success = service.ajouterCategorie(nomCategorie);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Catégorie ajoutée avec succès.");
            nomCategorieInput.clear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'ajout de la catégorie.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }





    @FXML
    public void GoToEventPagee(){
        AddEventPage.setVisible(false);
        AddEventPage.setManaged(false);

        ADDAcitivitesPage.setVisible(false);
        ADDAcitivitesPage.setManaged(false);

        UpdateAcitivitesPage.setVisible(false);
        UpdateAcitivitesPage.setManaged(false);

        UpdateEventPage.setVisible(false);
        UpdateEventPage.setManaged(false);


        ActivitesPage.setVisible(false);
        ActivitesPage.setManaged(false);

        ParticipantsPage.setVisible(false);
        ParticipantsPage.setManaged(false);

        EventsInterface.setVisible(true);
        EventsInterface.setManaged(true);
        refreshTable();
    }

    @FXML
    public void GoToAddEventPagee(){
        AddEventPage.setVisible(true);
        AddEventPage.setManaged(true);

        UpdateEventPage.setVisible(false);
        UpdateEventPage.setManaged(false);

        ADDAcitivitesPage.setVisible(false);
        ADDAcitivitesPage.setManaged(false);

        UpdateAcitivitesPage.setVisible(false);
        UpdateAcitivitesPage.setManaged(false);

        ActivitesPage.setVisible(false);
        ActivitesPage.setManaged(false);

        ParticipantsPage.setVisible(false);
        ParticipantsPage.setManaged(false);

        EventsInterface.setVisible(false);
        EventsInterface.setManaged(false);
    }

    @FXML
    public void GoToUpdateEventPagee(){
        AddEventPage.setVisible(false);
        AddEventPage.setManaged(false);

        UpdateEventPage.setVisible(true);
        UpdateEventPage.setManaged(true);

        ADDAcitivitesPage.setVisible(false);
        ADDAcitivitesPage.setManaged(false);

        UpdateAcitivitesPage.setVisible(false);
        UpdateAcitivitesPage.setManaged(false);

        ActivitesPage.setVisible(false);
        ActivitesPage.setManaged(false);

        ParticipantsPage.setVisible(false);
        ParticipantsPage.setManaged(false);

        EventsInterface.setVisible(false);
        EventsInterface.setManaged(false);

    }

    public void GoToUpdateCategoriePage(){
        AddEventPage.setVisible(false);
        AddEventPage.setManaged(false);

        UpdateEventPage.setVisible(false);
        UpdateEventPage.setManaged(false);

        ADDAcitivitesPage.setVisible(false);
        ADDAcitivitesPage.setManaged(false);

        UpdateAcitivitesPage.setVisible(false);
        UpdateAcitivitesPage.setManaged(false);

        ActivitesPage.setVisible(false);
        ActivitesPage.setManaged(false);

        ParticipantsPage.setVisible(false);
        ParticipantsPage.setManaged(false);

        EventsInterface.setVisible(false);
        EventsInterface.setManaged(false);

        UpdateAcitivitesPage.setVisible(true);
        UpdateAcitivitesPage.setManaged(true);
    }

    @FXML
    public void GoToParticipantPagee(){
        AddEventPage.setVisible(false);
        AddEventPage.setManaged(false);

        UpdateEventPage.setVisible(false);
        UpdateEventPage.setManaged(false);

        ADDAcitivitesPage.setVisible(false);
        ADDAcitivitesPage.setManaged(false);

        UpdateAcitivitesPage.setVisible(false);
        UpdateAcitivitesPage.setManaged(false);

        ActivitesPage.setVisible(false);
        ActivitesPage.setManaged(false);

        ParticipantsPage.setVisible(true);
        ParticipantsPage.setManaged(true);

        EventsInterface.setVisible(false);
        EventsInterface.setManaged(false);
    }

    @FXML
    public void GoToActivitesPagePagee(){
        AddEventPage.setVisible(false);
        AddEventPage.setManaged(false);

        UpdateEventPage.setVisible(false);
        UpdateEventPage.setManaged(false);

        ADDAcitivitesPage.setVisible(false);
        ADDAcitivitesPage.setManaged(false);

        UpdateAcitivitesPage.setVisible(false);
        UpdateAcitivitesPage.setManaged(false);

        ActivitesPage.setVisible(true);
        ActivitesPage.setManaged(true);

        ParticipantsPage.setVisible(false);
        ParticipantsPage.setManaged(false);

        EventsInterface.setVisible(false);
        EventsInterface.setManaged(false);
    }

    @FXML
    public void GoToAddActivitesPagePagee(){
        AddEventPage.setVisible(false);
        AddEventPage.setManaged(false);

        UpdateEventPage.setVisible(false);
        UpdateEventPage.setManaged(false);

        ADDAcitivitesPage.setVisible(true);
        ADDAcitivitesPage.setManaged(true);

        UpdateAcitivitesPage.setVisible(false);
        UpdateAcitivitesPage.setManaged(false);

        ActivitesPage.setVisible(false);
        ActivitesPage.setManaged(false);

        ParticipantsPage.setVisible(false);
        ParticipantsPage.setManaged(false);

        EventsInterface.setVisible(false);
        EventsInterface.setManaged(false);
    }

    @FXML
    public void GoToUpdateActivitesPagePagee(){
        AddEventPage.setVisible(false);
        AddEventPage.setManaged(false);

        UpdateEventPage.setVisible(false);
        UpdateEventPage.setManaged(false);

        ADDAcitivitesPage.setVisible(false);
        ADDAcitivitesPage.setManaged(false);

        UpdateAcitivitesPage.setVisible(true);
        UpdateAcitivitesPage.setManaged(true);

        ActivitesPage.setVisible(false);
        ActivitesPage.setManaged(false);

        ParticipantsPage.setVisible(false);
        ParticipantsPage.setManaged(false);

        EventsInterface.setVisible(false);
        EventsInterface.setManaged(false);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        participantService = new ParticipantService();

        if (evenementService == null) {
            evenementService = new EvenementService();
        }
        AddEventPage.setVisible(false);
        AddEventPage.setManaged(false);

        UpdateEventPage.setVisible(false);
        UpdateEventPage.setManaged(false);

        ADDAcitivitesPage.setVisible(false);
        ADDAcitivitesPage.setManaged(false);

        UpdateAcitivitesPage.setVisible(false);
        UpdateAcitivitesPage.setManaged(false);

        ActivitesPage.setVisible(false);
        ActivitesPage.setManaged(false);

        ParticipantsPage.setVisible(false);
        ParticipantsPage.setManaged(false);

        EventsInterface.setVisible(true);
        EventsInterface.setManaged(true);
        // Associer les colonnes aux attributs de la classe Categorie
        idAvisColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        ideventcolumnnA.setCellValueFactory(new PropertyValueFactory<>("nom"));
        idColumnParticipants.setCellValueFactory(new PropertyValueFactory<>("id"));
        ideventcolumnn.setCellValueFactory(new PropertyValueFactory<>("evenementId"));
        iduserrColumn.setCellValueFactory(new PropertyValueFactory<>("clientId"));
        dateColumnP.setCellValueFactory(new PropertyValueFactory<>("dateParticipation"));

        // Charger les données dans le TableView
        loadCategories();
        loadCategoriesIntoComboBox();
        configurerComboBox();
        configurerTable();
        addActionsToTable();
        addActionsToTableEvent();
        loadParticipants();
        addActionsToTableParticipant();

        chargerCategories();
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateDColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        dateFColumn.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("lieu"));
        prixColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
        placeColumn.setCellValueFactory(new PropertyValueFactory<>("placesDisponibles"));

        loadAndDisplayEvents();



    }
    private TableCell<Evenement, LocalDate> formatDateCell() {
        return new TableCell<Evenement, LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(formatter.format(date));
                }
            }
        };
    }

    private void configurerTable() {
        // Configuration des cellules de date
        dateDColumn.setCellFactory(col -> formatDateCell());
        dateFColumn.setCellFactory(col -> formatDateCell());

        // Configuration des autres colonnes
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        prixColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("lieu"));
        placeColumn.setCellValueFactory(new PropertyValueFactory<>("placesDisponibles"));

        // Chargement initial
        refreshTable();
    }
//    @FXML
//    private void GoToUpdateEventPagee() {
//        if (evenementSelectionne == null) return;
//
//        // Peupler les champs
//        nomEventUpdate.setText(evenementSelectionne.getNom());
//        descEventUpdate.setText(evenementSelectionne.getDescription());
//        dateDebutUpdate.setValue(evenementSelectionne.getDateDebut());
//        dateFinUpdate.setValue(evenementSelectionne.getDateFin());
//        prixEventUpdate.setText(String.valueOf(evenementSelectionne.getPrix()));
//        lieuEventUpdate.setText(evenementSelectionne.getLieu());
//        placesEventUpdate.setText(String.valueOf(evenementSelectionne.getPlacesDisponibles()));
//        imageEventUpdate.setText(evenementSelectionne.getImage());
//
//        // Navigation
//        showPage("UpdateEventPage");
//    }

//    @FXML
//    private void handleUpdateEvent() {
//        Evenement updated = new Evenement(
//                nomEventUpdate.getText(),
//                descEventUpdate.getText(),
//                dateDebutUpdate.getValue(),
//                dateFinUpdate.getValue(),
//                lieuEventUpdate.getText(),
//                Double.parseDouble(prixEventUpdate.getText()),
//                Integer.parseInt(placesEventUpdate.getText()),
//                imageEventUpdate.getText(),
//                categorieComboUpdate.getValue().getId()
//        );
//        updated.setId(evenementSelectionne.getId());
//
//        if (evenementService.modifierEvenement(updated)) {
//            refreshTable();
//            showPage("EventsInterface");
//        }
//    }


    private void showPage(String pageName) {
        // Logique de gestion de visibilité des pages
        AddEventPage.setVisible(false);
        UpdateEventPage.setVisible(pageName.equals("UpdateEventPage"));
        EventsInterface.setVisible(pageName.equals("EventsInterface"));
    }

    private void setupPageVisibility() {
        AddEventPage.setVisible(false);
        AddEventPage.setManaged(false);
        UpdateEventPage.setVisible(false);
        UpdateEventPage.setManaged(false);
        ADDAcitivitesPage.setVisible(false);
        ADDAcitivitesPage.setManaged(false);
        UpdateAcitivitesPage.setVisible(false);
        UpdateAcitivitesPage.setManaged(false);
        ActivitesPage.setVisible(false);
        ActivitesPage.setManaged(false);
        ParticipantsPage.setVisible(false);
        ParticipantsPage.setManaged(false);
        EventsInterface.setVisible(true);
        EventsInterface.setManaged(true);
    }

    private void loadAndDisplayEvents() {
        List<Evenement> evenements = evenementService.getAllEvenements();
        ObservableList<Evenement> eventList = FXCollections.observableArrayList(evenements);
        eventsTable.setItems(eventList);
        refreshTable();
    }

    private void loadCategories() {
        ObservableList<Categorie> categorieList = FXCollections.observableArrayList(categorieService.getAllCategories());
        CategoriesTableView.setItems(categorieList);
    }





    private void loadCategoriesIntoComboBox() {
        List<Categorie> categories = categorieService.getAllCategories(); // Appel au service qui récupère depuis la BDD
        ObservableList<Categorie> observableCategories = FXCollections.observableArrayList(categories);
        categorieComboBox.setItems(observableCategories);
    }


    private void clearFormFields() {
        nomEvent1.clear();
        desEvent1.clear();
        localisation1.clear();
        LieuIEventnput.clear();
        typeEvent1.clear();
        imageEvent1.clear();
        categorieComboBox.setValue(null);
        dateEvent1.setValue(null);
        dateEventInput2.setValue(null);
    }


    private void configurerComboBox() {
        categorieComboBox.setConverter(new StringConverter<Categorie>() {
            @Override
            public String toString(Categorie categorie) {
                return categorie != null ? categorie.getNom() : "";
            }

            @Override
            public Categorie fromString(String string) {
                return null;
            }
        });

        categorieComboBox.setCellFactory(lv -> new ListCell<Categorie>() {
            @Override
            protected void updateItem(Categorie item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNom());
            }
        });
    }

    private void chargerCategories() {
        List<Categorie> categories = categorieService.getAllCategories();
        ObservableList<Categorie> observableList = FXCollections.observableArrayList(categories);
        categorieComboBox.setItems(observableList);
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        // ... config

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                // Chemin de destination
                String destDir = "src/main/resources/storage/event_images/";
                String fileName = UUID.randomUUID() + "_" + selectedFile.getName();

                // Copie physique
                Path destination = Paths.get(destDir + fileName);
                Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

                // Stockage du chemin relatif
                imageEventInput.setText("/storage/event_images/" + fileName);
            } catch (IOException e) {
                afficherAlerte("Erreur", "Échec de l'upload de l'image", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleAddEvent() {
        try {
            // Validation des champs
            if (!validerFormulaire()) return;

            // Création de l'événement
            Evenement event = new Evenement(
                    nomEventInput.getText(),
                    descEventInput.getText(),
                    dateDebutInput.getValue(),
                    dateFinInput.getValue(),
                    lieuEventInput.getText(),
                    Double.parseDouble(prixEventInput.getText()),
                    Integer.parseInt(placesEventInput.getText()),
                    imageEventInput.getText(),
                    categorieComboBox.getValue().getId()
            );

            // Enregistrement
            if (evenementService.ajouterEvenement(event)) {
                afficherAlerte("Succès", "Événement ajouté avec succès!", Alert.AlertType.INFORMATION);
                reinitialiserFormulaire();
            } else {
                afficherAlerte("Erreur", "Échec de l'ajout", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur de format", "Vérifiez les champs numériques", Alert.AlertType.ERROR);
        } catch (Exception e) {
            afficherAlerte("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        }
        GoToEventPagee();
    }

    private boolean validerFormulaire() {
        StringBuilder erreurs = new StringBuilder();

        if (nomEventInput.getText().isBlank()) erreurs.append("- Nom requis\n");
        if (descEventInput.getText().isBlank()) erreurs.append("- Description requise\n");
        if (dateDebutInput.getValue() == null) erreurs.append("- Date début requise\n");
        if (dateFinInput.getValue() == null) erreurs.append("- Date fin requise\n");
        if (prixEventInput.getText().isBlank()) erreurs.append("- Prix requis\n");
        if (lieuEventInput.getText().isBlank()) erreurs.append("- Lieu requis\n");
        if (placesEventInput.getText().isBlank()) erreurs.append("- Places requises\n");
        if (categorieComboBox.getValue() == null) erreurs.append("- Catégorie requise\n");
        if (imageEventInput.getText().isBlank()) erreurs.append("- Image requise\n");

        if (erreurs.length() > 0) {
            afficherAlerte("Validation", erreurs.toString(), Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }
    private void refreshTable() {
        List<Evenement> evenements = evenementService.getAllEvenements();
        ObservableList<Evenement> eventList = FXCollections.observableArrayList(evenements);
        eventsTable.setItems(eventList);
        eventsTable.refresh();
    }
    private void reinitialiserFormulaire() {
        nomEventInput.clear();
        descEventInput.clear();
        dateDebutInput.setValue(null);
        dateFinInput.setValue(null);
        prixEventInput.clear();
        lieuEventInput.clear();
        placesEventInput.clear();
        categorieComboBox.setValue(null);
        imageEventInput.clear();
    }

    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleDeleteEvent(Evenement evenement) {
        if (evenement != null) {
            // Logique de suppression
            evenementService.supprimerEvenement(evenement.getId());
            refreshTable();
        }
    }






    private void addActionsToTable() {
        Callback<TableColumn<Evenement, Void>, TableCell<Evenement, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Evenement, Void> call(final TableColumn<Evenement, Void> param) {
                return new TableCell<>() {
                    private final Button btnEdit = new Button("Modifier");
                    private final Button btnDelete = new Button("Supprimer");
                    private final HBox hbox = new HBox(10, btnEdit, btnDelete);

                    {
                        btnEdit.getStyleClass().add("action-btn");
                        btnDelete.getStyleClass().add("delete-btn");

                        btnEdit.setOnAction(event -> {
                            Evenement e = getTableView().getItems().get(getIndex());

                            handleUpdateEvent(e);
                        });



                        btnDelete.setOnAction(event -> {
                            Evenement e = getTableView().getItems().get(getIndex());
                            handleDeleteEvent(e); // à définir
                        });

                        hbox.setAlignment(Pos.CENTER);
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(hbox);
                        }
                    }
                };
            }
        };

        actionsColumn.setCellFactory(cellFactory);
    }
    private void addActionsToTableEvent() {
        Callback<TableColumn<Categorie, Void>, TableCell<Categorie, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Categorie, Void> call(final TableColumn<Categorie, Void> param) {
                return new TableCell<>() {
                    private final Button btnEdit = new Button("Modifier");
                    private final Button btnDelete = new Button("Supprimer");
                    private final HBox hbox = new HBox(10, btnEdit, btnDelete);

                    {
                        btnEdit.getStyleClass().add("action-btn");
                        btnDelete.getStyleClass().add("delete-btn");

                        btnEdit.setOnAction(event -> {
                            Categorie e = getTableView().getItems().get(getIndex());
                            nomCategorieInputU.setText(e.getNom());
                            selectedCategprie = e;
                            GoToUpdateCategoriePage();
                        });



                        btnDelete.setOnAction(event -> {
                            Categorie e = getTableView().getItems().get(getIndex());
                            categorieService.deleteCategorie(e.getId());
                            loadCategories();
                        });

                        hbox.setAlignment(Pos.CENTER);
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(hbox);
                        }
                    }
                };
            }
        };

        actionsCategorieColumn.setCellFactory(cellFactory);
    }

    private void addActionsToTableParticipant() {
        Callback<TableColumn<Participant, Void>, TableCell<Participant, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Participant, Void> call(final TableColumn<Participant, Void> param) {
                return new TableCell<>() {

                    private final Button btnDelete = new Button("Supprimer");

                    {
                        btnDelete.getStyleClass().add("delete-btn");

                        btnDelete.setOnAction(event -> {
                            Participant p = getTableView().getItems().get(getIndex());
                            participantService.deleteParticipant(p.getId());
                            loadParticipants();
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnDelete);
                        }
                    }
                };
            }
        };

        actionsColumnP.setCellFactory(cellFactory);
    }

    @FXML
    private void handleSubmitUpdate() {
        String nouveauNom = nomCategorieInputU.getText().trim();

        if (nouveauNom.isEmpty()) {
            return;
        }

        boolean updated = categorieService.updateCategorie(selectedCategprie.getId(), nouveauNom);
        if (updated) {
            nomCategorieInputU.clear();
            GoToActivitesPagePagee();
            loadCategories();
        }
    }

   
    public void handleUpdateEvent(Evenement selectedEvent) {
        this.evenementSelectionne = selectedEvent;
        GoToUpdateEventPagee();

        nomEventUpdate.setText(selectedEvent.getNom());
        descEventUpdate.setText(selectedEvent.getDescription());
        dateDebutUpdate.setValue(selectedEvent.getDateDebut());
        dateFinUpdate.setValue(selectedEvent.getDateFin());
        lieuEventUpdate.setText(selectedEvent.getLieu());
        prixEventUpdate.setText(String.valueOf(selectedEvent.getPrix()));
        placesEventUpdate.setText(String.valueOf(selectedEvent.getPlacesDisponibles()));
        imageEventUpdate.setText(selectedEvent.getImage());
        loadCategoriesIntoComboBoxUpdate();


    }

    private void loadCategoriesIntoComboBoxUpdate() {
        List<Categorie> categories = categorieService.getAllCategories(); // Appel au service qui récupère depuis la BDD
        ObservableList<Categorie> observableCategories = FXCollections.observableArrayList(categories);
        categorieComboUpdate.setItems(observableCategories);
    }


    public void handleUpdateEvent(ActionEvent actionEvent) {
    }
    @FXML
    private void handleSubmitUpdateEvent() {
        try {
            // Validation des champs
            if (evenementSelectionne == null) {
                afficherAlerte("Erreur", "Aucun événement sélectionné pour la mise à jour.", Alert.AlertType.WARNING);
                return;
            }

            // Mise à jour des champs de l'événement sélectionné
            evenementSelectionne.setNom(nomEventUpdate.getText());
            evenementSelectionne.setDescription(descEventUpdate.getText());
            evenementSelectionne.setDateDebut(dateDebutUpdate.getValue());
            evenementSelectionne.setDateFin(dateFinUpdate.getValue());
            evenementSelectionne.setLieu(lieuEventUpdate.getText());
            evenementSelectionne.setPrix(Double.parseDouble(prixEventUpdate.getText()));
            evenementSelectionne.setPlacesDisponibles(Integer.parseInt(placesEventUpdate.getText()));
            evenementSelectionne.setImage(imageEventUpdate.getText());
            evenementSelectionne.setCategorieId(categorieComboUpdate.getValue().getId());

            // Enregistrement de la mise à jour
            if (evenementService.modifierEvenement(evenementSelectionne)) {
                afficherAlerte("Succès", "Événement mis à jour avec succès!", Alert.AlertType.INFORMATION);
            } else {
                afficherAlerte("Erreur", "Échec de la mise à jour de l'événement.", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur de format", "Vérifiez les champs numériques", Alert.AlertType.ERROR);
        } catch (Exception e) {
            afficherAlerte("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        }

        GoToEventPagee();
    }

    private void loadParticipants() {
        List<Participant> participants = participantService.getAllParticipants();
        ObservableList<Participant> observableParticipants = FXCollections.observableArrayList(participants);
        participantsTableView.setItems(observableParticipants);
    }
}

