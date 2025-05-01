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
import javafx.util.Callback;
import org.agritrace.entities.Service;
import org.agritrace.services.ServiceServices;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.time.LocalDateTime;

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
    private SplitPane splitPane;

    @FXML
    private VBox sideNavBar;

    @FXML
    private AnchorPane tablePane;

    @FXML
    private SplitPane verticalSplitPane;

    @FXML
    private Button homeButton;

    @FXML
    private Button servicesButton;

    @FXML
    private Button LoccationsButton;

    @FXML
    private Button logoutButton;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterStatus;

    @FXML
    private Button clearButton;

    private ObservableList<Service> serviceList = FXCollections.observableArrayList();
    private ServiceServices serviceServices = new ServiceServices();
    private FilteredList<Service> filteredServices;

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
        initializeColumns();
        initializeSplitPanes();
        setupColumnWidths();
        initializeActionColumn();
        setupSearch();
        loadTableData();
        initializeNavigation();
        filterStatus.getItems().addAll("All", "valable", "non valable");
        filterStatus.setValue("All");
        clearButton.setOnAction(e -> {
            searchField.clear();
            filterStatus.setValue("All");
        });
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

    private void initializeSplitPanes() {
        // Set default divider positions
        splitPane.setDividerPositions(0.20);
        verticalSplitPane.setDividerPositions(0.8);

        // Add listeners to enforce the divider positions
        splitPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            splitPane.setDividerPositions(0.20);
        });

        verticalSplitPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            verticalSplitPane.setDividerPositions(0.8);
        });
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

    private void initializeActionColumn() {
        actionColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttons = new HBox(5, editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    Service service = getTableView().getItems().get(getIndex());
                    handleEdit(service);
                });

                deleteButton.setOnAction(event -> {
                    Service service = getTableView().getItems().get(getIndex());
                    handleDelete(service);
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
        // Set the services button as active since we're on the services page
        servicesButton.getStyleClass().add("active-nav-button");

        homeButton.setOnAction(event -> navigateToHome());
        servicesButton.setOnAction(event -> {
            // Already on services page
            event.consume();
        });
        LoccationsButton.setOnAction(event -> navigateToLocations());
        logoutButton.setOnAction(event -> handleLogout());
    }

    private void navigateToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) homeButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Could not navigate to Home page", e.getMessage());
        }
    }

    private void navigateToLocations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LocationIndex.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            Stage stage = (Stage) LoccationsButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Could not navigate to Locations page", e.getMessage());
        }
    }

    private void handleLogout() {
        // Show confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("You will be redirected to the login page.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Navigate to login page
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) logoutButton.getScene().getWindow();
                    stage.setScene(new Scene(root));
                } catch (IOException e) {
                    showErrorAlert("Logout Error", "Could not navigate to Login page", e.getMessage());
                }
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
        // Clear existing items
        serviceList.clear();
        // Reload data from database
        serviceList.addAll(serviceServices.getAllData());
        // Refresh the table view
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
