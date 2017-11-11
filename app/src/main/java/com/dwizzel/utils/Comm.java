package com.dwizzel.utils;

import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Dwizzel on 10/11/2017.
 * https://stackoverflow.com/questions/26311243/sending-sms-programmatically-without-opening-message-app
 */

public class Comm {

    private static final String TAG = "TheKids.Comm";

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
        } catch (Exception e) {
            Log.w(TAG, "sendSMS.exception: ", e);
        }
    }

}
