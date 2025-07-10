package com.smartparking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service // This class is a service component for sending emails and it uses JavaMailSender for that
public class SetEmailService {
    @Autowired
    private JavaMailSender emailSender;

    // Method to set the email sender
    public void sendEmailConfig(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        
        // Send the email using the JavaMailSender
        emailSender.send(message);
    }
}//set email service class
