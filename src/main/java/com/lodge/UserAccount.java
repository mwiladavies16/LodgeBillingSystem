package com.lodge;

import java.io.Serializable;

public class UserAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String fullName;
    private final String username;
    private final String password;
    private final String role;
    private boolean approved;

    public UserAccount(
            String fullName,
            String username,
            String password,
            String role,
            boolean approved) {

        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.role = role;
        this.approved = approved;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}