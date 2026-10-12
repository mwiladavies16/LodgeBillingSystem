package com.lodge;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BookingRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String guestName;
    private final String phone;
    private final String roomNumber;
    private final String roomType;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final double pricePerNight;
    private String status;

    public BookingRecord(
            String guestName,
            String phone,
            String roomNumber,
            String roomType,
            LocalDate checkIn,
            LocalDate checkOut,
            double pricePerNight,
            String status) {

        this.guestName = guestName;
        this.phone = phone;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.pricePerNight = pricePerNight;
        this.status = status;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getPhone() {
        return phone;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Calculate the number of nights between check-in and check-out.
    public long getNights() {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    // Calculate the total cost of the booking.
    public double getTotal() {
        return getNights() * pricePerNight;
    }

    @Override
    public String toString() {
        return guestName
                + " | Room " + roomNumber
                + " | " + checkIn
                + " to " + checkOut
                + " | " + status;
    }
}