package com.smartparking.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartparking.dto.SpotsDTO;
import com.smartparking.entity.Spots;
import com.smartparking.enums.SpotStatus;
import com.smartparking.exceptions.PlatformExceptions.ExistentSpotException;
import com.smartparking.repository.SpotsRepository;

@Service // this class is a spot service component
public class SpotsService {
    @Autowired
    private SpotsRepository spotsRepository;

    // registering or saving a spot to a database
    public Spots saveSpot(Spots spot) {
        if (spotsRepository.findBySpotCode(spot.getSpotCode()).isPresent()) {
            throw new ExistentSpotException("A spot with this code already exists: " + spot.getSpotCode());
        }
        return spotsRepository.save(spot);
    }

    public Optional<Spots> findById(int id) {
        // find a spot by its id
        return spotsRepository.findById(id);
    }

    public List<Spots> findByStatus(SpotStatus status) {
        // find a spot by its Status
        return spotsRepository.findByStatus(status);
    }

    public List<Spots> findByIsReservableTrue() {
        // find a spot that can be reserved
        return spotsRepository.findByIsReservableTrue();
    }

    public List<Spots> findByStatusAndIsReservable(SpotStatus status, Boolean isReservable) {
        return spotsRepository.findByStatusAndIsReservable(status, isReservable);
    }

    // Optional is used to handle cases where the spot might not exist
    public Optional<Spots> findBySpotCode(String spotCode) {
        // find a spot by its code
        return spotsRepository.findBySpotCode(spotCode);
    }

    public Spots updateSpot(int id, Spots spot) {
        // update a spot by its id
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
        // delete a spot
        Optional<Spots> existingSpot = spotsRepository.findById(id);
        if (!existingSpot.isPresent()) {
            throw new RuntimeException("Spot not found with id: " + id);
        }
        spotsRepository.delete(existingSpot.get());
    }

    public List<Spots> findAll() {
        // find all spots
        return spotsRepository.findAll();
    }

    public Spots findClosestSpot(double userX, double userY) {
        // find the closest spot by coordinates
        List<Spots> allSpots = spotsRepository.findAll();
        Spots closestSpot = null;
        double closestDistance = Double.MAX_VALUE;
        for (Spots spots : allSpots) {
            if (!SpotStatus.EMPTY.equals(spots.getStatus())) {
                continue;
            }
            double currentDistance = calculateDistance(userX, userY, spots.getX(), spots.getY());
            if (currentDistance < closestDistance) {
                closestDistance = currentDistance;
                closestSpot = spots;
            }
        }
        return closestSpot;
    }

    // method to calculate the distance
    private double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.hypot(x2 - x1, y2 - y1);
    }

    public SpotsDTO convertDTO(Spots spot) {
        String spotColor;
        final double deltaLatitude = 0.00005;
        final double deltaLongitude = 0.000040;

        if (spot.getStatus() == SpotStatus.RESERVED) {
            spotColor = "Orange";
        } else if (spot.getStatus() == SpotStatus.MAINTENANCE) {
            spotColor = "Yellow";
        } else if (spot.getStatus() == SpotStatus.OCCUPIED) {
            spotColor = "Red";
        } else if (Boolean.TRUE.equals(spot.getIsReservable())) {
            spotColor = "Purple";
        } else {
            // Default color for empty spots.
            spotColor = "Green";
        }

        List<List<Double>> boundaries = List.of(
                List.of(spot.getX(), spot.getY()),
                List.of(spot.getX() + deltaLatitude, spot.getY() + deltaLongitude));

        List<Double> spotLabel = List.of(
                (spot.getX() + (spot.getX() + deltaLatitude)) / 2,
                (spot.getY() + (spot.getY() + deltaLongitude)) / 2);

        return new SpotsDTO(spot.getSpotCode(), spot.getSpotsID(), spot.getStatus(), spot.getLocationDescription(), spotColor,
                spot.getX(), spot.getY(), spot.getIsReservable(), boundaries, spotLabel);
    }

    public void updateSpotStatus(Spots spot, SpotStatus status) {
        spot.setStatus(status);
        spotsRepository.save(spot);
    }
}// spots service class
