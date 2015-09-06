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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.android.antitheft.activities.AntiTheftPreferences;
import com.android.antitheft.util.SharedPreferencesUtility;
import com.android.antitheft.receivers.AntiTheftBootReceiver;
import com.android.antitheft.sms.AntiTheftSMSReceiver;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

public class AntiTheftApplication extends Application {

    private static final String TAG = "AntiTheftApplication";
    
    private static AntiTheftApplication mInstance;

    private TelephonyManager mTelephonyManager;
    private AntiTheftBootReceiver mBootReceiver;
    private AntiTheftSMSReceiver mSMSReceiver;
    
    public static AntiTheftApplication getInstance(){
    	return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "AntiTheft app created");
        
        mInstance = this;
        
        SharedPreferencesUtility.init(this);
        
        boolean isEnabled = SharedPreferencesUtility.getInstance().getBoolPreference(SharedPreferencesUtility.ENABLE_ANTITHEFT, false);
        if(isEnabled){
        	mBootReceiver = new AntiTheftBootReceiver();
            mSMSReceiver = new AntiTheftSMSReceiver();
            Parse.initialize(this, "BvtKyhjpEjZ1raBviAITO5zdKxxf4ExUIM70TzuD", "VapasvHYrYObD42EAE9h6Jt5k788wYFm1Uu4cgFb");
        }
        else{
        	mBootReceiver = null;
        	mSMSReceiver = null;
        }
        
        if(Config.DEBUG){
        	Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
        }
        
    }
    
    public void registerReceivers(){
    	Toast.makeText(this, "Registering receivers...", Toast.LENGTH_LONG).show();
    	IntentFilter filter=new IntentFilter();
        filter.addAction(Config.INTENT_BOOT);
        filter.addAction(Config.INTENT_SMS_RECEIVED);
        registerReceiver(mBootReceiver, filter);
        registerReceiver(mSMSReceiver, filter);
    }
    
    public void unregisterReceivers(){
    	Toast.makeText(this, "Unregistering receivers...", Toast.LENGTH_LONG).show();
    	if(mBootReceiver!=null){
    		try{
    			unregisterReceiver(mBootReceiver);
    		}
    		catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    	
    	if(mSMSReceiver!=null){
    		try{
    			unregisterReceiver(mSMSReceiver);
    		}
    		catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    }
    
    /**
     * Makes sure that all the SystemUI services are running. If they are already running, this is a
     * no-op. This is needed to conditinally start all the services, as we only need to have it in
     * the main process.
     *
     * <p>This method must only be called from the main thread.</p>
     */
//    public void startServicesIfNeeded() {
//        if (mServicesStarted) {
//            return;
//        }
//
//        if (!mBootCompleted) {
//            // check to see if maybe it was already completed long before we began
//            // see ActivityManagerService.finishBooting()
//            if ("1".equals(SystemProperties.get("sys.boot_completed"))) {
//                mBootCompleted = true;
//                if (DEBUG) Log.v(TAG, "BOOT_COMPLETED was already sent");
//            }
//        }
//
//        Log.v(TAG, "Starting SystemUI services.");
//        final int N = SERVICES.length;
//        for (int i=0; i<N; i++) {
//            Class<?> cl = SERVICES[i];
//            if (DEBUG) Log.d(TAG, "loading: " + cl);
//            try {
//                mServices[i] = (SystemUI)cl.newInstance();
//            } catch (IllegalAccessException ex) {
//                throw new RuntimeException(ex);
//            } catch (InstantiationException ex) {
//                throw new RuntimeException(ex);
//            }
//            mServices[i].mContext = this;
//            mServices[i].mComponents = mComponents;
//            if (DEBUG) Log.d(TAG, "running: " + mServices[i]);
//            mServices[i].start();
//
//            if (mBootCompleted) {
//                mServices[i].onBootCompleted();
//            }
//        }
//        mServicesStarted = true;
//    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
//        if (mServicesStarted) {
//            int len = mServices.length;
//            for (int i = 0; i < len; i++) {
//                mServices[i].onConfigurationChanged(newConfig);
//            }
//        }
    }

//    @SuppressWarnings("unchecked")
//    public <T> T getComponent(Class<T> interfaceType) {
//        return (T) mComponents.get(interfaceType);
//    }
//
//    public SystemUI[] getServices() {
//        return mServices;
//    }
}
