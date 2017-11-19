package com.dwizzel;

/**
 * Created by Dwizzel on 07/11/2017.
 */

public final class Const {



    public static final class notif {

        public static final int TYPE_NOTIF_GPS = 0x00000001;
        public static final int TYPE_NOTIF_CREATED = 0x00000002;
        public static final int TYPE_NOTIF_SIGNED = 0x00000003;
    }

    public static final class error {

        public static final int NO_ERROR = 0;
        public static final int ERROR_INVALID_PASSWORD = 0x00000203;
        public static final int ERROR_INVALID_CREDENTIALS = 0x00000204;
        public static final int ERROR_INVALID_EMAIL = 0x00000205;
        public static final int ERROR_EMAIL_EXIST = 0x00000206;
        public static final int ERROR_WEAK_PASSWORD = 0x00000207;

    }

    public static final class except {

        public static final int GENERIC = 0x00000301;
        public static final int NO_CONNECTION = 0x00000302;
        public static final int NULL_POINTER = 0x00000303;

    }

    public static final class user {

        public static final int TYPE_EMAIL = 0x00000401;
        public static final int TYPE_GOOGLE = 0x00000402;
        public static final int TYPE_FACEBOOK = 0x00000403;
        public static final int TYPE_INSTAGRAM = 0x00000404;
        public static final int TYPE_TWITTER = 0x00000405;

    }

    public static final class gps {

        public static final int NO_ERROR= 0x00000501;
        public static final int NO_PERMISSION = 0x00000502;
        public static final int NO_PROVIDER = 0x00000503;
        public static final int GPS_ENABLE = 0x00000505;
        public static final int NETWORK_ENABLE = 0x00000506;


    }
}