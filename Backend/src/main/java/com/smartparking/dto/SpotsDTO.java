
package com.smartparking.dto;

import java.util.List;
import com.smartparking.enums.SpotStatus;

public class SpotsDTO {
    //declare variables
    private String spotCode, locationDescription, spotColor;
    private int spotsID;
    private SpotStatus status;
    private Double x, y;
    private Boolean isReservable;
    private List<List<Double>> boundaries;
    private List<Double> spotLabel;

    // Constructor
    public SpotsDTO(String spotCode, int spotsID, SpotStatus status, String locationDescription, String spotColor, Double x, Double y,
                   Boolean isReservable, List<List<Double>> boundaries, List<Double> spotLabel) {
        this.spotCode = spotCode;
        this.spotsID = spotsID;
        this.status = status;
        this.locationDescription = locationDescription;
        this.spotColor = spotColor;
        this.x = x;
        this.y = y;
        this.isReservable = isReservable;
        this.boundaries = boundaries;
        this.spotLabel = spotLabel;
    }

    // Default constructor
    public SpotsDTO() {}

    // Getters and Setters
    public String getSpotCode() {
        return spotCode;
    }

    public void setSpotCode(String spotCode) {
        this.spotCode = spotCode;
    }

    public int getSpotsID() {
        return spotsID;
    }

    public void setSpotsID(int spotsID) {
        this.spotsID = spotsID;
    }

    public SpotStatus getStatus() {
        return status;
    }

    public void setStatus(SpotStatus status) {
        this.status = status;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public String getSpotColor() {
        return spotColor;
    }

    public void setSpotColor(String spotColor) {
        this.spotColor = spotColor;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Boolean getIsReservable() {
        return isReservable;
    }

    public void setIsReservable(Boolean isReservable) {
        this.isReservable = isReservable;
    }

    public List<List<Double>> getBoundaries() {
        return boundaries;
    }

    public void setBoundaries(List<List<Double>> boundaries) {
        this.boundaries = boundaries;
    }

    public List<Double> getSpotLabel() {
        return spotLabel;
    }

    public void setSpotLabel(List<Double> spotLabel) {
        this.spotLabel = spotLabel;
    }

    @Override
    public String toString() {
        return "SpotCode: " + spotCode +  "\n" +
                "SpotsID: " + spotsID +  "\n" +
                "Status: " + status +  "\n" +
                "LocationDescription: " + locationDescription +  "\n" +
                "SpotColor: " + spotColor +  "\n" +
                "Latitude: " + x +  "\n" +
                "Longitude: " + y +  "\n" +
                "Reservable: " + isReservable +  "\n" +
                "Boundaries: " + boundaries +  "\n" +
                "Label: " + spotLabel;
    }
}// SpotDTO class