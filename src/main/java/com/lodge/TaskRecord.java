package com.lodge;

import java.io.Serializable;

public class TaskRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String type;
    private final String roomNumber;
    private final String description;
    private String status;

    public TaskRecord(
            String type,
            String roomNumber,
            String description,
            String status) {

        this.type = type;
        this.roomNumber = roomNumber;
        this.description = description;
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return type
                + " | Room " + roomNumber
                + " | " + description
                + " | " + status;
    }
}