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
    void updateTokenId();

    void deleteWatchingsItem(String uid);
    void modifyWatchingsItem(String uid);

    void deleteWatchersItem(String uid);
    void modifyWatchersItem(String uid);

    void getWatchersList();
    void getInvitationsList();
    void getWatchingsList();

    void createInviteId();
    void createInvitation(String inviteId, String name, String phone, String email, String code);
    void deleteInvitationsItem(String uid);

    void validateInviteCode(String code);
    void saveInviteInfo(InviteInfoModel inviteInfoModel);

}
