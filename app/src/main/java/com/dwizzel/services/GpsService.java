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

import com.dwizzel.Const;
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
* NOTES: pour le watcher juste le network suffit,
* mais pour celui qui est suivi ca prend absoluement le gps
*
* */

public class GpsService{

    private PositionObject mPosition = new PositionObject(0,0,0);
    private final static String TAG = "GpsService";
    private Context mContext;
    private TrackerService.TrackerBinder mTrackerBinder;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // in meters
    private static final long MIN_TIME_BW_UPDATES = 10000; // 60000 = 1 minute
    private LocationManager mLocationManager;
    private LocationListener locationListener;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;

    public GpsService(Context context, IBinder trackerBinder) {
        Tracer.log(TAG, "GpsService");
        mContext = context;
        mTrackerBinder = (TrackerService.TrackerBinder) trackerBinder;
        locationListener = new LocationListener();
    }

    private boolean hasPermission() {
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

    private boolean hasProvider(){
        Tracer.log(TAG, "hasProvider");
        if(mLocationManager == null) {
            mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            if(mLocationManager != null) {
                // getting network status
                isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                // getting GPS status
                isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }
        } catch (NullPointerException npe){
            Tracer.log(TAG, "getLocation.NullPointerException: ", npe);
        } catch (Exception e) {
            Tracer.log(TAG, "getLocation.Exception: ", e);
        }
        return (isGPSEnabled || isNetworkEnabled);
    }

    private Location getLastLocation(){
        Tracer.log(TAG, "getLocation");
        Location location = null;
        if(mLocationManager == null) {
            mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        }
        //minor check
        if ((isGPSEnabled || isNetworkEnabled) && mLocationManager != null) {
            try {
                if (isNetworkEnabled) {
                    location = mLocationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location == null) {
                        Tracer.log(TAG, "getLocation.location: can get last network location");
                    }
                }
                if (isGPSEnabled) {
                    if (location == null) {
                        location = mLocationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location == null) {
                            Tracer.log(TAG, "getLocation.location: can get last gps location");
                        }

                    }
                }
            } catch (NullPointerException npe) {
                Tracer.log(TAG, "getLastLocation.NullPointerException: ", npe);
            } catch (SecurityException se) {
                Tracer.log(TAG, "getLastLocation.SecurityException: ", se);
            } catch (Exception e) {
                Tracer.log(TAG, "getLastLocation.Exception[1]: ", e);
            }
        }
        return location;
    }

    boolean startLocationUpdate(){
        Tracer.log(TAG, "startLocationUpdate");
        //on GPS only
        ///check les droits
        if(!hasPermission() || !hasProvider()) {
            return false;
        }
        if(mLocationManager == null) {
            mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        }
        if (!isGPSEnabled || mLocationManager == null) {
            return false;
        }
        //minor check
        try {
            mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
            Tracer.log(TAG, "getLocation: ++ GPS Enabled");
        }catch (NullPointerException npe){
            Tracer.log(TAG, "getLocation.NullPointerException: ", npe);
        } catch (SecurityException se) {
            Tracer.log(TAG, "getLocation.SecurityException: ", se);
        }catch (Exception e){
            Tracer.log(TAG, "getLocation.Exception: ", e);
        }
        return true;
    }

    void stopLocationUpdate(){
        Tracer.log(TAG, "stopLocationUpdate");
        if(mLocationManager != null){
            mLocationManager.removeUpdates(locationListener);
        }
    }

    PositionObject getLastPosition(){
        Tracer.log(TAG, "getLastPosition");
        ///check les droits
        if(hasPermission() && hasProvider()) {
            Location location = getLastLocation();
            if (location != null) {
                mPosition = new PositionObject(location.getLatitude(), location.getLongitude(),
                        location.getAltitude());
                return mPosition;
            }
        }
        return null;
    }

    int checkGpsStatus(){
        Tracer.log(TAG, "checkGpsStatus");
        ///check les droits
        if(!hasPermission()) {
            return Const.gps.NO_PERMISSION;
        }
        if(!hasProvider()) {
            return Const.gps.NO_PROVIDER;
        }
        //good
        return Const.gps.NO_ERROR;
    }

    PositionObject getPosition(){
        return mPosition;
    }



    //----------------------------------------------------------------------------------------------
    // NESTED CLASS

    private class LocationListener implements android.location.LocationListener{
        @Override
        public void onLocationChanged(Location location) {
            Tracer.log(TAG, "onLocationChanged");
            //Called when the location has changed.
            mPosition = new PositionObject(location.getLatitude(),
                    location.getLongitude(), location.getAltitude());
            //on avertit le TrackerService que notre position a change
            if (mTrackerBinder != null) {
                mTrackerBinder.onGpsPositionUpdate();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Tracer.log(TAG, "onProviderDisabled");
            //Called when the provider is disabled by the user.
        }

        @Override
        public void onProviderEnabled(String provider) {
            Tracer.log(TAG, "onProviderEnabled");
            //Called when the provider is enabled by the user.
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Tracer.log(TAG, "onStatusChanged");
            // Called when the provider status changes. This method is called when a provider is unable
            // to fetch a location or if the provider has recently
            // become available after a period of unavailability.
        }
    }





}
