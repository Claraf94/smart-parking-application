package com.smartparking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.smartparking.entity.NotificationSent;
import com.smartparking.entity.Reservations;
import com.smartparking.enums.NotificationType;

public interface NotificationSentRepository extends JpaRepository<NotificationSent, Integer> {
    // Method to check if a notification has already been sent for a specific reservation and notification type
    //so it will not be sent again
    boolean existsByReservationAndNotificationType(Reservations reservation, NotificationType notificationType);
}
