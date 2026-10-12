
package com.lodge;

public class Booking {

    private final int id;
    private final String guestName;
    private final String phone;
    private final String roomType;
    private final String checkIn;
    private final String checkOut;
    private final int nights;
    private final double totalBill;

    public Booking(
            int id,
            String guestName,
            String phone,
            String roomType,
            String checkIn,
            String checkOut,
            int nights,
            double totalBill) {

        this.id = id;
        this.guestName = guestName;
        this.phone = phone;
        this.roomType = roomType;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.nights = nights;
        this.totalBill = totalBill;
    }

    public int getId() {
        return id;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getPhone() {
        return phone;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public int getNights() {
        return nights;
    }

    public double getTotalBill() {
        return totalBill;
    }
}
