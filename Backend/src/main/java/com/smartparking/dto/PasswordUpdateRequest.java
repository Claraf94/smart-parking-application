package com.smartparking.dto;

// This class represents a request to update a user's password.
public class PasswordUpdateRequest {
    //declare variables
    private String currentPassword;
    private String newPassword;

    //constructor
    public PasswordUpdateRequest(String currentPassword, String newPassword){
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    //default constructor
    public PasswordUpdateRequest(){}

    //getters and setters
    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public String toString() {
        return "Password Update Request\n" +
                "Current Password: " + currentPassword + "\n" +
                ", New Password: " + newPassword + "\n";
    }
}//password update request class
