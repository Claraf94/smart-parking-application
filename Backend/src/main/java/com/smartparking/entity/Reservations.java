package com.smartparking.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import com.smartparking.enums.ReservationStatus;

@Entity // indicates that this class is an entity and is mapped to a database table
@Table(name = "reservations") // specifies the name of the table in the database
public class Reservations {
    // declare variables
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reservationID; // unique identifier for the reservation
    @ManyToOne // indicates a many-to-one relationship with the user entity
    @JoinColumn(name = "userID", nullable = false) // userID cannot be null
    // instead of using userID, it is better to use the reference object to the
    // users entity
    // allowing for better encapsulation and better management of relationships in
    // the database
    private Users user;
    @ManyToOne // indicates a many-to-one relationship with the spots entity
    @JoinColumn(name = "spotsID", nullable = false) // spotsID cannot be null
    @NotNull(message = "Spot cannot be null")
    // instead of using SpotID, it is better to use the reference object to the
    // Spots entity
    // allowing for better encapsulation and better management of relationships in
    // the database
    private Spots spot;
    @Column(name = "phoneNumber", nullable = false) // phone number and number plate cannot be null
    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^\\+?353\\s?([1-9]\\d)\\s?\\d{6,7}$", message = "Invalid phone number format.") // Irish phone number format
    private String phoneNumber;
    @Column(name = "numberPlate", nullable = false)
    @NotBlank(message = "Number plate cannot be blank")
    @Pattern(regexp = "^\\d{2,3}-[A-Z]{1,2}-\\d{1,6}$", message = "Invalid number plate format.") // Ireland number                                                                     // plate format
    private String numberPlate;
    @Column(name = "startTime", nullable = false) // start, end and reservedAt times cannot be null
    @NotNull(message = "Start time cannot be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;
    @Column(name = "endTime", nullable = false)
    private LocalDateTime endTime;
    @CreationTimestamp
    @Column(name = "reservedAt", nullable = false, updatable = false)
    private LocalDateTime reservedAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "reservationStatus", nullable = false) // reservation status cannot be null
    @NotNull(message = "Reservation status cannot be null")
    private ReservationStatus reservationStatus = ReservationStatus.ACTIVE; // default reservation status

    // constructor
    public Reservations(int reservationID, Users user, Spots spot, String phoneNumber, String numberPlate,
            LocalDateTime startTime, LocalDateTime endTime, ReservationStatus reservationStatus) {
        this.reservationID = reservationID;
        this.user = user;
        this.spot = spot;
        this.phoneNumber = phoneNumber;
        this.numberPlate = numberPlate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reservationStatus = reservationStatus;
    }

    // default constructor
    public Reservations() {
    }

    // getters and setters
    public int getReservationID() {
        return reservationID;
    }

    public void setReservationID(int reservationID) {
        this.reservationID = reservationID;
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

    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    @Override
    public String toString() {
        return "Reservation details:\n" +
                "Reservation ID: " + reservationID +
                ", User ID: " + (user != null ? user.getUserID() : "Not available.") +
                ", Spot ID: " + (spot != null ? spot.getSpotsID() : "Not available.") +
                ", Phone Number: " + phoneNumber +
                ", Number Plate: " + numberPlate +
                ", Start Time: " + startTime +
                ", End Time: " + endTime +
                ", Reserved at: " + reservedAt +
                ", Status: " + reservationStatus;
    }
}// reservation class