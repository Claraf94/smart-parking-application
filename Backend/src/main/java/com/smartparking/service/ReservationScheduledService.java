package com.smartparking.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.smartparking.entity.Reservations;
import com.smartparking.enums.NotificationType;
import com.smartparking.enums.ReservationStatus;
import com.smartparking.repository.NotificationSentRepository;
import com.smartparking.repository.ReservationsRepository;

@Component
public class ReservationScheduledService {
    @Autowired
    private NotificationsService notificationsService;
    @Autowired
    private ReservationsRepository reservationsRepository;
    @Autowired
    private NotificationSentRepository notificationSentRepository;
    @Scheduled(cron = "0 * * * * ?") // Every minute

    public void notifyAboutReservations() {
        // Notify users about reservations that are about to expire
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusMinutes(10);
        List<Reservations> expiring = reservationsRepository.findByReservationStatusAndEndTimeBetween(ReservationStatus.ACTIVE, now, endTime);
        for (Reservations res : expiring) {
            if(!notificationSentRepository.existsByReservationAndNotificationType(res, NotificationType.RESERVATION_EXPIRING)) {
                // Create a notification for the user if it hasn't been sent yet
                notificationsService.createNotificationForUser(res.getUser(), NotificationType.RESERVATION_EXPIRING, "Your reservation for spot " + res.getSpot().getSpotsID() + " is about to expire.", res);
            }
        }
        // Notify users about reservations that have expired
        List<Reservations> expired = reservationsRepository.findByReservationStatusAndEndTimeBefore(ReservationStatus.ACTIVE, now);
        for (Reservations res : expired) {
            if(!notificationSentRepository.existsByReservationAndNotificationType(res, NotificationType.RESERVATION_EXPIRED)) {
                // Create a notification for the user if it hasn't been sent yet
                notificationsService.createNotificationForUser(res.getUser(), NotificationType.RESERVATION_EXPIRED, "Your reservation for spot " + res.getSpot().getSpotsID() + " has expired.", res);
            }
        }
    }
}