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

    public Optional<Spots> findById(int id){
        //find a spot by its id
        return spotsRepository.findById(id);
    }
    
    public List<Spots> findByStatus(String status) {
        //find a spot by its Status
        return spotsRepository.findByStatus(status);
    }

    public List<Spots> findByIsReservableTrue() {
        //find a spot that can be reserved
        return spotsRepository.findByIsReservableTrue();
    }
    
    public List<Spots> findByStatusAndIsReservable(String status, Boolean isReservable){
        return spotsRepository.findByStatusAndIsReservable(status, isReservable);
    }

    //Optional is used to handle cases where the spot might not exist
    public Optional<Spots> findBySpotCode(String spotCode) {
        //find a spot by its code
        return spotsRepository.findBySpotCode(spotCode);
    }

    public Spots updateSpot(int id, Spots spot) {
        //update a spot by its id
        Optional<Spots> existingSpot = spotsRepository.findById(id);
        if (existingSpot.isPresent()) {
            existingSpot.get().setStatus(spot.getStatus());
            existingSpot.get().setLocationDescription(spot.getLocationDescription());
            existingSpot.get().setX(spot.getX());
            existingSpot.get().setY(spot.getY());
            existingSpot.get().setIsReservable(spot.getIsReservable());
            return spotsRepository.save(existingSpot.get());
        } else {
            throw new RuntimeException("Spot not found with id: " + id);
        }
    }

    public void deleteSpot(int id) {
        //delete a spot
        Optional<Spots> existingSpot = spotsRepository.findById(id);
        if (!existingSpot.isPresent()) {
            throw new RuntimeException("Spot not found with id: " + id);
        }
        spotsRepository.delete(existingSpot.get());
    }

    public List<Spots> findAll() {
        //find all spots
        return spotsRepository.findAll();
    }
}//spots service class
