package com.smartparking.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smartparking.entity.Spots;
import com.smartparking.exceptions.PlatformExceptions.ExistentSpotException;
import com.smartparking.repository.SpotsRepository;

@Service //this class is a spot service component
public class SpotsService {
    @Autowired
    private SpotsRepository spotsRepository;
    //registering or saving a spot to a database
    public Spots saveSpot(Spots spot){
        if(spotsRepository.findBySpotCode(spot.getSpotCode()).isPresent()) {
            throw new ExistentSpotException("A spot with this code already exists: " + spot.getSpotCode());
        }
        return spotsRepository.save(spot);
    }

    public List<Spots> findByStatus(String status) {
        //find a spot by its Status
        return spotsRepository.findByStatus(status);
    }

    public List<Spots> findByIsReservableTrue() {
        //find a spot that can be reserved
        return spotsRepository.findByIsReservableTrue();
    }
    
    //Optional is used to handle cases where the spot might not exist
    public Optional<Spots> findBySpotCode(String spotCode) {
        //find a spot by its code
        return spotsRepository.findBySpotCode(spotCode);
    }

    public List<Spots> findAll() {
        //find all spots
        return spotsRepository.findAll();
    }

    public void deleteSpot(Spots spot) {
        //delete a spot
        spotsRepository.delete(spot);
    }
}//spots service class
