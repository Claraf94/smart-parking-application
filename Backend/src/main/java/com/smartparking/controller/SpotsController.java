package com.smartparking.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smartparking.entity.Spots;
import com.smartparking.service.SpotsService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/spots")
//This controller handles requests related to parking spots, such as registering new spots, finding spots by status or code, and retrieving all spots.
public class SpotsController {
    @Autowired
    private SpotsService spotsService;
    //register a new parking spot
    @PreAuthorize("hasRole('ADMIN')") //only admin can create new parking spots on the system
    @PostMapping("/register")
    public Spots registerNewSpot(@RequestBody Spots spot) {
        return spotsService.saveSpot(spot);
    }

    //returns a parking spot by its status
    @GetMapping("/findByStatus/{status}")
    public List<Spots> findByStatus(@PathVariable String status) {
        return spotsService.findByStatus(status);
    }

    //returns parking spot that can be reserved
    @GetMapping("/findReservable")
    public List<Spots> findReservableSpots() {
        return spotsService.findByIsReservableTrue();
    }

    //returns a parking spot by its code
    @GetMapping("/findBySpotCode/{code}")
    public Spots findBySpotCode(@PathVariable String spotCode) {
        return spotsService.findBySpotCode(spotCode).orElse(null);
    }
    
    //returns all parking spots
    @GetMapping("/all")
    public List<Spots> findAllSpots() {
        return spotsService.findAll();
    }

    //update a parking spot
    @PreAuthorize("hasRole('ADMIN')") //only admin can update parking spots on the system
    @PutMapping("/update/{id}")
    public Spots updateSpot(@PathVariable int id, @RequestBody Spots spot) {
        return spotsService.updateSpot(id, spot);
    }

    //delete a parking spot
    @PreAuthorize("hasRole('ADMIN')") //only admin can delete parking spots on the system
    @DeleteMapping("/delete/{id}")
    public void deleteSpot(@PathVariable int id) {
        spotsService.deleteSpot(id);
    }
    

}
