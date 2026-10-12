package com.lodge;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class DashboardController {

    // Dashboard information labels
    @FXML
    private Label contentTitle;

    @FXML
    private Label contentMessage;

    /*
     * Display the dashboard welcome information.
     */
    @FXML
    private void showDashboard() {

        contentTitle.setText("Dashboard");

        contentMessage.setText(
                "Welcome to the Lodge Billing System.\n\n" +
                        "Use the navigation buttons to manage guests, rooms, " +
                        "billing, payments, reports and users."
        );
    }

    /*
     * Open Guest Management.
     */
    @FXML
    private void openGuests() {
        openScreen("Guest Management", "guest.fxml");
    }

    /*
     * Open Room Management.
     */
    @FXML
    private void openRooms() {
        openScreen("Room Management", "rooms.fxml");
    }

    /*
     * Open Billing and Invoices.
     */
    @FXML
    private void openBilling() {
        openScreen("Billing & Invoices", "billing.fxml");
    }

    /*
     * Open Payments.
     */
    @FXML
    private void openPayments() {
        openScreen("Payments", "payments.fxml");
    }

    /*
     * Open Reports.
     */
    @FXML
    private void openReports() {
        openScreen("Reports", "reports.fxml");
    }

    /*
     * Open User Management.
     */
    @FXML
    private void openUsers() {
        openScreen("User Management", "users.fxml");
    }

    /*
     * Load a screen from the lodgebilling resources directory.
     */
    private void openScreen(String title, String fileName) {

        String resourcePath = "/lodgebilling/" + fileName;

        URL resource = getClass().getResource(resourcePath);

        // Check whether the requested FXML file exists.
        if (resource == null) {

            showError(
                    "Screen Not Found",
                    title + " cannot be opened because " + fileName +
                            " was not found.\n\n" +
                            "Expected resource location:\n" +
                            "src/main/resources/lodgebilling/" + fileName
            );

            return;
        }

        try {

            // Load the requested FXML screen.
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            // Get the existing application window.
            Stage stage =
                    (Stage) contentTitle.getScene().getWindow();

            // Create the new scene.
            Scene scene = new Scene(root, 1200, 750);

            // Load the existing stylesheet.
            URL cssURL =
                    getClass().getResource("/lodgebilling/syles.css");

            if (cssURL != null) {
                scene.getStylesheets().add(
                        cssURL.toExternalForm()
                );
            }

            // Display the requested screen.
            stage.setScene(scene);
            stage.setTitle("Lodge Billing System - " + title);
            stage.show();

        } catch (IOException | RuntimeException e) {

            System.err.println(
                    "Failed to open " + title +
                            " using resource: " + resourcePath
            );

            e.printStackTrace();

            showError(
                    "Unable to Open Screen",
                    "The " + title + " screen could not be opened.\n\n" +
                            "Check the FXML file and its controller configuration."
            );
        }
    }

    /*
     * Ask the user to confirm before logging out.
     */
    @FXML
    private void logout() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Logout");
        alert.setHeaderText(
                "Log out of the Lodge Billing System?"
        );
        alert.setContentText(
                "You will return to the login screen."
        );

        Optional<ButtonType> response = alert.showAndWait();

        if (response.isPresent()
                && response.get() == ButtonType.OK) {

            returnToLogin();
        }
    }

    /*
     * Return to the login screen.
     */
    private void returnToLogin() {

        String resourcePath = "/lodgebilling/login.fxml";

        URL resource = getClass().getResource(resourcePath);

        if (resource == null) {

            showError(
                    "Login Screen Not Found",
                    "The login screen could not be found at:\n" +
                            "src/main/resources/lodgebilling/login.fxml"
            );

            return;
        }

        try {

            // Load the login screen.
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            // Get the current application window.
            Stage stage =
                    (Stage) contentTitle.getScene().getWindow();

            // Create the login scene.
            Scene scene = new Scene(root, 1000, 650);

            // Apply the stylesheet.
            URL cssURL =
                    getClass().getResource("/lodgebilling/syles.css");

            if (cssURL != null) {
                scene.getStylesheets().add(
                        cssURL.toExternalForm()
                );
            }

            // Return to the login screen.
            stage.setScene(scene);
            stage.setTitle("Lodge Billing System - Login");
            stage.setMaximized(false);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException | RuntimeException e) {

            System.err.println("Failed to return to the login screen.");

            e.printStackTrace();

            showError(
                    "Logout Error",
                    "Could not return to the login screen.\n\n" +
                            "Check login.fxml and its controller configuration."
            );
        }
    }

    /*
     * Display an error message.
     */
    private void showError(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);

        alert.showAndWait();
    }
}