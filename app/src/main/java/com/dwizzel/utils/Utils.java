package com.dwizzel.utils;

/**
 * Created by Dwizzel on 03/11/2017.
 */


import com.dwizzel.thekids.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import java.util.regex.Pattern;

public class Utils {

    private final static String TAG = "TheKids.Utils";
    private static Utils sInst = null;
    private ProgressDialog mProgressDialog;
    private static int iCount = 0;

    private Utils() {
        // Required empty public constructor
    }

    public static Utils getInstance() {
        if(sInst == null) {
            sInst = new Utils();
        }
        Log.w(TAG, "count:" + iCount++);
        return sInst;
    }

    public static boolean hasPermission(Context context, String permission) {
        Log.w(TAG, "hasPermission");
        int res = context.checkCallingOrSelfPermission(permission);
        return res == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkConnectivity(Context context){
        Log.w(TAG, "checkConnectivity");
        boolean bConnected = false;
        if(context != null) {
            if(hasPermission(context, android.Manifest.permission.ACCESS_NETWORK_STATE)) {
                try {
                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    if (cm != null) {
                        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
                        bConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
                    }
                }catch(Exception e) {
                    //TODO: handle the errors
                    Log.w(TAG, "checkConnectivity.exception: ", e);
                }
            }
        }
        return bConnected;
    }

    public void showToastMsg(Activity activity, int msgId){
        Log.w(TAG, "showToastMsg");
        if(activity != null) {
            try {
                Toast.makeText(activity, msgId, Toast.LENGTH_SHORT).show();
            }catch(Exception e){
                //TODO: handle the errors
                Log.w(TAG, "showToastMsg[0].exception: ", e);
            }
        }
    }

    public void showToastMsg(Activity activity, String msg){
        Log.w(TAG, "showToastMsg");
        if(activity != null) {
            try {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }catch(Exception e){
                //TODO: handle the errors
                Log.w(TAG, "showToastMsg[1].exception: ", e);
            }
        }
    }

    public int isValidEmail(String email) {
        Log.w(TAG, "isValidEmail");
        if(email.equals("")){
            return R.string.email_invalid;
        }
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        if(!pattern.matcher(email).matches()){
            return R.string.email_invalid;
        }
        return 0;
    }

    public int isValidPsw(String[] psw) {
        Log.w(TAG, "isValidPsw");
        if(psw.length == 2){
            if(!psw[0].equals(psw[1])) {
                return R.string.psw_not_the_same;
            }
        }
        //check juste le premier
        if(psw[0].length() < 6) {
            return R.string.psw_too_short;
        }
        return 0;
    }

    public void showProgressDialog(Context context) {
        Log.w(TAG, "showProgressDialog");
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(context.getString(R.string.dialog_loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        Log.w(TAG, "hideProgressDialog");
        if(mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

}
