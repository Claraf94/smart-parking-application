package com.smartparking.dto;

import com.smartparking.enums.NotificationType;

public class ReservationNotification {
    //declare variables
    private String message;
    private NotificationType type;
    private int userID, reservationID;

    //constructor
    public ReservationNotification(String message, NotificationType type, int userID, int reservationID) {
        this.message = message;
        this.type = type;
        this.userID = userID;
        this.reservationID = reservationID;
    }
    
    //default constructor
    public ReservationNotification() {}

    //getters and setters
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getReservationID() {
        return reservationID;
    }

    public void setReservationID(int reservationID) {
        this.reservationID = reservationID;
    }

    @Override
    public String toString() {
        return "Reservation Notification\n" +
                "User ID: " + userID + "\n" +
                "Reservation ID: " + reservationID + "\n" +
                "Message: " + message + "\n" +
                "Type: " + type;
    }
}//reservation notification class