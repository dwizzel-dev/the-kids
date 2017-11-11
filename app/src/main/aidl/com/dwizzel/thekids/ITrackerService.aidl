// ITrackerService.aidl
package com.dwizzel.thekids;

import com.dwizzel.thekids.ITrackerServiceCallback;

// Declare any non-default types here with import statements

interface ITrackerService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    //return the counter number for testing thread looping
    long getCounter();

    //constantly track the counter for each minutes
    oneway void trackCounter(ITrackerServiceCallback callback);

    //constantly track the counter for each minutes
    oneway void untrackCounter(ITrackerServiceCallback callback);


}
