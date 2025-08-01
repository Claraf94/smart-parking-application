package com.smartparking.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smartparking.entity.ParkingTrack;
import com.smartparking.entity.Reservations;
import com.smartparking.entity.Spots;
import com.smartparking.entity.Users;
import com.smartparking.enums.NotificationType;
import com.smartparking.enums.ReservationStatus;
import com.smartparking.enums.SpotStatus;
import com.smartparking.repository.ParkingTrackRepository;
import com.smartparking.repository.ReservationsRepository;

@Service // this class is a reservation service component
public class ReservationsService {

    private final ParkingTrackRepository parkingTrackRepository;
    @Autowired
    private ReservationsRepository reservationsRepository;
    @Autowired
    private SpotsService spotsService;
    @Autowired
    private NotificationsService notificationsService;

    ReservationsService(ParkingTrackRepository parkingTrackRepository) {
        this.parkingTrackRepository = parkingTrackRepository;
    }

    // verify if a reservation exists before creating it by using the Spots entity
    // reference and the start and end times
    public Reservations createReservation(Reservations reservation) {
        if (reservation.getStartTime() == null) {
            throw new IllegalArgumentException("Start time is required. Please define it.");
        }
        if (reservation.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reservation time must be in the future.");
        }
        // check if the spot exists first
        Spots spot = reservation.getSpot();
        if (spot == null || spot.getSpotCode() == null || spot.getSpotCode().isEmpty()) {
            throw new IllegalArgumentException("Spot and its code are required for reservation and cannot be empty.");
        }
        var existingSpot = spotsService.findBySpotCode(spot.getSpotCode());
        if (existingSpot.isEmpty()) {
            throw new IllegalArgumentException("Invalid spot code.");
        }
        LocalDateTime newStartTime = reservation.getStartTime();
        LocalDateTime proposedEndTime = newStartTime.plusHours(4);
        // verifies if there is an active reservation at the spot that conflicts with a
        // new one
        List<Reservations> activeReservations = reservationsRepository
                .findBySpotAndReservationStatus(existingSpot.get(), ReservationStatus.ACTIVE);
        for (Reservations res : activeReservations) {
            LocalDateTime resStart = res.getStartTime();
            LocalDateTime resEnd = resStart.plusHours(4);
            // checking if overlaps with an exiting one
            boolean overlaps = newStartTime.isBefore(resEnd) && resStart.isBefore(proposedEndTime);
            if (!overlaps) {
                continue;
            }

            if (resStart.isAfter(newStartTime)) {
                proposedEndTime = resStart;
                long durationMin = Duration.between(newStartTime, proposedEndTime).toMinutes();
                // if the duration is less than 45 minutes, the system will not allow the
                if (durationMin < 45) {
                    throw new IllegalArgumentException(
                            "The reservation could not be completed because there is no enough time before the next one.");
                }
                continue;
            }
            throw new IllegalArgumentException(
                    "The reservation could not be completed because the time conflicts with an existing one.");
        }
        // if the reservation does not conflict, save it
        reservation.setEndTime(proposedEndTime);
        reservation.setSpot(existingSpot.get());
        reservation.setReservationStatus(ReservationStatus.ACTIVE);
        Reservations saveReservation = reservationsRepository.save(reservation);

        ParkingTrack pendingCheckIn = new ParkingTrack();
        pendingCheckIn.setSpot(saveReservation.getSpot());
        pendingCheckIn.setUser(saveReservation.getUser());
        pendingCheckIn.setReservation(saveReservation);
        pendingCheckIn.setConfirmCheckIn(false);
        pendingCheckIn.setCheckIn(null);
        pendingCheckIn.setCheckOut(null);
        // save the pending check-in for the reservation
        parkingTrackRepository.save(pendingCheckIn);

        Users user = saveReservation.getUser();
        if (user != null) {
            notificationsService.createNotificationForUser(user, NotificationType.SPOT_RESERVED,
                    "Reservation successfully created. Your reservation has a duration of 4 hours after the start time.",
                    saveReservation);
        }
        spotsService.updateSpotStatus(existingSpot.get(), SpotStatus.RESERVED);
        return saveReservation;
    }

    public Reservations save(Reservations reservation) {
        return reservationsRepository.save(reservation);
    }

    public List<Reservations> findByUser(Users user) {
        // find by user
        return reservationsRepository.findByUser(user);
    }

    public List<Reservations> findBySpot(Spots spot) {
        // find by spot
        return reservationsRepository.findBySpot(spot);
    }

    public List<Reservations> findByReservationStatus(ReservationStatus status) {
        // find by reservation status
        return reservationsRepository.findByReservationStatus(status);
    }

    public void cancelReservation(int reservationID) {
        Reservations res = reservationsRepository.findById(reservationID)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found."));
        if (res.getReservationStatus() == ReservationStatus.CANCELLED) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(res.getStartTime())) {
            throw new IllegalArgumentException("The reservation cannot be cancelled after the start time.");
        }
        res.setReservationStatus(ReservationStatus.CANCELLED);
        reservationsRepository.save(res);

        Users user = res.getUser();
        if (user != null) {
            notificationsService.createNotificationForUser(
                    user,
                    NotificationType.RESERVATION_CANCELLED,
                    "Your reservation has been cancelled.",
                    res);
        }
        List<Reservations> activeReservations = reservationsRepository.findBySpotAndReservationStatus(
                res.getSpot(), ReservationStatus.ACTIVE);

        if (activeReservations.isEmpty()) {
            spotsService.updateSpotStatus(res.getSpot(), SpotStatus.EMPTY);
        }

    }

    // find all reservations
    public List<Reservations> findAll() {
        return reservationsRepository.findAll();
    }
}// reservations service class