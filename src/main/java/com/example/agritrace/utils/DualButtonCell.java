package com.example.agritrace.utils;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;

public class DualButtonCell<T> extends TableCell<T, Void> {

    private final Button editButton = new Button("Update");
    private final Button deleteButton = new Button("Delete");
    private final HBox container = new HBox(8, editButton, deleteButton);

    public DualButtonCell(Consumer<T> editAction, Consumer<T> deleteAction) {
        container.setStyle("-fx-alignment: center;");

        // Style boutons
        styleButton(editButton, "#4CAF50", "#45a049");
        styleButton(deleteButton, "#F44336", "#e53935");

        // Action boutons
        editButton.setOnAction(event -> {
            T item = getTableView().getItems().get(getIndex());
            editAction.accept(item);
        });

        deleteButton.setOnAction(event -> {
            T item = getTableView().getItems().get(getIndex());
            deleteAction.accept(item);
        });
    }

    private void styleButton(Button button, String baseColor, String hoverColor) {
        button.setStyle(
                "-fx-background-color: " + baseColor + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 5;" +
                        "-fx-padding: 5 10 5 10;" +
                        "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: " + hoverColor + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 5;" +
                        "-fx-padding: 5 10 5 10;" +
                        "-fx-cursor: hand;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: " + baseColor + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 5;" +
                        "-fx-padding: 5 10 5 10;" +
                        "-fx-cursor: hand;"
        ));
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(container);
        }
    }
}
