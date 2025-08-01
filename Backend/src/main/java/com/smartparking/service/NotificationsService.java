package com.smartparking.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smartparking.entity.NotificationSent;
import com.smartparking.entity.Notifications;
import com.smartparking.entity.Reservations;
import com.smartparking.entity.Users;
import com.smartparking.enums.NotificationType;
import com.smartparking.repository.NotificationSentRepository;
import com.smartparking.repository.NotificationsRepository;
import com.smartparking.repository.ReservationsRepository;
import com.smartparking.repository.UsersRepository;

@Service // this class is a notification service component
public class NotificationsService {
    @Autowired
    private NotificationsRepository notificationsRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private NotificationSentRepository notificationSentRepository;
    @Autowired
    private ReservationsRepository reservationsRepository;
    @Autowired
    private SetEmailService setEmailService;
    private static final BigDecimal DEFAULT_FINE_AMOUNT = new BigDecimal("50.00");

    // get the message according to the notification type
    private String getMessageForNotificationType(NotificationType type) {
        switch (type) {
            case SPOT_NOT_AVAILABLE:
                return "This spot is not available.";
            case SPOT_RESERVED_VIOLATION:
                return "You occupied a reserved spot without permission.";
            case SPOT_RESERVED:
                return "Your have successfully reserved a parking spot.";
            case RESERVATION_EXPIRING:
                return "Attention! Your reservation is about to expire.";
            case RESERVATION_EXPIRED:
                return "Attention! Your time to occupy the reserved spot has expired.";
            case RESERVATION_CANCELLED:
                return "Your reservation has been cancelled.";
            case RESERVATION_NOT_POSSIBLE:
                return "Your reservation is not possible at this time.";
            case UNAUTHORIZED_CHECKIN:
                return "You are not authorized to check in at this spot.";
            case FINE_APPLIED:
                return "A fine has been applied to your account.";
            case GENERAL_INFORMATION:
                return "You have a new notification.";
            default:
                return "You have a new notification.";
        }
    }

    // create a new notification. not applied for when there is a fine
    public Notifications createNotification(Users user, NotificationType type, String message) {
        // verifying if the users exists on the database in case this is provided
        if (user != null) {
            if (user.getUserID() == 0 || usersRepository.findById(user.getUserID()).isEmpty()) {
                throw new IllegalArgumentException("User does not exist on the database.");
            }
            if (notificationsRepository.existsByUserAndNotificationTypeAndTextMessage(user, type, message)) {
                return null;
            }
        }

        Notifications notification = new Notifications();
        notification.setUser(user);
        notification.setNotificationType(type);
        notification.setTextMessage(message);
        notification.setFine(BigDecimal.ZERO);
        notification.setIsPaid(false);
        Notifications savedNotification = notificationsRepository.save(notification);

        sendEmailIfNeeded(user, type, "Parking Notification", message);
        return savedNotification;
    }

    // create a new notification related to a fine application
    public Notifications createFineNotification(Users user) {
        // verifying if the user exists on the database
        if (user == null || user.getUserID() == 0 || usersRepository.findById(user.getUserID()).isEmpty()) {
            throw new IllegalArgumentException("User does not exist.");
        }
        final String messageFineApplied = "A fine of € " + DEFAULT_FINE_AMOUNT
                + " has been applied to your account due to problems following the parking rules.";
        Notifications notification = new Notifications();
        notification.setUser(user);
        notification.setNotificationType(NotificationType.FINE_APPLIED);
        notification.setTextMessage(messageFineApplied);
        notification.setFine(DEFAULT_FINE_AMOUNT);
        notification.setIsPaid(false);

        // send email notification
        Notifications saved = notificationsRepository.save(notification);
        try {
            sendEmailIfNeeded(user, NotificationType.FINE_APPLIED,
                    "Parking Fine Notification - Fine Applied", saved.getTextMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saved;
    }

    // personalized notification for user with automatic email sending
    public Notifications createNotificationForUser(Users user, NotificationType type) {
        String message = getMessageForNotificationType(type);
        return createNotificationForUser(user, type, message);
    }

    public Notifications createNotificationForUser(Users user, NotificationType type, String message) {
        return createNotification(user, type, message);
    }

    public Notifications createNotificationForUser(Users user, NotificationType type, String message,
            Reservations reservation) {
        if (type == null) {
            throw new IllegalArgumentException("Notification type must not be null.");
        }
        if (message == null || message.isBlank()) {
            message = getMessageForNotificationType(type);
        }
        if (user != null) {
            if (user.getUserID() == 0 || usersRepository.findById(user.getUserID()).isEmpty()) {
                throw new IllegalArgumentException("User does not exist on the database.");
            }
        }
        if (reservation != null
                && notificationSentRepository.existsByReservationAndNotificationType(reservation, type)) {
            return null;
        }
        Notifications notification = new Notifications();
        notification.setUser(user);
        notification.setNotificationType(type);
        notification.setTextMessage(message);
        notification.setFine(BigDecimal.ZERO);
        notification.setIsPaid(false);
        Notifications savedNotification = notificationsRepository.save(notification);

        try {
            sendEmailIfNeeded(user, type, "Parking Notification", message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (reservation != null) {
            // save the notification as sent
            NotificationSent notificationSent = new NotificationSent();
            notificationSent.setReservation(reservation);
            notificationSent.setNotificationType(type);
            notificationSentRepository.save(notificationSent);
        }
        return savedNotification;
    }

    // mark a fine as paid
    public Notifications markAsPaid(int notificationId) {
        System.out.println(">> markAsPaid chamado com id: " + notificationId);
        List<Notifications> all = notificationsRepository.findAll();
        System.out.println(">> Todas notificações (IDs e isPaid):");
        all.forEach(n -> System.out.println("   id=" + n.getNotificationID() + ", isPaid=" + n.isPaid()));

        Optional<Notifications> optNotification = notificationsRepository.findById(notificationId);
        if (optNotification.isEmpty()) {
            System.out.println(">> findById retornou vazio para id: " + notificationId);
            throw new IllegalArgumentException("Notification not found with the provided id.");
        }

        Notifications notification = optNotification.get();
        System.out.println(">> Notificação encontrada: id=" + notification.getNotificationID() + ", fine="
                + notification.getFine() + ", isPaid=" + notification.isPaid());

        if (notification.getFine() != null && notification.getFine().compareTo(BigDecimal.ZERO) > 0) {
            notification.setIsPaid(true);
            return notificationsRepository.save(notification);
        } else {
            throw new IllegalArgumentException("You have no fine to be paid.");
        }
    }

    // returns a list of all unpaid fines to the user
    public List<Notifications> getUnpaidFines(Users user) {
        if (user != null && user.getUserID() != 0 && usersRepository.findById(user.getUserID()).isPresent()) {
            return notificationsRepository.findByUserAndIsPaidFalseAndFineGreaterThan(user, BigDecimal.ZERO);
        }
        throw new IllegalArgumentException("User must have a valid ID and not be empty.");
    }

    // sends a notification to all users
    public void sendNotificationToAllUsers(String subject, String message) {
        createNotification(null, NotificationType.GENERAL_INFORMATION, message);
        // search for all users in the database
        List<Users> everyUser = usersRepository.findAll();
        for (Users user : everyUser) {
            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                setEmailService.sendEmailConfig(user.getEmail(), subject, message);
            }
        }
    }

    public Optional<Reservations> findReservationById(int reservationID) {
        // find a reservation by its ID
        return reservationsRepository.findById(reservationID);
    }

    public boolean sendNotificationEmail(NotificationType type) {
        // Define the logic to determine if a notification should be sent based on its
        // type
        return type == NotificationType.FINE_APPLIED ||
                type == NotificationType.SPOT_RESERVED ||
                type == NotificationType.RESERVATION_EXPIRING ||
                type == NotificationType.RESERVATION_EXPIRED ||
                type == NotificationType.RESERVATION_CANCELLED ||
                type == NotificationType.RESERVATION_NOT_POSSIBLE ||
                type == NotificationType.UNAUTHORIZED_CHECKIN;
    }

    // send email if needed based on the notification type and user
    private void sendEmailIfNeeded(Users user, NotificationType type, String subject, String message) {
        if (!sendNotificationEmail(type)) {
            return;
        }
        if (user == null || user.getEmail() == null || user.getEmail().isBlank()) {
            return;
        }
        try {
            setEmailService.sendEmailConfig(user.getEmail(), subject, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Notifications> getNotifications(Users user) {
        // returns all notifications for a specific user
        return notificationsRepository.findByUser(user);
    }

    public List<Notifications> getAllNotifications() {
        // returns all notifications in the database
        return notificationsRepository.findAll();
    }
}// notifications service class
