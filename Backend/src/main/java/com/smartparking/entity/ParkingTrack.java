package com.smartparking.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity //indicates that this class is an entity and is mapped to a database table
@Table(name = "parkingTrack") //specifies the name of the table in the database
public class ParkingTrack {
    //declare variables
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int parkingID; //unique identifier for the parking track
    @ManyToOne //indicates a many-to-one relationship with the Users entity
    @JoinColumn(name = "userID", nullable = false) //userID cannot be null
    @NotNull(message = "User cannot be null")
    //instead of using userID, it is better to use the reference object to the Users entity
    //allowing for better encapsulation and better management of relationships in the database
    private Users user;
    @ManyToOne //indicates a many-to-one relationship with the Spots entity
    @JoinColumn(name = "spotsID", nullable = false) //spotsID cannot be null
    @NotNull(message = "Spot cannot be null")
    //instead of using userID, it is better to use the reference object to the Spots entity
    //allowing for better encapsulation and better management of relationships in the database
    private Spots spot; 
    @Column(name = "checkIn", nullable = true)
    private LocalDateTime checkIn;
    @Column(name = "checkOut", nullable = true)
    private LocalDateTime checkOut;
    @Column(name = "confirmCheckIn", nullable = false) //confirm check in and confirm check out cannot be null
    private boolean confirmCheckIn = false; // default confirm check in status
    @Column(name = "confirmCheckOut", nullable = false) 
    private boolean confirmCheckOut = false; // default confirm check out status
    @ManyToOne
    @JoinColumn(name = "reservationID", nullable = true)
    private Reservations reservation; //optional relationship with Reservations entity

    //constructor
    public ParkingTrack(Users user, Spots spot, LocalDateTime checkIn, LocalDateTime checkOut, boolean confirmCheckIn, boolean confirmCheckOut, Reservations reservation) {
        this.user = user;
        this.spot = spot;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.confirmCheckIn = confirmCheckIn;
        this.confirmCheckOut = confirmCheckOut;
        this.reservation = reservation;
    }
    //constructor without reservation
    public ParkingTrack(Users user, Spots spot, LocalDateTime checkIn, LocalDateTime checkOut, boolean confirmCheckIn, boolean confirmCheckOut) {
        this.user = user;
        this.spot = spot;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.confirmCheckIn = confirmCheckIn;
        this.confirmCheckOut = confirmCheckOut;
    }

    //basic constructor
    public ParkingTrack(Users user, Spots spot){
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
    public void setCheckIn(LocalDateTime checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDateTime getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDateTime checkOut){
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

    public Reservations getReservation() {
        return reservation;
    }

    public void setReservation(Reservations reservation) {
        this.reservation = reservation;
    }

   @Override
    public String toString() {
        return "Parking Track ID: " + parkingID +
            ", User: " + (user != null ? user.getFirstName() + " " + user.getLastName() : "Not available.") +
            ", Spot: " + spot.getSpotsID() +
            ", Check In: " + checkIn +
            ", Check Out: " + checkOut +
            ", Confirm Check In: " + confirmCheckIn +
            ", Confirm Check Out: " + confirmCheckOut +
            ", Reservation ID: " + (reservation != null ? reservation.getReservationID() : "No reservation associated.");
    }
}//parking track class