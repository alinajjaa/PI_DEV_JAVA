package org.agritrace.controllers;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.agritrace.entities.Location;
import org.agritrace.entities.Service;
import org.agritrace.services.LocationServices;
import org.agritrace.services.ServiceServices;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tooltip;
import javafx.util.StringConverter;

public class LocationCalendar {
    @FXML
    private CalendarView calendarView;
    @FXML
    private TextField userIdField;
    @FXML
    private ComboBox<Service> serviceComboBox;
    @FXML
    private Button applyFilterButton;
    @FXML
    private Button clearFilterButton;

    private LocationServices locationServices;
    private ServiceServices serviceServices;
    private Calendar bookingsCalendar;
    private Integer filterUserId;
    private Service filterService;

    @FXML
    public void initialize() {
        // Initialize services
        locationServices = new LocationServices();
        serviceServices = new ServiceServices();

        // Setup calendar
        setupCalendar();

        // Load services into combo box
        loadServices();

        // Setup filter controls
        setupFilterControls();

        // Load initial data
        loadLocations();
    }

    private void setupCalendar() {
        bookingsCalendar = new Calendar("Bookings");
        bookingsCalendar.setStyle(Calendar.Style.STYLE1);

        // Add event handler for entry clicks
        calendarView.setEntryDetailsCallback(param -> {
            Entry<?> entry = param.getEntry();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Booking Details");
            alert.setHeaderText(entry.getTitle());
            alert.setContentText(entry.getLocation());
            alert.show();
            return null;
        });

        CalendarSource source = new CalendarSource("Location Bookings");
        source.getCalendars().add(bookingsCalendar);

        calendarView.getCalendarSources().add(source);
        calendarView.setRequestedTime(LocalTime.now());
    }

    private void loadServices() {
        List<Service> services = serviceServices.getAllData();
        ObservableList<Service> servicesList = FXCollections.observableArrayList(services);
        serviceComboBox.setItems(servicesList);
        
        // Set a custom string converter for better display
        serviceComboBox.setConverter(new StringConverter<Service>() {
            @Override
            public String toString(Service service) {
                return service == null ? "All Services" : service.getNom() + " - #" + service.getId();
            }

            @Override
            public Service fromString(String string) {
                return null; // Not needed for our use case
            }
        });
        
        serviceComboBox.setValue(null); // Show "All Services" by default
    }

    private void setupFilterControls() {
        // User ID validation
        userIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                userIdField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Filter button actions
        applyFilterButton.setOnAction(e -> applyFilters());
        clearFilterButton.setOnAction(e -> clearFilters());
    }

    private void applyFilters() {
        filterUserId = userIdField.getText().isEmpty() ? null : Integer.parseInt(userIdField.getText());
        filterService = serviceComboBox.getValue();
        loadLocations();
    }

    private void clearFilters() {
        userIdField.clear();
        serviceComboBox.setValue(null);
        filterUserId = null;
        filterService = null;
        loadLocations();
    }

    private void loadLocations() {
        // Clear existing entries
        bookingsCalendar.clear();

        // Get all locations
        for (Location location : locationServices.getAllData()) {
            // Apply filters
            if (filterUserId != null && location.getIdUser() != filterUserId) continue;
            if (filterService != null && location.getServiceId() != filterService.getId()) continue;

            // Create calendar entry
            Entry<?> entry = new Entry<>();
            entry.setInterval(location.getDateDebut(), location.getDateFin().plusDays(1));
            
            // Get service details for the entry
            Service service = serviceServices.getAllData().stream()
                .filter(s -> s.getId() == location.getServiceId())
                .findFirst()
                .orElse(null);

            // Set entry details
            String title = service != null ? service.getNom() : "Service #" + location.getServiceId();
            entry.setTitle(title);
            
            // Store all details in the location field
            StringBuilder details = new StringBuilder();
            details.append("Service: ").append(title).append("\n");
            details.append("User ID: ").append(location.getIdUser()).append("\n");
            details.append("Price: $").append(location.getPrixTotal());
            if (service != null) {
                details.append("\nLocation: ").append(formatAddress(service.getAdresse()));
            }
            entry.setLocation(details.toString());
            
            // Set as full day event
            entry.setFullDay(true);

            bookingsCalendar.addEntry(entry);
        }
    }

    private String formatAddress(String fullAddress) {
        if (fullAddress == null || fullAddress.isEmpty()) {
            return "";
        }
        
        String[] mainParts = fullAddress.split(";");
        if (mainParts.length > 0) {
            String addressPart = mainParts[0];
            String[] locationParts = addressPart.split(",");
            if (locationParts.length == 2) {
                String country = locationParts[0].trim();
                String city = locationParts[1].trim();
                return city + ", " + country;
            }
            return addressPart;
        }
        return fullAddress;
    }

    // Method to be called from ServiceIndex to show specific service bookings
    public void showServiceBookings(Service service) {
        filterService = service;
        filterUserId = null;
        loadLocations();
    }
}
