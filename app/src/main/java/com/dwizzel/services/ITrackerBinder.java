package com.dwizzel.services;

import com.google.firebase.auth.AuthCredential;

/**
 * Created by Dwizzel on 14/11/2017.
 */

public interface ITrackerBinder {

    long getCounter();
    long getTimeDiff();
    void registerCallback(ITrackerBinderCallback callback);
    void unregisterCallback();
    boolean isSignedIn();
    void signOut();
    void signIn(String user, String psw);
    void signIn(AuthCredential authCredential);
    void createUser(String user, String psw);
    void getWatchersList();
    void getInvitationsList();
    void createInviteId();
    void createInvitation(String inviteId, String name, String phone, String email);

}
