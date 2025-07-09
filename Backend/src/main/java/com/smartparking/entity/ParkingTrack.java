package com.smartparking.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity //indicates that this class is an entity and is mapped to a database table
@Table(name = "parkingTrack") //specifies the name of the table in the database
public class ParkingTrack {
    //declare variables
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int parkingID; //unique identifier for the parking track
    @ManyToOne //indicates a many-to-one relationship with the Users entity
    @JoinColumn(name = "userID", nullable = false) //userID cannot be null
    //instead of using userID, it is better to use the reference object to the Users entity
    //allowing for better encapsulation and better management of relationships in the database
    private Users user;
    @ManyToOne //indicates a many-to-one relationship with the Spots entity
    @JoinColumn(name = "spotsID", nullable = false) //spotsID cannot be null
    //instead of using userID, it is better to use the reference object to the Spots entity
    //allowing for better encapsulation and better management of relationships in the database
    private Spots spot; 
    @Column(name = "checkIn", insertable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime checkIn;//check in and check out time cannot be null
    @Column(name = "checkOut", insertable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime checkOut;
    @Column(nullable = false) //confirm check in and confirm check out cannot be null
    private boolean confirmCheckIn = false; // default confirm check in status
    @Column(nullable = false) 
    private boolean confirmCheckOut = false; // default confirm check out status
    
    //constructor
    public ParkingTrack(Users user, Spots spot) {
        this.user = user;
        this.spot = spot;
    }   

    //default constructor
    public ParkingTrack(){}

    //getters and setters
    public int getParkingID() {
        return parkingID;
    }   
    
    public void setParkingID(int parkingID) {
        this.parkingID = parkingID;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Spots getSpot() {
        return spot;
    }

    public void setSpot(Spots spot) {
        this.spot = spot;
    }

    public LocalDateTime getCheckIn() {
        return checkIn;
    }

    public LocalDateTime getCheckOut() {
        return checkOut;
    }

    public boolean isConfirmCheckIn() {
        return confirmCheckIn;
    }

    public void setConfirmCheckIn(boolean confirmCheckIn) {
        this.confirmCheckIn = confirmCheckIn;
    }

    public boolean isConfirmCheckOut() {
        return confirmCheckOut;
    }

    public void setConfirmCheckOut(boolean confirmCheckOut) {
        this.confirmCheckOut = confirmCheckOut;
    }

   @Override
    public String toString() {
        return "Parking Track ID: " + parkingID +
            ", User: " + (user != null ? user.getFullName() : "N/A") +
            ", Spot: " + (spot != null ? spot.getSpotCode() : "N/A") +
            ", Check In: " + (checkIn != null ? checkIn : "N/A") +
            ", Check Out: " + (checkOut != null ? checkOut : "N/A") +
            ", Confirm Check In: " + confirmCheckIn +
            ", Confirm Check Out: " + confirmCheckOut;
    }
}//parking track class