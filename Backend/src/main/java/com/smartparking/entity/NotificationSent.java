package com.smartparking.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.smartparking.enums.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "notificationSent", uniqueConstraints = @UniqueConstraint(columnNames = {"reservationID", "notificationType"})) // specifies the name of the table in the database
public class NotificationSent {
    //declare variables
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sentNotificationID; // unique identifier for the notification sent
    @ManyToOne // indicates a many-to-one relationship with the Reservations entity
    @JoinColumn(name = "reservationID", nullable = false) // reservationID cannot be null
    private Reservations reservation; // the reservation for which the notification was sent
    @Enumerated(EnumType.STRING) // specifies that the enum will be stored as a string in the database
    @Column(name = "notificationType", nullable = false) // notificationType cannot be null
    private NotificationType notificationType; // type of notification sent
    @CreationTimestamp
    @Column(name = "sentAt", updatable = false)
    private LocalDateTime sentAt; // timestamp when the notification was sent

    //constructor
    public NotificationSent(Reservations reservation, NotificationType notificationType) {
        this.reservation = reservation;
        this.notificationType = notificationType;
    }

    //default constructor
    public NotificationSent() {}

    //getters and setters
    public int getSentNotificationID() {
        return sentNotificationID;
    }

    public void setSentNotificationID(int sentNotificationID) {
        this.sentNotificationID = sentNotificationID;
    }

    public Reservations getReservation() {
        return reservation;
    }

    public void setReservation(Reservations reservation) {
        this.reservation = reservation;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    @Override
    public String toString() {
        return "Notification details:\n" +
                "Id: " + sentNotificationID +
                ", Reservation: " + reservation +
                ", Type: " + notificationType +
                ", Sent at: " + sentAt;
    }
}
