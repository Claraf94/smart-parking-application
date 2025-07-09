package com.smartparking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smartparking.entity.Notifications;
import com.smartparking.entity.Users;
import com.smartparking.service.NotificationsService;

@RestController
@RequestMapping("/notifications")
//this controller will handle notifications related to parking spots, reservations, and other relevant events.
public class NotificationsController {
    @Autowired
    private NotificationsService notificationsService;

    //creating a notification
    @PostMapping("/create")
    public Notifications createNotification(@RequestBody Notifications notification) {
        Users user = new Users();
        //setting the user ID for the notification
        //this is necessary to link the notification to the user who will receive it
        user.setUserID(notification.getUser().getUserID());
        return notificationsService.createNotification(user, notification.getNotificationType(), notification.getTextMessage());
    }
}//notifications controller class
