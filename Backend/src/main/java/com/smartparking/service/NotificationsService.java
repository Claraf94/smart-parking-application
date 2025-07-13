package com.smartparking.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smartparking.entity.Notifications;
import com.smartparking.entity.Users;
import com.smartparking.enums.NotificationType;
import com.smartparking.repository.NotificationsRepository;
import com.smartparking.repository.UsersRepository;

@Service //this class is a notification service component
public class NotificationsService{
    @Autowired
    private NotificationsRepository notificationsRepository;
    @Autowired
    private UsersRepository usersRepository;
    private static final BigDecimal DEFAULT_FINE_AMOUNT = new BigDecimal("20.00");

    //create a new notification. not applied for when there is a fine
    public Notifications createNotification(Users user, NotificationType type, String message) {
        //verifying if the users exists on the database 
        if (user != null) {
            if (user.getUserID() == 0 || usersRepository.findById(user.getUserID()).isEmpty()) {
                throw new IllegalArgumentException("User does not exist on the database.");
            }
        }

        Notifications notification = new Notifications();
        notification.setUser(user);
        notification.setNotificationType(type);
        notification.setTextMessage(message);
        notification.setFine(BigDecimal.ZERO);
        notification.setIsPaid(false);
        return notificationsRepository.save(notification);
    }

    //create a new notification related to a fine application
    public Notifications createFineNotification(Users user){
        //verifying if the user exists on the database 
        if(user == null || user.getUserID() == 0 || usersRepository.findById(user.getUserID()).isEmpty()) {
            throw new IllegalArgumentException("User does not exist.");
        }

        Notifications notification = new Notifications();
        notification.setUser(user);
        notification.setNotificationType(NotificationType.FINE_APPLIED);
        notification.setTextMessage("A fine of € "+ DEFAULT_FINE_AMOUNT + " has been applied to your account due to problems following the parking rules.");
        notification.setFine(DEFAULT_FINE_AMOUNT);
        notification.setIsPaid(false);
        return notificationsRepository.save(notification);
    }

    //personalized notification for user
    public Notifications createNotificationForUser(Users user, NotificationType type){
        if(user == null || type == null || user.getUserID() == 0 || usersRepository.findById(user.getUserID()).isEmpty()) {
            throw new IllegalArgumentException("User and Notification type must not be null and user must exists.");
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

    //mark a fine as paid
    public Notifications markAsPaid(String notificationId){
        int id = Integer.parseInt(notificationId);
        if(notificationsRepository.findById(id).isEmpty()){
            throw new IllegalArgumentException("Notification not found with the provided id.");
        }

        Notifications notification = notificationsRepository.findById(id).get();
        if(notification.getFine() != null && notification.getFine().compareTo(BigDecimal.ZERO) > 0){
            notification.setIsPaid(true);
            return notificationsRepository.save(notification);
        }else{
            throw new IllegalArgumentException("You have no fine to be paid.");
        }
    }

    //returns a list of all unpaid fines to the user
    public List<Notifications> getUnpaidFines(Users user){
        if(user != null && user.getUserID() != 0 && usersRepository.findById(user.getUserID()).isPresent()){
            return notificationsRepository.findByUserAndIsPaidFalseAndFineGreaterThan(user, BigDecimal.ZERO);
        }
        throw new IllegalArgumentException("User must have a valid ID and not be empty.");
    }
}//notifications service class
