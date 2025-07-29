package com.smartparking.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smartparking.entity.Reservations;
import com.smartparking.entity.Users;
import com.smartparking.enums.ReservationStatus;
import com.smartparking.service.ReservationsService;
import com.smartparking.service.SpotsService;
import com.smartparking.service.UsersService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/reservations")
//this controller handles all operations related to reservations, including creating, retrieving, and deleting reservations.
public class ReservationsController {
    @Autowired
    private ReservationsService reservationsService;
    @Autowired
    private SpotsService spotsService;
    @Autowired
    private UsersService usersService;

    //create a new reservations
    @PostMapping("/create")
    public ResponseEntity<Reservations> createReservation(@RequestBody @Valid Reservations reservation) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Users user = usersService.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Authenticated user not found."));
        reservation.setUser(user);
        if(reservation.getReservationStatus() == null){
            reservation.setReservationStatus(ReservationStatus.ACTIVE);
        }
        System.out.println("Creating reservation: " + reservation);
        Reservations savedReservation = reservationsService.createReservation(reservation);
        return ResponseEntity.ok(savedReservation);
    }

    //returns reservation by user
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reservations>> getReservationsByUser(@PathVariable int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }
        Users user = new Users();
        user.setUserID(userId);
        return ResponseEntity.ok(reservationsService.findByUser(user));
    }

    @GetMapping("/user")
    public ResponseEntity<List<Reservations>> getLoggedUserReservations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Users user = usersService.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found."));
        return ResponseEntity.ok(reservationsService.findByUser(user));
    }

    //returns reservation by spot
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/status/{status}")
    public List<Reservations> getReservationsByStatus(@PathVariable String status) {
        try{
            ReservationStatus enumStatus = ReservationStatus.valueOf(status.toUpperCase());
            return reservationsService.findByReservationStatus(enumStatus);
        }catch(IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid status.");
        }
    }

    //delete a reservation by ID
    @PutMapping(value = "/cancel/{reservationId}", consumes = "application/json") 
    public ResponseEntity<Void> deleteReservation(@PathVariable int reservationId) {
        reservationsService.cancelReservation(reservationId);
        return ResponseEntity.noContent().build();
    }
}//reservations controller class
