package com.smartparking.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import com.smartparking.enums.SpotStatus;

@Entity //indicates that this class is an entity and is mapped to a database table
@Table(name = "spots") //specifies the name of the table in the database
public class Spots {
     //declare variables
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int spotsID; //unique identifier for the parking spot
    @Column(name = "spotCode", nullable = false, unique = true) //spot code cannot be null
    @NotBlank(message = "Spot code cannot be blank")
    private String spotCode;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SpotStatus status = SpotStatus.EMPTY; //default status
    @Column(name = "locationDescription")
    private String locationDescription;
    @Column(name = "x", nullable = false) //coordinates cannot be null
    private int x = 0; //default x coordinate
    @Column(name = "y", nullable = false) //coordinates cannot be null
    private int y = 0; //default y coordinate
    @Column(name = "isReservable", nullable = false) //reservable status cannot be null
    private Boolean isReservable = true;
    @CreationTimestamp
    @Column(name = "created", nullable = false, updatable = false)//created time cannot be null
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

    public SpotStatus getStatus() {
        return status;
    }

    public void setStatus(SpotStatus status) {
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