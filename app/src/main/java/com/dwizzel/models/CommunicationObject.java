package com.dwizzel.models;

import android.app.Activity;
import com.facebook.AccessToken;

/**
 * Created by Dwizzel on 07/11/2017.
 */

public final class CommunicationObject{

    //pour les observer/observable communication
    public static final class NotifObjectObserver extends Object {

        private int type;
        private AccessToken token;
        private Activity activity;

        public NotifObjectObserver(int type, AccessToken token, Activity activity) {
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

    //pour les retour du service
    public static final class ServiceResponseObject extends Object {

        private long id;
        private int type;
        private String msg;
        private Object args;

    }

    //pour les envois du service
    public static final class ServiceRequestObject extends Object {

        private long id;
        private int type;
        private String msg;
        private Object args;

    }

}
