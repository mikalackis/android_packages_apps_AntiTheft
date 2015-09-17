
package com.android.antitheft.listeners;

import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.util.Log;

import com.android.antitheft.services.DeviceFinderService;

public class AntiTheftPhoneStateListener extends PhoneStateListener {

    public static final String TAG = DeviceFinderService.class.getSimpleName();

    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        Log.i(TAG, "SERVICE_STATE: " + serviceState.getState());
    }

}
