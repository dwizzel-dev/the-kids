package com.dwizzel.services;

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

    String getUserLoginName();

    String getUserID();

    void onSignedOut(Object obj);

}
