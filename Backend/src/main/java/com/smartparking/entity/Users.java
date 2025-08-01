package com.smartparking.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.smartparking.enums.UserType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity //indicates that this class is an entity and is mapped to a database table
@Table(name = "users") //specifies the name of the table in the database
public class Users {
    //declare variables
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userID; //unique identifier for the user
    @Column(name = "email", nullable = false, unique = true) //email must be unique and cannot be null
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should have a valid format")
    private String email;
    @Column(name = "password", nullable = false) //password, first and last name cannot be null
    @NotBlank(message = "Password cannot be blank")
    private String password;
    @Column(name = "firstName", nullable = false)
    @NotBlank(message = "First name cannot be blank")
    private String firstName;
    @Column(name = "lastName", nullable = false)
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;
    @NotNull(message = "User type cannot be null")
    @Column(name = "userType", nullable = false) //user type cannot be null
    @Enumerated(EnumType.STRING) //stores the enum as a string in the database
    private UserType userType = UserType.USER; // default user type
    @CreationTimestamp
    @Column(name = "created", nullable = false, updatable = false)//created time cannot be null
    private LocalDateTime created; //current time stamp
    @Column(name = "numberPlate", nullable = false, unique = true, length = 10)
    @NotBlank(message = "Number plate cannot be blank")
    private String numberPlate;

    //constructor
    public Users(String email, String password, String firstName, String lastName, UserType userType, String numberPlate) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
        this.numberPlate = numberPlate;
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

    public String getFirstName(){
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }   

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
    }

    @Override
    public String toString() {
        return "User details:\n" +
                "User ID: " + userID +
                ", Email: " + email +
                ", Full name: " + firstName + " " + lastName +
                ", Type: " + userType +
                ", Number Plate: " + numberPlate +
                ", Created: " + created; 
    }
}//users class