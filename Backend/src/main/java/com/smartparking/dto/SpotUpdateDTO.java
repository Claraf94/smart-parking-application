package com.smartparking.dto;

import com.smartparking.enums.SpotStatus;

public class SpotUpdateDTO {
    //declare variables
    private SpotStatus status;
    private Boolean isReservable;
    private String locationDescription;
    
    //constructor
    public SpotUpdateDTO(SpotStatus status, Boolean isReservable, String locationDescription) {
        this.status = status;
        this.isReservable = isReservable;
        this.locationDescription = locationDescription; 

    }

    //default constructor
    public SpotUpdateDTO() {}

    //getters and setters
    public SpotStatus getStatus() {
        return status;
    }

    public void setStatus(SpotStatus status) {
        this.status = status;
    }

    public Boolean getIsReservable() {
        return isReservable;
    }

    public void setIsReservable(Boolean isReservable) {
        this.isReservable = isReservable;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    @Override
    public String toString() {
        return "Status: " + status + "\n" +
                "Reservable: " + isReservable + "\n" +
                "Location Description: " + locationDescription;
    }
}
