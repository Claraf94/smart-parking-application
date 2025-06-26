package com.smartparking.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity //indicates that this class is an entity and is mapped to a database table
@Table(name = "reservations") //specifies the name of the table in the database
public class Reservations {
    //declare variables
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reservationID; //unique identifier for the reservation
    @ManyToOne //indicates a many-to-one relationship with the user entity
    @JoinColumn(name = "userID", nullable = false) //userID cannot be null
    //instead of using userID, it is better to use the reference object to the users entity
    //allowing for better encapsulation and better management of relationships in the database
    private Users userID;
    @ManyToOne //indicates a many-to-one relationship with the spots entity
    @JoinColumn(name = "spotsID", nullable = false) //spotsID cannot be null
    //instead of using SpotID, it is better to use the reference object to the Spots entity
    //allowing for better encapsulation and better management of relationships in the database
    private Spots spot;
    @Column(nullable = false) //phone number and number plate cannot be null
    private String phoneNumber, numberPlate;
    @Column(nullable = false) //start, end and reservedAt times cannot be null
    private LocalDateTime startTime, endTime, reservedAt;
    @Column(nullable = false) //reservation status cannot be null
    private String reservationStatus = "active"; // default reservation status

    //getters and setters
    public int getReservationID() {
        return reservationID;
    }
    
    public void setReservationID(int reservationID) {
        this.reservationID = reservationID;
    }

    public Users getUserID() {
        return userID;
    }

    public void setUserID(Users userID) {
        this.userID = userID;
    }

    public Spots getSpot() {
        return spot;
    }

    public void setSpot(Spots spot) {
        this.spot = spot;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getReservedAt() {
        return reservedAt;
    }

    public void setReservedAt(LocalDateTime reservedAt) {
        this.reservedAt = reservedAt;
    }

    public String getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(String reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    @PrePersist //this method is called before the entity is saved to the database
    protected void onCreate() {
        this.reservedAt = LocalDateTime.now(); // set the time that the reservation was made to the current time
    }

    @Override
    public String toString() {  
        return "Reservation details:\n" +
               "Reservation ID: " + reservationID +
               ", User ID: " + userID.getUserID() +
               ", Spot ID: " + spot.getSpotID() +
               ", Phone Number: " + phoneNumber +
               ", Number Plate: " + numberPlate +
               ", Start Time: " + startTime +
               ", End Time: " + endTime +
               ", Reserved at: " + reservedAt +
               ", Status: " + reservationStatus;
    }
}//reservation class