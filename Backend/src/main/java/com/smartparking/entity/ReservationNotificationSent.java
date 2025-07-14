package com.smartparking.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity
public class ReservationNotificationSent {
    //declare variables
    @Id
    @GeneratedValue(strategy = GeneratedType.IDENTITY)
    private int sentNotificationId; // unique identifier for the notification sent
    @ManyToOne // indicates a many-to-one relationship with the Reservations entity
    private Reservations reservation; // the reservation for which the notification was sent
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType; // type of notification sent
    private LocalDateTime sentAt; // timestamp when the notification was sent

    //getters and setters
    public int getSentNotificationId() {
        return sentNotificationId;
    }

    public void setSentNotificationId(int sentNotificationId) {
        this.sentNotificationId = sentNotificationId;
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
}
