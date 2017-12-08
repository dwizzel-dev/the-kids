package com.dwizzel.services;

import com.dwizzel.utils.Tracer;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class TokenIdService extends FirebaseInstanceIdService {

    private static final String TAG = "TokenIdService";
    private ITrackerService mTrackerService;

    TokenIdService(ITrackerService trackerService){
        super();
        mTrackerService = trackerService;
    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        Tracer.log(TAG, "onTokenRefresh");
        String token = FirebaseInstanceId.getInstance().getToken();
        //remet au service principal
        mTrackerService.onTokenRefreshed(token);
        }
}