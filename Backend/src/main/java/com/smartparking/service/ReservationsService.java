package com.smartparking.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smartparking.entity.Reservations;
import com.smartparking.entity.Spots;
import com.smartparking.entity.Users;
import com.smartparking.enums.NotificationType;
import com.smartparking.enums.ReservationStatus;
import com.smartparking.repository.ReservationsRepository;


@Service //this class is a reservation service component
public class ReservationsService {
    @Autowired
    private ReservationsRepository reservationsRepository;
    @Autowired
    private SpotsService spotsService;
    @Autowired
    private NotificationsService notificationsService;

    //verify if a reservation exists before creating it by using the Spots entity reference and the start and end times
    public Reservations createReservation(Reservations reservation) {
        if(reservation.getStartTime() == null){
            throw new IllegalArgumentException("Start time is required. Please define it.");
        }
        //check if the spot exists first
        Spots spot = reservation.getSpot();
        if (spot == null || spot.getSpotCode() == null || spot.getSpotCode().isEmpty()) {
            throw new IllegalArgumentException("Spot and its code are required for reservation and cannot be empty.");
        }
        var existingSpot = spotsService.findBySpotCode(spot.getSpotCode());
        if(existingSpot.isEmpty()){
            throw new IllegalArgumentException("Invalid spot code.");
        }
        LocalDateTime newStartTime = reservation.getStartTime();
        LocalDateTime endTime = newStartTime.plusHours(4); //each reservation has a duration of 4 hours as a default
        //verifies if there is an active reservation at the spot that conflicts with a new one
        List<Reservations> activeReservations = reservationsRepository.findBySpotAndReservationStatus(existingSpot.get(), ReservationStatus.ACTIVE);
        Reservations nextRes = null;
        for (Reservations res : activeReservations) {
            LocalDateTime resStart = res.getStartTime();
            LocalDateTime resEnd = resStart.plusHours(4);
            //checking if overlaps with an exiting one
            boolean conflicts = !endTime.isBefore(resStart) && !newStartTime.isAfter(resEnd);
            if(conflicts){
                if(resStart.isAfter(newStartTime)){
                    if(nextRes == null || resStart.isBefore(nextRes.getStartTime())){
                        nextRes = res;
                    }
                }else{
                    throw new IllegalArgumentException("The reservation could not be completed because the time conflicts with an existing one.");

                }
            }
        }
        //shorten the duration in case another one is scheduled soon after
        if(nextRes != null){
            LocalDateTime nextStarTime = nextRes.getStartTime();
            if(endTime.isAfter(nextStarTime)){
                endTime = nextStarTime;
                long durationMin = Duration.between(newStartTime, endTime).toMinutes();
                //if the duration is less than 45 minutes, the system will not allow the reservation
                if(durationMin < 45){
                    throw new IllegalArgumentException("The reservation could not be completed because there is no enough time before the next one.");
                }
                System.out.println("NOTE: The spot must be released before the next reservation time at " + nextStarTime);
            }
        }
        //if the reservation does not conflict, save it
        reservation.setEndTime(endTime);
        reservation.setSpot(existingSpot.get());
        reservation.setReservationStatus(ReservationStatus.ACTIVE);
        Reservations saveReservation = reservationsRepository.save(reservation);
        Users user = saveReservation.getUser();
        if(user != null){
            notificationsService.createNotificationForUser(user, NotificationType.SPOT_RESERVED, "Reservation successfully created.");
        }
        return saveReservation;
    }   

    public Reservations save(Reservations reservation) {
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

    public List<Reservations> findByReservationStatus(ReservationStatus status) {
        //find by reservation status
        return reservationsRepository.findByReservationStatus(status);
    }

    public void cancelReservation(int reservationID) {
        if(!reservationsRepository.existsById(reservationID)){
            throw new IllegalArgumentException("Reservation not found.");
        }
        //cancel a reservation by ID
        Reservations res = reservationsRepository.findById(reservationID).get();
        res.setReservationStatus(ReservationStatus.CANCELLED);
        reservationsRepository.save(res);

        Users user = res.getUser();
        if(user != null){
            notificationsService.createNotificationForUser(user, NotificationType.RESERVATION_CANCELLED, "The reservation was cancelled succesfully.");
        }
    }
}//reservations service class