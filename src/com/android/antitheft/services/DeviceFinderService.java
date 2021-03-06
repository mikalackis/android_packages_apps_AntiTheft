/*
 * Copyright (C) 2013 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.antitheft.services;

import com.android.antitheft.Config;
import com.android.antitheft.DeviceInfo;
import com.android.antitheft.listeners.ParseSaveCallback;
import com.android.antitheft.parse.LocationParseObject;
import com.android.antitheft.parse.ParseHelper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

//import com.google.android.gms.common.GooglePlayServicesClient;
//import com.google.android.gms.location.LocationClient;
//
//

import android.accounts.Account;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.widget.Toast;

public class DeviceFinderService extends AntiTheftService implements LocationListener,
        ConnectionCallbacks, OnConnectionFailedListener {

    private static final String TAG = DeviceFinderService.class.getSimpleName();

    // Binder given to clients
    private final IBinder mBinder = new DeviceFinderServiceBinder();

    private static final int LOCATION_UPDATE_INTERVAL = 5000;
    private static final int MAX_LOCATION_UPDATES = 1;
    private static final int LOCATION_ACCURACY_THRESHOLD = 5; // meters
    private boolean mConstantReporting = false;

    protected GoogleApiClient mGoogleApiClient;
    private Location mLastLocationUpdate;
    private String mKeyId;
    private int mCurrentLocationmode = -1;

    private int mUpdateCount = 0;

    private boolean mIsRunning = false;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mIsRunning) {
            final Context context = getApplicationContext();
            mIsRunning = true;
            final ContentResolver contentResolver = getContentResolver();
            try {
                mCurrentLocationmode = Settings.Secure.getInt(contentResolver,
                        Settings.Secure.LOCATION_MODE);
                if (mCurrentLocationmode != Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {
                    Settings.Secure.putInt(contentResolver, Settings.Secure.LOCATION_MODE,
                            Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
                }
            } catch (SettingNotFoundException e) {
                Log.e(TAG, "Unable find location settings.", e);
            }
            int state = intent.getIntExtra(SERVICE_PARAM, Config.ANTITHEFT_STATE.NORMAL.getState());
            if (state == Config.ANTITHEFT_STATE.THEFT.getState()) {
                mConstantReporting = true;
            }

            buildGoogleApiClient();
        }

        if (mGoogleApiClient.isConnected()) {
            restartLocationUpdates();
        }

        return START_REDELIVER_INTENT;
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private LocationRequest getLocationRequest() {
        LocationRequest lr = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(LOCATION_UPDATE_INTERVAL);
        if (!mConstantReporting) {
            lr.setNumUpdates(MAX_LOCATION_UPDATES);
        }
        return lr;
    }

    private void restartLocationUpdates() {
        mUpdateCount = 0;
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation != null) {
            onLocationChanged(lastLocation, true);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, getLocationRequest(), this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsRunning = false;
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onLocationChanged(final Location location) {
        onLocationChanged(location, false);
    }

    private void onLocationChanged(final Location location, boolean fromLastLocation) {
        mLastLocationUpdate = location;
        if (!fromLastLocation)
            mUpdateCount++;
        LocationParseObject locationObject = new LocationParseObject();
        locationObject.setImei(DeviceInfo.getInstance().getIMEI());
        locationObject.setLatitudeLongitude(location.getLatitude(), location.getLongitude());
        locationObject.saveInBackground(new ParseSaveCallback("Location"));
        if (mLastLocationUpdate != null) {
            maybeStopLocationUpdates(mLastLocationUpdate.getAccuracy());
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        restartLocationUpdates();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        stopSelf();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    private void maybeStopLocationUpdates(float accuracy) {
        // if mUpdateCount, then this is a case we have the last known location. Don't stop in that
        // case.
        if (!mConstantReporting) {
            if ((mUpdateCount != 0)
                    && (accuracy <= LOCATION_ACCURACY_THRESHOLD || mUpdateCount == MAX_LOCATION_UPDATES)) {
                stopUpdates();
            }
        } else {
            Log.i(TAG, "Constant reporting in progress");
        }
    }

    public void stopUpdates() {
        // revert previous location settings
        final ContentResolver contentResolver = getContentResolver();
        Settings.Secure.putInt(contentResolver, Settings.Secure.LOCATION_MODE,
                mCurrentLocationmode);
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        stopSelf();
    }

    /**
     * Class used for the client Binder. Because we know this service always runs in the same
     * process as its clients, we don't need to deal with IPC.
     */
    public class DeviceFinderServiceBinder extends Binder {
        public DeviceFinderService getService() {
            // Return this instance of DeviceFinder so clients can call public methods
            return DeviceFinderService.this;
        }
    }

}
