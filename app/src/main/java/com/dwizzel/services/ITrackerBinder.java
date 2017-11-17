package com.dwizzel.services;

import com.dwizzel.objects.PositionObject;
import com.dwizzel.objects.UserObject;
import com.google.firebase.auth.AuthCredential;

/**
 * Created by Dwizzel on 14/11/2017.
 */

public interface ITrackerBinder {

    long getCounter();

    void registerCallback(ITrackerBinderCallback callback);

    void unregisterCallback();

    boolean isSignedIn();

    void signOut();

    void signIn(String user, String psw);

    void signIn(AuthCredential authCredential);

    void createUser(String user, String psw);

    void onSignedIn(Object obj);

    UserObject getUser();

    void onSignedOut(Object obj);

    void onGpsPositionUpdate(PositionObject position);

}
