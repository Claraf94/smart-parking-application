package com.smartparking.dto;

public class CoordinatesRequestDTO {
    //declare variables
    private Double x, y;

    //constructor
    public CoordinatesRequestDTO(Double x, Double y) {
        this.x = x;
        this.y = y;
    }
    
    //default constructor
    public CoordinatesRequestDTO() {}

    //getters and setters
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

    @Override
    public String toString() {
        return "Coordinates request:\n" +
                "x=" + x +
                ", y=" + y;
    }
}
