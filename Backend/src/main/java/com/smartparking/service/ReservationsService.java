package com.smartparking.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smartparking.entity.Reservations;
import com.smartparking.entity.Spots;
import com.smartparking.entity.Users;
import com.smartparking.exceptions.PlatformExceptions.ReservationConflictException;
import com.smartparking.repository.ReservationsRepository;


@Service //this class is a reservation service component
public class ReservationsService {
    @Autowired
    private ReservationsRepository reservationsRepository;
    //verify if a reservation exists by using the Spots entity reference and the start and end times
    public Reservations saveReservation(Reservations reservation) {
        boolean exists = reservationsRepository.existsBySpotAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            reservation.getSpot(), 
            reservation.getStartTime(), 
            reservation.getEndTime()
        );

        if(exists) {
            throw new ReservationConflictException("This reservation conflicts with an existing reservation for the same spot and period.");
        }
        //if the reservation does not conflict, save it
        return reservationsRepository.save(reservation);
    }   

    public List<Reservations> findByUser(Users user) {
        //find by user
        return reservationsRepository.findByUser(user);
    }

    public List<Reservations> findBySpot(Spots spot) {
        //find by spot
        return reservationsRepository.findBySpot(spot);
    }

    public List<Reservations> findByReservationStatus(String status) {
        //find by reservation status
        return reservationsRepository.findByReservationStatus(status);
    }

    public void deleteReservation(int reservationID) {
        //delete a reservation by ID
        reservationsRepository.deleteById(reservationID);
    }
}//reservations service class