package com.android.antitheft.receivers;

import com.android.antitheft.DeviceInfo;
import com.android.antitheft.ParseHelper;
import com.android.antitheft.services.DeviceFinderService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AntiTheftBootReceiver extends BroadcastReceiver {
	
	private static final String TAG = "AntiTheftBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
    	Log.i(TAG, "Boot received, should send data");
        ParseHelper.initializeActivityParseObject("AntiTheft online", DeviceInfo.getIMEI(context), "LOCATION").saveEventually();
        DeviceFinderService.reportLocation(context);
    }
    
}