package com.smartparking.repository;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.smartparking.entity.Notifications;
import com.smartparking.entity.Users;
import com.smartparking.enums.NotificationType;

@Repository
public interface NotificationsRepository extends JpaRepository<Notifications, Integer> {
    //find notifications by using the Users entity reference 
    List<Notifications> findByUser(Users user);
    //find unpaid fines for a specific user
    List<Notifications> findByUserAndIsPaidFalseAndFineGreaterThan(Users user, BigDecimal fine);
    //check if a notification already exists for a user with the same message and type
    boolean existsByUserAndNotificationTypeAndTextMessage(Users user, NotificationType notificationType, String textMessage);
    //find notifications by user and type
    List<Notifications> findByUserAndNotificationType(Users user, NotificationType notificationType);    
}//notifications repository class