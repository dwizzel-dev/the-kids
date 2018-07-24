package com.dwizzel;

/**
 * Created by Dwizzel on 07/11/2017.
 */

public final class Const {
    public static final class error {
        public static final int NO_ERROR = 0;

        public static final int ERROR_INVALID_PASSWORD = 200;
        public static final int ERROR_INVALID_CREDENTIALS = 201;
        public static final int ERROR_INVALID_EMAIL = 202;
        public static final int ERROR_EMAIL_EXIST = 203;
        public static final int ERROR_WEAK_PASSWORD = 204;

        public static final int ERROR_SMS_NOT_SENT = 210;
        public static final int ERROR_SMS_SEND_PERMISSION = 211;

        public static final int ERROR_INVITE_ID_FAILURE = 220;
        public static final int ERROR_INVALID_INVITE_CODE_FAILURE = 221;
        public static final int ERROR_INVALID_INVITE_CODE = 222;

        public static final int ERROR_INVITE_CREATION_FAILURE = 230;
        public static final int ERROR_INVITATION_CREATION_FAILURE = 231;

        public static final int ERROR_INVITE_INFOS_FAILURE = 240;



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
        public static final int TRAJECT_UPDATE = 903;

        public static final int WATCHER_REMOVE = 910;
        public static final int INVITATION_REMOVE = 911;
        public static final int WATCHING_REMOVE = 912;
        public static final int TRAJECT_REMOVE = 913;

        public static final int WATCHER_ADDED = 920;
        public static final int INVITATION_ADDED = 921;
        public static final int WATCHING_ADDED = 922;
        public static final int TRAJECT_ADDED = 923;
    }
    public static final class conn {
        public static final int CONNECTED = 1000;
        public static final int RECONNECTED = 1001;
        public static final int RECONNECTING = 1002;
        public static final int NOT_CONNECTED = 1003;
    }
    public static final class gpsUpdateType {
        public static final int SOFT = 1100;
        public static final int MED = 1101;
        public static final int HARD = 1102;
    }
    public static final class response {
        public static final int ON_WATCHERS_LIST = 2000;
        public static final int ON_INVITATIONS_LIST = 2001;
        public static final int ON_WATCHINGS_LIST = 2002;

        public static final int ON_EMPTY_WATCHERS_LIST = 2010;
        public static final int ON_EMPTY_INVITATIONS_LIST = 2011;
        public static final int ON_EMPTY_WATCHINGS_LIST = 2012;

        public static final int ON_INVITE_CODE_VALIDATED = 2020;
        public static final int ON_INVITE_ID_CREATED = 2021;
        public static final int ON_INVITE_ID_ACTIVATED = 2022;
        public static final int ON_INVITATION_CREATED = 2023;
    }



}