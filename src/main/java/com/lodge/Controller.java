package com.lodge;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;

public class Controller {

    // Login screen controls
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    // Demonstration login credentials
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "admin123";

    @FXML
    private void handleLogin() {

        // Get the values entered by the user
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Check for empty fields
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter your username and password.");
            return;
        }

        // Validate the login credentials
        if (username.equals(VALID_USERNAME)
                && password.equals(VALID_PASSWORD)) {

            messageLabel.setText("");

            try {
                // Locate the dashboard FXML file
                URL dashboardURL =
                        getClass().getResource("/lodgebilling/dashboard.fxml");

                if (dashboardURL == null) {
                    messageLabel.setText(
                            "Dashboard file not found. Please check dashboard.fxml."
                    );
                    System.err.println(
                            "Missing resource: /lodgebilling/dashboard.fxml"
                    );
                    return;
                }

                // Load the dashboard
                FXMLLoader loader = new FXMLLoader(dashboardURL);
                Parent dashboardRoot = loader.load();

                // Get the current login window
                Stage stage = (Stage) usernameField.getScene().getWindow();

                // Create and display the dashboard scene
                Scene dashboardScene =
                        new Scene(dashboardRoot, 1200, 750);

                // Apply the existing stylesheet if available
                URL cssURL =
                        getClass().getResource("/lodgebilling/syles.css");

                if (cssURL != null) {
                    dashboardScene.getStylesheets().add(
                            cssURL.toExternalForm()
                    );
                }

                stage.setTitle("Lodge Billing System - Dashboard");
                stage.setScene(dashboardScene);
                stage.centerOnScreen();
                stage.show();

            } catch (Exception e) {
                messageLabel.setText(
                        "Unable to open dashboard. Please check the application."
                );

                System.err.println("Failed to open dashboard:");
                e.printStackTrace();
            }

        } else {
            messageLabel.setText(
                    "Invalid username or password. Please try again."
            );

            // Clear the password so the user can re-enter it
            passwordField.clear();
            passwordField.requestFocus();
        }
    }

    @FXML
    private void handleClear() {

        usernameField.clear();
        passwordField.clear();
        messageLabel.setText("");

        usernameField.requestFocus();
    }

    @FXML
    private void handleExit() {

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
}