package com.smartparking.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.smartparking.entity.ParkingTrack;
import com.smartparking.entity.Spots;
import com.smartparking.entity.Users;
import com.smartparking.repository.ParkingTrackRepository;
import com.smartparking.repository.SpotsRepository;

@Service //This class is a parking track service component
public class ParkingTrackService {
    @Autowired
    private ParkingTrackRepository parkingTrackRepository;
    @Autowired
    private SpotsRepository spotsRepository;
    @Autowired
    private UsersService usersService;
    
    public boolean checkInSpot (String spotCode) {
        // Check if the parking spot is available
        //it will be considered available if there is no parking track associated 
        //by using the check in and check out times
        Optional<Spots> spot = spotsRepository.findBySpotCode(spotCode);
        if(spot.isEmpty()){
            throw new IllegalArgumentException("Wrong code provided." +spotCode);
        }
        Spots spotSpot = spot.get();
        //if the register exists, it means that the parking spot is not available
        Optional<ParkingTrack> trackCheckin = parkingTrackRepository.findBySpotAndConfirmCheckInFalseAndCheckOutIsNull(spotSpot);
        if (trackCheckin.isPresent()) {
            return false;
        }
        //if the parking spot is not checked in, it is available
        String username = ((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Users user = usersService.findByEmail(username).get();
        ParkingTrack newCheckin = new ParkingTrack();
        newCheckin.setSpot(spotSpot);
        newCheckin.setUser(user);
        newCheckin.setConfirmCheckIn(true);
        newCheckin.setCheckOut(null);
        parkingTrackRepository.save(newCheckin);
        return true;
    }

    public boolean checkOutSpot(int parkingTrackId) {
        if(parkingTrackId <= 0){
            throw new IllegalArgumentException("Invalid ID for the spot.");
        }
        // Check if the parking spot is checked in
        Optional<ParkingTrack> parkingTrack = parkingTrackRepository.findById(parkingTrackId);
        if (parkingTrack.isPresent()) {
            ParkingTrack track = parkingTrack.get();
            //if the parking spot is checked in, it can be released
            if(track.isConfirmCheckIn() && track.getCheckOut() == null) {
                track.setConfirmCheckOut(true);
                track.setCheckOut(LocalDateTime.now());
                parkingTrackRepository.save(track);
                return true;
            }
            //if the parking spot is not checked in, it cannot be released
            return false;
        }
        //if no register found or not checked in, it cannot be released
        return false;
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

    public List<ParkingTrack> getAll() {
        //find all parking tracks
        return parkingTrackRepository.findAll();
    }
}//parking track service class