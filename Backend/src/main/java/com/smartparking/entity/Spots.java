package com.smartparking.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity //indicates that this class is an entity and is mapped to a database table
@Table(name = "spots") //specifies the name of the table in the database
public class Spots {
     //declare variables
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int spotsID; //unique identifier for the parking spot
    @Column(nullable = false, unique = true) //spot code cannot be null
    @NotBlank(message = "Spot code cannot be blank")
    private String spotCode;
    @Column(nullable = false) //spot status cannot be null
    @NotBlank(message = "Status cannot be blank")
    private String status = "empty"; // default status 
    private String locationDescription;
    @Column(nullable = false) //coordinates cannot be null
    private int x = 0; //default x coordinate
    @Column(nullable = false) 
    private int y = 0; //default y coordinate
    @Column(nullable = false) //reservable status cannot be null
    private Boolean isReservable = true;
    @Column(nullable = false, updatable = false, insertable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")//created time cannot be null
    @NotNull(message = "Created timestamp cannot be blank")
    private LocalDateTime created; //current timestamp

    //constructor
    public Spots(String spotCode, String locationDescription, int x, int y, Boolean isReservable) {
        this.spotCode = spotCode;
        this.locationDescription = locationDescription;
        this.x = x;
        this.y = y;
        this.isReservable = isReservable;
    }

    //default constructor
    public Spots(){}
    
    //getters and setters
    public int getSpotsID() {
        return spotsID;
    }

    public void setSpotsID(int spotsID) {
        this.spotsID = spotsID;
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

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
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

    @Override
    public String toString() {
        return "Spot details:\n" +
               "Spot ID: " + spotsID +
               ", Spot Code: " + (spotCode != null ? spotCode : "Not available") +
               ", Status: " + status +
               ", Location Description: " + (locationDescription != null ? locationDescription : "Not available.") +
               ", Coordinates: (" + x + ", " + y + ")" +
               ", Reservable: " + isReservable +
               ", Created: " + created;
    }
}//spots class