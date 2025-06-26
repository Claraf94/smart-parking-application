package com.smartparking.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.smartparking.entity.ParkingTrack;
import com.smartparking.entity.Spots;
import com.smartparking.entity.Users;

@Repository
public interface ParkingTrackRepository extends JpaRepository<ParkingTrack, Integer> {
    //tracking parking usage by using the Users entity reference
    List<ParkingTrack> findByUser(Users user);
    //tracking parking usage by using the Spots entity reference
    List<ParkingTrack> findBySpot(Spots spot);
    //tracking parking usage by check-in status
    List<ParkingTrack> findByConfirmCheckInTrue();
    //tracking parking usage by check-out status
    List<ParkingTrack> findByConfirmCheckOutTrue();
    List<ParkingTrack> findByConfirmCheckInFalse();
}//parking track repository class