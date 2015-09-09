package com.android.antitheft.receivers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.android.antitheft.AntiTheftApplication;
import com.android.antitheft.Config;
import com.android.antitheft.DeviceInfo;
import com.android.antitheft.ParseHelper;
import com.android.antitheft.lockscreen.LockPatternUtilsHelper;
import com.android.antitheft.security.AntiTheftSecurityHelper;
import com.android.antitheft.services.DeviceFinderService;
import com.android.antitheft.util.PrefUtils;
import com.android.internal.widget.LockPatternUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AntiTheftBootReceiver extends BroadcastReceiver {
	
	private static final String TAG = "AntiTheftApplication";

    @Override
    public void onReceive(Context context, Intent intent) {
    	Log.i(TAG, "Boot received, should send data ");
        ParseHelper.initializeActivityParseObject("AntiTheft online", DeviceInfo.getIMEI(context)).saveEventually();
        int mCurrentState  = PrefUtils.getInstance().getIntegerPreference(PrefUtils.ANTITHEFT_MODE, Config.ANTITHEFT_STATE.NORMAL.getState());
        if(mCurrentState == Config.ANTITHEFT_STATE.LOCKDOWN.getState()){
        	LockPatternUtilsHelper.performAdminLock(Config.LOCK_SCREEN_PASS, context);
        	// should start camera service
        }
        DeviceFinderService.reportLocation(AntiTheftApplication.getInstance(), mCurrentState);
    }
    
}