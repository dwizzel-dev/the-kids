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

        private Object args;
        private int err = 0;
        private String msg = "";

        public ServiceResponseObject() {

        }

        public ServiceResponseObject(int err, String msg) {
            this.err = err;
            this.msg = msg;
        }

        public ServiceResponseObject(int err) {
            this.err = err;
        }

        public ServiceResponseObject(Object obj) {
            this.args = obj;
        }

        public ServiceResponseObject(Object obj, String msg) {
            this.args = obj;
            this.msg = msg;
        }

        public int getErr(){
            return err;
        }

        public String getMsg(){
            return msg;
        }

        public Object getArgs(){
            return args;
        }

    }

    //pour les infos de base du user
    public static final class UserObject extends Object {

        private String username;
        private String uid;

        public UserObject(String username, String uid) {
            this.username = username;
            this.uid = uid;
        }

        public String getUsername(){
            return username;
        }

        public Object getUserId(){
            return uid;
        }

    }

}
