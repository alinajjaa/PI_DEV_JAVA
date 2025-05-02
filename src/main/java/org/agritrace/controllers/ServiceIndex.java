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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.agritrace.entities.Service;
import org.agritrace.services.LanguageManager;
import org.agritrace.services.ServiceServices;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class ServiceIndex {
    @FXML
    private TableView<Service> serviceTable;

    @FXML
    private TableColumn<Service, Integer> idColumn;

    @FXML
    private TableColumn<Service, String> nameColumn, typeColumn, descColumn,
            statusColumn, addressColumn, imageColumn;

    @FXML
    private TableColumn<Service, Integer> priceColumn;

    @FXML
    private TableColumn<Service, LocalDateTime> dateColumn;

    @FXML
    private TableColumn<Service, Void> actionColumn; // for buttons

    @FXML
    private VBox sideNavBar;

    @FXML
    private Button homeButton;

    @FXML
    private Button servicesButton;

    @FXML
    private Button locationsButton;

    @FXML
    private Button logoutButton;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterStatus;

    @FXML
    private Button clearButton;

    @FXML
    private Button calendarButton;

    @FXML
    private ComboBox<String> languageComboBox;

    @FXML
    private Button addButton;

    private ObservableList<Service> serviceList = FXCollections.observableArrayList();
    private ServiceServices serviceServices = new ServiceServices();
    private FilteredList<Service> filteredServices;
    private LanguageManager languageManager;

    // Column width definitions
    private enum ColumnWidth {
        ID(0.06),
        NAME(0.085),
        TYPE(0.1),
        DESCRIPTION(0.14),
        PRICE(0.1),
        STATUS(0.1),
        ADDRESS(0.1),
        DATE(0.1),
        ACTION(0.18);

        private final double multiplier;

        ColumnWidth(double multiplier) {
            this.multiplier = multiplier;
        }

        public double getMultiplier() {
            return multiplier;
        }
    }

    private void bindColumnWidth(TableColumn<?, ?> column, ColumnWidth width) {
        column.prefWidthProperty().bind(serviceTable.widthProperty().multiply(width.getMultiplier()));
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ServiceIndex.fxml"));
                loader.setResources(languageManager.getMessages());
                Parent root = loader.load();
                Scene scene = homeButton.getScene();
                scene.setRoot(root);
            } catch (IOException ex) {
                showErrorAlert("Error", "Could not change language", ex.getMessage());
            }
        });

        initializeColumns();
        setupColumnWidths();
        setupActionColumn();
        setupSearch();
        loadTableData();
        initializeNavigation();
        filterStatus.getItems().addAll("All", "valable", "non valable");
        filterStatus.setValue("All");
        clearButton.setOnAction(e -> {
            searchField.clear();
            filterStatus.setValue("All");
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
        
        setupAddButton();
    }

    private void initializeColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        descColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
    }

    private void setupColumnWidths() {
        // Bind column widths using the enum values
        bindColumnWidth(idColumn, ColumnWidth.ID);
        bindColumnWidth(nameColumn, ColumnWidth.NAME);
        bindColumnWidth(typeColumn, ColumnWidth.TYPE);
        bindColumnWidth(descColumn, ColumnWidth.DESCRIPTION);
        bindColumnWidth(priceColumn, ColumnWidth.PRICE);
        bindColumnWidth(statusColumn, ColumnWidth.STATUS);
        bindColumnWidth(addressColumn, ColumnWidth.ADDRESS);
        bindColumnWidth(dateColumn, ColumnWidth.DATE);
        bindColumnWidth(actionColumn, ColumnWidth.ACTION);
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final HBox buttons = new HBox(5);
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final Button calendarButton = new Button("Calendar");

            {
                buttons.getChildren().addAll(editButton, deleteButton, calendarButton);
                editButton.getStyleClass().add("edit-button");
                deleteButton.getStyleClass().add("delete-button");
                calendarButton.getStyleClass().add("calendar-button");

                editButton.setOnAction(event -> {
                    Service service = getTableView().getItems().get(getIndex());
                    handleEdit(service);
                });

                deleteButton.setOnAction(event -> {
                    Service service = getTableView().getItems().get(getIndex());
                    handleDelete(service);
                });

                calendarButton.setOnAction(event -> {
                    Service service = getTableView().getItems().get(getIndex());
                    showCalendar(service);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
    }

    private void setupSearch() {
        // Create filtered list
        filteredServices = new FilteredList<>(serviceList, p -> true);

        // Add listeners to search field and status filter
        searchField.textProperty().addListener((observable, oldValue, newValue) -> updateFilter());
        filterStatus.valueProperty().addListener((observable, oldValue, newValue) -> updateFilter());

        // Bind the filtered list to the table
        serviceTable.setItems(filteredServices);
    }

    private void updateFilter() {
        filteredServices.setPredicate(service -> {
            String searchText = searchField.getText().toLowerCase();
            String statusFilter = filterStatus.getValue();

            // If text field is empty and status is "All", show everything
            if ((searchText == null || searchText.isEmpty()) &&
                    (statusFilter == null || statusFilter.equals("All"))) {
                return true;
            }

            boolean matchesSearch = searchText == null || searchText.isEmpty() ||
                    service.getNom().toLowerCase().contains(searchText) ||
                    service.getType().toLowerCase().contains(searchText) ||
                    service.getDescription().toLowerCase().contains(searchText) ||
                    service.getAdresse().toLowerCase().contains(searchText);

            boolean matchesStatus = statusFilter == null || statusFilter.equals("All") ||
                    service.getStatus().equals(statusFilter);

            return matchesSearch && matchesStatus;
        });
    }

    private void loadTableData() {
        serviceList.addAll(serviceServices.getAllData());
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

        locationsButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/LocationIndex.fxml"));
                loader.setResources(languageManager.getMessages());
                Parent root = loader.load();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
                Stage stage = (Stage) locationsButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                showErrorAlert("Navigation Error", "Could not navigate to Locations", e.getMessage());
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

    private void handleEdit(Service service) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddService.fxml"));
            Parent root = loader.load();

            AddService addServiceController = loader.getController();

            // Send reference of this controller to AddService
            addServiceController.setServiceIndexController(this);
            addServiceController.setEditMode(service);

            Stage stage = new Stage();
            stage.setTitle("Edit Service");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDelete(Service service) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Are you sure you want to delete this service?");
        alert.setContentText("Service: " + service.getNom());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                serviceServices.deleteEntity(service);
                serviceList.remove(service);
            }
        });
    }

    private void showCalendar(Service service) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LocationCalendar.fxml"));
            Parent root = loader.load();
            
            LocationCalendar calendarController = loader.getController();
            calendarController.showServiceBookings(service);
            
            Stage stage = new Stage();
            stage.setTitle("Bookings Calendar - " + service.getNom());
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Calendar Error", "Could not open calendar view", e.getMessage());
        }
    }

    private void setupAddButton() {
        addButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddService.fxml"));
                loader.setResources(languageManager.getMessages());
                Parent root = loader.load();
                AddService addServiceController = loader.getController();
                addServiceController.setServiceIndexController(this);

                Stage stage = new Stage();
                stage.setTitle(languageManager.getMessage("service.add"));
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                showErrorAlert("Add Error", "Could not open add window", e.getMessage());
            }
        });
    }

    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void updateServiceInList(Service updatedService) {
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).getId() == updatedService.getId()) {
                serviceList.set(i, updatedService);
                break;
            }
        }
        serviceTable.refresh();
    }

    public void refreshList() {
        serviceList.clear();
        serviceList.addAll(serviceServices.getAllData());
        serviceTable.refresh();
    }

    public void handleAddButtonAction(Service s) {
        // Add the new item to the ObservableList
        serviceList.add(s);
        // Refresh the list to ensure data consistency
        refreshList();
    }

    @FXML
    public void AddService(javafx.event.ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddService.fxml"));
            Parent root = loader.load();

            AddService addServiceController = loader.getController();

            addServiceController.setServiceIndexController(this);

            Stage stage = new Stage();
            stage.setTitle("Add Service");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
