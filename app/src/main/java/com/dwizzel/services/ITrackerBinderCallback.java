package com.dwizzel.services;

import com.dwizzel.objects.ServiceResponseObject;

/**
 * Created by Dwizzel on 14/11/2017.
 */

public interface ITrackerBinderCallback {

    void handleResponse(ServiceResponseObject sroj);
    void onSignedIn(ServiceResponseObject sro);
    void onSignedOut(ServiceResponseObject sro);
    void onCreated(ServiceResponseObject sro);

}
