
package com.android.antitheft;

import com.android.antitheft.listeners.AntiTheftPhoneStateListener;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telecom.Log;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class DeviceInfo {
    
    private static DeviceInfo mInstance;
    
    private TelephonyManager mTelephonyManager;
    
    private ConnectivityManager mConnectivityManager;
    
    public static DeviceInfo getInstance(){
        if (mInstance==null) {
            mInstance = new DeviceInfo();
        }
        return mInstance;
    }
    
    private DeviceInfo(){
        mTelephonyManager = (TelephonyManager) AntiTheftApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
        mConnectivityManager=(ConnectivityManager)AntiTheftApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private static final String TAG = "DeviceInfo";

    public String getIMEI() {
        Log.i(TAG, mTelephonyManager.getDeviceId());
        return mTelephonyManager.getDeviceId() != null ? mTelephonyManager.getDeviceId() : "";
    }
    
    public void registerServiceStateListener(){
        mTelephonyManager.listen(new AntiTheftPhoneStateListener(), PhoneStateListener.LISTEN_SERVICE_STATE);
    }
    
    public void unregisterServiceStateListener(){
        mTelephonyManager.listen(new AntiTheftPhoneStateListener(), PhoneStateListener.LISTEN_NONE);
    }
    
    public boolean isDeviceRoaming(){
        NetworkInfo ni = mConnectivityManager.getActiveNetworkInfo();
        if(ni!=null){
            return ni.isRoaming();
        }
        return false;
    }

}
