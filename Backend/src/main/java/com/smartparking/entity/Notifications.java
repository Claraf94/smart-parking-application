package com.smartparking.entity;

import java.time.LocalDateTime;
import com.smartparking.enums.NotificationType;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity //indicates that this class is an entity and is mapped to a database table
@Table(name = "notifications") //specifies the name of the table in the database
public class Notifications {
    //declare variables
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int notificationID; //unique identifier for the notification
    @ManyToOne //indicates a many-to-one relationship with the Users entity
    @JoinColumn(name = "userID", nullable = false)
    //instead of using userID, it is better to use the reference object to the users entity
    //allowing for better encapsulation and better management of relationships in the database
    private Users user;
    @Column(nullable = false) //text message cannot be null
    private String textMessage;
    @Enumerated(EnumType.STRING) //specifies that the enum will be stored as a string in the database
    @Column(nullable = false) 
    private NotificationType notificationType; //type of notification, e.g., SPOT_NOT_AVAILABLE, SPOT_RESERVED_VIOLATION, etc.
    @Column(nullable = false) //fine cannot be null
    private BigDecimal fine = BigDecimal.ZERO; //fine associated with the notification, default is zero
    @Column(nullable = false, updatable = false, insertable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP") //created time cannot be null
    private LocalDateTime created; //current time stamp


    //constructor
    public Notifications(Users user, String textMessage, NotificationType notificationType, BigDecimal fine) {
        this.user = user;
        this.textMessage = textMessage;
        this.notificationType = notificationType;
        this.fine = fine;
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

    public LocalDateTime getCreated() {
        return created;
    }

    @Override
    public String toString() {
        return "Notification ID: " + notificationID +
               ", User: " + (user != null ? user.getFullName() : "Not available.") +
               ", Message: " + textMessage +
                ", Type: " + notificationType +
               ", Fine: " + fine +
               ", Created: " + created;
    }
}//notifications class