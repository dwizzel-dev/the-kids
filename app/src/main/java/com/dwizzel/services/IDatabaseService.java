package com.dwizzel.services;

import com.dwizzel.datamodels.InviteInfoModel;

/**
 * Created by Dwizzel on 19/11/2017.
 */


public interface IDatabaseService {

    void keepUserActive();
    void deactivateUser();
    void getUserInfos();
    void updateUserPosition();
    void updateUserInfos();
    void getWatchersList();
    void getInvitationsList();
    void createInviteId();
    void createInvitation(String inviteId, String name, String phone, String email);
    void getWatchingsList();
    void validateInviteCode(String code);
    void saveInviteInfo(InviteInfoModel inviteInfoModel);

}
