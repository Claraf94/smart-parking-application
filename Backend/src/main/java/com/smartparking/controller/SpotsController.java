package com.smartparking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartparking.entity.Spots;
import com.smartparking.service.SpotsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/spots")
public class SpotsController {
    @Autowired
    private SpotsService spotsService;
    // Register a new parking spot
    @PostMapping("/register")
    public Spots registerNewSpot(@RequestBody Spots spot) {
        return spotsService.saveSpot(spot);
    }

    //request to find a parking spot by its status
    @GetMapping("/findByStatus/{status}")
    public List<Spots> findByStatus(@PathVariable String status) {
        return spotsService.findByStatus(status);
    }

    //request to find a parking spot that can be reserved
    @GetMapping("/findReservable")
    public List<Spots> findReservableSpots() {
        return spotsService.findByIsReservableTrue();
    }

    //request to find a parking spot by its code
    @GetMapping("/findBySpotCode/{code}")
    public Spots findBySpotCode(@PathVariable String spotCode) {
        return spotsService.findBySpotCode(spotCode).orElse(null);
    }
    
    //request to find all parking spots
    @GetMapping("/all")
    public List<Spots> findAllSpots() {
        return spotsService.findAll();
    }
    

}
