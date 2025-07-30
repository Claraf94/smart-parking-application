package com.smartparking.dto;

public class UserDTO {
    private int userID;
    private String firstName;
    private String lastName;
    private String registeredAt;
    private String email;
    private String userType;
    private String created;

    // Constructor
    public UserDTO(int userID, String firstName, String lastName, String email,
            String userType, String created, boolean hasUnpaidFine) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userType = userType;
        this.created = created;
    }

    // Default constructor
    public UserDTO() {
    }

    // Getters e Setters
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getFirstName() {
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

    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(String registeredAt) {
        this.registeredAt = registeredAt;
    }

    @Override
    public String toString() {
        return "User ID: " + userID + "\n" +
                ", Full Name: " + getFullName() + "\n" +
                ", Email: " + email + "\n" +
                ", User Type: " + userType + "\n" +
                ", Created: " + created + "\n";

    }
}// userDTO class