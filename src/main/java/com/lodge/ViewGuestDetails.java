package com.lodge;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/** Separate screen for searching and viewing guest booking details. */
public class ViewGuestDetails {

    public Node getView() {
        VBox screen = new VBox(10);
        screen.setFillWidth(true);

        TextField search = new TextField();
        search.setPromptText("Search guest name, phone number, or room number");
        HBox.setHgrow(search, Priority.ALWAYS);

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll(
                "All Statuses", "Booked", "Checked In", "Checked Out", "Cancelled"
        );
        statusFilter.setValue("All Statuses");

        TableView<BookingRecord> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No guest bookings found."));

        addColumn(table, "Guest Name", BookingRecord::getGuestName);
        addColumn(table, "Phone", BookingRecord::getPhone);
        addColumn(table, "Room", BookingRecord::getRoomNumber);
        addColumn(table, "Room Type", BookingRecord::getRoomType);
        addColumn(table, "Check In", b -> String.valueOf(b.getCheckIn()));
        addColumn(table, "Check Out", b -> String.valueOf(b.getCheckOut()));
        addColumn(table, "Nights", b -> String.valueOf(b.getNights()));
        addColumn(table, "Status", BookingRecord::getStatus);

        List<BookingRecord> allBookings = List.copyOf(LodgeData.bookings());

        Runnable applyFilter = () -> {
            String query = search.getText() == null
                    ? "" : search.getText().trim().toLowerCase(Locale.ROOT);
            String selectedStatus = statusFilter.getValue();

            table.setItems(FXCollections.observableArrayList(
                    allBookings.stream().filter(booking -> {
                        boolean matchesSearch = safe(booking.getGuestName()).toLowerCase(Locale.ROOT).contains(query)
                                || safe(booking.getPhone()).toLowerCase(Locale.ROOT).contains(query)
                                || safe(booking.getRoomNumber()).toLowerCase(Locale.ROOT).contains(query);
                        boolean matchesStatus = selectedStatus == null
                                || "All Statuses".equals(selectedStatus)
                                || selectedStatus.equalsIgnoreCase(safe(booking.getStatus()));
                        return matchesSearch && matchesStatus;
                    }).toList()
            ));
        };

        search.textProperty().addListener((obs, oldValue, newValue) -> applyFilter.run());
        statusFilter.setOnAction(event -> applyFilter.run());
        applyFilter.run();

        Button viewButton = new Button("View Selected Guest");
        viewButton.setOnAction(event -> {
            BookingRecord selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showMessage("Select a guest booking first.");
                return;
            }
            showMessage("Guest: " + selected.getGuestName()
                    + "\nPhone: " + selected.getPhone()
                    + "\nRoom: " + selected.getRoomNumber()
                    + "\nRoom Type: " + selected.getRoomType()
                    + "\nCheck In: " + selected.getCheckIn()
                    + "\nCheck Out: " + selected.getCheckOut()
                    + "\nNights: " + selected.getNights()
                    + "\nPrice per night: " + money(selected.getPricePerNight())
                    + "\nTotal: " + money(selected.getTotal())
                    + "\nStatus: " + selected.getStatus());
        });

        screen.getChildren().addAll(new HBox(10, search, statusFilter), table, viewButton);
        VBox.setVgrow(table, Priority.ALWAYS);
        return screen;
    }

    private <T> void addColumn(TableView<T> table, String title, Function<T, String> getter) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> new SimpleStringProperty(getter.apply(data.getValue())));
        table.getColumns().add(column);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String money(double amount) {
        return String.format(Locale.US, "K%.2f", amount);
    }

    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Guest Details");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
