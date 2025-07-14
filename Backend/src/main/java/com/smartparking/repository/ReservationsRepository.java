package com.smartparking.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.smartparking.entity.Reservations;
import com.smartparking.entity.Spots;
import com.smartparking.entity.Users;
import com.smartparking.enums.ReservationStatus;

@Repository
public interface ReservationsRepository extends JpaRepository<Reservations, Integer> {
    //find a reservation by using the Users entity reference
    List<Reservations> findByUser(Users user);
    //find a reservation by using the Spots entity reference
    List<Reservations> findBySpot(Spots spot);
    //find a reservation by the reservation status
    List<Reservations> findByReservationStatus(ReservationStatus reservationStatus);
    //check if a reservation exists and it is ACTIVE
    List<Reservations> findBySpotAndReservationStatus(Spots spot, ReservationStatus reservationStatus);
    List<Reservations> findBySpotAndUserAndReservationStatus(Spots spot, Users user, ReservationStatus reservationStatus);
    //check the active reservation of the user
    Optional<Reservations> findFirstBySpotAndUserAndReservationStatusOrderByStartTimeDesc(Spots spot, Users user, ReservationStatus reservationStatus);
    //check the upcoming reservation 
    Optional<Reservations> findFirstBySpotAndReservationStatusAndStartTimeAfterOrderByStartTimeAsc(Spots spot, ReservationStatus status, LocalDateTime now);
    //check for the last reservation related to a specific user
    Optional<Reservations> findFirstByUserAndReservationStatusOrderByStartTimeDesc(Users user, ReservationStatus status);
    //check the user which the reservation is near to expire
    List<Reservations> findByStatusAndCheckOutTimeBetween(String status, LocalDateTime startTime, LocalDateTime endTime);
    //check the user which the reservation expired
    List<Reservations> findByStatusAndCheckOutTimeBefore(String status, LocalDateTime time);
}//reservations repository class