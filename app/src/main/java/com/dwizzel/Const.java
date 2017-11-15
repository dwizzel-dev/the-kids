package com.dwizzel;

/**
 * Created by Dwizzel on 07/11/2017.
 */

public final class Const {

    public static final class notif {

        public static final int TYPE_NOTIF_ERROR = 0x00000001;
        public static final int TYPE_NOTIF_LOGIN = 0x00000002;
        public static final int TYPE_NOTIF_PROFILE = 0x00000003;
        public static final int TYPE_NOTIF_SIGNED = 0x00000004;
        public static final int TYPE_NOTIF_LOADING = 0x00000005;

    }

    public static final class family {

        public static final int TYPE_FAMILY_MOTHER = 0x00000101;
        public static final int TYPE_FAMILY_FATHER = 0x00000102;
        public static final int TYPE_FAMILY_BROTHER = 0x00000103;
        public static final int TYPE_FAMILY_SISTER = 0x00000104;
        public static final int TYPE_FAMILY_UNCLE = 0x00000105;
        public static final int TYPE_FAMILY_HUNT = 0x00000106;
        public static final int TYPE_FAMILY_FRIEND = 0x00000107;
        public static final int TYPE_FAMILY_GRANPA = 0x00000108;
        public static final int TYPE_FAMILY_GRANMA = 0x00000109;

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

}