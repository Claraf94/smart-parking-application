package com.smartparking.dto;


public class GeneralNotifications {
    //declare variables
    private String message;
    private String subject;
    
    // Constructor
    public GeneralNotifications(String message, String subject) {
        this.message = message;
        this.subject = subject;
    }

    // Default constructor
    public GeneralNotifications() {}
    
    // Getters and Setters
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    @Override
    public String toString() {
        return "Subject: " + subject + "\n"
             + "Message: " + message; 
    }
}
