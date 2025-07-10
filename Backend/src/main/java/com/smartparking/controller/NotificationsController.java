package com.smartparking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Notifications> createNotification(@RequestBody Notifications notification) {
        if(notification.getUser() != null && notification.getUser().getUserID() != 0) {
            //if the user is not null and the user ID is not 0, then the notification can be created
            //ensuring that the user is valid before creating a notification
            Users user = new Users();
            //setting the user ID for the notification
            //this is necessary to link the notification to the user who will receive it
            user.setUserID(notification.getUser().getUserID());
            return ResponseEntity.status(HttpStatus.CREATED).body(notificationsService.createNotification(user, notification.getNotificationType(), notification.getTextMessage()));
        }else{
            return ResponseEntity.badRequest().build();
        }
    }
}//notifications controller class
