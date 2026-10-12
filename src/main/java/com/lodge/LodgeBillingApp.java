
package com.lodge;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LodgeBillingApp {

    private TextField guestNameField;
    private TextField phoneField;
    private ComboBox<String> roomTypeCombo;
    private TextField priceField;
    private DatePicker checkInPicker;
    private DatePicker checkOutPicker;
    private Label nightsLabel;
    private Label totalLabel;
    private ComboBox<BookingRecord> bookingPicker;

    public VBox getView() {
        Label heading = new Label("LODGE BILLING SYSTEM");
        heading.setStyle(
                "-fx-font-size: 25px; -fx-font-weight: bold; " +
                        "-fx-text-fill: #1f2937;"
        );

        bookingPicker = new ComboBox<>(
                FXCollections.observableArrayList(LodgeData.bookings())
        );
        bookingPicker.setPromptText("Load an existing booking");
        bookingPicker.setMaxWidth(Double.MAX_VALUE);

        Button loadBookingButton = new Button("Load Booking");
        loadBookingButton.setOnAction(e -> loadSelectedBooking());

        HBox bookingRow = new HBox(10, bookingPicker, loadBookingButton);

        guestNameField = new TextField();
        guestNameField.setPromptText("Enter guest name");

        phoneField = new TextField();
        phoneField.setPromptText("Enter phone number");

        roomTypeCombo = new ComboBox<>();
        roomTypeCombo.getItems().addAll(
                "Standard", "Family", "Single", "Deluxe",
                "Suite", "Twin", "Double"
        );
        roomTypeCombo.setPromptText("Select room type");
        roomTypeCombo.setMaxWidth(Double.MAX_VALUE);

        priceField = new TextField();
        priceField.setPromptText("Enter price per night");

        checkInPicker = new DatePicker();
        checkInPicker.setPromptText("Select check-in date");
        checkInPicker.setMaxWidth(Double.MAX_VALUE);

        checkOutPicker = new DatePicker();
        checkOutPicker.setPromptText("Select check-out date");
        checkOutPicker.setMaxWidth(Double.MAX_VALUE);

        nightsLabel = new Label("Nights: 0");

        totalLabel = new Label("Total Bill: K0.00");
        totalLabel.setStyle(
                "-fx-font-size: 21px; -fx-font-weight: bold; " +
                        "-fx-text-fill: #15803d;"
        );

        Button calculateButton = new Button("Calculate Bill");
        Button clearButton = new Button("Clear");

        calculateButton.setOnAction(e -> calculateBill());
        clearButton.setOnAction(e -> clearForm());

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);

        form.add(new Label("Guest Name:"), 0, 0);
        form.add(guestNameField, 1, 0);

        form.add(new Label("Phone Number:"), 0, 1);
        form.add(phoneField, 1, 1);

        form.add(new Label("Room Type:"), 0, 2);
        form.add(roomTypeCombo, 1, 2);

        form.add(new Label("Price per Night (K):"), 0, 3);
        form.add(priceField, 1, 3);

        form.add(new Label("Check-in Date:"), 0, 4);
        form.add(checkInPicker, 1, 4);

        form.add(new Label("Check-out Date:"), 0, 5);
        form.add(checkOutPicker, 1, 5);

        form.add(nightsLabel, 1, 6);
        form.add(totalLabel, 1, 7);
        form.add(new HBox(10, calculateButton, clearButton), 1, 8);

        VBox root = new VBox(
                20,
                heading,
                new Label("Load a saved booking"),
                bookingRow,
                form
        );

        root.setPadding(new Insets(15));
        return root;
    }

    private void loadSelectedBooking() {
        BookingRecord booking = bookingPicker.getValue();

        if (booking == null) {
            showMessage("Select a booking first.");
            return;
        }

        guestNameField.setText(booking.getGuestName());
        phoneField.setText(booking.getPhone());
        roomTypeCombo.setValue(booking.getRoomType());
        priceField.setText(String.valueOf(booking.getPricePerNight()));
        checkInPicker.setValue(booking.getCheckIn());
        checkOutPicker.setValue(booking.getCheckOut());

        calculateBill();
    }

    private void calculateBill() {
        String guest = guestNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String roomType = roomTypeCombo.getValue();
        LocalDate checkIn = checkInPicker.getValue();
        LocalDate checkOut = checkOutPicker.getValue();

        if (guest.isEmpty() || phone.isEmpty() || roomType == null
                || priceField.getText().isBlank()
                || checkIn == null || checkOut == null) {
            showMessage("Please complete all billing fields.");
            return;
        }

        if (!checkOut.isAfter(checkIn)) {
            showMessage("Check-out must be after check-in.");
            return;
        }

        double price;

        try {
            price = Double.parseDouble(priceField.getText().trim());

            if (!Double.isFinite(price) || price <= 0) {
                showMessage("Enter a price greater than zero.");
                return;
            }
        } catch (NumberFormatException ex) {
            showMessage("Enter a valid price per night.");
            return;
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        double total = nights * price;

        nightsLabel.setText("Nights: " + nights);
        totalLabel.setText(String.format("Total Bill: K%.2f", total));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bill Details");
        alert.setHeaderText("Lodge Bill");
        alert.setContentText(
                "Guest: " + guest +
                        "\nPhone: " + phone +
                        "\nRoom Type: " + roomType +
                        "\nCheck-in: " + checkIn +
                        "\nCheck-out: " + checkOut +
                        "\nNights: " + nights +
                        String.format("\nPrice per night: K%.2f", price) +
                        String.format("\nTotal bill: K%.2f", total)
        );

        alert.showAndWait();
    }

    private void clearForm() {
        guestNameField.clear();
        phoneField.clear();
        roomTypeCombo.setValue(null);
        priceField.clear();
        checkInPicker.setValue(null);
        checkOutPicker.setValue(null);
        bookingPicker.setValue(null);
        nightsLabel.setText("Nights: 0");
        totalLabel.setText("Total Bill: K0.00");
    }

    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Lodge Billing System");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Launch the lodge application through the login screen.
    public static void main(String[] args) {
        Application.launch(LoginApp.class, args);
    }
}
