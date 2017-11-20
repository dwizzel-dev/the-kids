package com.dwizzel.services;

import com.google.firebase.firestore.GeoPoint;

/**
 * Created by Dwizzel on 19/11/2017.
 */

public interface IGpsService {

    boolean startLocationUpdate();
    void stopLocationUpdate();
    GeoPoint getLastPosition();
    int checkGpsStatus();
    GeoPoint getPosition();

}
