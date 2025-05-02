package org.agritrace.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.agritrace.entities.Location;
import org.agritrace.services.LanguageManager;
import org.agritrace.services.LocationServices;

import java.io.IOException;
import java.time.LocalDate;

public class LocationIndex {

    @FXML
    private TableView<Location> locationsTable;

    @FXML
    private TableColumn<Location, Integer> idColumn;

    @FXML
    private TableColumn<Location, String> nameColumn;

    @FXML
    private TableColumn<Location, String> addressColumn;

    @FXML
    private TableColumn<Location, Integer> capacityColumn;

    @FXML
    private TableColumn<Location, String> availabilityColumn;

    @FXML
    private TableColumn<Location, String> detailsColumn;

    @FXML
    private TableColumn<Location, Void> actionsColumn;

    @FXML
    private Button addButton;

    @FXML
    private Button homeButton;

    @FXML
    private Button servicesButton;

    @FXML
    private Button locationsButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button calendarButton;

    @FXML
    private VBox sideNavBar;

    @FXML
    private TextField searchField;

    @FXML
    private Button clearButton;

    @FXML
    private ComboBox<String> languageComboBox;

    private ObservableList<Location> locationList = FXCollections.observableArrayList();
    private FilteredList<Location> filteredLocations;
    private LocationServices locationServices = new LocationServices();
    private LanguageManager languageManager;

    // Column width definitions
    private enum ColumnWidth {
        ID(0.08),
        NAME(0.15),
        ADDRESS(0.20),
        CAPACITY(0.12),
        AVAILABILITY(0.15),
        DETAILS(0.15),
        ACTIONS(0.15);

        private final double multiplier;
        
        ColumnWidth(double multiplier) {
            this.multiplier = multiplier;
        }
        
        public double getMultiplier() {
            return multiplier;
        }
    }

    private void bindColumnWidth(TableColumn<?, ?> column, ColumnWidth width) {
        column.prefWidthProperty().bind(locationsTable.widthProperty().multiply(width.getMultiplier()));
    }

    @FXML
    public void initialize() {
        languageManager = LanguageManager.getInstance();
        
        // Setup language selector
        languageComboBox.setItems(FXCollections.observableArrayList("English", "Français"));
        languageComboBox.setValue(languageManager.getCurrentLocale().getLanguage().equals("fr") ? "Français" : "English");
        
        languageComboBox.setOnAction(e -> {
            String selected = languageComboBox.getValue();
            String lang = selected.equals("Français") ? "fr" : "en";
            languageManager.setLanguage(lang);
            
            // Reload the current scene
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/LocationIndex.fxml"));
                loader.setResources(languageManager.getMessages());
                Parent root = loader.load();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
                Stage stage = (Stage) homeButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                showErrorAlert("Error", "Could not change language", ex.getMessage());
            }
        });

        initializeColumns();
        setupColumnWidths();
        setupSearch();
        loadTableData();
        initializeNavigation();
        setupAddButton();
        clearButton.setOnAction(e -> searchField.clear());
    }

    private void initializeColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("serviceId")); // Temporarily using serviceId
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("idUser")); // Temporarily using idUser
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("prixTotal")); // Temporarily using prixTotal
        availabilityColumn.setCellValueFactory(col -> {
            Location location = col.getValue();
            LocalDate now = LocalDate.now();
            String availability = (location.getDateDebut().isAfter(now) || now.isBefore(location.getDateFin())) 
                ? "Available" : "Booked";
            return new SimpleStringProperty(availability);
        });
        detailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));
        setupActionsColumn();
    }

    private void setupColumnWidths() {
        bindColumnWidth(idColumn, ColumnWidth.ID);
        bindColumnWidth(nameColumn, ColumnWidth.NAME);
        bindColumnWidth(addressColumn, ColumnWidth.ADDRESS);
        bindColumnWidth(capacityColumn, ColumnWidth.CAPACITY);
        bindColumnWidth(availabilityColumn, ColumnWidth.AVAILABILITY);
        bindColumnWidth(detailsColumn, ColumnWidth.DETAILS);
        bindColumnWidth(actionsColumn, ColumnWidth.ACTIONS);
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button(languageManager.getMessage("button.edit"));
            private final Button deleteButton = new Button(languageManager.getMessage("button.delete"));

            {
                editButton.setOnAction(event -> {
                    Location location = getTableView().getItems().get(getIndex());
                    handleEdit(location);
                });

                deleteButton.setOnAction(event -> {
                    Location location = getTableView().getItems().get(getIndex());
                    handleDelete(location);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });
    }

    private void setupSearch() {
        filteredLocations = new FilteredList<>(locationList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredLocations.setPredicate(location -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                return String.valueOf(location.getServiceId()).contains(lowerCaseFilter) ||
                       String.valueOf(location.getIdUser()).contains(lowerCaseFilter) ||
                       location.getDetails().toLowerCase().contains(lowerCaseFilter);
            });
        });
        locationsTable.setItems(filteredLocations);
    }

    private void loadTableData() {
        locationList.clear();
        locationList.addAll(locationServices.getAllData());
    }

    private void setupAddButton() {
        addButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddLocation.fxml"));
                loader.setResources(languageManager.getMessages());
                Parent root = loader.load();
                AddLocation addLocationController = loader.getController();
                addLocationController.setLocationIndexController(this);

                Stage stage = new Stage();
                stage.setTitle(languageManager.getMessage("location.add"));
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                showErrorAlert("Add Error", "Could not open add window", e.getMessage());
            }
        });
    }

    private void handleEdit(Location location) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddLocation.fxml"));
            loader.setResources(languageManager.getMessages());
            Parent root = loader.load();
            AddLocation addLocationController = loader.getController();
            addLocationController.setLocationIndexController(this);
            addLocationController.setEditMode(location);

            Stage stage = new Stage();
            stage.setTitle(languageManager.getMessage("location.edit"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Edit Error", "Could not open edit window", e.getMessage());
        }
    }

    private void handleDelete(Location location) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(languageManager.getMessage("dialog.confirm.title"));
        alert.setHeaderText(languageManager.getMessage("location.delete.confirm"));
        alert.setContentText(String.format(languageManager.getMessage("location.delete.message"), location.getId()));

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                locationServices.deleteEntity(location);
                locationList.remove(location);
                locationsTable.refresh();
            }
        });
    }

    private void initializeNavigation() {
        homeButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));
                loader.setResources(languageManager.getMessages());
                Parent root = loader.load();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
                Stage stage = (Stage) homeButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                showErrorAlert("Navigation Error", "Could not navigate to Home", e.getMessage());
            }
        });

        servicesButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ServiceIndex.fxml"));
                loader.setResources(languageManager.getMessages());
                Parent root = loader.load();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
                Stage stage = (Stage) servicesButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                showErrorAlert("Navigation Error", "Could not navigate to Services", e.getMessage());
            }
        });

        calendarButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/LocationCalendar.fxml"));
                loader.setResources(languageManager.getMessages());
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle(languageManager.getMessage("calendar.title"));
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                showErrorAlert("Calendar Error", "Could not open calendar view", e.getMessage());
            }
        });

        logoutButton.setOnAction(event -> handleLogout());
    }

    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            loader.setResources(languageManager.getMessages());
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Logout Error", "Could not return to login", e.getMessage());
        }
    }

    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void refreshList() {
        locationList.clear();
        locationList.addAll(locationServices.getAllData());
        locationsTable.refresh();
    }

    public void updateLocationInList(Location updatedLocation) {
        for (int i = 0; i < locationList.size(); i++) {
            if (locationList.get(i).getId() == updatedLocation.getId()) {
                locationList.set(i, updatedLocation);
                break;
            }
        }
        locationsTable.refresh();
    }
}
