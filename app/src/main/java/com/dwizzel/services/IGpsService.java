package com.dwizzel.services;

import com.dwizzel.objects.PositionObject;

/**
 * Created by Dwizzel on 19/11/2017.
 */

public interface IGpsService {

    boolean startLocationUpdate();
    void stopLocationUpdate();
    PositionObject getLastPosition();
    int checkGpsStatus();
    PositionObject getPosition();

}
