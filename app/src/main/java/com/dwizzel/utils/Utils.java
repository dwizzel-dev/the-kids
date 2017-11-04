package com.dwizzel.utils;

/**
 * Created by Dwizzel on 03/11/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.dwizzel.thekids.R;

public class Utils {

    private static Utils sInst = null;

    private Utils() {
        // Required empty public constructor
    }

    public static synchronized Utils getInstance() {
        if(sInst == null) {
            synchronized (Utils.class) {
                sInst = new Utils();
            }
        }
        return sInst;
    }

    public boolean checkConnectivity(Context context){
        boolean bConnected = false;
        if(context != null) {
            try {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
                bConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
            }catch(Exception e){
                //TODO: handle the errors
            }
        }
        return bConnected;
    }

    public void showToastMsg(Activity activity, int msgId){
        if(activity != null) {
            try {
                Toast.makeText(activity, msgId, Toast.LENGTH_SHORT).show();
            }catch(Exception e){
                //TODO: handle the errors
            }
        }
    }

    public void showToastMsg(Activity activity, String msg){
        if(activity != null) {
            try {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }catch(Exception e){
                //TODO: handle the errors
            }
        }
    }

}
