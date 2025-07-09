package com.smartparking.service;

import com.smartparking.entity.Notifications;
import com.smartparking.entity.Users;
import com.smartparking.enums.NotificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smartparking.repository.NotificationsRepository;

@Service //this class is a notification service component
public class NotificationsService{
    @Autowired
    private NotificationsRepository notificationsRepository;
    //create a new notification
    public Notifications createNotification(Users user, NotificationType type, String message) {
        Notifications notification = new Notifications();
        notification.setUser(user);
        notification.setNotificationType(type);
        notification.setTextMessage(message);
        return notificationsRepository.save(notification);
    }

    //personalized notification for user
    public Notifications createNotificationForUser(Users user, NotificationType type){
        if(user == null || type == null) {
            throw new IllegalArgumentException("User and NotificationType must not be null");
        }
        
        String message = "";
        switch (type) {
            case SPOT_NOT_AVAILABLE:
                message = "This spot is not available.";
                break;
            case SPOT_RESERVED_VIOLATION:
                message = "You occupied a reserved spot without permission.";
                break;
            case SPOT_RESERVED:
                message = "Your have successfully reserved a parking spot.";
                break;
            case RESERVATION_EXPIRED:
                message = "Your time to occupy the reserved spot has expired.";
                break;  
            case RESERVATION_CANCELLED:
                message = "Your reservation has been cancelled.";
                break;
            case RESERVATION_NOT_POSSIBLE:
                message = "Your reservation is not possible at this time.";             
                break;
            case UNAUTHORIZED_CHECKIN:
                message = "You are not authorized to check in at this spot.";
                break;
            case FINE_APPLIED:
                message = "A fine has been applied to your account.";       
                break;
            case GENERAL_INFORMATION:
                message = "You have a new notification.";
                break;
            default:
                message = "You have a new notification.";
                break;
        }
        return createNotification(user, type, message);
    }
}//notifications service class
