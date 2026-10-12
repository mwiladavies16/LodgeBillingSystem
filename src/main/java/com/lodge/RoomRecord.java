package com.lodge;

import java.io.Serializable;

public class RoomRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String roomNumber;
    private final String roomType;
    private String status;

    public RoomRecord(
            String roomNumber,
            String roomType,
            String status) {

        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.status = status;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Room " + roomNumber
                + " | " + roomType
                + " | " + status;
    }
}