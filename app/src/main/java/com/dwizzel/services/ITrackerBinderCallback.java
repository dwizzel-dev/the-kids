package com.dwizzel.services;

/**
 * Created by Dwizzel on 14/11/2017.
 */

public interface ITrackerBinderCallback {

    void handleResponse(long counter);

    void onSignedIn(Object obj);

    void onSignedOut(Object obj);

    void onGpsEnabled(Object obj);

    void onGpsEnable(Object obj);

    void onGpsDisable(Object obj);

    void onGpsDisabled(Object obj);

    void onGpsUpdate(Object obj);

}
