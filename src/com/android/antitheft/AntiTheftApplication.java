/*
 * Copyright (C) 2014 The Android Open Source Project
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
 * limitations under the License
 */

package com.android.antitheft;

import android.app.Application;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.antitheft.listeners.AntiTheftPhoneStateListener;
import com.android.antitheft.receivers.AntiTheftBootReceiver;
import com.android.antitheft.receivers.AntiTheftSMSReceiver;
import com.android.antitheft.security.AntiTheftSecurityHelper;
import com.android.antitheft.util.AntiTheftNotifier;
import com.android.antitheft.util.PrefUtils;

public class AntiTheftApplication extends Application {

    private static final String TAG = "AntiTheftApplication";

    private static AntiTheftApplication mInstance;

    public static AntiTheftApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "AntiTheft app created");

        mInstance = this;
        PrefUtils.init(this);
        ParseHelper.parseInit(this);
        DeviceInfo.getInstance().registerServiceStateListener();
        
        Settings.Global.putInt(getContentResolver(),
                Settings.Global.DATA_ROAMING, 1);
        
        Log.i(TAG,"DATA ROAMING: "+Settings.Global.getInt(getContentResolver(), Settings.Global.DATA_ROAMING, 0));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    }

    public void disableAllReceivers() {
        ComponentName bootReceiver = new ComponentName(getApplicationContext(),
                AntiTheftBootReceiver.class);
        getPackageManager().setComponentEnabledSetting(bootReceiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        ComponentName smsReceiver = new ComponentName(getApplicationContext(),
                AntiTheftSMSReceiver.class);
        getPackageManager().setComponentEnabledSetting(smsReceiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        final TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tm.listen(new AntiTheftPhoneStateListener(), PhoneStateListener.LISTEN_NONE);
    }

    public void enableAllReceivers() {
        ComponentName bootReceiver = new ComponentName(getApplicationContext(),
                AntiTheftBootReceiver.class);
        getPackageManager().setComponentEnabledSetting(bootReceiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        ComponentName smsReceiver = new ComponentName(getApplicationContext(),
                AntiTheftSMSReceiver.class);
        getPackageManager().setComponentEnabledSetting(smsReceiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        final TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tm.listen(new AntiTheftPhoneStateListener(), PhoneStateListener.LISTEN_SERVICE_STATE);
    }

}
