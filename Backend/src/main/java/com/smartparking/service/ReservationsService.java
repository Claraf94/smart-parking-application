package com.smartparking.service;

import java.time.*;
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

        LocalDateTime startTime = reservation.getStartTime();
        LocalDateTime endTime = startTime.plusHours(4);
        // verifies if there is an active reservation at the spot that conflicts with a
        // new one
        List<Reservations> activeReservations = reservationsRepository
                .findBySpotAndReservationStatus(existingSpot.get(), ReservationStatus.ACTIVE);
        for (Reservations res : activeReservations) {
            LocalDateTime resStart = res.getStartTime();
            LocalDateTime resEnd = resStart.plusHours(4);
            // checking if overlaps with an exiting one
            boolean overlaps = startTime.isBefore(resEnd) && resStart.isBefore(endTime);
            if (!overlaps)
                continue;

            if (resStart.isAfter(startTime)) {
                endTime = resStart;
                long durationMin = Duration.between(startTime, endTime).toMinutes();
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
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
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
                    "Reservation successfully created.",
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
            String bodyMessage = String.format(
                    "Hello %s,%n%n" +
                            "Your reservation has been successfully cancelled.%n%n" +
                            "Spot: %s – %s%n" +
                            "Date and Time: %s at %s%n%n" +
                            "Thank you for using ParkTime!",
                    user.getFirstName(),
                    res.getSpot().getSpotCode(),
                    res.getSpot().getLocationDescription(),
                    res.getStartTime().toLocalDate(),
                    res.getStartTime().toLocalTime().withSecond(0).withNano(0));

            notificationsService.createNotificationForUser(
                    user,
                    NotificationType.RESERVATION_CANCELLED,
                    bodyMessage,
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