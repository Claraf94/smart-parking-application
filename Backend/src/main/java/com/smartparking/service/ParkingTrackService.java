package com.smartparking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.smartparking.entity.ParkingTrack;
import com.smartparking.entity.Reservations;
import com.smartparking.entity.Spots;
import com.smartparking.entity.Users;
import com.smartparking.enums.ReservationStatus;
import com.smartparking.enums.SpotStatus;
import com.smartparking.repository.ParkingTrackRepository;
import com.smartparking.repository.ReservationsRepository;
import com.smartparking.repository.SpotsRepository;

@Service //This class is a parking track service component
public class ParkingTrackService {
    @Autowired
    private ParkingTrackRepository parkingTrackRepository;
    @Autowired
    private SpotsRepository spotsRepository;
    @Autowired
    private UsersService usersService;
    @Autowired
    private NotificationsService notificationsService;
    @Autowired
    private ReservationsRepository reservationsRepository;
    
    public boolean checkInSpot (String spotCode) {
        //Check if the parking spot is available
        //it will be considered available if there is no parking track associated 
        //by using the check in and check out times
        Optional<Spots> spot = spotsRepository.findBySpotCode(spotCode);
        if(spot.isEmpty()){
            throw new IllegalArgumentException("Wrong spot code: " +spotCode);
        }
        Spots spotSpot = spot.get();
        //if it is a reservable spot, only allows checkin if the user has an active reservation
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Users> userUser = usersService.findByEmail(username);
        if(userUser.isEmpty()){
            throw new IllegalArgumentException("User is not authenticated in the database. Check in cannot be done");
        }
        Users user = userUser.get();
        //blocks multiple check ins for the same user
        if(parkingTrackRepository.existsByUserAndConfirmCheckInTrueAndCheckOutIsNull(user)){
            throw new IllegalArgumentException("You already checked in. You cannot do it again.");
        }
        //checking if the user has a reservation for that time
        if(spotSpot.getIsReservable()){
            List<Reservations> userRes = reservationsRepository.findBySpotAndUserAndReservationStatus(spotSpot, user, ReservationStatus.ACTIVE);
            LocalDateTime now = LocalDateTime.now();
            for(Reservations res: userRes){
                if (res.getStartTime() != null && res.getEndTime() != null && !now.isBefore(res.getStartTime()) && now.isBefore(res.getEndTime())){
                    ParkingTrack reservationCheckin = new ParkingTrack();
                    reservationCheckin.setSpot(spotSpot);
                    reservationCheckin.setUser(user);
                    reservationCheckin.setConfirmCheckIn(true);
                    reservationCheckin.setCheckOut(null);
                    reservationCheckin.setReservation(res);
                    parkingTrackRepository.save(reservationCheckin);

                    //update spot status to occupied
                    spotSpot.setStatus(SpotStatus.OCCUPIED);
                    spotsRepository.save(spotSpot);

                    System.out.println("Check-in succesfully done for the reservation: " + res.getReservationID());
                    return true;
                }
            }
            throw new IllegalArgumentException("This spot requires a valid reservation for the check in. You are not authorized to do it.");
        }
        //check in for non-reservable spots
        Optional<ParkingTrack> trackCheckin = parkingTrackRepository.findBySpotAndConfirmCheckInTrueAndCheckOutIsNull(spotSpot);
        if (trackCheckin.isPresent()) {
            return false;
        }
        
        ParkingTrack newCheckin = new ParkingTrack();
        newCheckin.setSpot(spotSpot);
        newCheckin.setUser(user);
        newCheckin.setConfirmCheckIn(true);
        newCheckin.setCheckOut(null);
        parkingTrackRepository.save(newCheckin);

        //update spot status to occupied
        spotSpot.setStatus(SpotStatus.OCCUPIED);
        spotsRepository.save(spotSpot);
        return true;
    }

    public boolean checkOutSpot(String spotCode) {
        Optional<Spots> spot = spotsRepository.findBySpotCode(spotCode);
        if (spot.isEmpty()) {
            throw new IllegalArgumentException("Wrong code provided." + spotCode);
        }
        Spots spotSpot = spot.get();

        //check if the parking spot is currently checked in
        Optional<ParkingTrack> trackCheckOut = parkingTrackRepository.findBySpotAndConfirmCheckInTrueAndCheckOutIsNull(spotSpot);
        if (trackCheckOut.isEmpty()) {
            return false;
        }
        //retrieving the authenticated user that did the check in
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Users> userUser = usersService.findByEmail(username);
        if (userUser.isEmpty()) {
            throw new IllegalArgumentException("User is not authenticade in the database. Check out cannot be done.");
        }

        Users user = userUser.get();
        //check if the user has a check in for that spot
        Optional<ParkingTrack> userTrack = parkingTrackRepository.findByUserAndSpotAndConfirmCheckInTrueAndCheckOutIsNull(user, spotSpot);
        if(userTrack.isEmpty()) {
            throw new IllegalArgumentException("You do not have any check in record for this spot.");
        }
        
        ParkingTrack newCheckOut = userTrack.get();
        //only the user responsible for the check in can do the check out
        if(newCheckOut.getUser().getUserID() != user.getUserID()){
            throw new IllegalArgumentException("This spot was checked in by another user. You are not allowed to do the check out.");
        }
        newCheckOut.setConfirmCheckOut(true);
        newCheckOut.setCheckOut(LocalDateTime.now());
        parkingTrackRepository.save(newCheckOut);
        
        //update spot status to empty
        spotSpot.setStatus(SpotStatus.EMPTY);
        spotsRepository.save(spotSpot);

        //checking out from reservable spots
        if(spotSpot.getIsReservable()){
            Optional<Reservations> res = reservationsRepository.findFirstBySpotAndUserAndReservationStatusOrderByStartTimeDesc(spotSpot, user, ReservationStatus.ACTIVE);
            if(res.isPresent()){
                Reservations reservation = res.get();
                //apply fine if left the place after the reservation ends
                if(LocalDateTime.now().isAfter(reservation.getEndTime())){
                    notificationsService.createFineNotification(user);
                    System.out.println("Fine applied due to leaving the spot after the reservation end time.");
                }
                //updates status from a reservation spot
                reservation.setReservationStatus(ReservationStatus.FINISHED);
                reservationsRepository.save(reservation);
                System.out.println("Check out successfully done for the reservation: " + reservation.getReservationID());
            }else{
                throw new IllegalArgumentException("No active reservation was found for this spot.");
            }
        }
        return true;
    }

     public List<ParkingTrack> getByUser(Users user) {
        //find by its user
        return parkingTrackRepository.findByUser(user);
    }

    public List<ParkingTrack> getBySpot(Spots spot) {
        //find by its spot
        return parkingTrackRepository.findBySpot(spot);
    }

    public List<ParkingTrack> getCheckedIn() {
        //find by check in status
        return parkingTrackRepository.findByConfirmCheckInTrue();
    }

    public List<ParkingTrack> getCheckedOut() {
        //find by check out status
        return parkingTrackRepository.findByConfirmCheckOutTrue();
    }

    public List<ParkingTrack> getPendingCheckIns() {
        //find by spots that a reservation was made but has no check-in register yet
        return parkingTrackRepository.findByConfirmCheckInFalse();
    }

    public Spots findSpotBySpotCode(String spotCode){
        Optional<Spots> spotSpot = spotsRepository.findBySpotCode(spotCode);
        if(spotSpot.isPresent()){
            return spotSpot.get();
        }else{
            throw new IllegalArgumentException("Spot not found with the code provided." +spotCode);
        }
    }

    public List<ParkingTrack> getAll() {
        //find all parking tracks
        return parkingTrackRepository.findAll();
    }
}//parking track service class