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
}