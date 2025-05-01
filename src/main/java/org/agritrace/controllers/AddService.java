package org.agritrace.controllers;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import netscape.javascript.JSObject;
import org.agritrace.entities.Service;
import org.agritrace.services.ServiceServices;

/**
 * Controller for the AddService form.
 */
public class AddService {

    @FXML
    private Button btnajout;

    @FXML
    private TextField tf_nom;

    @FXML
    private TextField tf_type;

    @FXML
    private TextArea tf_desc;
    
    @FXML
    private TextField tf_prix;

    @FXML
    private ChoiceBox<String> tf_status;

    @FXML
    private WebView mapWebView;

    private boolean editMode = false;
    private Service serviceToEdit = null;
    private ServiceIndex serviceIndexController;
    private String addressToInject = null;

    /**
     * Initializes the form with default values and validation.
     */
    @FXML
    public void initialize() {
        // Initialize the status choice box with options
        tf_status.getItems().addAll("valable", "non valable");
        tf_status.setValue("valable"); // Set default value

        // Add listeners for real-time validation
        tf_prix.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tf_prix.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Add tooltips
        tf_nom.setTooltip(new Tooltip("Enter service name (3-50 characters)"));
        tf_type.setTooltip(new Tooltip("Enter service type (3-30 characters)"));
        tf_desc.setTooltip(new Tooltip("Enter service description (10-500 characters)"));
        tf_prix.setTooltip(new Tooltip("Enter price (numeric value)"));
        tf_status.setTooltip(new Tooltip("Select service status"));

        WebEngine webEngine = mapWebView.getEngine();
        webEngine.load(getClass().getResource("/google_maps_picker.html").toExternalForm());
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED && addressToInject != null) {
                // addressToInject format: country,city;lat;lng
                String adresse = addressToInject;
                String[] parts = adresse.split(";");
                String location = parts[0];
                String lat = parts.length > 1 ? parts[1] : "";
                String lng = parts.length > 2 ? parts[2] : "";
                webEngine.executeScript(
                    "setAddressFromJava('" + adresse.replace("'", "\\'") + "', '" + location.replace("'", "\\'") + "', '" + lat + "', '" + lng + "');"
                );
            }
        });
    }

    /**
     * Validates the input fields.
     * 
     * @return true if all fields are valid, false otherwise
     */
    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();

        // Validate Name
        String name = tf_nom.getText().trim();
        if (name.isEmpty()) {
            errors.append("Name is required\n");
        } else if (name.length() < 3 || name.length() > 50) {
            errors.append("Name must be between 3 and 50 characters\n");
        }

        // Validate Type
        String type = tf_type.getText().trim();
        if (type.isEmpty()) {
            errors.append("Type is required\n");
        } else if (type.length() < 3 || type.length() > 30) {
            errors.append("Type must be between 3 and 30 characters\n");
        }

        // Validate Description
        String description = tf_desc.getText().trim();
        if (description.isEmpty()) {
            errors.append("Description is required\n");
        } else if (description.length() < 10 || description.length() > 500) {
            errors.append("Description must be between 10 and 500 characters\n");
        }

        // Validate Price
        String priceStr = tf_prix.getText().trim();
        if (priceStr.isEmpty()) {
            errors.append("Price is required\n");
        } else {
            try {
                int price = Integer.parseInt(priceStr);
                if (price <= 0) {
                    errors.append("Price must be greater than 0\n");
                }
            } catch (NumberFormatException e) {
                errors.append("Price must be a valid number\n");
            }
        }

        // Validate Status
        if (tf_status.getValue() == null) {
            errors.append("Status is required\n");
        }

        // Validate Address
        String address = getAddressFromMap();
        if (address.isEmpty()) {
            errors.append("Address is required\n");
        } else if (address.length() < 5 || address.length() > 100) {
            errors.append("Address must be between 5 and 100 characters\n");
        }

        if (errors.length() > 0) {
            showErrorAlert("Validation Error", errors.toString());
            return false;
        }

        return true;
    }

    /**
     * Shows an error alert with the given title and content.
     * 
     * @param title the title of the alert
     * @param content the content of the alert
     */
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Saves the service.
     * 
     * @param event the action event
     */
    @FXML
    void SaveService(ActionEvent event) {
        if (!validateInput()) {
            return;
        }

        try {
            String nom = tf_nom.getText().trim();
            String type = tf_type.getText().trim();
            String description = tf_desc.getText().trim();
            String adresse = getAddressFromMap();
            String status = tf_status.getValue();
            int prix = Integer.parseInt(tf_prix.getText().trim());
            String image = "default.png";
            LocalDateTime updatedAt = LocalDateTime.now();

            Service service = new Service(nom, type, description, prix, status, adresse, image, updatedAt);

            ServiceServices ss = new ServiceServices();
            if (editMode && serviceToEdit != null) {
                service.setId(serviceToEdit.getId());
                ss.updateEntity(service, serviceToEdit.getId());
                serviceIndexController.updateServiceInList(service);
            } else {
                ss.addEntity(service);
                serviceIndexController.refreshList();
            }

            // Close the window on success
            ((Stage) btnajout.getScene().getWindow()).close();

        } catch (Exception e) {
            showErrorAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    /**
     * Sets the service index controller.
     * 
     * @param controller the service index controller
     */
    public void setServiceIndexController(ServiceIndex controller) {
        this.serviceIndexController = controller;
    }

    /**
     * Sets the edit mode and populates the fields with the given service.
     * 
     * @param service the service to edit
     */
    public void setEditMode(Service service) {
        this.serviceToEdit = service;
        this.editMode = true;
        populateFields(service);
        btnajout.setText("Update"); // Change button text for edit mode
    }

    /**
     * Populates the fields with the given service.
     */
    private void populateFields(Service serviceToEdit) {
        if (serviceToEdit != null) {
            tf_nom.setText(serviceToEdit.getNom());
            tf_type.setText(serviceToEdit.getType());
            tf_desc.setText(serviceToEdit.getDescription());
            tf_prix.setText(String.valueOf(serviceToEdit.getPrix()));
            tf_status.setValue(serviceToEdit.getStatus());
            // Store address to inject after WebView loads
            addressToInject = serviceToEdit.getAdresse();
        }
    }

    private String getAddressFromMap() {
        WebEngine webEngine = mapWebView.getEngine();
        Object value = webEngine.executeScript(
            "document.getElementById('service_adresse') ? document.getElementById('service_adresse').value : ''"
        );
        return value != null ? value.toString() : "";
    }
}
