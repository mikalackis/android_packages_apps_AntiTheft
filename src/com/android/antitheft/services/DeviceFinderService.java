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

import com.android.antitheft.DeviceInfo;
import com.android.antitheft.ParseHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.accounts.Account;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

public class DeviceFinderService extends Service implements LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks,  GooglePlayServicesClient.OnConnectionFailedListener{

    private static final String TAG = DeviceFinderService.class.getSimpleName();
    private static PowerManager.WakeLock sWakeLock;

    private static final String EXTRA_ACCOUNT = "account";
    private static final String EXTRA_KEY_ID = "key_id";

    private static final int LOCATION_UPDATE_INTERVAL = 5000;
    private static final int MAX_LOCATION_UPDATES = 10;
    private static final int LOCATION_ACCURACY_THRESHOLD = 5; //meters

    private LocationClient mLocationClient;
    private Location mLastLocationUpdate;
    private String mKeyId;

    private int mUpdateCount = 0;

    private boolean mIsRunning = false;

    public static void reportLocation(Context context) {
        if (sWakeLock == null) {
            PowerManager pm = (PowerManager)
                    context.getSystemService(Context.POWER_SERVICE);
            sWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        }
        if (!sWakeLock.isHeld()) {
            sWakeLock.acquire();
        }
        Intent intent = new Intent(context, DeviceFinderService.class);
        context.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mIsRunning) {
            final Context context = getApplicationContext();
            mIsRunning = true;
            final ContentResolver contentResolver = getContentResolver();
            try {
                int currentLocationMode = Settings.Secure.getInt(contentResolver, Settings.Secure.LOCATION_MODE);
                if (currentLocationMode != Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {
                    Settings.Secure.putInt(contentResolver, Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
                }
            } catch (SettingNotFoundException e) {
            	Log.e(TAG, "Unable find location settings.", e);
            }
            mLocationClient = new LocationClient(context, this, this);
            mLocationClient.connect();
        }

        // Reset the session
//        if (intent != null) {
//            Bundle extras = intent.getExtras();
//            if (extras != null) mKeyId = extras.getString(EXTRA_KEY_ID);
//        }

        if (mLocationClient.isConnected()) {
            restartLocationUpdates();
        }

        return START_STICKY;
    }

    private LocationRequest getLocationRequest() {
        return LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(LOCATION_UPDATE_INTERVAL)
                .setNumUpdates(MAX_LOCATION_UPDATES);
    }

    private void restartLocationUpdates() {
        mUpdateCount = 0;
        Location lastLocation = mLocationClient.getLastLocation();
        if (lastLocation != null) {
            onLocationChanged(lastLocation, true);
        }
        mLocationClient.requestLocationUpdates(getLocationRequest(), this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sWakeLock != null) {
            sWakeLock.release();
        }
        mIsRunning = false;
    }

    @Override
    public void onLocationChanged(final Location location) {
        onLocationChanged(location, false);
    }

    private void onLocationChanged(final Location location, boolean fromLastLocation) {
        mLastLocationUpdate = location;
        if (!fromLastLocation) mUpdateCount++;
        ParseHelper.initializeLocationParseObject(DeviceInfo.getIMEI(this), location.getLatitude(), location.getLongitude()).saveInBackground();
        // call parse here
    }

    @Override
    public void onConnected(Bundle bundle) {
        restartLocationUpdates();
    }

    @Override
    public void onDisconnected() {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        stopSelf();
    }

    private void maybeStopLocationUpdates(float accuracy) {
        // if mUpdateCount, then this is a case we have the last known location. Don't stop in that case.
        if ((mUpdateCount != 0) && (accuracy <= LOCATION_ACCURACY_THRESHOLD || mUpdateCount == MAX_LOCATION_UPDATES)) {
            stopUpdates();
        }
    }

    private void stopUpdates() {
        mLocationClient.removeLocationUpdates(this);
        mLocationClient.disconnect();
        stopSelf();
    }
}
