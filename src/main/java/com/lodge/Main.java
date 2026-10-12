package com.lodge;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main entry point for the Lodge Billing System.
 * Uses LoginApp, which opens LodgeDashboard after successful authentication.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        new LoginApp().showLogin(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}