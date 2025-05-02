package org.agritrace.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.agritrace.entities.Location;
import org.agritrace.services.LocationServices;

import java.io.IOException;
import java.time.LocalDate;

public class LocationIndex {

    @FXML
    private TableView<Location> locationTable;

    @FXML
    private TableColumn<Location, Integer> idColumn;

    @FXML
    private TableColumn<Location, Integer> serviceIdColumn;

    @FXML
    private TableColumn<Location, Integer> userIdColumn;

    @FXML
    private TableColumn<Location, String> detailsColumn;

    @FXML
    private TableColumn<Location, Double> totalPriceColumn;

    @FXML
    private TableColumn<Location, LocalDate> startDateColumn;

    @FXML
    private TableColumn<Location, LocalDate> endDateColumn;

    @FXML
    private TableColumn<Location, Void> actionColumn;

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
    private SplitPane splitPane;

    @FXML
    private AnchorPane tablePane;

    @FXML
    private SplitPane verticalSplitPane;

    @FXML
    private TextField searchField;
    @FXML
    private Button clearButton;

    private ObservableList<Location> locationList = FXCollections.observableArrayList();
    private FilteredList<Location> filteredLocations;
    private LocationServices locationServices = new LocationServices();

    // Column width definitions
    private enum ColumnWidth {
        ID(0.08),
        SERVICE_ID(0.12),
        USER_ID(0.12),
        DETAILS(0.16),
        TOTAL_PRICE(0.12),
        START_DATE(0.12),
        END_DATE(0.12),
        ACTION(0.16);

        private final double multiplier;
        
        ColumnWidth(double multiplier) {
            this.multiplier = multiplier;
        }
        
        public double getMultiplier() {
            return multiplier;
        }
    }

    private void bindColumnWidth(TableColumn<?, ?> column, ColumnWidth width) {
        column.prefWidthProperty().bind(locationTable.widthProperty().multiply(width.getMultiplier()));
    }

    @FXML
    public void initialize() {
        initializeColumns();
        initializeSplitPanes();
        setupColumnWidths();
        initializeActionColumn();
        setupSearch();
        loadTableData();
        initializeNavigation();
        clearButton.setOnAction(e -> searchField.clear());
        
        calendarButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/LocationCalendar.fxml"));
                Parent root = loader.load();
                
                Stage stage = new Stage();
                stage.setTitle("Bookings Calendar");
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                showError("Calendar Error", "Could not open calendar view", e.getMessage());
            }
        });
    }

    private void initializeColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        serviceIdColumn.setCellValueFactory(new PropertyValueFactory<>("serviceId"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        detailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("prixTotal"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
    }

    private void initializeSplitPanes() {
        splitPane.setDividerPositions(0.20);
        verticalSplitPane.setDividerPositions(0.8);

        splitPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            splitPane.setDividerPositions(0.20);
        });

        verticalSplitPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            verticalSplitPane.setDividerPositions(0.8);
        });
    }

    private void setupColumnWidths() {
        bindColumnWidth(idColumn, ColumnWidth.ID);
        bindColumnWidth(serviceIdColumn, ColumnWidth.SERVICE_ID);
        bindColumnWidth(userIdColumn, ColumnWidth.USER_ID);
        bindColumnWidth(detailsColumn, ColumnWidth.DETAILS);
        bindColumnWidth(totalPriceColumn, ColumnWidth.TOTAL_PRICE);
        bindColumnWidth(startDateColumn, ColumnWidth.START_DATE);
        bindColumnWidth(endDateColumn, ColumnWidth.END_DATE);
        bindColumnWidth(actionColumn, ColumnWidth.ACTION);
    }

    private void initializeActionColumn() {
        actionColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttons = new HBox(5, editButton, deleteButton);

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
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
    }

    private void setupSearch() {
        // Create filtered list
        filteredLocations = new FilteredList<>(locationList, p -> true);
        
        // Add listener to search field
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredLocations.setPredicate(location -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                
                String searchText = newValue.toLowerCase().trim();
                
                // Search in multiple fields
                return String.valueOf(location.getServiceId()).contains(searchText) ||
                       String.valueOf(location.getIdUser()).contains(searchText) ||
                       location.getDetails().toLowerCase().contains(searchText) ||
                       String.valueOf(location.getPrixTotal()).contains(searchText);
            });
        });
        
        // Bind the filtered list to the table
        locationTable.setItems(filteredLocations);
    }

    private void loadTableData() {
        locationList.clear();
        locationList.addAll(locationServices.getAllData());
    }

    private void initializeNavigation() {
        // Set the locations button as active since we're on the locations page
        locationsButton.getStyleClass().add("active-nav-button");
        
        homeButton.setOnAction(event -> navigateToHome());
        servicesButton.setOnAction(event -> navigateToServices());
        locationsButton.setOnAction(event -> {
            // Already on locations page
            event.consume();
        });
        logoutButton.setOnAction(event -> handleLogout());
    }

    private void navigateToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            Stage stage = (Stage) homeButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Could not navigate to Home page", e.getMessage());
        }
    }

    private void navigateToServices() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ServiceIndex.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            Stage stage = (Stage) servicesButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Could not navigate to Services page", e.getMessage());
        }
    }

    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("You will be redirected to the login page.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
                    Parent root = loader.load();
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
                    Stage stage = (Stage) logoutButton.getScene().getWindow();
                    stage.setScene(scene);
                } catch (IOException e) {
                    showErrorAlert("Logout Error", "Could not navigate to Login page", e.getMessage());
                }
            }
        });
    }

    private void handleEdit(Location location) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddLocation.fxml"));
            Parent root = loader.load();
            AddLocation addLocationController = loader.getController();
            addLocationController.setLocationIndexController(this);
            addLocationController.setEditMode(location);

            Stage stage = new Stage();
            stage.setTitle("Edit Location");
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
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Are you sure you want to delete this location?");
        alert.setContentText("Location ID: " + location.getId());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                locationServices.deleteEntity(location);
                locationList.remove(location);
            }
        });
    }

    @FXML
    public void AddLocation(javafx.event.ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddLocation.fxml"));
            Parent root = loader.load();
            AddLocation addLocationController = loader.getController();
            addLocationController.setLocationIndexController(this);

            Stage stage = new Stage();
            stage.setTitle("Add Location");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Add Error", "Could not open add window", e.getMessage());
        }
    }

    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void refreshList() {
        locationList.clear();
        locationList.addAll(locationServices.getAllData());
        locationTable.refresh();
    }

    public void updateLocationInList(Location updatedLocation) {
        for (int i = 0; i < locationList.size(); i++) {
            if (locationList.get(i).getId() == updatedLocation.getId()) {
                locationList.set(i, updatedLocation);
                break;
            }
        }
        locationTable.refresh();
    }
}
