
package com.android.antitheft.listeners;

import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.util.Log;

public class AntiTheftPhoneStateListener extends PhoneStateListener {
    
    /**
     * 0 - Normal operation condition, the phone is registered with an operator either in home network or in roaming.
     * 3 - Radio of telephony is explicitly powered off.
     * 1 - Phone is not registered with any operator, the phone can be currently searching a new operator to register to, or not searching to registration at all, or registration is denied, or radio signal is not available.
     */

    public static final String TAG = AntiTheftPhoneStateListener.class.getSimpleName();

    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        Log.i(TAG, "SERVICE_STATE: " + serviceState.getState());
    }

}
