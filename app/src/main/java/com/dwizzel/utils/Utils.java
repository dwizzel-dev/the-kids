package com.dwizzel.utils;

/**
 * Created by Dwizzel on 03/11/2017.
 */

import com.dwizzel.thekids.R;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Patterns;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class Utils {

    private final static String TAG = "Utils";
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
        //Tracer.log(TAG, "count:" + iCount++);
        return sInst;
    }

    private boolean hasPermission(Context context, String permission) {
        Tracer.log(TAG, "hasPermission");
        try {
            int res = context.checkCallingOrSelfPermission(permission);
            return res == PackageManager.PERMISSION_GRANTED;
        }catch (Exception e){
            Tracer.log(TAG, "hasPermission.exception: ", e);
        }
        return false;
    }

    public boolean checkConnectivity(Context context){
        Tracer.log(TAG, "checkConnectivity");
        boolean bConnected = false;
        try {
            if (hasPermission(context, Manifest.permission.ACCESS_NETWORK_STATE) &&
                    hasPermission(context, Manifest.permission.INTERNET)) {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm != null) {
                    NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
                    bConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
                }
            }
        }catch (Exception e){
            Tracer.log(TAG, "checkConnectivity.exception: ", e);
        }
        return bConnected;
    }

    public void showToastMsg(Context context, int msgId){
        Tracer.log(TAG, "showToastMsg");
        try {
            Toast.makeText(context, context.getResources().getString(msgId), Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            Tracer.log(TAG, "showToastMsg[0].exception: ", e);
        }
    }

    public void showToastMsg(Context context, String msg){
        Tracer.log(TAG, "showToastMsg");
        try {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Tracer.log(TAG, "showToastMsg[1].exception: ", e);
        }
    }

    public int isValidEmail(String email) {
        Tracer.log(TAG, "isValidEmail");
        if(email.equals("")){
            return R.string.err_email_invalid;
        }
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        if(!pattern.matcher(email).matches()){
            return R.string.err_email_invalid;
        }
        return 0;
    }

    public int isValidPsw(String[] psw) {
        Tracer.log(TAG, "isValidPsw");
        if(psw.length == 2){
            if(!psw[0].equals(psw[1])) {
                return R.string.err_psw_not_the_same;
            }
        }
        //check juste le premier
        if(psw[0].length() < 6) {
            return R.string.err_psw_too_short;
        }
        return 0;
    }

    public void showProgressDialog(Context context) {
        Tracer.log(TAG, "showProgressDialog");
        try {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setMessage(context.getString(R.string.dialog_loading));
                mProgressDialog.setIndeterminate(true);
            }
            mProgressDialog.show();
        }catch(Exception e){
            Tracer.log(TAG, "showProgressDialog.exception: ", e);
        }
    }

    public void hideProgressDialog() {
        Tracer.log(TAG, "hideProgressDialog");
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            mProgressDialog = null;
        }catch(Exception e){
            Tracer.log(TAG, "hideProgressDialog.exception: ", e);
        }
    }

    public Locale getLocale(Context context){
        try {
            return context.getResources().getConfiguration().locale;
        }catch (Exception e){
            Tracer.log(TAG, "getLocale.exception: ", e);
        }
        return null;
    }

    public void showSettingsAlert(final Context context){
        Tracer.log(TAG, "showSettingsAlert");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(R.string.alert_gps_title);
        alertDialog.setMessage(R.string.alert_gps_title);
        // settings button
        alertDialog.setPositiveButton(R.string.butt_settings, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
        // cancel button
        alertDialog.setNegativeButton(R.string.butt_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // show message
        alertDialog.show();
    }

    public String formatDate(Context context, Date date){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    Utils.getInstance().getLocale(context));
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return simpleDateFormat.format(date);
        }catch(Exception e){
            Tracer.log(TAG, "formatDate.exception: ", e);
        }
        return "0000-00-00 00:00:00";

    }



}