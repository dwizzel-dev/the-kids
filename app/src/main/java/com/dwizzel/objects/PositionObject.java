package com.dwizzel.objects;

/**
 * Created by Dwizzel on 16/11/2017.
 */

public class PositionObject {

    private double latitude = 0.00;
    private double longitude = 0.00;
    private double altitude = 0.00;

    public PositionObject(double latitude, double longitude, double altitude){
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    double getLatitude() {
        return latitude;
        }

    double getLongitude() {
        return longitude;
    }

    double getAltitude() {
        return altitude;
    }

    void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    void setAltitude(double altitude) {
        this.altitude = altitude;
    }

}
