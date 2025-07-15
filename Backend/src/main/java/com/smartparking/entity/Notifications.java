package com.smartparking.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.smartparking.enums.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity //indicates that this class is an entity and is mapped to a database table
@Table(name = "notifications") //specifies the name of the table in the database
public class Notifications { 
    //declare variables
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int notificationID; //unique identifier for the notification
    @ManyToOne //indicates a many-to-one relationship with the Users entity
    @JoinColumn(name = "userID", nullable = true)
    //instead of using userID, it is better to use the reference object to the users entity
    //allowing for better encapsulation and better management of relationships in the database
    private Users user;
    @ManyToOne // indicates a many-to-one relationship with the Reservations entity
    @JoinColumn(name = "reservationID", nullable = false) // reservationID cannot be null
    private Reservations reservation;
    @Column(name = "textMessage", nullable = false) //text message cannot be null
    @NotBlank(message = "Text message cannot be blank")
    private String textMessage;
    @Enumerated(EnumType.STRING) //specifies that the enum will be stored as a string in the database
    @Column(name = "notificationType", nullable = false) //notification type cannot be null
    @NotNull(message = "Notification type cannot be null")
    private NotificationType notificationType; //type of notification, e.g., SPOT_NOT_AVAILABLE, SPOT_RESERVED_VIOLATION, etc.
    @Column(name = "fine", nullable = false) //fine cannot be null
    @NotNull(message = "Fine cannot be null")
    private BigDecimal fine = BigDecimal.ZERO; //fine associated with the notification, default is zero
    @Column(name = "isPaid", nullable = true)
    private Boolean isPaid;
    @CreationTimestamp
    @Column(name = "created", nullable = false, updatable = false) //created time cannot be null
    private LocalDateTime created; //current time stamp

    //constructor
    public Notifications(Users user, Reservations reservation, String textMessage, NotificationType notificationType, BigDecimal fine, Boolean isPaid) {
        this.user = user;
        this.reservation = reservation;
        this.textMessage = textMessage;
        this.notificationType = notificationType;
        this.fine = fine;
        this.isPaid = isPaid;
    }

    //default constructor
    public Notifications(){}

    //getters and setters
    public int getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Reservations getReservation() {
        return reservation;
    }
    
    public void setReservation(Reservations reservation) {
        this.reservation = reservation;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public BigDecimal getFine() {
        return fine;
    }

    public void setFine(BigDecimal fine) {
        this.fine = fine;
    }

    public Boolean getIsPaid(){
        return isPaid;
    }

    public void setIsPaid(Boolean isPaid){
        this.isPaid = isPaid;
    }
    public LocalDateTime getCreated() {
        return created;
    }

    @Override
    public String toString() {
        return "Notification ID: " + notificationID +
               ", User: " + (user != null ? user.getFirstName() + " " + user.getLastName() : "Not available.") +
               ", Message: " + textMessage +
               ", Reservation ID: " + (reservation != null ? reservation.getReservationID() : "Not available.") +
               ", Type: " + notificationType +
               ", Fine: " + fine +
               ", Paid: " + isPaid +
               ", Created: " + created;
    }
}//notifications class