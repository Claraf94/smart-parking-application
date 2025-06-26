package com.smartparking.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity //indicates that this class is an entity and is mapped to a database table
@Table(name = "spots") //specifies the name of the table in the database
public class Spots {
     //declare variables
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int spotID; //unique identifier for the parking spot
    @Column(nullable = false) //spot code cannot be null
    private String spotCode;
    @Column(nullable = false) //spot status cannot be null
    private String status = "empty"; // default status 
    private String locationDescription;
    @Column(nullable = false) //reservable status cannot be null
    private Boolean isReservable = true;
    @Column(nullable = false) //created time cannot be null
    private LocalDateTime created; //current time stamp

    //getters and setters
    public int getSpotID() {
        return spotID;
    }

    public void setSpotID(int spotID) {
        this.spotID = spotID;
    }

    public String getSpotCode() {
        return spotCode;
    }

    public void setSpotCode(String spotCode) {
        this.spotCode = spotCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public Boolean getIsReservable() {
        return isReservable;
    }

    public void setIsReservable(Boolean isReservable) {
        this.isReservable = isReservable;
    }

    public LocalDateTime getCreated() {
        return created;
    }   

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @PrePersist //this method is called before the entity is saved to the database
    protected void onCreate() {
        this.created = LocalDateTime.now(); // set the created time to the current time
    }

    @Override
    public String toString() {
        return "Spot details:\n" +
               "Spot ID: " + spotID +
               ", Spot Code: " + spotCode +
               ", Status: " + status +
               ", Location Description: " + locationDescription +
               ", Reservable: " + isReservable +
               ", Created: " + created;
    }
}//spots class