package com.dwizzel.services;

import com.dwizzel.models.CommunicationObject;

/**
 * Created by Dwizzel on 14/11/2017.
 */

public interface ITrackerBinderCallback {

    void handleResponse(long counter);

    void onSignedIn(Object obj);

    void onSignedOut(Object obj);

}
