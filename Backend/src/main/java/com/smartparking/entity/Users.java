package com.smartparking.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity //indicates that this class is an entity and is mapped to a database table
@Table(name = "users") //specifies the name of the table in the database
public class Users {
    //declare variables
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userID; //unique identifier for the user
    @Column(nullable = false, unique = true) //email must be unique and cannot be null
    private String email;
    @Column(nullable = false) //password and full name cannot be null
    private String password, fullName;
    @Column(nullable = false) //user type cannot be null
    private String userType = "USER"; // default user type
    @Column(nullable = false, insertable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")//created time cannot be null
    private LocalDateTime created; //current time stamp

    //constructor
    public Users(String email, String password, String fullName, String userType) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.userType = userType;
    }

    //default constructor
    public Users(){}

    //getters and setters
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    @Override
    public String toString() {
        return "User details:\n" +
                "User ID: " + userID +
                ", Email: " + email +
                ", Full name: " + fullName +
                ", Type: " + userType +
                ", Created: " + created; 
    }
}//users class