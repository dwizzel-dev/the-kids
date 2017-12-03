package com.dwizzel;

/**
 * Created by Dwizzel on 07/11/2017.
 */

public final class Const {
    public static final class error {
        public static final int NO_ERROR = 0;

        public static final int ERROR_INVALID_PASSWORD = 203;
        public static final int ERROR_INVALID_CREDENTIALS = 204;
        public static final int ERROR_INVALID_EMAIL = 205;
        public static final int ERROR_EMAIL_EXIST = 206;
        public static final int ERROR_WEAK_PASSWORD = 207;

        public static final int ERROR_SMS_NOT_SENT = 208;
        public static final int ERROR_SMS_SEND_PERMISSION = 209;

        public static final int ERROR_INVITE_ID_FAILURE = 211;
        public static final int ERROR_INVALID_INVITE_CODE_FAILURE = 212;
        public static final int ERROR_INVALID_INVITE_CODE = 213;

        public static final int ERROR_INVITE_CREATION_FAILURE = 214;
        public static final int ERROR_INVITATION_CREATION_FAILURE = 215;



    }
    public static final class except {
        public static final int GENERIC = 301;
        public static final int NO_CONNECTION = 302;
        public static final int NULL_POINTER = 303;
    }
    public static final class user {
        public static final int TYPE_EMAIL = 401;
        public static final int TYPE_GOOGLE = 402;
        public static final int TYPE_FACEBOOK = 403;
        public static final int TYPE_INSTAGRAM = 404;
        public static final int TYPE_TWITTER = 405;
    }
    public static final class gps {
        public static final int NO_ERROR = 500;
        public static final int NO_PERMISSION = 501;
        public static final int NO_PROVIDER = 502;
        public static final int GPS_ENABLE = 503;
        public static final int NETWORK_ENABLE = 504;
    }
    public static final class watchers {
        public static final int EMPTY_LIST = 601;
    }
    public static final class status {
        public static final int ONLINE = 700;
        public static final int OFFLINE = 701;
        public static final int OCCUPIED = 702;
    }
    public static final class invitation {
        public static final int ACCEPTED = 800;
        public static final int PENDING = 802;
        public static final int INNACTIVE = 803;
        public static final int REMOVE = 804;
        public static final int DEFAULT_CODE = 0;
    }
    public static final class notif {
        public static final int WATCHER_UPDATE = 900;
        public static final int INVITATION_UPDATE = 901;
        public static final int WATCHING_UPDATE = 902;
    }
    public static final class conn {
        public static final int CONNECTED = 1000;
        public static final int RECONNECTED = 1001;
        public static final int RECONNECTING = 1002;
        public static final int NOT_CONNECTED = 1003;
    }
    public static final class response {
        public static final String ON_WATCHERS_LIST = "ON_WATCHERS_LIST";
        public static final String ON_INVITATIONS_LIST = "ON_INVITATIONS_LIST";
        public static final String ON_WATCHINGS_LIST = "ON_WATCHINGS_LIST";

        public static final String ON_EMPTY_WATCHERS_LIST = "ON_EMPTY_WATCHERS_LIST";
        public static final String ON_EMPTY_INVITATIONS_LIST = "ON_EMPTY_INVITATIONS_LIST";
        public static final String ON_EMPTY_WATCHINGS_LIST = "ON_EMPTY_WATCHINGS_LIST";

        public static final String ON_INVITE_ID_CREATED = "ON_INVITE_ID_CREATED";
        public static final String ON_INVITE_ID_ACTIVATED = "ON_INVITE_ID_ACTIVATED";
        public static final String ON_INVITATION_CREATED = "ON_INVITATION_CREATED";



    }

}