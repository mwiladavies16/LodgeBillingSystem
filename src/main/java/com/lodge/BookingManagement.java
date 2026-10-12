package com.lodge;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class BookingManagement {

    private final ObservableList<BookingRecord> bookings =
            FXCollections.observableArrayList();

    private TableView<BookingRecord> table;

    private TextField guestField;
    private TextField phoneField;
    private TextField roomField;
    private TextField nightsField;
    private TextField totalField;

    public Parent getView() {
        VBox root = new VBox(12);
        root.setPadding(new Insets(20));

        Label heading = new Label("Booking Management");
        heading.setStyle(
                "-fx-font-size: 25px; -fx-font-weight: bold;"
        );

        guestField = new TextField();
        guestField.setPromptText("Guest name");

        phoneField = new TextField();
        phoneField.setPromptText("Phone number");

        roomField = new TextField();
        roomField.setPromptText("Room type");

        nightsField = new TextField();
        nightsField.setPromptText("Number of nights");

        totalField = new TextField();
        totalField.setPromptText("Total bill (K)");

        Button addButton = new Button("Add Booking");
        Button deleteButton = new Button("Delete Selected");

        HBox form = new HBox(
                8, guestField, phoneField, roomField,
                nightsField, totalField, addButton
        );

        table = new TableView<>();
        table.setItems(bookings);
        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        TableColumn<BookingRecord, String> guestCol =
                new TableColumn<>("Guest");
        guestCol.setCellValueFactory(
                data -> data.getValue().guestProperty()
        );

        TableColumn<BookingRecord, String> phoneCol =
                new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(
                data -> data.getValue().phoneProperty()
        );

        TableColumn<BookingRecord, String> roomCol =
                new TableColumn<>("Room");
        roomCol.setCellValueFactory(
                data -> data.getValue().roomProperty()
        );

        TableColumn<BookingRecord, String> nightsCol =
                new TableColumn<>("Nights");
        nightsCol.setCellValueFactory(
                data -> data.getValue().nightsProperty()
        );

        TableColumn<BookingRecord, String> totalCol =
                new TableColumn<>("Total");
        totalCol.setCellValueFactory(
                data -> data.getValue().totalProperty()
        );

        table.getColumns().addAll(
                guestCol, phoneCol, roomCol, nightsCol, totalCol
        );

        addButton.setOnAction(e -> addBooking());
        deleteButton.setOnAction(e -> deleteBooking());

        root.getChildren().addAll(
                heading, form, table, deleteButton
        );

        return root;
    }

    private void addBooking() {
        String guest = guestField.getText().trim();
        String phone = phoneField.getText().trim();
        String room = roomField.getText().trim();
        String nightsText = nightsField.getText().trim();
        String totalText = totalField.getText().trim();

        if (guest.isEmpty() || phone.isEmpty()
                || room.isEmpty() || nightsText.isEmpty()
                || totalText.isEmpty()) {
            showMessage("Please fill in all fields.");
            return;
        }

        try {
            int nights = Integer.parseInt(nightsText);
            double total = Double.parseDouble(totalText);

            if (nights <= 0 || !Double.isFinite(total) || total < 0) {
                showMessage("Enter valid nights and bill amount.");
                return;
            }

            bookings.add(new BookingRecord(
                    guest, phone, room,
                    String.valueOf(nights),
                    String.format("K%.2f", total)
            ));

            guestField.clear();
            phoneField.clear();
            roomField.clear();
            nightsField.clear();
            totalField.clear();

        } catch (NumberFormatException ex) {
            showMessage("Nights must be a whole number and total must be numeric.");
        }
    }

    private void deleteBooking() {
        BookingRecord selected =
                table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showMessage("Select a booking first.");
            return;
        }

        bookings.remove(selected);
    }

    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class BookingRecord {
        private final javafx.beans.property.StringProperty guest;
        private final javafx.beans.property.StringProperty phone;
        private final javafx.beans.property.StringProperty room;
        private final javafx.beans.property.StringProperty nights;
        private final javafx.beans.property.StringProperty total;

        public BookingRecord(
                String guest, String phone, String room,
                String nights, String total) {
            this.guest = new javafx.beans.property.SimpleStringProperty(guest);
            this.phone = new javafx.beans.property.SimpleStringProperty(phone);
            this.room = new javafx.beans.property.SimpleStringProperty(room);
            this.nights = new javafx.beans.property.SimpleStringProperty(nights);
            this.total = new javafx.beans.property.SimpleStringProperty(total);
        }

        public javafx.beans.property.StringProperty guestProperty() {
            return guest;
        }

        public javafx.beans.property.StringProperty phoneProperty() {
            return phone;
        }

        public javafx.beans.property.StringProperty roomProperty() {
            return room;
        }

        public javafx.beans.property.StringProperty nightsProperty() {
            return nights;
        }

        public javafx.beans.property.StringProperty totalProperty() {
            return total;
        }
    }
}