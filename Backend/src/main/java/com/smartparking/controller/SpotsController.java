package com.smartparking.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smartparking.entity.Spots;
import com.smartparking.enums.SpotStatus;
import com.smartparking.service.SpotsService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/spots")
//This controller handles requests related to parking spots, such as registering new spots, finding spots by status or code, and retrieving all spots.
public class SpotsController {
    @Autowired
    private SpotsService spotsService;
    //register a new parking spot
    @PreAuthorize("hasRole('ADMIN')") //only admin can create new parking spots on the system
    @PostMapping("/register")
    public ResponseEntity<Spots> registerNewSpot(@RequestBody Spots spot) {
        return ResponseEntity.status(HttpStatus.CREATED).body(spotsService.saveSpot(spot));
    }

    //returns all parking spots and can be filtered by its query parameter such as status
    @GetMapping("")
    public List<Spots> findSpots(
        @RequestParam(required = false) SpotStatus status,
        @RequestParam(required = false) Boolean isReservable
    ){
        if(status != null && isReservable != null){
            return spotsService.findByStatusAndIsReservable(status, isReservable); //filter by both status and reservable
        }
        if(status != null){
            return spotsService.findByStatus(status); //filter by status
        }
        if(isReservable != null && isReservable){
            return spotsService.findByIsReservableTrue(); //filter by reservable spots
        }
        return spotsService.findAll();//returns all spots without any filter applied
    }    

    //returns a parking spot by its code
    @GetMapping("/{spotCode}")
    public Spots findBySpotCode(@PathVariable String spotCode) {
        return spotsService.findBySpotCode(spotCode).orElse(null);
    }
    
    //update a parking spot
    @PreAuthorize("hasRole('ADMIN')") //only admin can update parking spots on the system
    @PutMapping("/update/{id}")
    public ResponseEntity<Spots> updateSpot(@PathVariable int id, @RequestBody Spots spot) {
        if (id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        try{
            return ResponseEntity.ok(spotsService.updateSpot(id, spot));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //delete a parking spot
    @PreAuthorize("hasRole('ADMIN')") //only admin can delete parking spots on the system
    @DeleteMapping("/delete/{id}")
    //response body will be void/empty 
    public ResponseEntity<Void> deleteSpot(@PathVariable int id) {
        if (id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        spotsService.deleteSpot(id);
        return ResponseEntity.noContent().build();
    }
}//spots controller class
