
package com.android.antitheft;

import com.android.antitheft.listeners.AntiTheftPhoneStateListener;
import com.android.antitheft.parse.DeviceConfiguration;
import com.android.antitheft.util.ArielAlarmManager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Date;
import java.util.UUID;

public class DeviceInfo {

    private static final String TAG = "DeviceInfo";

    public static final String UUID_PREFIX = "ariel";
    
    private static DeviceInfo mInstance;

    private DeviceConfiguration mDeviceConfiguration;
    
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

    public String getIMEI() {
        //return mTelephonyManager.getDeviceId() != null ? mTelephonyManager.getDeviceId() : "";
        Log.i(TAG, getUniquePsuedoID());
        return getUniquePsuedoID();
    }

    /**
     * Listen to service state changes
     * TODO check how to detect roaming
     */
    public void registerServiceStateListener() {
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

    public DeviceConfiguration getDeviceConfiguration() {
        return mDeviceConfiguration;
    }

    public void setDeviceConfiguration(DeviceConfiguration mDeviceConfiguration) {
        this.mDeviceConfiguration = mDeviceConfiguration;
        Log.i(AntiTheftApplication.TAG, "Device lease start: " + mDeviceConfiguration.getLeaseStartDate());
        Log.i(AntiTheftApplication.TAG, "Device lease end: " + mDeviceConfiguration.getLeaseEndDate());
        Date today = new Date();
        if(mDeviceConfiguration.getLeaseEndDate().after(today) && mDeviceConfiguration.isExpirationLock()) {
            ArielAlarmManager.getInstance().setAlarm(mDeviceConfiguration.getLeaseEndDate());
        }
    }

    public String getUniquePsuedoID() {
        // If all else fails, if the user does have lower than API 9 (lower
        // than Gingerbread), has reset their device or 'Secure.ANDROID_ID'
        // returns 'null', then simply the ID returned will be solely based
        // off their Android device information. This is where the collisions
        // can happen.
        // Thanks http://www.pocketmagic.net/?p=1662!
        // Try not to use DISPLAY, HOST or ID - these items could change.
        // If there are collisions, there will be overlapping data
        String m_szDevIDShort = UUID_PREFIX + (Build.BOARD.length() % 10)
                + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10)
                + (Build.DEVICE.length() % 10)
                + (Build.MANUFACTURER.length() % 10)
                + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

        // Thanks to @Roman SL!
        // http://stackoverflow.com/a/4789483/950427
        // Only devices with API >= 9 have android.os.Build.SERIAL
        // http://developer.android.com/reference/android/os/Build.html#SERIAL
        // If a user upgrades software or roots their device, there will be a
        // duplicate entry
        String serial = null;
        try {
            serial = Build.class.getField("SERIAL").get(null)
                    .toString();

            // Go ahead and return the serial for api => 9
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode())
                    .toString();
        } catch (Exception exception) {
            // String needs to be initialized
            serial = "serial"; // some value
        }

        // Thanks @Joe!
        // http://stackoverflow.com/a/2853253/950427
        // Finally, combine the values we have found by using the UUID class to
        // create a unique identifier
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode())
                .toString();
    }

}
