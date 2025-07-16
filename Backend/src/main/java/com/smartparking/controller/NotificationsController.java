package com.smartparking.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.smartparking.dto.GeneralNotifications;
import com.smartparking.entity.Notifications;
import com.smartparking.entity.Reservations;
import com.smartparking.entity.Users;
import com.smartparking.service.NotificationsService;
import com.smartparking.service.UsersService;


@RestController
@RequestMapping("/notifications")
//this controller will handle notifications related to parking spots, reservations, and other relevant events.
public class NotificationsController {
    @Autowired
    private NotificationsService notificationsService;
    @Autowired
    private UsersService usersService;

    //creating a notification
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Notifications> createNotification(@RequestBody Notifications notification) {
        try{
            Users user = null;
            if(notification.getUser() != null && notification.getUser().getUserID() != 0) {
                Optional<Users> registeredUser = usersService.findById(notification.getUser().getUserID());
               if(registeredUser.isEmpty()) {
                   return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
               }
                user = registeredUser.get();
            }
            Notifications createdNotifications = notificationsService.createNotification(user, notification.getNotificationType(), notification.getTextMessage());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdNotifications);
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }

    //creating a notification associated with a reservation
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create/reservation-notification")
    public ResponseEntity<Notifications> createNotificationForReservation(@RequestBody Notifications notification) {
        try{
            int userID = notification.getUser().getUserID();
            int reservationID = notification.getReservation().getReservationID();
            Optional<Users> user = usersService.findById(userID);
            Optional<Reservations> reservation = notificationsService.findReservationById(reservationID);
            if(user.isEmpty() || reservation.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Notifications createdNotification = notificationsService.createNotificationForUser(user.get(), notification.getNotificationType(), notification.getTextMessage(), reservation.get());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdNotification);
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }

    //get a list with unpaid fines
    @GetMapping("/unpaid-fines")
    public ResponseEntity<List<Notifications>> getUnpaidFines(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<Users> user = usersService.findByEmail(authentication.getName());
        if(user.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(notificationsService.getUnpaidFines(user.get()));
    }

    //confirm a fine was paid 
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}/pay")
    public ResponseEntity<Notifications> payFine(@PathVariable String id) {
        try{
            return ResponseEntity.ok(notificationsService.markAsPaid(id));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    //sending a general notification to all users
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/general")
    public ResponseEntity<Void> sendGeneralNotification(@RequestBody GeneralNotifications request) {
        if(request.getMessage() == null || request.getMessage().isBlank() || request.getSubject() == null || request.getSubject().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        notificationsService.sendNotificationToAllUsers(request.getMessage(), request.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}//notifications controller class
