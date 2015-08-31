package com.android.antitheft.sms;

import com.android.antitheft.services.DeviceFinderService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class AntiTheftSMSReceiver extends BroadcastReceiver{
	
	private final String TAG="SMSReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Bundle pudsBundle = intent.getExtras();
        Object[] pdus = (Object[]) pudsBundle.get("pdus");
        SmsMessage messages =SmsMessage.createFromPdu((byte[]) pdus[0]);    
        Log.i(TAG,  messages.getMessageBody());
        if(messages.getMessageBody().equals(AntiTheftSMSConstants.AT_PING)) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(messages.getOriginatingAddress(), null, "PONG", null, null);
        }
        else if(messages.getMessageBody().equals(AntiTheftSMSConstants.AT_LOCATION_PING)){
        	DeviceFinderService.reportLocation(context);
        	SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(messages.getOriginatingAddress(), null, "Location service started", null, null);
        }
	}

}
