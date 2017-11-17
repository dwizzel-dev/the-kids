package com.dwizzel.objects;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by Dwizzel on 17/11/2017.
     <uses-permission android:name="android.permission.GET_ACCOUNTS" />
     <uses-permission android:name="android.permission.READ_PROFILE" />
     <uses-permission android:name="android.permission.READ_CONTACTS" />
     <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
     <uses-permission android:name="android.permission.INTERNET"/>
     <uses-permission android:name="android.permission.SEND_SMS"/>
     <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
     <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
     <uses-permission android:name="com.dwizzel.permission.SERVICE" />
 */

public class PermissionObject {

    private boolean getAccounts;
    private boolean readContacts;
    private boolean accessNetworkState;
    private boolean internet;
    private boolean sendSms;
    private boolean accessFineLocation;
    private boolean accessCoarseLocation;

    public PermissionObject(Context context){
        getAccounts = context.checkCallingOrSelfPermission(
                Manifest.permission.GET_ACCOUNTS) ==
                PackageManager.PERMISSION_GRANTED;
        readContacts = context.checkCallingOrSelfPermission(
                Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED;
        accessNetworkState = context.checkCallingOrSelfPermission(
                Manifest.permission.ACCESS_NETWORK_STATE) ==
                PackageManager.PERMISSION_GRANTED;
        internet = context.checkCallingOrSelfPermission(
                Manifest.permission.INTERNET) ==
                PackageManager.PERMISSION_GRANTED;
        sendSms = context.checkCallingOrSelfPermission(
                Manifest.permission.SEND_SMS) ==
                PackageManager.PERMISSION_GRANTED;
        accessFineLocation = context.checkCallingOrSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
        accessCoarseLocation = context.checkCallingOrSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public boolean isGetAccounts() {
        return getAccounts;
    }

    public void setGetAccounts(boolean getAccounts) {
        this.getAccounts = getAccounts;
    }

    public boolean isReadContacts() {
        return readContacts;
    }

    public void setReadContacts(boolean readContacts) {
        this.readContacts = readContacts;
    }

    public boolean isAccessNetworkState() {
        return accessNetworkState;
    }

    public void setAccessNetworkState(boolean accessNetworkState) {
        this.accessNetworkState = accessNetworkState;
    }

    public boolean isInternet() {
        return internet;
    }

    public void setInternet(boolean internet) {
        this.internet = internet;
    }

    public boolean isSendSms() {
        return sendSms;
    }

    public void setSendSms(boolean sendSms) {
        this.sendSms = sendSms;
    }

    public boolean isAccessFineLocation() {
        return accessFineLocation;
    }

    public void setAccessFineLocation(boolean accessFineLocation) {
        this.accessFineLocation = accessFineLocation;
    }

    public boolean isAccessCoarseLocation() {
        return accessCoarseLocation;
    }

    public void setAccessCoarseLocation(boolean accessCoarseLocation) {
        this.accessCoarseLocation = accessCoarseLocation;
    }



}
