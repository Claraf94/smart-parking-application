package com.smartparking.dto;

// This class represents a request to reset a user's password.
public class PasswordResetRequest {
    //declare variables
    private String tokenPassword, newPassword, confirmNewPassword, email;

    //constructor
    public PasswordResetRequest(String tokenPassword, String newPassword, String confirmNewPassword, String email){
        this.tokenPassword = tokenPassword;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
        this.email = email;
    }

    //default constructor
    public PasswordResetRequest(){}

    //getters and setters
    public String getTokenPassword() {
        return tokenPassword;
    }

    public void setTokenPassword(String tokenPassword) {
        this.tokenPassword = tokenPassword;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Password Reset Request\n" +
                "Token: " + tokenPassword + "\n" +
                ", New Password: " + newPassword + "\n" +
                ", Confirm New Password: " + confirmNewPassword + "\n" +
                ", Email: " + email;
    }
}//password reset request class
