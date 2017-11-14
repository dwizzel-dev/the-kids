// ITrackerService.aidl
package com.dwizzel.thekids;

import com.dwizzel.thekids.ITrackerServiceCallback;

// Declare any non-default types here with import statements

interface ITrackerService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    //return the counter number for testing thread looping
    long getCounter();

    //si est logue ou pas
    boolean isSignedIn();

    //constantly track the counter for each minutes
    oneway void trackCounter(ITrackerServiceCallback callback);

    //stop tracking the counter
    oneway void untrackCounter();

    //sign in avec email et psw
    oneway void signInUser(ITrackerServiceCallback callback, String email, String psw);

}
