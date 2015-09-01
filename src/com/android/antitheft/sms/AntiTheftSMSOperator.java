package com.android.antitheft.sms;

import com.android.antitheft.lockscreen.LockPatternUtilsHelper;
import com.android.antitheft.services.DeviceFinderService;

import android.content.Context;
import android.telephony.SmsManager;

/**
 * 
 * @author mikalackis
 *
 */
public class AntiTheftSMSOperator {
	
	private Context mContext;
	
	public AntiTheftSMSOperator(final Context context){
		mContext = context;
	}
	
	public void checkMessage(final String msg,final String returnNumber){
		if(msg.equals(AntiTheftSMSConstants.WHERE)) {
			DeviceFinderService.reportLocation(mContext);
        	reportStatusToSender(returnNumber, "Location service started");
        }
        else if(msg.equals(AntiTheftSMSConstants.SMILE)){
        	// take picture
        	reportStatusToSender(returnNumber, "Taking picture");
        }
        else if(msg.equals(AntiTheftSMSConstants.ACTOR)){
        	// take video
        	reportStatusToSender(returnNumber, "Taking video");
        }
        else if(msg.equals(AntiTheftSMSConstants.LOCKDOWN)){
        	//change pin code and lock screen, disable power button, perform wipe etc
        	reportStatusToSender(returnNumber, "Lockdown initialized");
        	LockPatternUtilsHelper lockHelper = new LockPatternUtilsHelper(mContext);
        	lockHelper.performAdminLock("bye bye", "6969");
        }
        else if(msg.equals(AntiTheftSMSConstants.SCREW_POWER)){
        	// disable power button
        	reportStatusToSender(returnNumber, "Power disable initialized");
        }
	}
	
	private void reportStatusToSender(final String sender, final String message){
		SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(sender, null, message, null, null);
	}

}
