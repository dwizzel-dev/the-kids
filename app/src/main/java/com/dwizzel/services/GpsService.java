package com.dwizzel.services;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.dwizzel.objects.PermissionObject;
import com.dwizzel.objects.PositionObject;
import com.dwizzel.thekids.R;
import com.dwizzel.utils.Tracer;

/**
 * Created by Dwizzel on 16/11/2017.
 * https://blog.codecentric.de/en/2014/05/android-gps-positioning-location-strategies/

         Criteria criteria = new Criteria();
         criteria.setPowerRequirement(Criteria.POWER_LOW); // Chose your desired power consumption level.
         criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
         criteria.setSpeedRequired(true); // Chose if speed for first location fix is required.
         criteria.setAltitudeRequired(false); // Choose if you use altitude.
         criteria.setBearingRequired(false); // Choose if you use bearing.
         criteria.setCostAllowed(false); // Choose if this provider can waste money :-)
         return locationManager.getBestProvider(criteria, true);

 */

/*
* TODO: meilleur gestion des exceptions de connectivity et permissions
*
* */

public class GpsService implements LocationListener {

    private PositionObject mPosition = new PositionObject(0,0,0);
    private final static String TAG = "GpsService";
    private Context mContext;
    private boolean mCanGetLocation = false;
    private TrackerService.TrackerBinder mTrackerBinder;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // in meters
    private static final long MIN_TIME_BW_UPDATES = 10000; // 60000 = 1 minute
    private LocationManager mLocationManager;

    public GpsService(Context context, IBinder trackerBinder) {
        Tracer.log(TAG, "GpsService");
        mContext = context;
        mTrackerBinder = (TrackerService.TrackerBinder) trackerBinder;
    }

    public boolean hasPermission() {
        Tracer.log(TAG, "hasPermission");
        if(mContext != null) {
            PermissionObject perms = new PermissionObject(mContext);
            Tracer.log(TAG, "Permissions[ACCESS_NETWORK_STATE]: " + perms.isAccessNetworkState());
            Tracer.log(TAG, "Permissions[ACCESS_FINE_LOCATION]: " + perms.isAccessFineLocation());
            Tracer.log(TAG, "Permissions[INTERNET]: " + perms.isInternet());
            Tracer.log(TAG, "Permissions[ACCESS_COARSE_LOCATION]: " + perms.isAccessCoarseLocation());
            return (perms.isAccessNetworkState() && perms.isAccessFineLocation()
                    && perms.isInternet() && perms.isAccessCoarseLocation());
        }
        return false;
    }

    public void showSettingsAlert(){
        Tracer.log(TAG, "showSettingsAlert");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle(R.string.alert_gps_title);
        alertDialog.setMessage(R.string.alert_gps_title);
        // settings button
        alertDialog.setPositiveButton(R.string.butt_settings, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
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

    private void getLocation(){
        Tracer.log(TAG, "getLocation");

        Location location = null;
        boolean isGPSEnabled = false;
        boolean isNetworkEnabled = false;

        //check si on a les droits avant tout
        if(hasPermission()) {
            mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            try {
                // getting GPS status
                isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                // getting network status
                isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (NullPointerException npe){
                Tracer.log(TAG, "getLocation.NullPointerException[0]: ", npe);
            } catch (Exception e) {
                Tracer.log(TAG, "getLocation.Exception[0]: ", e);
            }
            //minor check
            if (isGPSEnabled || isNetworkEnabled) {
                mCanGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    try {
                        mLocationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Tracer.log(TAG, "getLocation: ++ Network Enabled");
                        location = mLocationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            mPosition = new PositionObject(location.getLatitude(),
                                    location.getLongitude(), location.getAltitude());
                        }
                    } catch (NullPointerException npe){
                        Tracer.log(TAG, "getLocation.NullPointerException[1]: ", npe);
                    } catch (SecurityException se) {
                        Tracer.log(TAG, "getLocation.SecurityException[0]: ", se);
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        try {
                            mLocationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Tracer.log(TAG, "getLocation: ++ GPS Enabled");

                            location = mLocationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                mPosition = new PositionObject(location.getLatitude(),
                                        location.getLongitude(), location.getAltitude());
                            }
                        } catch (NullPointerException npe){
                            Tracer.log(TAG, "getLocation.NullPointerException[2]: ", npe);
                        } catch (SecurityException se) {
                            Tracer.log(TAG, "getLocation.SecurityException[1]: ", se);
                        }
                    }
                }
            }
        }else{
            Tracer.log(TAG, "getLocation : no permissions");
        }
    }

    void stopUsingGPS(){
        Tracer.log(TAG, "stopUsingGPS");
        mCanGetLocation = false;
        if(mLocationManager != null){
            mLocationManager.removeUpdates(GpsService.this);
            mLocationManager = null;
        }
    }

    boolean startUsingGPS(){
        Tracer.log(TAG, "startUsingGPS");
        //on reset au cas ou etait deja start
        stopUsingGPS();
        //et on get
        getLocation();
        //selon le getLocation qui va changer le boolean
        return mCanGetLocation;
    }

    PositionObject getPosition(){
        return mPosition;
    }

    @Override
    public void onLocationChanged(Location location) {
        Tracer.log(TAG, "onLocationChanged");
        mPosition = new PositionObject(location.getLatitude(),
                location.getLongitude(), location.getAltitude());
        //on avertit le TrackerService que notre position a change
        if(mTrackerBinder != null) {
            mTrackerBinder.onGpsPositionUpdate(mPosition);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        Tracer.log(TAG, "onProviderDisabled");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Tracer.log(TAG, "onProviderEnabled");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Tracer.log(TAG, "onStatusChanged");
    }

}
