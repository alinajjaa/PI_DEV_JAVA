package org.agritrace.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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

    private ServiceServices serviceServices;
    private ObservableList<Service> serviceList;
    private FilteredList<Service> filteredServices;

    private static final String DEFAULT_IMAGE = "/images/default-service.png";

    @FXML
    public void initialize() {
        serviceServices = new ServiceServices();
        serviceList = FXCollections.observableArrayList();

        // Set preferred width for the services container
        servicesContainer.setPrefWidth(900);  // Accommodate 3 cards of 280px width + gaps

        // Setup search
        setupSearch();

        // Load services
        loadServices();

        // Setup navigation
        setupNavigation();

        // Clear button action
        clearButton.setOnAction(e -> searchField.clear());

        // Style the active nav button
        homeButton.getStyleClass().add("active-nav-button");
    }

    private void setupSearch() {
        filteredServices = new FilteredList<>(serviceList, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredServices.setPredicate(service -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }

                String searchText = newValue.toLowerCase().trim();

                return service.getNom().toLowerCase().contains(searchText) ||
                       service.getType().toLowerCase().contains(searchText) ||
                       service.getDescription().toLowerCase().contains(searchText) ||
                       service.getAdresse().toLowerCase().contains(searchText);
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
                    System.out.println("Loading image from resources: " + resourcePath);
                } else {
                    // Try loading as direct file path
                    File file = new File(imagePath);
                    if (file.exists()) {
                        image = new Image(file.toURI().toString());
                        System.out.println("Loading image from file: " + file.getAbsolutePath());
                    } else {
                        // Fall back to default image
                        System.out.println("Image not found, using default: " + imagePath);
                        image = new Image(getClass().getResourceAsStream(DEFAULT_IMAGE));
                    }
                }
            } else {
                // Load default image
                System.out.println("No image path provided, using default");
                image = new Image(getClass().getResourceAsStream(DEFAULT_IMAGE));
            }
            
            if (image.isError()) {
                System.out.println("Error loading image: " + imagePath);
                image = new Image(getClass().getResourceAsStream(DEFAULT_IMAGE));
            }
            
            imageView.setImage(image);
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            // If any error occurs, try to load the default image
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
}
