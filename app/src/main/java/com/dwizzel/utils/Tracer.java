package com.dwizzel.utils;

import android.util.Log;

/**
 * Created by Dwizzel on 16/11/2017.
 */

public final class Tracer {

    private static final boolean logToFile = false;
    private static final String filename = "";
    private static final String TAG = "TK.";

    public static void log(String tag, String msg){
        Log.d(TAG + tag, msg);
        if(logToFile){
            //write to file
        }
    }

    public static void log(String tag, String msg, Exception e){
        Log.d(TAG + tag, msg, e);
        if(logToFile){
            //write to file
        }
    }

    public static void tog(String tag, String msg){
        Log.d(tag, msg);
        if(logToFile){
            //write to file
        }
    }

    public static void tog(String tag, String msg, Exception e){
        Log.d(tag, msg, e);
        if(logToFile){
            //write to file
        }
    }

}
