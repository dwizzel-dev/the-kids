package com.dwizzel.models;

import android.app.Activity;

import com.facebook.AccessToken;


/**
 * Created by Dwizzel on 07/11/2017.
 */

public class NotifObjectObserver extends Object{

    private int type;
    private AccessToken token;
    private Activity activity;

    public NotifObjectObserver(int type, AccessToken token, Activity activity){
        this.type = type;
        this.token = token;
        this.activity = activity;
    }

    public int getType() {
        return type;
    }

    public AccessToken getToken() {
        return token;
    }

    public Activity getActivity() {
        return activity;
    }





}
