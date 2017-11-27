package com.dwizzel.services;

/**
 * Created by Dwizzel on 19/11/2017.
 */


public interface IDatabaseService {

    void activateUser();
    void deactivateUser();
    void getUserInfos();
    void updateUserPosition();
    void updateUserInfos();
    void getWatchersList();
    void getInvitationsList();
    void createInviteId();
    void createInvitation(String inviteId, String name, String phone, String email);

}
