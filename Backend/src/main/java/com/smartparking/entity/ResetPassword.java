package com.smartparking.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity//indicates that this class is an entity and is mapped to a database table
@Table(name = "resetPassword")//specifies the name of the table in the database
public class ResetPassword {
    //declare variables
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tokenID; //unique identifier for the reset password request
    @ManyToOne //indicates a many-to-one relationship with the Users entity
    @JoinColumn(name = "userID", nullable = false) // userID cannot be null
    //instead of using userID, it is better to use the reference object to the users entity
    private Users user; //reference to the Users entity, allowing for better encapsulation and management of relationships in the database
    @Column(name = "token", nullable = false, unique = true) //token cannot be null and must be unique
    @NotNull(message = "Token cannot be null")
    private String token;
    @Column(name = "expirationTime", nullable = false)//expiration time cannot be null
    @NotNull(message = "Expiration time cannot be null")
    private LocalDateTime expirationTime; //expiration time for the reset password request
    @CreationTimestamp
    @Column(name = "created", nullable = false, updatable = false)//created time cannot be null
    private LocalDateTime created; //current time stamp

    //constructor
    public ResetPassword(Users user, String token, LocalDateTime expirationTime) {
        this.user = user;
        this.token = token;
        this.expirationTime = expirationTime;
    }

    //default constructor
    public ResetPassword() {}

    //getters and setters
    public int getTokenID() {
        return tokenID;
    }

    public void setTokenID(int tokenID) {
        this.tokenID = tokenID;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    @Override
    public String toString() {
        return "Password reset information:\n" +
                "TokenID:" + tokenID +
                ", User: " + (user != null ? user.getEmail() : "Not available") +
                ", Token: " + token +
                ", Expiration Time: " + expirationTime +
                ", created " + created;
    }
}//reset password class
