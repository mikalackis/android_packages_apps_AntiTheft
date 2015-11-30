
package com.android.antitheft.listeners;

import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.PhoneStateListener;
import android.telephony.Rlog;
import android.telephony.ServiceState;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.antitheft.AntiTheftApplication;
//import com.android.internal.telephony.Phone;
//import com.android.phone.PhoneUtils;

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
        if(serviceState.getState() == 0){
            
            // When subscriptions are not ready, use default phone
            // if (mPhone == null) mPhone = PhoneGlobals.getPhone();
            
//            int subId = getDataSubscription();
//            Phone mPhone = PhoneUtils.getPhoneFromSubId(subId);
//            Log.i(TAG,"data roaming: "+mPhone.getDataRoamingEnabled());
//            mPhone.setDataRoamingEnabled(true);
//            Log.i(TAG,"data roaming new: "+mPhone.getDataRoamingEnabled());
        }
    }
    
//    public int getDataSubscription() {
//        int subId = SubscriptionManager.INVALID_SUBSCRIPTION_ID;
//
//        try {
//            subId = Settings.Global.getInt(AntiTheftApplication.getInstance().getContentResolver(),
//                    Settings.Global.MULTI_SIM_DATA_CALL_SUBSCRIPTION);
//        } catch (SettingNotFoundException snfe) {
//            Rlog.e(TAG, "Settings Exception Reading Dual Sim Data Call Values");
//        }
//        
//        Log.i(TAG, "Found subID: "+subId);
//
//        // FIXME can this be removed? We should not set defaults
//        int phoneId = SubscriptionManager.getPhoneId(subId);
//        if (phoneId < 0 || phoneId >= TelephonyManager.getDefault().getPhoneCount()) {
//            subId = 0;
//            Log.i(TAG, "Subscription is invalid..." + subId + " Set to 0");
//            setDataSubscription(subId);
//        }
//
//        return subId;
//    }
//    
//    //FIXME can this be removed, it is only called in getDataSubscription
//    public void setDataSubscription(int subId) {
//        boolean enabled;
//
//        Settings.Global.putInt(AntiTheftApplication.getInstance().getContentResolver(),
//                Settings.Global.MULTI_SIM_DATA_CALL_SUBSCRIPTION, subId);
//        Log.i(TAG, "setDataSubscription: " + subId);
//
//        // Update the current mobile data flag
//        enabled = Settings.Global.getInt(AntiTheftApplication.getInstance().getContentResolver(),
//                Settings.Global.MOBILE_DATA + subId, 0) != 0;
//        Settings.Global.putInt(AntiTheftApplication.getInstance().getContentResolver(),
//                Settings.Global.MOBILE_DATA, enabled ? 1 : 0);
//        Log.i(TAG, "set mobile_data: " + enabled);
//
//        // Update the current data roaming flag
//        enabled = Settings.Global.getInt(AntiTheftApplication.getInstance().getContentResolver(),
//                Settings.Global.DATA_ROAMING + subId, 0) != 0;
//        Settings.Global.putInt(AntiTheftApplication.getInstance().getContentResolver(),
//                Settings.Global.DATA_ROAMING, enabled ? 1 : 0);
//        Log.i(TAG, "set data_roaming: " + enabled);
//    }

}
