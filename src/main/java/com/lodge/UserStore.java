package com.lodge;

import java.util.List;

public class UserStore {

    private static final UserStore INSTANCE = new UserStore();

    private UserStore() {
        initializeAdministrator();
    }

    public static UserStore getInstance() {
        return INSTANCE;
    }

    // Create the default administrator if no accounts exist.
    private void initializeAdministrator() {
        List<UserAccount> users = LodgeData.users();

        boolean adminExists = users.stream()
                .anyMatch(user ->
                        "admin".equalsIgnoreCase(user.getUsername()));

        if (!adminExists) {
            users.add(new UserAccount(
                    "System Administrator",
                    "admin",
                    "admin123",
                    "Administrator",
                    true
            ));

            LodgeData.save();
        }
    }

    // Authenticate a user.
    public UserAccount authenticate(String username, String password) {
        if (username == null || password == null) {
            return null;
        }

        for (UserAccount user : LodgeData.users()) {
            if (user.getUsername() != null
                    && user.getUsername().equalsIgnoreCase(username.trim())
                    && user.getPassword() != null
                    && user.getPassword().equals(password)) {
                return user;
            }
        }

        return null;
    }

    // Register a new account. Administrator approval is required.
    public void register(
            String fullName,
            String username,
            String password,
            String role) {

        if (fullName == null || fullName.trim().isEmpty()
                || username == null || username.trim().isEmpty()
                || password == null || password.isEmpty()
                || role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Please complete all fields.");
        }

        if (password.length() < 6) {
            throw new IllegalArgumentException(
                    "Password must contain at least 6 characters.");
        }

        if (!List.of(
                "Reception",
                "Cleaning Management",
                "Technician Management",
                "User"
        ).contains(role)) {
            throw new IllegalArgumentException(
                    "Invalid account role.");
        }

        for (UserAccount user : LodgeData.users()) {
            if (user.getUsername() != null
                    && user.getUsername().equalsIgnoreCase(username.trim())) {
                throw new IllegalArgumentException(
                        "That username is already taken.");
            }
        }

        UserAccount newUser = new UserAccount(
                fullName.trim(),
                username.trim(),
                password,
                role,
                false
        );

        LodgeData.users().add(newUser);
        LodgeData.save();
    }

    // Approve a registration from the Administrator dashboard.
    public static void approve(UserAccount account) {
        if (account == null) {
            return;
        }

        account.setApproved(true);
        LodgeData.save();
    }

    // Reject and remove a registration.
    public static void reject(UserAccount account) {
        if (account == null) {
            return;
        }

        LodgeData.users().remove(account);
        LodgeData.save();
    }
}