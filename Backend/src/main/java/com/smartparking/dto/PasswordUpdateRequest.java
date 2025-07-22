package com.smartparking.dto;

// This class represents a request to update a user's password.
public class PasswordUpdateRequest {
    //declare variables
    private String currentPassword, newPassword, confirmNewPassword;

    //constructor
    public PasswordUpdateRequest(String currentPassword, String newPassword, String confirmNewPassword){
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
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

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }

    @Override
    public String toString() {
        return "Password Update Request\n" +
                "Current Password: " + currentPassword + "\n" +
                ", New Password: " + newPassword + "\n" +
                ", Confirm New Password: " + confirmNewPassword + "\n";
    }
}//password update request class
