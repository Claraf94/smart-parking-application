package com.smartparking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service//this class is a token email service component
public class TokenEmailService {
    @Autowired
    private SetEmailService setEmailService;

    // Method to send a token email
    public void sendEmail(String to, String subject, String body) {
        setEmailService.sendEmailConfig(to, subject, body);
    }
}//token email service class
