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
    private Users userID;
    @ManyToOne //indicates a many-to-one relationship with the Spots entity
    @JoinColumn(name = "spotsID", nullable = false) //spotsID cannot be null
    //instead of using userID, it is better to use the reference object to the Spots entity
    //allowing for better encapsulation and better management of relationships in the database
    private Spots spotsID; 
    @Column(nullable = false) //check in and check out time cannot be null
    private LocalDateTime checkIn, checkOut;
    @Column(nullable = false) //confirm check in and confirm check out cannot be null
    private boolean confirmCheckIn = false; // default confirm check in status
    @Column(nullable = false) 
    private boolean confirmCheckOut = false; // default confirm check out status
    
    //getters and setters
    public int getParkingID() {
        return parkingID;
    }   
    
    public void setParkingID(int parkingID) {
        this.parkingID = parkingID;
    }

    public Users getUserID() {
        return userID;
    }

    public void setUserID(Users userID) {
        this.userID = userID;
    }

    public Spots getSpotsID() {
        return spotsID;
    }

    public void setSpotsID(Spots spotsID) {
        this.spotsID = spotsID;
    }

    public LocalDateTime getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDateTime checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDateTime getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDateTime checkOut) {
        this.checkOut = checkOut;
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

    @PrePersist //this method is called before the entity is saved to the database
    protected void onCheckIn() {
        this.checkIn = LocalDateTime.now(); //set the check in time to the current time
    }

    @Override
    public String toString() {
        return "Parking Track ID: " + parkingID +
               ", User: " + userID.getFullName() +
               ", Spot: " + spotsID.getSpotCode() +
               ", Check In: " + checkIn +
               ", Check Out: " + checkOut +
               ", Confirm Check In: " + confirmCheckIn +
               ", Confirm Check Out: " + confirmCheckOut;
    }
}//parking track class