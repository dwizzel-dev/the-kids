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
    void batchUserWatcher();

}
