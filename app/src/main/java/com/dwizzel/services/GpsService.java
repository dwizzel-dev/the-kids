package com.dwizzel.services;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.dwizzel.models.PositionModel;
import com.dwizzel.thekids.R;
import com.dwizzel.utils.Tracer;

/**
 * Created by Dwizzel on 16/11/2017.
 */

/*
* TODO: meilleur gestion des exceptions de connectivity et permissions
*
* */

public class GpsService extends Service implements LocationListener {


    private PositionModel mPosition;
    private final static String TAG = "GpsService";
    private Context mContext;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    Location location; // location
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    protected LocationManager locationManager;

    public GpsService(){}

    public GpsService(Context context) {
        this.mContext = context;
        getLocation();
    }

    private boolean hasPermission() {
        Tracer.log(TAG, "hasPermission");
        if(mContext != null) {
            return (mContext.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
                            == PackageManager.PERMISSION_GRANTED
                    &&
                    mContext.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED
                    &&
                    mContext.checkCallingOrSelfPermission(android.Manifest.permission.INTERNET)
                            == PackageManager.PERMISSION_GRANTED
                    &&
                    mContext.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED
            );
        }
        return false;
    }

    public boolean canGetLocation() {
        Tracer.log(TAG, "canGetLocation");
        return this.canGetLocation;
    }

    public void showSettingsAlert(){
        Tracer.log(TAG, "showSettingsAlert");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle(R.string.alert_gps_title);
        alertDialog.setMessage(R.string.alert_gps_title);
        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);
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

    public Location getLocation() {
        Tracer.log(TAG, "getLocation");
        //check si on a les droits avant tout
        if(hasPermission()) {
            try {
                locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
                // getting GPS status
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                // getting network status
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }catch (NullPointerException npe){
                Tracer.log(TAG, "getLocation.NullPointerException: ", npe);
            } catch (Exception e) {
                Tracer.log(TAG, "getLocation.Exception: ", e);
            }
            //minor check
            if (isGPSEnabled || isNetworkEnabled) {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    try {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Tracer.log(TAG, "getLocation: ++ Network Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                mPosition = new PositionModel(location.getLatitude(),
                                        location.getLongitude(), location.getAltitude());
                            }
                        }
                    } catch (SecurityException se) {
                        Tracer.log(TAG, "getLocation.SecurityException: ", se);
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        try {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Tracer.log(TAG, "getLocation: ++ GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    mPosition = new PositionModel(location.getLatitude(),
                                            location.getLongitude(), location.getAltitude());
                                }
                            }
                        } catch (SecurityException se) {
                            Tracer.log(TAG, "getLocation.SecurityException: ", se);
                        }
                    }
                }
            }
        return location;
        }
    return null;
    }

    public void stopUsingGPS(){
        Tracer.log(TAG, "stopUsingGPS");
        if(locationManager != null){
            locationManager.removeUpdates(GpsService.this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Tracer.log(TAG, "onLocationChanged");
        mPosition = new PositionModel(location.getLatitude(),
                location.getLongitude(), location.getAltitude());
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

    @Override
    public IBinder onBind(Intent intent) {
        Tracer.log(TAG, "onBind");
        return null;
    }


}
