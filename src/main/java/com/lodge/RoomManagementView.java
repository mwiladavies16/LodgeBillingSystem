package com.lodge;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RoomManagementView {

    private final ObservableList<Room> rooms =
            FXCollections.observableArrayList();

    private TableView<Room> table;
    private TextField roomNumberField;
    private ComboBox<String> roomTypeCombo;
    private TextField priceField;
    private Label countLabel;

    public Parent getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        Label heading = new Label("Room Management");
        heading.setStyle(
                "-fx-font-size: 26px;" +
                        "-fx-font-weight: bold;"
        );

        countLabel = new Label("Total rooms: 0");

        roomNumberField = new TextField();
        roomNumberField.setPromptText("Room number");

        roomTypeCombo = new ComboBox<>();
        roomTypeCombo.getItems().addAll(
                "Single", "Double", "Deluxe"
        );
        roomTypeCombo.setPromptText("Room type");

        priceField = new TextField();
        priceField.setPromptText("Price per night");

        Button addButton = new Button("Add Room");
        Button statusButton = new Button("Change Status");
        Button deleteButton = new Button("Delete Room");

        HBox form = new HBox(
                10, roomNumberField, roomTypeCombo, priceField, addButton
        );

        table = new TableView<>();
        table.setItems(rooms);
        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        TableColumn<Room, String> numberColumn =
                new TableColumn<>("Room Number");
        numberColumn.setCellValueFactory(
                data -> data.getValue().roomNumberProperty()
        );

        TableColumn<Room, String> typeColumn =
                new TableColumn<>("Room Type");
        typeColumn.setCellValueFactory(
                data -> data.getValue().roomTypeProperty()
        );

        TableColumn<Room, String> priceColumn =
                new TableColumn<>("Price per Night");
        priceColumn.setCellValueFactory(
                data -> data.getValue().priceProperty()
        );

        TableColumn<Room, String> statusColumn =
                new TableColumn<>("Status");
        statusColumn.setCellValueFactory(
                data -> data.getValue().statusProperty()
        );

        table.getColumns().addAll(
                numberColumn, typeColumn, priceColumn, statusColumn
        );

        addButton.setOnAction(e -> addRoom());

        statusButton.setOnAction(e -> changeStatus());

        deleteButton.setOnAction(e -> deleteRoom());

        HBox actions = new HBox(10, statusButton, deleteButton);

        root.getChildren().addAll(
                heading, countLabel, form, table, actions
        );

        return root;
    }

    private void addRoom() {
        String number = roomNumberField.getText().trim();
        String type = roomTypeCombo.getValue();
        String priceText = priceField.getText().trim();

        if (number.isEmpty() || type == null || priceText.isEmpty()) {
            showMessage("Please complete all room fields.");
            return;
        }

        if (rooms.stream().anyMatch(
                room -> room.getRoomNumber().equalsIgnoreCase(number))) {
            showMessage("That room number already exists.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);

            if (!Double.isFinite(price) || price <= 0) {
                showMessage("Enter a valid price greater than zero.");
                return;
            }

            rooms.add(new Room(number, type, price));

            roomNumberField.clear();
            roomTypeCombo.setValue(null);
            priceField.clear();

            updateCount();

        } catch (NumberFormatException ex) {
            showMessage("Please enter a valid numeric price.");
        }
    }

    private void changeStatus() {
        Room selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showMessage("Please select a room first.");
            return;
        }

        switch (selected.getStatus()) {
            case "AVAILABLE" ->
                    selected.setStatus("OCCUPIED");
            case "OCCUPIED" ->
                    selected.setStatus("MAINTENANCE");
            default ->
                    selected.setStatus("AVAILABLE");
        }

        table.refresh();
    }

    private void deleteRoom() {
        Room selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showMessage("Please select a room to delete.");
            return;
        }

        rooms.remove(selected);
        updateCount();
    }

    private void updateCount() {
        countLabel.setText("Total rooms: " + rooms.size());
    }

    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Room Management");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class Room {
        private final javafx.beans.property.StringProperty roomNumber;
        private final javafx.beans.property.StringProperty roomType;
        private final javafx.beans.property.StringProperty price;
        private final javafx.beans.property.StringProperty status;

        public Room(String number, String type, double price) {
            this.roomNumber =
                    new javafx.beans.property.SimpleStringProperty(number);
            this.roomType =
                    new javafx.beans.property.SimpleStringProperty(type);
            this.price =
                    new javafx.beans.property.SimpleStringProperty(
                            String.format("K%.2f", price));
            this.status =
                    new javafx.beans.property.SimpleStringProperty("AVAILABLE");
        }

        public String getRoomNumber() {
            return roomNumber.get();
        }

        public String getStatus() {
            return status.get();
        }

        public void setStatus(String value) {
            status.set(value);
        }

        public javafx.beans.property.StringProperty roomNumberProperty() {
            return roomNumber;
        }

        public javafx.beans.property.StringProperty roomTypeProperty() {
            return roomType;
        }

        public javafx.beans.property.StringProperty priceProperty() {
            return price;
        }

        public javafx.beans.property.StringProperty statusProperty() {
            return status;
        }
    }
}