package com.lodge;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class LodgeDashboard {

    private static final String NAVY = "#102F56";
    private static final String BLUE = "#174A83";
    private static final String GOLD = "#D4A72C";
    private static final String WHITE = "#FFFFFF";

    private static final List<String> ROOM_TYPES = List.of(
            "Standard", "Family", "Single", "Deluxe",
            "Suite", "Twin", "Double"
    );

    private final UserAccount currentUser;

    private BorderPane root;
    private VBox sidebar;
    private VBox content;
    private Stage dashboardStage;

    public LodgeDashboard(UserAccount currentUser) {
        this.currentUser = currentUser;
    }

    public void show(Stage stage) {
        dashboardStage = stage;

        root = new BorderPane();
        root.setStyle("-fx-background-color: #EAF0F7;");

        sidebar = new VBox(12);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setPrefWidth(235);
        sidebar.setMinWidth(235);
        sidebar.setStyle("-fx-background-color: " + NAVY + ";");

        buildSidebar();

        StackPane contentBackground = new StackPane();
        contentBackground.setStyle("-fx-background-color: #EAF0F7;");

        Image backgroundImage = loadImage("/lodgebilling/images/Building.jpeg");

        if (backgroundImage != null) {
            ImageView backgroundView = new ImageView(backgroundImage);
            backgroundView.setPreserveRatio(false);
            backgroundView.fitWidthProperty().bind(contentBackground.widthProperty());
            backgroundView.fitHeightProperty().bind(contentBackground.heightProperty());
            // Keep the background image fully visible and sharp.
            backgroundView.setOpacity(1.0);

            contentBackground.getChildren().add(backgroundView);
        }

        content = new VBox(12);
        content.setPadding(new Insets(14));
        content.setFillWidth(true);
        content.setMaxWidth(Double.MAX_VALUE);
        // Keep the lodge photo clear behind the dashboard content.
        content.setStyle("-fx-background-color: rgba(235,242,250,0.12);");

        // Scroll the dashboard vertically and horizontally when needed.
        // The background image stays fixed behind the scrolling content.
        ScrollPane dashboardScroll = new ScrollPane(content);
        dashboardScroll.setFitToWidth(true);
        dashboardScroll.setFitToHeight(false);
        dashboardScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        dashboardScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        dashboardScroll.setPannable(true);
        dashboardScroll.setStyle(
                "-fx-background: transparent;" +
                        "-fx-background-color: transparent;" +
                        "-fx-padding: 0;"
        );
        dashboardScroll.getStylesheets().clear();

        contentBackground.getChildren().add(dashboardScroll);

        root.setTop(createTopBar());
        root.setLeft(sidebar);
        // The clear lodge photo stays behind the scrollable dashboard content.
        root.setCenter(contentBackground);

        showDashboard();

        Scene scene = new Scene(root, 1200, 760);
        stage.setTitle("Lodge Billing System");
        stage.setMinWidth(950);
        stage.setMinHeight(650);
        stage.setScene(scene);
        stage.show();
    }

    private Image loadImage(String path) {
        var resource = getClass().getResource(path);

        if (resource == null) {
            System.out.println("Image not found: " + path);
            return null;
        }

        return new Image(resource.toExternalForm());
    }

    private void buildSidebar() {
        Image logoImage = loadImage("/lodgebilling/images/logo.jpeg");

        if (logoImage != null) {
            ImageView logo = new ImageView(logoImage);
            logo.setFitWidth(135);
            logo.setFitHeight(100);
            logo.setPreserveRatio(true);
            logo.setSmooth(true);

            StackPane logoHolder = new StackPane(logo);
            logoHolder.setPadding(new Insets(0, 0, 5, 0));
            sidebar.getChildren().add(logoHolder);
        } else {
            Label logoText = new Label("LODGE");
            logoText.setStyle(
                    "-fx-text-fill: " + GOLD + ";" +
                            "-fx-font-size: 22px;" +
                            "-fx-font-weight: bold;"
            );
            sidebar.getChildren().add(logoText);
        }

        Label appTitle = new Label("LODGE BILLING");
        appTitle.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 17px;" +
                        "-fx-font-weight: bold;"
        );

        Label roleLabel = new Label(
                currentUser.getFullName() + "\n" + currentUser.getRole()
        );
        roleLabel.setWrapText(true);
        roleLabel.setStyle(
                "-fx-text-fill: #D9E5F4;" +
                        "-fx-font-size: 12px;"
        );

        sidebar.getChildren().addAll(
                appTitle,
                roleLabel,
                createSidebarDivider()
        );

        addMenu("Dashboard", this::showDashboard);

        String role = currentUser.getRole();

        if ("Administrator".equalsIgnoreCase(role)) {
            addMenu("View Guest Details", this::showGuestDetails);
            addMenu("Room Management", this::showRooms);
            addMenu("Bookings", this::showBookings);
            addMenu("Lodge Billing", this::showBilling);
            addMenu("View Bills", this::showBills);
            addMenu("Cleaning Management", this::showCleaning);
            addMenu("Technician Management", this::showMaintenance);
        } else if ("Reception".equalsIgnoreCase(role)) {
            addMenu("View Guest Details", this::showGuestDetails);
            addMenu("Room Management", this::showRooms);
            addMenu("Bookings", this::showBookings);
            addMenu("Lodge Billing", this::showBilling);
            addMenu("View Bills", this::showBills);
        } else if ("Cleaning Management".equalsIgnoreCase(role)) {
            addMenu("Cleaning Tasks", this::showCleaning);
            addMenu("My Tasks", this::showMyTasks);
        } else if ("Technician Management".equalsIgnoreCase(role)) {
            addMenu("Maintenance Tasks", this::showMaintenance);
            addMenu("My Tasks", this::showMyTasks);
        } else {
            addMenu("My Tasks", this::showMyTasks);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button profileButton = createMenuButton("My Profile");
        profileButton.setOnAction(e -> showProfile());

        // Logout has been moved to the top-right dashboard header.
        sidebar.getChildren().addAll(spacer, profileButton);
    }

    private BorderPane createTopBar() {
        BorderPane topBar = new BorderPane();
        topBar.setPadding(new Insets(12, 24, 12, 24));
        topBar.setStyle(
                "-fx-background-color: " + NAVY + ";" +
                        "-fx-border-color: " + GOLD + ";" +
                        "-fx-border-width: 0 0 2 0;"
        );

        Label title = new Label("LODGE BILLING SYSTEM");
        title.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;"
        );
        topBar.setLeft(title);
        BorderPane.setAlignment(title, Pos.CENTER_LEFT);

        Label signedInUser = new Label(
                currentUser.getFullName() + "  |  " + currentUser.getRole()
        );
        signedInUser.setStyle(
                "-fx-text-fill: #D9E5F4;" +
                        "-fx-font-size: 12px;"
        );
        topBar.setRight(signedInUser);
        BorderPane.setAlignment(signedInUser, Pos.CENTER_RIGHT);

        HBox centreActions = new HBox(12);
        centreActions.setAlignment(Pos.CENTER);

        // Keep User Management beside Logout in the top-centre header.
        if ("Administrator".equalsIgnoreCase(currentUser.getRole())) {
            Button userManagementButton = new Button("USER MANAGEMENT");
            userManagementButton.setStyle(
                    "-fx-background-color: " + GOLD + ";" +
                            "-fx-text-fill: " + NAVY + ";" +
                            "-fx-font-size: 12px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 7;" +
                            "-fx-padding: 10 16 10 16;" +
                            "-fx-cursor: hand;"
            );
            userManagementButton.setOnMouseEntered(e -> userManagementButton.setStyle(
                    "-fx-background-color: #E8C979;" +
                            "-fx-text-fill: " + NAVY + ";" +
                            "-fx-font-size: 12px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 7;" +
                            "-fx-padding: 10 16 10 16;" +
                            "-fx-cursor: hand;"
            ));
            userManagementButton.setOnMouseExited(e -> userManagementButton.setStyle(
                    "-fx-background-color: " + GOLD + ";" +
                            "-fx-text-fill: " + NAVY + ";" +
                            "-fx-font-size: 12px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 7;" +
                            "-fx-padding: 10 16 10 16;" +
                            "-fx-cursor: hand;"
            ));
            userManagementButton.setOnAction(e -> showApprovals());
            centreActions.getChildren().add(userManagementButton);
        }

        Button logoutButton = new Button("LOG OUT");
        logoutButton.setStyle(
                "-fx-background-color: #B8323B;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 7;" +
                        "-fx-padding: 10 18 10 18;" +
                        "-fx-cursor: hand;"
        );
        logoutButton.setOnMouseEntered(e -> logoutButton.setStyle(
                "-fx-background-color: #D6454D;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 7;" +
                        "-fx-padding: 10 18 10 18;" +
                        "-fx-cursor: hand;"
        ));
        logoutButton.setOnMouseExited(e -> logoutButton.setStyle(
                "-fx-background-color: #B8323B;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 7;" +
                        "-fx-padding: 10 18 10 18;" +
                        "-fx-cursor: hand;"
        ));
        logoutButton.setOnAction(e -> {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Logout");
            confirmation.setHeaderText("Log out of Lodge Billing System?");
            confirmation.setContentText("Are you sure you want to log out?");

            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    new LoginApp().showLogin(dashboardStage);
                }
            });
        });

        centreActions.getChildren().add(logoutButton);
        topBar.setCenter(centreActions);
        BorderPane.setAlignment(centreActions, Pos.CENTER);
        return topBar;
    }

    private Node createSidebarDivider() {
        Region divider = new Region();
        divider.setPrefHeight(2);
        divider.setMaxWidth(Double.MAX_VALUE);
        divider.setStyle("-fx-background-color: " + GOLD + ";");
        return divider;
    }

    private void addMenu(String text, Runnable action) {
        Button button = createMenuButton(text);
        button.setOnAction(e -> action.run());
        sidebar.getChildren().add(button);
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(12, 14, 12, 14));
        button.setStyle(
                "-fx-background-color: " + BLUE + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 9;" +
                        "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e ->
                button.setStyle(
                        "-fx-background-color: " + GOLD + ";" +
                                "-fx-text-fill: " + NAVY + ";" +
                                "-fx-font-size: 13px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 9;" +
                                "-fx-cursor: hand;"
                )
        );

        button.setOnMouseExited(e ->
                button.setStyle(
                        "-fx-background-color: " + BLUE + ";" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 13px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 9;" +
                                "-fx-cursor: hand;"
                )
        );

        return button;
    }

    private void page(String title, String subtitle) {
        content.getChildren().clear();

        Label heading = new Label(title);
        heading.setStyle(
                "-fx-text-fill: " + NAVY + ";" +
                        "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;"
        );

        Label description = new Label(subtitle);
        description.setStyle(
                "-fx-text-fill: #46566B;" +
                        "-fx-font-size: 13px;"
        );

        VBox headingBox = new VBox(5, heading, description);

        Region line = new Region();
        line.setPrefHeight(3);
        line.setMaxWidth(Double.MAX_VALUE);
        line.setStyle("-fx-background-color: " + GOLD + ";");

        content.getChildren().addAll(headingBox, line);
    }

    private VBox createSummaryCard(String title, String value) {
        Label titleLabel = new Label(title);
        titleLabel.setWrapText(true);
        titleLabel.setStyle(
                "-fx-text-fill: #DCE8F7;" +
                        "-fx-font-size: 12px;"
        );

        Label valueLabel = new Label(value);
        valueLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 23px;" +
                        "-fx-font-weight: bold;"
        );

        VBox card = new VBox(8, titleLabel, valueLabel);
        card.setPadding(new Insets(12));
        card.setMinWidth(110);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle(
                "-fx-background-color: " + BLUE + ";" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: " + GOLD + ";" +
                        "-fx-border-radius: 14;" +
                        "-fx-border-width: 1.5;"
        );

        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private void styleTable(TableView<?> table) {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: " + GOLD + ";" +
                        "-fx-border-radius: 6;"
        );
    }

    private VBox panel(Node... nodes) {
        VBox box = new VBox(8, nodes);
        box.setPadding(new Insets(12));
        box.setMaxWidth(Double.MAX_VALUE);
        box.setStyle(
                "-fx-background-color: rgba(255,255,255,0.94);" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #C8D6E8;" +
                        "-fx-border-radius: 12;"
        );
        return box;
    }

    private void showDashboard() {
        content.getChildren().clear();

        Label heading = new Label("WELCOME TO THE LODGE BILLING SYSTEM");
        heading.setWrapText(true);
        heading.setMaxWidth(Double.MAX_VALUE);
        heading.setAlignment(Pos.CENTER);
        heading.setStyle(
                "-fx-text-fill: " + NAVY + ";" +
                        "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;"
        );

        Label welcome = new Label(
                "Welcome, " + currentUser.getFullName() + "!  " +
                        "Manage guest bookings, room availability, billing and lodge operations from one place."
        );
        welcome.setWrapText(true);
        welcome.setMaxWidth(Double.MAX_VALUE);
        welcome.setAlignment(Pos.CENTER);
        welcome.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: " + NAVY + ";"
        );

        VBox welcomePanel = panel(heading, welcome);
        welcomePanel.setAlignment(Pos.CENTER);
        welcomePanel.setMaxWidth(Double.MAX_VALUE);

        Label urgentTitle = new Label("URGENT ACTIONS");
        urgentTitle.setMaxWidth(Double.MAX_VALUE);
        urgentTitle.setAlignment(Pos.CENTER);
        urgentTitle.setStyle(
                "-fx-font-size: 17px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + NAVY + ";"
        );

        Button bookingsAction = new Button("Manage Bookings");
        Button guestsAction = new Button("View Guest Details");
        Button billingAction = new Button("Open Billing");
        Button billsAction = new Button("View Bills");
        styleActionButton(bookingsAction);
        styleActionButton(guestsAction);
        styleActionButton(billingAction);
        styleActionButton(billsAction);
        bookingsAction.setOnAction(e -> showBookings());
        guestsAction.setOnAction(e -> showGuestDetails());
        billingAction.setOnAction(e -> showBilling());
        billsAction.setOnAction(e -> showBills());

        HBox urgentActions = new HBox(8, bookingsAction, guestsAction, billingAction, billsAction);
        urgentActions.setAlignment(Pos.CENTER);
        urgentActions.setFillHeight(true);

        long roomCount = LodgeData.rooms().size();
        long bookingCount = LodgeData.bookings().size();
        long taskCount = LodgeData.tasks().stream()
                .filter(t -> !isFinishedTask(t.getStatus()))
                .count();

        long availableRooms = LodgeData.rooms().stream()
                .filter(r -> "Available".equalsIgnoreCase(r.getStatus()))
                .count();

        HBox cards = new HBox(10,
                createSummaryCard("Total Rooms", String.valueOf(roomCount)),
                createSummaryCard("Available Rooms", String.valueOf(availableRooms)),
                createSummaryCard("Total Bookings", String.valueOf(bookingCount)),
                createSummaryCard("Open Tasks", String.valueOf(taskCount))
        );
        cards.setAlignment(Pos.CENTER);
        cards.setFillHeight(true);
        cards.setMaxWidth(Double.MAX_VALUE);

        VBox dashboardBody = new VBox(12, welcomePanel, urgentTitle, urgentActions, cards);
        dashboardBody.setMaxWidth(Double.MAX_VALUE);
        dashboardBody.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(cards, Priority.NEVER);
        content.getChildren().add(dashboardBody);
    }

    private void showProfile() {
        page("My Profile", "Your signed-in account details.");

        VBox details = panel(
                profileLine("Full Name", currentUser.getFullName()),
                profileLine("Username", currentUser.getUsername()),
                profileLine("Role", currentUser.getRole()),
                profileLine("Account Status",
                        currentUser.isApproved() ? "Approved" : "Pending")
        );

        content.getChildren().add(details);
    }

    private Label profileLine(String title, String value) {
        Label label = new Label(title + ":  " + value);
        label.setWrapText(true);
        label.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-text-fill: " + NAVY + ";"
        );
        return label;
    }

    private void showGuestDetails() {
        page("View Guest Details", "Search and inspect guest booking information.");
        Node guestView = new ViewGuestDetails().getView();
        VBox box = panel(guestView);
        VBox.setVgrow(guestView, Priority.ALWAYS);
        VBox.setVgrow(box, Priority.ALWAYS);
        content.getChildren().add(box);
    }

    private <T> void addColumn(
            TableView<T> table,
            String title,
            java.util.function.Function<T, String> valueFunction) {

        TableColumn<T, String> column = new TableColumn<>(title);
        column.setCellValueFactory(data ->
                new SimpleStringProperty(
                        valueFunction.apply(data.getValue())
                )
        );
        table.getColumns().add(column);
    }

    private void showRooms() {
        if (!isAdministrator() && !"Reception".equalsIgnoreCase(currentUser.getRole())) {
            alert("You do not have permission to manage rooms.");
            return;
        }

        page("Room Management", "Add rooms and check room availability.");

        TextField roomNumber = new TextField();
        roomNumber.setPromptText("Room number");

        ComboBox<String> roomType = new ComboBox<>();
        roomType.getItems().addAll(ROOM_TYPES);
        roomType.setPromptText("Select room type");

        Button addRoom = new Button("Add Room");
        styleActionButton(addRoom);

        ListView<RoomRecord> roomList = new ListView<>();
        refreshRoomList(roomList);

        addRoom.setOnAction(e -> {
            String number = roomNumber.getText().trim();
            String type = roomType.getValue();

            if (number.isEmpty() || type == null) {
                alert("Enter a room number and select a room type.");
                return;
            }

            boolean exists = LodgeData.rooms().stream()
                    .anyMatch(r -> r.getRoomNumber().equalsIgnoreCase(number));

            if (exists) {
                alert("That room number already exists.");
                return;
            }

            LodgeData.rooms().add(new RoomRecord(number, type, "Available"));
            LodgeData.save();

            roomNumber.clear();
            roomType.setValue(null);
            refreshRoomList(roomList);
            alert("Room added successfully.");
        });

        VBox form = panel(
                new Label("Add a New Room"),
                roomNumber,
                roomType,
                addRoom
        );

        VBox listPanel = panel(new Label("Registered Rooms"), roomList);
        VBox.setVgrow(roomList, Priority.ALWAYS);
        VBox.setVgrow(listPanel, Priority.ALWAYS);

        content.getChildren().addAll(form, listPanel);
    }

    private void refreshRoomList(ListView<RoomRecord> list) {
        list.setItems(FXCollections.observableArrayList(LodgeData.rooms()));
    }

    private void showBookings() {
        if (!isAdministrator() && !"Reception".equalsIgnoreCase(currentUser.getRole())) {
            alert("You do not have permission to manage bookings.");
            return;
        }

        page("Bookings", "Create, check in, check out or cancel bookings.");

        TextField guest = new TextField();
        guest.setPromptText("Guest full name");

        TextField phone = new TextField();
        phone.setPromptText("Phone number");

        ComboBox<RoomRecord> room = new ComboBox<>();
        room.setPromptText("Select available room");
        refreshAvailableRooms(room);

        DatePicker checkIn = new DatePicker(LocalDate.now());
        DatePicker checkOut = new DatePicker(LocalDate.now().plusDays(1));

        TextField price = new TextField();
        price.setPromptText("Price per night");

        Button create = new Button("Create Booking");
        styleActionButton(create);

        create.setOnAction(e -> {
            String guestName = guest.getText().trim();
            String phoneNumber = phone.getText().trim();
            RoomRecord selectedRoom = room.getValue();

            if (guestName.isEmpty() || phoneNumber.isEmpty()
                    || selectedRoom == null
                    || checkIn.getValue() == null
                    || checkOut.getValue() == null
                    || price.getText().trim().isEmpty()) {
                alert("Please complete all booking fields.");
                return;
            }

            if (!checkOut.getValue().isAfter(checkIn.getValue())) {
                alert("Check-out must be after check-in.");
                return;
            }

            if (checkIn.getValue().isBefore(LocalDate.now())) {
                alert("Check-in cannot be in the past.");
                return;
            }

            double nightlyRate;

            try {
                nightlyRate = Double.parseDouble(price.getText().trim());

                if (!Double.isFinite(nightlyRate) || nightlyRate < 0) {
                    alert("Enter a valid non-negative price.");
                    return;
                }
            } catch (NumberFormatException ex) {
                alert("Enter a valid price per night.");
                return;
            }

            boolean overlaps = LodgeData.bookings().stream()
                    .filter(b -> b.getRoomNumber().equals(
                            selectedRoom.getRoomNumber()))
                    .filter(b -> !"Cancelled".equalsIgnoreCase(b.getStatus())
                            && !"Checked Out".equalsIgnoreCase(b.getStatus()))
                    .anyMatch(b ->
                            checkIn.getValue().isBefore(b.getCheckOut())
                                    && checkOut.getValue().isAfter(b.getCheckIn()));

            if (overlaps) {
                alert("This room already has a booking during those dates.");
                return;
            }

            BookingRecord booking = new BookingRecord(
                    guestName,
                    phoneNumber,
                    selectedRoom.getRoomNumber(),
                    selectedRoom.getRoomType(),
                    checkIn.getValue(),
                    checkOut.getValue(),
                    nightlyRate,
                    "Booked"
            );

            LodgeData.bookings().add(booking);
            LodgeData.save();

            alert("Booking created.\nTotal: " + money(booking.getTotal()));

            guest.clear();
            phone.clear();
            price.clear();
            refreshAvailableRooms(room);
        });

        TableView<BookingRecord> table = new TableView<>();
        styleTable(table);
        addColumn(table, "Guest", BookingRecord::getGuestName);
        addColumn(table, "Room", BookingRecord::getRoomNumber);
        addColumn(table, "Check In", b -> String.valueOf(b.getCheckIn()));
        addColumn(table, "Check Out", b -> String.valueOf(b.getCheckOut()));
        addColumn(table, "Total", b -> money(b.getTotal()));
        addColumn(table, "Status", BookingRecord::getStatus);

        table.setItems(FXCollections.observableArrayList(LodgeData.bookings()));

        Button checkInButton = new Button("Check In");
        Button checkOutButton = new Button("Check Out");
        Button cancelButton = new Button("Cancel Booking");

        styleActionButton(checkInButton);
        styleActionButton(checkOutButton);
        styleActionButton(cancelButton);

        checkInButton.setOnAction(e -> {
            BookingRecord selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                alert("Select a booking first.");
                return;
            }

            if (!"Booked".equalsIgnoreCase(selected.getStatus())) {
                alert("Only booked reservations can be checked in.");
                return;
            }

            if (LocalDate.now().isBefore(selected.getCheckIn())) {
                alert("The check-in date has not arrived yet.");
                return;
            }

            if (LocalDate.now().isAfter(selected.getCheckOut())) {
                alert("The check-out date has passed.");
                return;
            }

            selected.setStatus("Checked In");
            findRoom(selected.getRoomNumber()).ifPresent(
                    r -> r.setStatus("Occupied")
            );

            LodgeData.save();
            refreshBookingTable(table);
            refreshAvailableRooms(room);
        });

        checkOutButton.setOnAction(e -> {
            BookingRecord selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                alert("Select a booking first.");
                return;
            }

            if (!"Checked In".equalsIgnoreCase(selected.getStatus())) {
                alert("Only checked-in guests can check out.");
                return;
            }

            selected.setStatus("Checked Out");

            findRoom(selected.getRoomNumber()).ifPresent(r -> {
                r.setStatus("Cleaning");

                LodgeData.tasks().add(new TaskRecord(
                        "Cleaning",
                        r.getRoomNumber(),
                        "Clean room after guest checkout",
                        "Needs Cleaning"
                ));
            });

            LodgeData.save();
            refreshBookingTable(table);
            refreshAvailableRooms(room);
            alert("Guest checked out. A cleaning task was created.");
        });

        cancelButton.setOnAction(e -> {
            BookingRecord selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                alert("Select a booking first.");
                return;
            }

            if (!"Booked".equalsIgnoreCase(selected.getStatus())) {
                alert("Only bookings that have not checked in can be cancelled.");
                return;
            }

            selected.setStatus("Cancelled");
            LodgeData.save();
            refreshBookingTable(table);
            refreshAvailableRooms(room);
        });

        VBox bookingForm = panel(
                new Label("Create New Booking"),
                guest, phone, room,
                new Label("Check-in"), checkIn,
                new Label("Check-out"), checkOut,
                price, create
        );

        VBox bookingsPanel = panel(
                new Label("Existing Bookings"),
                table,
                new HBox(10, checkInButton, checkOutButton, cancelButton)
        );

        VBox.setVgrow(table, Priority.ALWAYS);
        VBox.setVgrow(bookingsPanel, Priority.ALWAYS);

        content.getChildren().addAll(bookingForm, bookingsPanel);
    }

    private void refreshAvailableRooms(ComboBox<RoomRecord> combo) {
        String previous = combo.getValue() == null
                ? null : combo.getValue().getRoomNumber();

        List<RoomRecord> available = LodgeData.rooms().stream()
                .filter(r -> "Available".equalsIgnoreCase(r.getStatus()))
                .toList();

        combo.setItems(FXCollections.observableArrayList(available));

        if (previous != null) {
            available.stream()
                    .filter(r -> r.getRoomNumber().equals(previous))
                    .findFirst()
                    .ifPresent(combo::setValue);
        }
    }

    private void refreshBookingTable(TableView<BookingRecord> table) {
        table.setItems(FXCollections.observableArrayList(LodgeData.bookings()));
        table.refresh();
    }

    private Optional<RoomRecord> findRoom(String roomNumber) {
        return LodgeData.rooms().stream()
                .filter(r -> r.getRoomNumber().equalsIgnoreCase(roomNumber))
                .findFirst();
    }

    private void showCleaning() {
        showTaskPage("Cleaning", "Cleaning tasks", "Needs Cleaning",
                List.of("Needs Cleaning", "In Progress", "Cleaned"));
    }

    private void showMaintenance() {
        showTaskPage("Maintenance", "Maintenance tasks", "Pending",
                List.of("Pending", "In Progress", "Completed"));
    }

    private void showTaskPage(
            String taskType,
            String title,
            String initialStatus,
            List<String> statuses) {

        page(title, "Review tasks and update their progress.");

        if (isAdministrator()) {
            TextField roomNumber = new TextField();
            roomNumber.setPromptText("Room number");

            TextField description = new TextField();
            description.setPromptText("Task description");

            Button create = new Button("Create Task");
            styleActionButton(create);

            create.setOnAction(e -> {
                String room = roomNumber.getText().trim();
                String desc = description.getText().trim();

                if (room.isEmpty() || desc.isEmpty()) {
                    alert("Enter a room number and task description.");
                    return;
                }

                boolean roomExists = LodgeData.rooms().stream()
                        .anyMatch(r -> r.getRoomNumber().equalsIgnoreCase(room));

                if (!roomExists) {
                    alert("That room does not exist.");
                    return;
                }

                LodgeData.tasks().add(new TaskRecord(
                        taskType, room, desc, initialStatus
                ));

                LodgeData.save();
                roomNumber.clear();
                description.clear();
                alert("Task created successfully.");
                showTaskPage(taskType, title, initialStatus, statuses);
            });

            content.getChildren().add(panel(
                    new Label("Create New Task"),
                    roomNumber,
                    description,
                    create
            ));
        }

        List<TaskRecord> matchingTasks = LodgeData.tasks().stream()
                .filter(t -> t.getType().equalsIgnoreCase(taskType))
                .toList();

        TableView<TaskRecord> table = new TableView<>();
        styleTable(table);

        addColumn(table, "Room", TaskRecord::getRoomNumber);
        addColumn(table, "Description", TaskRecord::getDescription);
        addColumn(table, "Status", TaskRecord::getStatus);

        table.setItems(FXCollections.observableArrayList(matchingTasks));

        ComboBox<String> status = new ComboBox<>();
        status.getItems().addAll(statuses);
        status.setValue(statuses.get(0));

        Button update = new Button("Update Selected Task");
        styleActionButton(update);

        update.setOnAction(e -> {
            TaskRecord selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                alert("Select a task first.");
                return;
            }

            String newStatus = status.getValue();
            selected.setStatus(newStatus);

            updateRoomAfterTask(selected);
            LodgeData.save();

            table.refresh();
            alert("Task updated.");
        });

        VBox taskPanel = panel(
                new Label("Task List"),
                table,
                new HBox(10, status, update)
        );

        VBox.setVgrow(table, Priority.ALWAYS);
        VBox.setVgrow(taskPanel, Priority.ALWAYS);
        content.getChildren().add(taskPanel);
    }

    private void updateRoomAfterTask(TaskRecord task) {
        findRoom(task.getRoomNumber()).ifPresent(room -> {
            if ("Cleaning".equalsIgnoreCase(task.getType())) {
                if ("Cleaned".equalsIgnoreCase(task.getStatus())) {
                    boolean hasOtherOpenCleaning = LodgeData.tasks().stream()
                            .anyMatch(t ->
                                    t != task
                                            && "Cleaning".equalsIgnoreCase(t.getType())
                                            && t.getRoomNumber().equalsIgnoreCase(
                                            task.getRoomNumber())
                                            && !"Cleaned".equalsIgnoreCase(t.getStatus()));

                    if (!hasOtherOpenCleaning) {
                        room.setStatus("Available");
                    }
                } else {
                    room.setStatus("Cleaning");
                }
            }

            if ("Maintenance".equalsIgnoreCase(task.getType())
                    && !"Completed".equalsIgnoreCase(task.getStatus())) {
                room.setStatus("Maintenance");
            } else if ("Maintenance".equalsIgnoreCase(task.getType())
                    && "Completed".equalsIgnoreCase(task.getStatus())
                    && "Maintenance".equalsIgnoreCase(room.getStatus())) {
                room.setStatus("Available");
            }
        });
    }

    private void showMyTasks() {
        page("My Tasks", "Tasks assigned to your department.");

        TableView<TaskRecord> table = new TableView<>();
        styleTable(table);

        addColumn(table, "Type", TaskRecord::getType);
        addColumn(table, "Room", TaskRecord::getRoomNumber);
        addColumn(table, "Description", TaskRecord::getDescription);
        addColumn(table, "Status", TaskRecord::getStatus);

        table.setItems(FXCollections.observableArrayList(LodgeData.tasks()));

        VBox box = panel(table);
        VBox.setVgrow(table, Priority.ALWAYS);
        VBox.setVgrow(box, Priority.ALWAYS);

        content.getChildren().add(box);
    }

    private void showBilling() {
        page("Lodge Billing", "Calculate and review lodge bills.");

        try {
            Node billingView = new LodgeBillingApp().getView();
            VBox box = panel(billingView);
            VBox.setVgrow(billingView, Priority.ALWAYS);
            VBox.setVgrow(box, Priority.ALWAYS);
            content.getChildren().add(box);
        } catch (Exception ex) {
            content.getChildren().add(panel(
                    new Label("Unable to load the billing form."),
                    new Label("Check that LodgeBillingApp has a public getView() method.")
            ));
            ex.printStackTrace();
        }
    }

    private void showBills() {
        page("View Bills", "Search booking bill totals and inspect bill details.");

        Node billsView = new ViewBills().getView();
        VBox box = panel(billsView);
        VBox.setVgrow(billsView, Priority.ALWAYS);
        VBox.setVgrow(box, Priority.ALWAYS);
        content.getChildren().add(box);
    }

    private void showApprovals() {
        if (!isAdministrator()) {
            alert("Only administrators can approve user accounts.");
            return;
        }

        page("User Approvals", "Approve or reject employee registration requests.");

        TableView<UserAccount> table = new TableView<>();
        styleTable(table);

        addColumn(table, "Full Name", UserAccount::getFullName);
        addColumn(table, "Username", UserAccount::getUsername);
        addColumn(table, "Role", UserAccount::getRole);
        addColumn(table, "Approved",
                u -> u.isApproved() ? "Yes" : "Pending");

        ObservableList<UserAccount> rows =
                FXCollections.observableArrayList(LodgeData.users());

        table.setItems(rows);

        Button approve = new Button("Approve Selected");
        Button reject = new Button("Reject Selected");

        styleActionButton(approve);
        styleActionButton(reject);

        approve.setOnAction(e -> {
            UserAccount selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                alert("Select a user first.");
                return;
            }

            UserStore.approve(selected);
            LodgeData.save();
            table.refresh();
            alert("User account approved.");
        });

        reject.setOnAction(e -> {
            UserAccount selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                alert("Select a user first.");
                return;
            }

            Alert confirmation = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Reject registration for " + selected.getUsername() + "?",
                    ButtonType.YES,
                    ButtonType.NO
            );

            confirmation.setHeaderText("Confirm rejection");

            if (confirmation.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                UserStore.reject(selected);
                LodgeData.save();
                rows.setAll(LodgeData.users());
            }
        });

        VBox box = panel(
                table,
                new HBox(10, approve, reject)
        );

        VBox.setVgrow(table, Priority.ALWAYS);
        VBox.setVgrow(box, Priority.ALWAYS);
        content.getChildren().add(box);
    }

    private void styleActionButton(Button button) {
        button.setStyle(
                "-fx-background-color: " + BLUE + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 16;" +
                        "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e ->
                button.setStyle(
                        "-fx-background-color: " + GOLD + ";" +
                                "-fx-text-fill: " + NAVY + ";" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 8;" +
                                "-fx-padding: 10 16;" +
                                "-fx-cursor: hand;"
                )
        );

        button.setOnMouseExited(e ->
                button.setStyle(
                        "-fx-background-color: " + BLUE + ";" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 8;" +
                                "-fx-padding: 10 16;" +
                                "-fx-cursor: hand;"
                )
        );
    }

    private boolean isAdministrator() {
        return "Administrator".equalsIgnoreCase(currentUser.getRole());
    }

    private boolean isFinishedTask(String status) {
        return "Cleaned".equalsIgnoreCase(status)
                || "Completed".equalsIgnoreCase(status);
    }

    private String money(double amount) {
        return String.format(Locale.US, "K%.2f", amount);
    }

    private void alert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Lodge Billing System");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
