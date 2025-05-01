package org.agritrace.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.agritrace.entities.Service;
import org.agritrace.services.ServiceServices;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import java.net.URL;
import java.io.File;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

public class Home {
    @FXML
    private FlowPane servicesContainer;
    @FXML
    private TextField searchField;
    @FXML
    private Button clearButton;
    @FXML
    private Button homeButton;
    @FXML
    private Button servicesButton;
    @FXML
    private Button LoccationsButton;
    @FXML
    private WebView locationPicker;
    @FXML
    private Slider rangeSelector;
    @FXML
    private Label rangeLabel;
    @FXML
    private Button applyFilterButton;

    private ServiceServices serviceServices;
    private ObservableList<Service> serviceList;
    private FilteredList<Service> filteredServices;

    private static final String DEFAULT_IMAGE = "/images/default-service.png";

    private double selectedLat;
    private double selectedLng;

    @FXML
    public void initialize() {
        serviceServices = new ServiceServices();
        serviceList = FXCollections.observableArrayList();

        // Set preferred width for the services container
        servicesContainer.setPrefWidth(900);  // Accommodate 3 cards of 280px width + gaps

        // Initialize range selector
        setupRangeSelector();

        // Setup location picker
        setupLocationPicker();

        // Setup search
        setupSearch();

        // Load services
        loadServices();

        // Setup navigation
        setupNavigation();

        // Clear button action
        clearButton.setOnAction(e -> clearSearch());

        // Apply filter button action
        applyFilterButton.setOnAction(e -> applyLocationFilter());

        // Style the active nav button
        homeButton.getStyleClass().add("active-nav-button");
    }

    private void setupLocationPicker() {
        try {
            locationPicker.getEngine().setJavaScriptEnabled(true);
            String url = getClass().getResource("/google_maps_picker.html").toExternalForm();
            locationPicker.getEngine().load(url);
            
            locationPicker.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    try {
                        JSObject window = (JSObject) locationPicker.getEngine().executeScript("window");
                        JavaCallback callback = new JavaCallback();
                        window.setMember("javaCallback", callback);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class JavaCallback {
        public void onLocationSelected(double lat, double lng) {
            javafx.application.Platform.runLater(() -> {
                try {
                    selectedLat = lat;
                    selectedLng = lng;
                    
                    double range = rangeSelector.getValue();
                    applyLocationFilter();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void setupRangeSelector() {
        // Update label when slider value changes
        rangeSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            int value = newVal.intValue();
            rangeLabel.setText(value + " km");
            
            // If location is selected, automatically apply filter
            if (selectedLat != 0 || selectedLng != 0) {
                applyLocationFilter();
            }
        });
    }

    private void setupSearch() {
        filteredServices = new FilteredList<>(serviceList, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredServices.setPredicate(service -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return isWithinRange(service, rangeSelector.getValue());
                }

                String searchText = newValue.toLowerCase().trim();

                return (service.getNom().toLowerCase().contains(searchText) ||
                       service.getType().toLowerCase().contains(searchText) ||
                       service.getDescription().toLowerCase().contains(searchText) ||
                       service.getAdresse().toLowerCase().contains(searchText)) &&
                       isWithinRange(service, rangeSelector.getValue());
            });

            // Update the displayed cards
            updateServiceCards();
        });
    }

    private void loadServices() {
        serviceList.clear();
        serviceList.addAll(serviceServices.getAllData());
        updateServiceCards();
    }

    private void updateServiceCards() {
        servicesContainer.getChildren().clear();
        servicesContainer.getStyleClass().add("services-container");

        for (Service service : filteredServices) {
            VBox card = createServiceCard(service);
            servicesContainer.getChildren().add(card);
        }
    }

    private VBox createServiceCard(Service service) {
        VBox card = new VBox(10);  // 10 is the spacing between elements
        card.setPadding(new Insets(15));
        card.setPrefWidth(280);  // Set width for 3 cards per row (900/3 = 300, minus gaps)
        card.setMaxWidth(280);   // Ensure card doesn't grow beyond intended size
        card.getStyleClass().add("service-card");

        // Service image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(250);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        
        // Load image from service or use default
        String imagePath = service.getImage();
        try {
            Image image;
            if (imagePath != null && !imagePath.isEmpty()) {
                // Try to load the image from resources first
                String resourcePath = "/images/" + imagePath;
                URL resourceUrl = getClass().getResource(resourcePath);
                
                if (resourceUrl != null) {
                    // Image found in resources
                    image = new Image(resourceUrl.toExternalForm());
                } else {
                    // Try loading as direct file path
                    File file = new File(imagePath);
                    if (file.exists()) {
                        image = new Image(file.toURI().toString());
                    } else {
                        // Fall back to default image
                        image = new Image(getClass().getResourceAsStream(DEFAULT_IMAGE));
                    }
                }
            } else {
                // Load default image
                image = new Image(getClass().getResourceAsStream(DEFAULT_IMAGE));
            }
            
            if (image.isError()) {
                image = new Image(getClass().getResourceAsStream(DEFAULT_IMAGE));
            }
            
            imageView.setImage(image);
        } catch (Exception e) {
            try {
                imageView.setImage(new Image(getClass().getResourceAsStream(DEFAULT_IMAGE)));
            } catch (Exception ex) {
                System.err.println("Error loading default image: " + ex.getMessage());
            }
        }

        // Add rounded corners and effect to image
        Rectangle clip = new Rectangle(
            imageView.getFitWidth(), imageView.getFitHeight()
        );
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        imageView.setClip(clip);

        // Add effect
        DropShadow shadow = new DropShadow();
        shadow.setRadius(5.0);
        shadow.setOffsetY(3.0);
        shadow.setColor(Color.color(0, 0, 0, 0.3));
        imageView.setEffect(shadow);

        // Service name
        Label nameLabel = new Label(service.getNom());
        nameLabel.getStyleClass().add("service-name");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(250);

        // Service type
        Label typeLabel = new Label(service.getType());
        typeLabel.getStyleClass().add("service-type");
        typeLabel.setWrapText(true);
        typeLabel.setMaxWidth(250);

        // Description
        Label descLabel = new Label(service.getDescription());
        descLabel.getStyleClass().add("service-description");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(250);

        // Price
        Label priceLabel = new Label(String.format("Price: $%d", service.getPrix()));
        priceLabel.getStyleClass().add("service-price");

        // Address
        Label addressLabel = new Label("📍 " + service.getAdresse());
        addressLabel.getStyleClass().add("service-address");
        addressLabel.setWrapText(true);
        addressLabel.setMaxWidth(250);

        // Book button
        Button bookButton = new Button("Book Now");
        bookButton.getStyleClass().add("book-button");
        bookButton.setMaxWidth(Double.MAX_VALUE);
        bookButton.setOnAction(e -> handleBooking(service));

        // Add all elements to the card
        card.getChildren().addAll(
            imageView,
            nameLabel,
            typeLabel,
            new Separator(),
            descLabel,
            priceLabel,
            addressLabel,
            bookButton
        );

        return card;
    }

    private void handleBooking(Service service) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/BookService.fxml"));
            Parent root = loader.load();
            
            BookService controller = loader.getController();
            controller.setService(service);
            
            Stage stage = new Stage();
            stage.setTitle("Book Service - " + service.getNom());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while opening the booking form.");
            alert.showAndWait();
        }
    }

    private void setupNavigation() {
        servicesButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ServiceIndex.fxml"));
                Parent root = loader.load();
                
                // Make sure to apply the stylesheet
                root.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
                
                Stage stage = (Stage) servicesButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        LoccationsButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/LocationIndex.fxml"));
                Parent root = loader.load();
                Scene scene = LoccationsButton.getScene();
                scene.setRoot(root);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        selectedLat = 0;
        selectedLng = 0;
        rangeSelector.setValue(10); // Reset to default
        
        // Reset the location picker using JavaScript
        locationPicker.getEngine().executeScript("window.resetPicker()");
        
        // Reset the filter to show all services
        filteredServices.setPredicate(service -> true);
        updateServiceCards();
    }

    private void applyLocationFilter() {
        if (selectedLat == 0 && selectedLng == 0) {
            filteredServices.setPredicate(service -> true);
            updateServiceCards();
            return;
        }

        filteredServices.setPredicate(service -> {
            if (service == null || service.getAdresse() == null || service.getAdresse().isEmpty()) {
                return false;
            }

            try {
                boolean inRange = isWithinRange(service, rangeSelector.getValue());
                
                if (searchField.getText() == null || searchField.getText().trim().isEmpty()) {
                    return inRange;
                }

                String searchText = searchField.getText().toLowerCase().trim();
                boolean matchesSearch = service.getNom().toLowerCase().contains(searchText) ||
                                     service.getType().toLowerCase().contains(searchText) ||
                                     service.getDescription().toLowerCase().contains(searchText) ||
                                     service.getAdresse().toLowerCase().contains(searchText);
                
                return matchesSearch && inRange;
            } catch (Exception e) {
                return false;
            }
        });

        updateServiceCards();
    }

    private boolean isWithinRange(Service service, double range) {
        try {
            String[] parts = service.getAdresse().split(";");
            if (parts.length >= 3) {
                double serviceLat = Double.parseDouble(parts[1].trim());
                double serviceLng = Double.parseDouble(parts[2].trim());
                double distance = calculateDistance(selectedLat, selectedLng, serviceLat, serviceLng);
                return distance <= range;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // Distance in kilometers
    }
}
