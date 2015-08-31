package com.android.antitheft;

import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;

import android.content.Context;
import android.telecom.Log;
import android.telephony.TelephonyManager;

public class DeviceInfo {
	
	private static final String TAG = "DeviceInfo";
	
	public static String getIMEI(final Context context){
		TelephonyManager telephonyManager =
	            (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		Log.i(TAG,telephonyManager.getDeviceId());
		return telephonyManager.getDeviceId() != null ? telephonyManager.getDeviceId() : "";
	}

}
