package com.dwizzel.services;

import com.dwizzel.datamodels.InviteInfoModel;
import com.google.firebase.auth.AuthCredential;

/**
 * Created by Dwizzel on 14/11/2017.
 */

public interface ITrackerBinder {

    long getCounter();
    long getTimeDiff();

    void registerCallback(ITrackerBinderCallback callback);
    void unregisterCallback();

    void signIn(String user, String psw);
    void signIn(AuthCredential authCredential);
    void createUser(String user, String psw);
    boolean isSignedIn();
    void signOut();

    void getWatchersList();
    void getInvitationsList();
    void getWatchingsList();

    void deleteWatchingsItem(String uid);
    void deleteWatchersItem(String uid);

    void modifyWatchingsItem(String uid);
    void modifyWatchersItem(String uid);

    void createInviteId();
    void createInvitation(String inviteId, String name, String phone, String email, String code);

    void validateInviteCode(String code);
    void saveInviteInfo(InviteInfoModel inviteInfoModel);


}
