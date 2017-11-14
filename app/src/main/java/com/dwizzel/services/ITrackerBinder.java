package com.dwizzel.services;

/**
 * Created by Dwizzel on 14/11/2017.
 */

public interface ITrackerBinder {

    long getCounter();

    void registerCallback(ITrackerBinderCallback callback);

    void unregisterCallback();

    boolean isSignedIn();

    void signOut();

}
