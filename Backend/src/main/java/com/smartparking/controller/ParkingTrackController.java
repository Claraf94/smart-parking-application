package com.smartparking.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smartparking.entity.ParkingTrack;
import com.smartparking.entity.Spots;
import com.smartparking.entity.Users;
import com.smartparking.service.ParkingTrackService;

@RestController
@RequestMapping("/parkingTrack")
//this controller handles parking track operations such as checking in and out of parking spots, retrieving user and spot activities, and managing parking status.
public class ParkingTrackController {
    @Autowired
    private ParkingTrackService parkingTrackService;

    //put the spot as occupied
    @PutMapping("/checkin/{id}")
    public ResponseEntity<String> checkinSpot(@PathVariable int id){
        if(parkingTrackService.checkInSpot(id)){
            return ResponseEntity.ok("Check in successful at the spot: " + id);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Check in failed at the spot: " + id);
        }
    }

    //put the spot as released
    @PutMapping("/checkout/{id}")
    public ResponseEntity<String> checkoutSpot(@PathVariable int id){
        if(parkingTrackService.checkOutSpot(id)){
            return ResponseEntity.ok("Check out successful at the spot: " + id);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Check out failed at the spot: " + id);
        }
    }

    //returns every activity made by a specific user by its ID
    @GetMapping("/user/{userId}")
    public List<ParkingTrack> getByUser(@PathVariable int userId){
        if(userId <=0){
            throw new IllegalArgumentException("Invalid type of ID.");
        }
        Users user = new Users();
        user.setUserID(userId);
        return parkingTrackService.getByUser(user);
    }
    
    //returns parking activity for a specific spot by its ID
    @GetMapping("/spots/{spotsId}")
    public List<ParkingTrack> getBySpot(@PathVariable int spotsId){
        if(spotsId <=0){
            throw new IllegalArgumentException("Invalid type of ID.");
        }
        Spots spot = new Spots();
        spot.setSpotsID(spotsId);
        return parkingTrackService.getBySpot(spot);
    }

    //returns occupied spots, where the check-in was made but not the check-out
    @GetMapping("/checked-in")
    public List<ParkingTrack> getCheckedIn(){
        return parkingTrackService.getCheckedIn();
    }

    //returns the spots that are currently free, where both check-in and check-out was made
    @GetMapping("/checked-out")
    public List<ParkingTrack> getCheckedOut(){
        return parkingTrackService.getCheckedOut();
    }

    //returns reservations where the driver has not checked-in yet
    @GetMapping("/pending-checkin")
    public List<ParkingTrack> getPendingCheckIns(){
        return parkingTrackService.getPendingCheckIns();
    }
    
    //returns all parking track register
    @GetMapping("/all")
    public List<ParkingTrack> trackAllSpots(){
        return parkingTrackService.getAll();
    }
}//parking track controller class


