package com.smartparking.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.smartparking.entity.Reservations;
import com.smartparking.entity.Spots;
import com.smartparking.entity.Users;

@Repository
public interface ReservationsRepository extends JpaRepository<Reservations, Integer> {
    //find a reservation by using the Users entity reference
    List<Reservations> findByUser(Users user);
    //find a reservation by using the Spots entity reference
    List<Reservations> findBySpot(Spots spot);
    //find a reservation by the reservation status
    List<Reservations> findByReservationStatus(String reservationStatus);
    //check if a reservation exists by using the Spots entity reference and the start and end times
    boolean existsBySpotAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(Spots spot, LocalDateTime startTime, LocalDateTime endTime);
}//reservations repository class