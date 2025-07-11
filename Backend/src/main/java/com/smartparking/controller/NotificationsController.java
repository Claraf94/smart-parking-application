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
import com.smartparking.entity.Notifications;
import com.smartparking.entity.Users;
import com.smartparking.service.NotificationsService;
import com.smartparking.service.UsersService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/notifications")
//this controller will handle notifications related to parking spots, reservations, and other relevant events.
public class NotificationsController {
    @Autowired
    private NotificationsService notificationsService;
    @Autowired
    private UsersService usersService;

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

    //get a list with unpaid fines
    @GetMapping("unpaid-fines")
    public ResponseEntity<List<Notifications>> getUnpaidFines(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if(authentication.getName() == null || authentication.getName().isBlank()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<Users> userOptional = usersService.findByEmail(authentication.getName());
        if(userOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(notificationsService.getUnpaidFines(userOptional.get()));
    }

    //confirm a fine was paid 
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}/pay")
    public ResponseEntity<Notifications> payFine(@PathVariable String id) {
        try{
            return ResponseEntity.ok(notificationsService.markAsPaid(id));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


}//notifications controller class
