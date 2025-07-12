package com.smartparking.repository;

import java.util.List;
import java.util.Optional;
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
    //retrieves all where check in was confirmed
    List<ParkingTrack> findByConfirmCheckInTrue();
    //retrieves all where check out was confirmed
    List<ParkingTrack> findByConfirmCheckOutTrue();
    //retrieves all where check in has not been confirmed such as in reservations
    List<ParkingTrack> findByConfirmCheckInFalse();
    //checking for a spot that has not been checked in and has no check out (possibly available)
    Optional<ParkingTrack> findBySpotAndConfirmCheckInFalseAndCheckOutIsNull(Spots spot);
    //checking active check in (spot checked in but not checked out yet)
    Optional<ParkingTrack> findBySpotAndConfirmCheckInTrueAndCheckOutIsNull(Spots spot);
}//parking track repository class