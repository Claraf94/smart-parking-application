package com.smartparking.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.smartparking.entity.Notifications;
import com.smartparking.entity.Reservations;
import com.smartparking.entity.Spots;
import com.smartparking.enums.NotificationType;
import com.smartparking.enums.ReservationStatus;
import com.smartparking.enums.SpotStatus;
import com.smartparking.repository.NotificationSentRepository;
import com.smartparking.repository.ParkingTrackRepository;
import com.smartparking.repository.ReservationsRepository;
import com.smartparking.repository.SpotsRepository;

@Component
public class ReservationScheduledService {
    @Autowired
    private NotificationsService notificationsService;
    @Autowired
    private ReservationsRepository reservationsRepository;
    @Autowired
    private ParkingTrackRepository parkingTrackRepository;
    @Autowired
    private NotificationSentRepository notificationSentRepository;
    @Autowired
    private SpotsRepository spotsRepository;

    @Scheduled(cron = "0 * * * * ?") // Every minute

    public void notifyAboutReservations() {
        // Notify users about reservations that are about to expire
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusMinutes(10);
        List<Reservations> expiring = reservationsRepository
                .findByReservationStatusAndEndTimeBetween(ReservationStatus.ACTIVE, now, endTime);
        for (Reservations res : expiring) {
            if (!notificationSentRepository.existsByReservationAndNotificationType(res,
                    NotificationType.RESERVATION_EXPIRING)) {
                // Create a notification for the user if it hasn't been sent yet
                String message = """
                        Hello %s,

                        Just a reminder: your reservation is about to expire in less than 10 minutes.

                        Details:
                        ---------------
                        Spot: %s - %s
                        Ends at: %s %s

                        Thank you for using ParkTime!
                        """.formatted(
                        res.getUser().getFirstName(),
                        res.getSpot().getSpotCode(),
                        res.getSpot().getLocationDescription(),
                        res.getEndTime().toLocalDate(),
                        res.getEndTime().toLocalTime().withSecond(0).withNano(0));
                Notifications notification = notificationsService.createNotificationForUser(
                        res.getUser(),
                        NotificationType.RESERVATION_EXPIRING,
                        message,
                        res);
                if (notification != null) {
                    System.out.println("Notification sent for expiring reservation: " + res.getReservationID());
                }
            }
        }
        // Notify users about reservations that have expired
        List<Reservations> expired = reservationsRepository
                .findByReservationStatusAndEndTimeBefore(ReservationStatus.ACTIVE, now);
        for (Reservations res : expired) {
            if (!notificationSentRepository.existsByReservationAndNotificationType(res,
                    NotificationType.RESERVATION_EXPIRED)) {
                // Create a notification for the user if it hasn't been sent yet
                String message = """
                        Hello %s,

                        Your reservation has expired.

                        Details:
                        ---------------
                        Spot: %s - %s
                        Ended at: %s %s

                        The spot is now available again.

                        Thank you for using ParkTime!
                        """.formatted(
                        res.getUser().getFirstName(),
                        res.getSpot().getSpotCode(),
                        res.getSpot().getLocationDescription(),
                        res.getEndTime().toLocalDate(),
                        res.getEndTime().toLocalTime().withSecond(0).withNano(0));
                Notifications notification = notificationsService.createNotificationForUser(
                        res.getUser(),
                        NotificationType.RESERVATION_EXPIRED,
                        message,
                        res);
                if (notification != null) {
                    System.out.println("Notification sent for expired reservation: " + res.getReservationID());
                }
            }
        }

        // notify users about a reservation that has been cancelled because of a no-show
        // within 15 minutes
        List<Reservations> cancelled = reservationsRepository
                .findByReservationStatusAndStartTimeBefore(ReservationStatus.ACTIVE, now.minusMinutes(15));
        for (Reservations res : cancelled) {
            if (!parkingTrackRepository.existsByReservationAndConfirmCheckInTrue(res)) {
                // the reservation is cancelled and the time is available for another
                // reservation and notifies the user
                res.setReservationStatus(ReservationStatus.CANCELLED);
                reservationsRepository.save(res);
                Spots spot = res.getSpot();
                spot.setStatus(SpotStatus.EMPTY);
                spotsRepository.save(spot);
                if (!notificationSentRepository.existsByReservationAndNotificationType(res,
                        NotificationType.RESERVATION_CANCELLED)) {
                    String message = """
                            Hello %s,

                            Your reservation was cancelled because no check-in was made within 15 minutes of the scheduled start time.

                            Details:
                            ---------------
                            Spot: %s - %s
                            Scheduled: %s %s

                            Please ensure timely check-ins for future reservations.

                            Thank you for using ParkTime!
                            """
                            .formatted(
                                    res.getUser().getFirstName(),
                                    res.getSpot().getSpotCode(),
                                    res.getSpot().getLocationDescription(),
                                    res.getStartTime().toLocalDate(),
                                    res.getStartTime().toLocalTime().withSecond(0).withNano(0));
                    Notifications notification = notificationsService.createNotificationForUser(
                            res.getUser(),
                            NotificationType.RESERVATION_CANCELLED,
                            message,
                            res);
                    if (notification != null) {
                        System.out.println(
                                "Notification sent to inform user about cancellation: " + res.getReservationID());
                    }
                }
            }
        }
    }
}// reservation scheduled service class