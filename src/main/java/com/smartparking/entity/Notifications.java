package com.smartparking.entity;

import java.time.LocalDateTime;
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
    private BigDecimal fine;
    @Column(nullable = false) //created time cannot be null
    private LocalDateTime created; //current time stamp

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

    public BigDecimal getFine() {
        return fine;
    }

    public void setFine(BigDecimal fine) {
        this.fine = fine;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @PrePersist //this method is called before the entity is saved to the database
    protected void onCreate() {
        this.created = LocalDateTime.now(); //set the created time to the current time
    }

    @Override
    public String toString() {
        return "Notification ID: " + notificationID +
               ", User: " + user.getFullName() +
               ", Message: " + textMessage +
               ", Fine: " + fine +
               ", Created: " + created;
    }
}//notifications class