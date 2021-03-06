package com.dwizzel.services;

import com.dwizzel.objects.ServiceResponseObject;

/**
 * Created by Dwizzel on 19/11/2017.
 */

public interface ITrackerService {

    void onUserSignedIn(ServiceResponseObject sro);
    void onUserCreated(ServiceResponseObject sro);
    void onUserWatchersList(ServiceResponseObject sro);
    void onUserInvitationsList(ServiceResponseObject sro);
    void onGpsPositionUpdate();
    void onUserSignedOut(ServiceResponseObject sro);
    void onInviteIdCreated(ServiceResponseObject sro);
    void onInvitationCreated(ServiceResponseObject sro);
    void onUserWatchingsList(ServiceResponseObject sro);
    void onActivateInvite(ServiceResponseObject sro);
    void onValidateInviteCode(ServiceResponseObject sro);
    void onTokenRefreshed(String token);

}
