package com.lodge;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class LoginApp extends Application {

    private static final String NAVY = "#101D35";
    private static final String GOLD = "#D8B15A";

    @Override
    public void start(Stage stage) {
        showLogin(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void showLogin(Stage stage) {
        stage.setTitle("Lodge Billing System - Login");
        stage.setMinWidth(800);
        stage.setMinHeight(600);

        StackPane root = createBackground("/images/Building.jpeg");
        VBox loginCard = createCard(420);

        ImageView logo = createLogo(110);

        Label title = new Label("LODGE BILLING SYSTEM");
        title.setStyle(
                "-fx-text-fill: " + GOLD + ";" +
                        "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;"
        );

        Label subtitle = new Label("Welcome back! Please sign in.");
        subtitle.setStyle(
                "-fx-text-fill: #E0E6EF;" +
                        "-fx-font-size: 13px;"
        );

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        styleInput(usernameField);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        styleInput(passwordField);

        Label message = createMessageLabel();

        Button loginButton = new Button("SIGN IN");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setPrefHeight(44);
        stylePrimaryButton(loginButton);

        Label registerPrompt = new Label("Don't have an account?");
        registerPrompt.setStyle("-fx-text-fill: #E0E6EF;");

        Button registerButton = new Button("Create Account");
        styleSecondaryButton(registerButton);

        HBox registerRow = new HBox(
                8,
                registerPrompt,
                registerButton
        );
        registerRow.setAlignment(Pos.CENTER);

        // LOGIN ACTION
        loginButton.setOnAction(event -> {

            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                message.setText(
                        "Please enter your username and password."
                );
                return;
            }

            try {
                UserAccount account = UserStore.getInstance()
                        .authenticate(username, password);

                if (account == null) {
                    message.setText(
                            "Invalid username or password."
                    );

                    passwordField.clear();
                    passwordField.requestFocus();
                    return;
                }

                if (!account.isApproved()) {
                    message.setText(
                            "Your account is awaiting administrator approval."
                    );
                    return;
                }

                /*
                 * CORRECT CONSTRUCTOR:
                 * Your LodgeDashboard accepts UserAccount only.
                 */
                LodgeDashboard dashboard = new LodgeDashboard(account);
                dashboard.show(stage);

            } catch (Exception ex) {
                ex.printStackTrace();

                message.setText(
                        "Login failed: " + ex.getMessage()
                );
            }
        });

        // Enter in username moves focus to password.
        usernameField.setOnAction(
                event -> passwordField.requestFocus()
        );

        // Enter in password submits the login.
        passwordField.setOnAction(
                event -> loginButton.fire()
        );

        loginButton.setDefaultButton(true);

        // Open account registration.
        registerButton.setOnAction(
                event -> showRegistration()
        );

        loginCard.getChildren().addAll(
                logo,
                title,
                subtitle,
                usernameField,
                passwordField,
                message,
                loginButton,
                registerRow
        );

        root.getChildren().add(loginCard);

        StackPane.setAlignment(loginCard, Pos.CENTER);
        StackPane.setMargin(loginCard, new Insets(20));

        Scene scene = new Scene(root, 1000, 700);

        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();

        usernameField.requestFocus();
    }

    // ACCOUNT REGISTRATION WINDOW
    private void showRegistration() {

        Stage registerStage = new Stage();

        registerStage.setTitle("Create Lodge Account");
        registerStage.setMinWidth(450);
        registerStage.setMinHeight(600);

        StackPane root = createBackground("/images/Building.jpeg");
        VBox card = createCard(400);

        ImageView logo = createLogo(85);

        Label heading = new Label("CREATE ACCOUNT");
        heading.setStyle(
                "-fx-text-fill: " + GOLD + ";" +
                        "-fx-font-size: 21px;" +
                        "-fx-font-weight: bold;"
        );

        Label subtitle = new Label(
                "Register for lodge system access"
        );
        subtitle.setStyle("-fx-text-fill: #E0E6EF;");

        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full name");
        styleInput(fullNameField);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Choose username");
        styleInput(usernameField);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(
                "Password (at least 6 characters)"
        );
        styleInput(passwordField);

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm password");
        styleInput(confirmPasswordField);

        ComboBox<String> roleBox = new ComboBox<>();

        roleBox.getItems().addAll(
                "Reception",
                "Cleaning Management",
                "Technician Management",
                "User"
        );

        roleBox.setPromptText("Select account role");
        roleBox.setMaxWidth(Double.MAX_VALUE);
        roleBox.setPrefHeight(42);
        roleBox.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 7;" +
                        "-fx-font-size: 13px;"
        );

        Label message = createMessageLabel();

        Button createButton = new Button("CREATE ACCOUNT");
        createButton.setMaxWidth(Double.MAX_VALUE);
        createButton.setPrefHeight(43);
        stylePrimaryButton(createButton);

        Button cancelButton = new Button("CANCEL");
        cancelButton.setMaxWidth(Double.MAX_VALUE);
        cancelButton.setPrefHeight(40);
        styleSecondaryButton(cancelButton);

        // CREATE ACCOUNT ACTION
        createButton.setOnAction(event -> {

            String fullName = fullNameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            String role = roleBox.getValue();

            if (fullName.isEmpty()
                    || username.isEmpty()
                    || password.isEmpty()
                    || confirmPassword.isEmpty()
                    || role == null) {

                message.setText("Please complete all fields.");
                return;
            }

            if (password.length() < 6) {
                message.setText(
                        "Password must contain at least 6 characters."
                );
                return;
            }

            if (!password.equals(confirmPassword)) {
                message.setText("Passwords do not match.");
                return;
            }

            try {
                UserStore.getInstance().register(
                        fullName,
                        username,
                        password,
                        role
                );

                Alert alert = new Alert(
                        Alert.AlertType.INFORMATION
                );

                alert.setTitle("Registration");
                alert.setHeaderText("Registration submitted");
                alert.setContentText(
                        "Your registration request has been processed. " +
                                "If administrator approval is required, wait " +
                                "for approval before signing in."
                );

                alert.showAndWait();
                registerStage.close();

            } catch (Exception ex) {
                ex.printStackTrace();

                message.setText(
                        "Registration failed: " + ex.getMessage()
                );
            }
        });

        confirmPasswordField.setOnAction(
                event -> createButton.fire()
        );

        createButton.setDefaultButton(true);

        cancelButton.setOnAction(
                event -> registerStage.close()
        );

        card.getChildren().addAll(
                logo,
                heading,
                subtitle,
                fullNameField,
                usernameField,
                passwordField,
                confirmPasswordField,
                roleBox,
                message,
                createButton,
                cancelButton
        );

        root.getChildren().add(card);

        StackPane.setAlignment(card, Pos.CENTER);
        StackPane.setMargin(card, new Insets(15));

        registerStage.setScene(
                new Scene(root, 520, 700)
        );

        registerStage.centerOnScreen();
        registerStage.show();
    }

    // CREATE A STYLED CARD
    private VBox createCard(double width) {

        VBox card = new VBox(15);

        card.setAlignment(Pos.CENTER);
        card.setPadding(
                new Insets(30, 40, 30, 40)
        );
        card.setMaxWidth(width);

        card.setBackground(
                new Background(
                        new BackgroundFill(
                                Color.web(NAVY, 0.95),
                                new CornerRadii(18),
                                Insets.EMPTY
                        )
                )
        );

        card.setBorder(
                new Border(
                        new BorderStroke(
                                Color.web(GOLD),
                                BorderStrokeStyle.SOLID,
                                new CornerRadii(18),
                                new BorderWidths(1.5)
                        )
                )
        );

        return card;
    }

    // BACKGROUND IMAGE
    private StackPane createBackground(String imagePath) {

        StackPane root = new StackPane();
        Image backgroundImage = loadImage(imagePath);

        if (backgroundImage != null) {

            ImageView backgroundView =
                    new ImageView(backgroundImage);

            backgroundView.setPreserveRatio(false);

            backgroundView.fitWidthProperty()
                    .bind(root.widthProperty());

            backgroundView.fitHeightProperty()
                    .bind(root.heightProperty());

            root.getChildren().add(backgroundView);

        } else {
            root.setStyle(
                    "-fx-background-color: " + NAVY + ";"
            );
        }

        Region overlay = new Region();

        overlay.setStyle(
                "-fx-background-color: rgba(5, 14, 30, 0.68);"
        );

        root.getChildren().add(overlay);

        return root;
    }

    // LOGO
    private ImageView createLogo(double size) {

        Image image = loadImage("/images/logo.jpeg");

        if (image == null) {
            System.err.println(
                    "Logo not found. Check " +
                            "src/main/resources/images/logo.jpeg"
            );

            return new ImageView();
        }

        ImageView logo = new ImageView(image);

        logo.setFitWidth(size);
        logo.setFitHeight(size);
        logo.setPreserveRatio(true);
        logo.setSmooth(true);

        return logo;
    }

    // LOAD AN IMAGE
    private Image loadImage(String path) {

        try {
            var resource = getClass().getResource(path);

            if (resource == null) {
                System.err.println(
                        "Image not found: " + path +
                                ". Check src/main/resources/images/"
                );

                return null;
            }

            Image image = new Image(
                    resource.toExternalForm()
            );

            if (image.isError()) {
                System.err.println(
                        "Could not load image: " + path +
                                " - " + image.getException()
                );

                return null;
            }

            return image;

        } catch (Exception ex) {
            System.err.println(
                    "Error loading image: " + path
            );

            ex.printStackTrace();
            return null;
        }
    }

    // MESSAGE LABEL
    private Label createMessageLabel() {

        Label message = new Label();

        message.setWrapText(true);
        message.setMaxWidth(320);

        message.setStyle(
                "-fx-text-fill: #FF8585;" +
                        "-fx-font-size: 12px;"
        );

        return message;
    }

    // INPUT FIELD STYLING
    private void styleInput(Control control) {

        control.setMaxWidth(Double.MAX_VALUE);
        control.setPrefHeight(42);

        control.setStyle(
                "-fx-background-color: #FFFFFF;" +
                        "-fx-background-radius: 7;" +
                        "-fx-border-radius: 7;" +
                        "-fx-border-color: " + GOLD + ";" +
                        "-fx-padding: 8 12 8 12;" +
                        "-fx-font-size: 13px;"
        );
    }

    // PRIMARY BUTTON STYLING
    private void stylePrimaryButton(Button button) {

        String normalStyle =
                "-fx-background-color: " + GOLD + ";" +
                        "-fx-text-fill: " + NAVY + ";" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 7;" +
                        "-fx-cursor: hand;";

        String hoverStyle =
                "-fx-background-color: #E8C979;" +
                        "-fx-text-fill: " + NAVY + ";" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 7;" +
                        "-fx-cursor: hand;";

        button.setStyle(normalStyle);

        button.setOnMouseEntered(
                event -> button.setStyle(hoverStyle)
        );

        button.setOnMouseExited(
                event -> button.setStyle(normalStyle)
        );
    }

    // SECONDARY BUTTON STYLING
    private void styleSecondaryButton(Button button) {

        String normalStyle =
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: " + GOLD + ";" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: " + GOLD + ";" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 8 14 8 14;" +
                        "-fx-cursor: hand;";

        String hoverStyle =
                "-fx-background-color: " + GOLD + ";" +
                        "-fx-text-fill: " + NAVY + ";" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: " + GOLD + ";" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 8 14 8 14;" +
                        "-fx-cursor: hand;";

        button.setStyle(normalStyle);

        button.setOnMouseEntered(
                event -> button.setStyle(hoverStyle)
        );

        button.setOnMouseExited(
                event -> button.setStyle(normalStyle)
        );
    }
}
