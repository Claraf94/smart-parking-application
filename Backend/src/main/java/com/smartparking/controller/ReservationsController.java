package com.smartparking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smartparking.entity.Reservations;
import com.smartparking.entity.Spots;
import com.smartparking.entity.Users;
import com.smartparking.service.ReservationsService;

@RestController
@RequestMapping("/reservations")
//this controller handles all operations related to reservations, including creating, retrieving, and deleting reservations.
public class ReservationsController {
    @Autowired
    private ReservationsService reservationsService;

    //create a new reservations
    @PostMapping("/create")
    public Reservations createReservation(@RequestBody Reservations reservation) {
        return reservationsService.createReservation(reservation);
    }

    //returns reservation by user
    @GetMapping("/user/{userId}")
    public List<Reservations> getReservationsByUser(@PathVariable int userId) {
        Users user = new Users();
        user.setUserID(userId); 
        return reservationsService.findByUser(user);
    }

    //returns reservation by spot
    @GetMapping("/spot/{spotId}")
    public List<Reservations> getReservationsBySpot(@PathVariable int spotId) {
        Spots spot = new Spots();
        spot.setSpotsID(spotId);
        return reservationsService.findBySpot(spot);
    }

    //returns reservation by status
    @GetMapping("/status/{status}")
    public List<Reservations> getReservationsByStatus(@PathVariable String status) {
        return reservationsService.findByReservationStatus(status);
    }

    //delete a reservation by ID
    @DeleteMapping("/delete/{reservationId}")
    public void deleteReservation(@PathVariable int reservationId) {
        reservationsService.deleteReservation(reservationId);
    }

}//reservations controller class
