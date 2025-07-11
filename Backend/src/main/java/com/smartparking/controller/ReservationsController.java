package com.smartparking.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smartparking.entity.Reservations;
import com.smartparking.entity.Users;
import com.smartparking.exceptions.PlatformExceptions.ReservationConflictException;
import com.smartparking.service.ReservationsService;
import com.smartparking.service.SpotsService;

@RestController
@RequestMapping("/reservations")
//this controller handles all operations related to reservations, including creating, retrieving, and deleting reservations.
public class ReservationsController {
    @Autowired
    private ReservationsService reservationsService;
    @Autowired
    private SpotsService spotsService;

    //create a new reservations
    @PostMapping("/create")
    public ResponseEntity<Reservations> createReservation(@RequestBody Reservations reservation) {
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(reservationsService.createReservation(reservation));
        }catch (IllegalArgumentException | ReservationConflictException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    //returns reservation by user
    @GetMapping("/user/{userId}")
    public List<Reservations> getReservationsByUser(@PathVariable int userId) {
        if(userId<=0){
            throw new IllegalArgumentException("Invalid user ID.");
        }
        Users user = new Users();
        user.setUserID(userId); 
        return reservationsService.findByUser(user);
    }

    //returns reservation by spot
    @GetMapping("/spot/{spotId}")
    public List<Reservations> getReservationsBySpot(@PathVariable int spotId) {
        if(spotId <=0){
            throw new IllegalArgumentException("Invalid spot ID.");
        }
        if(spotsService.findById(spotId).isEmpty()){
            throw new IllegalArgumentException("Spot not found.");
        }
        return reservationsService.findBySpot(spotsService.findById(spotId).get());
    }

    //returns reservation by status
    @GetMapping("/status/{status}")
    public List<Reservations> getReservationsByStatus(@PathVariable String status) {
        return reservationsService.findByReservationStatus(status);
    }

    //delete a reservation by ID
    @DeleteMapping("/delete/{reservationId}")
    //response body will be void/empty 
    public ResponseEntity<Void> deleteReservation(@PathVariable int reservationId) {
        reservationsService.deleteReservation(reservationId);
        return ResponseEntity.noContent().build();
    }
}//reservations controller class
