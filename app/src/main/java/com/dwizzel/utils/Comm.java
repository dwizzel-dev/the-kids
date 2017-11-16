package com.dwizzel.utils;

import android.telephony.SmsManager;

/**
 * Created by Dwizzel on 10/11/2017.
 * https://stackoverflow.com/questions/26311243/sending-sms-programmatically-without-opening-message-app
 */

public class Comm {

    private static final String TAG = "Comm";

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
        } catch (Exception e) {
            Tracer.log(TAG, "sendSMS.exception: ", e);
        }
    }

}
