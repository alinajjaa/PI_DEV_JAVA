package org.agritrace.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.agritrace.entities.Location;
import org.agritrace.entities.Service;
import org.agritrace.services.LocationServices;
import org.agritrace.services.ServiceServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class AddLocation {

    @FXML
    private ComboBox<Service> serviceComboBox;

    @FXML
    private TextField userIdField;

    @FXML
    private TextArea detailsField;

    @FXML
    private TextField prixTotalField;

    @FXML
    private DatePicker dateDebutPicker;

    @FXML
    private DatePicker dateFinPicker;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private LocationServices locationServices = new LocationServices();
    private ServiceServices serviceServices = new ServiceServices();
    private LocationIndex locationIndexController;
    private Location locationToEdit;
    private boolean isEditMode = false;
    private Stage stage;

    @FXML
    public void initialize() {
        loadServices();

        // Add listeners for real-time validation
        userIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                userIdField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        prixTotalField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                prixTotalField.setText(oldValue);
            }
        });

        // Set default dates
        dateDebutPicker.setValue(LocalDate.now());
        dateFinPicker.setValue(LocalDate.now().plusDays(1));

        // Add tooltips
        serviceComboBox.setTooltip(new Tooltip("Select a service for this location"));
        userIdField.setTooltip(new Tooltip("Enter numeric user ID"));
        detailsField.setTooltip(new Tooltip("Enter location details (10-500 characters)"));
        prixTotalField.setTooltip(new Tooltip("Enter total price (numeric value)"));
        dateDebutPicker.setTooltip(new Tooltip("Select start date"));
        dateFinPicker.setTooltip(new Tooltip("Select end date"));
    }

    private void loadServices() {
        ObservableList<Service> services = FXCollections.observableArrayList(serviceServices.getAllData());
        serviceComboBox.setItems(services);
        
        serviceComboBox.setConverter(new StringConverter<Service>() {
            @Override
            public String toString(Service service) {
                return service != null ? service.getNom() + " - #" + service.getId() : "";
            }

            @Override
            public Service fromString(String string) {
                return null;
            }
        });

        // Add a listener to update price when service is selected
        serviceComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && prixTotalField.getText().isEmpty()) {
                prixTotalField.setText(String.valueOf(newVal.getPrix()));
            }
        });
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        Service selectedService = serviceComboBox.getValue();
        LocalDate startDate = dateDebutPicker.getValue();
        LocalDate endDate = dateFinPicker.getValue();

        // Check for availability
        if (!isTimeSlotAvailable(selectedService.getId(), startDate, endDate)) {
            return;
        }

        try {
            Location location = new Location();
            location.setServiceId(selectedService.getId());
            location.setIdUser(Integer.parseInt(userIdField.getText().trim()));
            location.setDetails(detailsField.getText().trim());
            location.setPrixTotal(Double.parseDouble(prixTotalField.getText().trim()));
            location.setDateDebut(startDate);
            location.setDateFin(endDate);

            if (isEditMode) {
                location.setId(locationToEdit.getId());
                locationServices.updateEntity(location, locationToEdit.getId());
                locationIndexController.updateLocationInList(location);
            } else {
                locationServices.addEntity(location);
                locationIndexController.refreshList();
            }

            // Close the window on success
            ((Stage) saveButton.getScene().getWindow()).close();

        } catch (Exception e) {
            showErrorAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();

        // Validate Service Selection
        if (serviceComboBox.getValue() == null) {
            errors.append("Please select a service\n");
        }

        // Validate User ID
        String userIdStr = userIdField.getText().trim();
        if (userIdStr.isEmpty()) {
            errors.append("User ID is required\n");
        } else {
            try {
                int userId = Integer.parseInt(userIdStr);
                if (userId <= 0) {
                    errors.append("User ID must be greater than 0\n");
                }
            } catch (NumberFormatException e) {
                errors.append("User ID must be a valid number\n");
            }
        }

        // Validate Details
        String details = detailsField.getText().trim();
        if (details.isEmpty()) {
            errors.append("Details are required\n");
        } else if (details.length() < 10 || details.length() > 500) {
            errors.append("Details must be between 10 and 500 characters\n");
        }

        // Validate Total Price
        String priceStr = prixTotalField.getText().trim();
        if (priceStr.isEmpty()) {
            errors.append("Total Price is required\n");
        } else {
            try {
                double price = Double.parseDouble(priceStr);
                if (price <= 0) {
                    errors.append("Total Price must be greater than 0\n");
                }
            } catch (NumberFormatException e) {
                errors.append("Total Price must be a valid number\n");
            }
        }

        // Validate Dates
        LocalDate startDate = dateDebutPicker.getValue();
        LocalDate endDate = dateFinPicker.getValue();

        if (startDate == null) {
            errors.append("Start Date is required\n");
        } else if (startDate.isBefore(LocalDate.now())) {
            errors.append("Start Date cannot be in the past\n");
        }

        if (endDate == null) {
            errors.append("End Date is required\n");
        } else if (startDate != null && endDate.isBefore(startDate)) {
            errors.append("End Date must be after Start Date\n");
        }

        if (errors.length() > 0) {
            showErrorAlert("Validation Error", errors.toString());
            return false;
        }

        return true;
    }

    private boolean isTimeSlotAvailable(int serviceId, LocalDate startDate, LocalDate endDate) {
        // Get all existing bookings for this service
        List<Location> existingBookings = locationServices.getAllData().stream()
            .filter(loc -> loc.getServiceId() == serviceId)
            .collect(Collectors.toList());

        // Check for any overlapping bookings
        for (Location booking : existingBookings) {
            // Skip the current booking if we're in edit mode
            if (isEditMode && booking.getId() == locationToEdit.getId()) {
                continue;
            }
            
            // Check if the new booking overlaps with any existing booking
            boolean overlaps = !(endDate.isBefore(booking.getDateDebut()) || 
                               startDate.isAfter(booking.getDateFin()));
            
            if (overlaps) {
                // Find the actual overlapping period
                LocalDate overlapStart = startDate.isAfter(booking.getDateDebut()) ? startDate : booking.getDateDebut();
                LocalDate overlapEnd = endDate.isBefore(booking.getDateFin()) ? endDate : booking.getDateFin();
                
                // Get the service name
                Service service = serviceServices.getAllData().stream()
                    .filter(s -> s.getId() == serviceId)
                    .findFirst()
                    .orElse(null);
                String serviceName = service != null ? service.getNom() : "Service #" + serviceId;
                
                // Show error with specific overlap information
                showErrorAlert("Booking Error", String.format(
                    "%s is already booked during this period.\nConflicting booking: %s to %s",
                    serviceName,
                    overlapStart.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    overlapEnd.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                ));
                return false;
            }
        }

        return true;
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleCancel() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    public void setLocationIndexController(LocationIndex controller) {
        this.locationIndexController = controller;
    }

    public void setEditMode(Location location) {
        this.locationToEdit = location;
        this.isEditMode = true;
        populateFields();
        saveButton.setText("Update"); // Change button text for edit mode
    }

    private void populateFields() {
        if (locationToEdit != null) {
            // Find and select the service
            serviceComboBox.getItems().stream()
                .filter(service -> service.getId() == locationToEdit.getServiceId())
                .findFirst()
                .ifPresent(service -> serviceComboBox.setValue(service));

            userIdField.setText(String.valueOf(locationToEdit.getIdUser()));
            detailsField.setText(locationToEdit.getDetails());
            prixTotalField.setText(String.valueOf(locationToEdit.getPrixTotal()));
            dateDebutPicker.setValue(locationToEdit.getDateDebut());
            dateFinPicker.setValue(locationToEdit.getDateFin());
        }
    }
}
