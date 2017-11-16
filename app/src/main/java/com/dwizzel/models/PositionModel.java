package com.dwizzel.models;

/**
 * Created by Dwizzel on 16/11/2017.
 */

public class PositionModel {

    private double latitude = 0.00;
    private double longitude = 0.00;
    private double altitude = 0.00;

    public PositionModel(){}

    public PositionModel(double latitude, double longitude, double altitude){
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public double getLatitude() {
        return latitude;
        }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

}
