package com.smartparking.dto;

public class EmailRequest {
    private String email;

    //constructor
    public EmailRequest(String email){
        this.email = email;
    }

    //default constructor
    public EmailRequest(){}

    //getters and setters
    public String getEmail(){
        return email;
    }
    
    public void setEmail(String email){
        this.email = email;
    }

    @Override
    public String toString(){
        return "Email provided: " + email;
    }
}
