package org.agritrace.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.agritrace.entities.Location;
import org.agritrace.entities.Service;
import org.agritrace.services.LocationServices;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BookService {
    @FXML
    private Label serviceName;
    @FXML
    private Label serviceType;
    @FXML
    private Label servicePrice;
    @FXML
    private TextArea detailsField;
    @FXML
    private DatePicker dateDebutPicker;
    @FXML
    private DatePicker dateFinPicker;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private Button confirmButton;
    @FXML
    private Button cancelButton;

    private Service service;
    private LocationServices locationServices;
    private static final int DEFAULT_USER_ID = 1; // Temporary until user management is integrated

    @FXML
    public void initialize() {
        locationServices = new LocationServices();

        // Initialize date pickers with current and next day
        dateDebutPicker.setValue(LocalDate.now());
        dateFinPicker.setValue(LocalDate.now().plusDays(1));

        // Add listeners for date changes to update total price
        dateDebutPicker.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());
        dateFinPicker.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());
        
        // Add validation listeners
        dateDebutPicker.valueProperty().addListener((obs, oldVal, newVal) -> validateDates());
        dateFinPicker.valueProperty().addListener((obs, oldVal, newVal) -> validateDates());
    }

    public void setService(Service service) {
        this.service = service;
        
        // Update UI with service details
        serviceName.setText(service.getNom());
        serviceType.setText(service.getType());
        servicePrice.setText(String.format("$%d per day", service.getPrix()));
        
        // Calculate initial total price
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        if (dateDebutPicker.getValue() != null && dateFinPicker.getValue() != null && service != null) {
            long days = ChronoUnit.DAYS.between(dateDebutPicker.getValue(), dateFinPicker.getValue()) + 1;
            double totalPrice = days * service.getPrix();
            totalPriceLabel.setText(String.format("$%.2f", totalPrice));
        }
    }

    private void validateDates() {
        LocalDate startDate = dateDebutPicker.getValue();
        LocalDate endDate = dateFinPicker.getValue();
        
        boolean isValid = true;
        String errorMessage = "";

        if (startDate != null && startDate.isBefore(LocalDate.now())) {
            errorMessage = "Start date cannot be in the past";
            isValid = false;
        } else if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            errorMessage = "End date must be after start date";
            isValid = false;
        }

        confirmButton.setDisable(!isValid);
        
        // Show tooltip with error message if invalid
        if (!isValid) {
            Tooltip tooltip = new Tooltip(errorMessage);
            confirmButton.setTooltip(tooltip);
        } else {
            confirmButton.setTooltip(null);
        }
    }

    @FXML
    private void handleConfirm() {
        if (!validateInput()) {
            return;
        }

        try {
            Location location = new Location(
                service.getId(),
                DEFAULT_USER_ID,
                detailsField.getText().trim(),
                calculateTotalPrice(),
                dateDebutPicker.getValue(),
                dateFinPicker.getValue()
            );

            locationServices.addEntity(location);
            
            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Booking Confirmed");
            alert.setHeaderText(null);
            alert.setContentText("Your booking has been confirmed successfully!");
            alert.showAndWait();

            // Close the window
            closeWindow();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while processing your booking: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private boolean validateInput() {
        if (detailsField.getText().trim().isEmpty()) {
            showError("Please provide booking details");
            return false;
        }

        if (dateDebutPicker.getValue() == null || dateFinPicker.getValue() == null) {
            showError("Please select both start and end dates");
            return false;
        }

        if (dateDebutPicker.getValue().isBefore(LocalDate.now())) {
            showError("Start date cannot be in the past");
            return false;
        }

        if (dateFinPicker.getValue().isBefore(dateDebutPicker.getValue())) {
            showError("End date must be after start date");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private double calculateTotalPrice() {
        long days = ChronoUnit.DAYS.between(dateDebutPicker.getValue(), dateFinPicker.getValue()) + 1;
        return days * service.getPrix();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }
}
