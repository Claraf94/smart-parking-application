package com.smartparking.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smartparking.entity.ParkingTrack;
import com.smartparking.entity.Spots;
import com.smartparking.entity.Users;
import com.smartparking.repository.ParkingTrackRepository;

@Service //This class is a parking track service component
public class ParkingTrackService {
    @Autowired
    private ParkingTrackRepository parkingTrackRepository;
    
    public boolean getSpot (int Id) {
        // Check if the parking spot is available
        //it will be considered available if there is no parking track associated 
        //by using the check in and check out times
        Optional<ParkingTrack> parkingTrack = parkingTrackRepository.findById(Id);
        //if the register exists, it means that the parking spot is not available
        if (parkingTrack.isPresent()) {
            ParkingTrack track = parkingTrack.get();
            //if the parking spot is not checked in, it is available
            if(!track.isConfirmCheckIn() && track.getCheckOut() == null) {
            track.setConfirmCheckIn(true);
            parkingTrackRepository.save(track);
            return true;
            }   
        //if is checked in, it is not available
        return false;
        }
        //no register found, so the parking spot is available
        return false;
    }

    public boolean releaseSpot(int Id) {
        // Check if the parking spot is checked in
        Optional<ParkingTrack> parkingTrack = parkingTrackRepository.findById(Id);
        if (parkingTrack.isPresent()) {
            ParkingTrack track = parkingTrack.get();
            //if the parking spot is checked in, it can be released
            if(track.isConfirmCheckIn() && track.getCheckOut() == null) {
                track.setConfirmCheckOut(true);
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