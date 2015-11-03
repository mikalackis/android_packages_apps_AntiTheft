
package com.android.antitheft.sms;

import com.android.antitheft.AntiTheftApplication;
import com.android.antitheft.Config;
import com.android.antitheft.DeviceInfo;
import com.android.antitheft.ParseHelper;
import com.android.antitheft.lockscreen.LockPatternUtilsHelper;
import com.android.antitheft.security.AntiTheftSecurityHelper;
import com.android.antitheft.services.DeviceFinderService;
import com.android.antitheft.services.WhosThatService;
import com.android.antitheft.services.WhosThatSoundService;
import com.android.antitheft.services.DeviceFinderService.DeviceFinderServiceBinder;
import com.android.antitheft.util.PrefUtils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.telephony.SmsManager;

/**
 * @author mikalackis
 */
public class AntiTheftSMSOperator {

    public static void checkMessage(final Context mContext, final String msg, final String returnNumber) {
        if (msg.equals(AntiTheftSMSConstants.WHERE)) {
            DeviceFinderService.startAntiTheftService(DeviceFinderService.class.getName(),
                    AntiTheftApplication.getInstance(), Config.ANTITHEFT_STATE.NORMAL.getState());
            reportStatusToSender(returnNumber, "Location service started");
            ParseHelper.initializeActivityParseObject(AntiTheftSMSConstants.WHERE,
                    DeviceInfo.getInstance().getIMEI()).saveEventually();
        }
        else if (msg.equals(AntiTheftSMSConstants.SMILE)) {
            // take picture
            reportStatusToSender(returnNumber, "Taking picture");
            WhosThatService.startAntiTheftService(WhosThatService.class.getName(),
                    AntiTheftApplication.getInstance(),
                    WhosThatService.CAMERA_FACETRACK_IMAGE);
            ParseHelper.initializeActivityParseObject(AntiTheftSMSConstants.SMILE,
                    DeviceInfo.getInstance().getIMEI()).saveEventually();
        }
        else if (msg.equals(AntiTheftSMSConstants.SMILE_NOW)) {
            // take picture
            reportStatusToSender(returnNumber, "Taking picture");
            WhosThatService.startAntiTheftService(WhosThatService.class.getName(),
                    AntiTheftApplication.getInstance(),
                    WhosThatService.CAMERA_IMAGE);
            ParseHelper.initializeActivityParseObject(AntiTheftSMSConstants.SMILE_NOW,
                    DeviceInfo.getInstance().getIMEI()).saveEventually();
        }
        else if (msg.equals(AntiTheftSMSConstants.STOP_SMILE)) {
            // stop take picture
            reportStatusToSender(returnNumber, "Stop taking picture");
            AntiTheftApplication.getInstance().stopService(
                    new Intent(AntiTheftApplication.getInstance(), WhosThatService.class));
            ParseHelper.initializeActivityParseObject(AntiTheftSMSConstants.STOP_SMILE,
                    DeviceInfo.getInstance().getIMEI()).saveEventually();
        }
        else if (msg.equals(AntiTheftSMSConstants.ACTOR)) {
            // take video
            reportStatusToSender(returnNumber, "Taking video");
            WhosThatService.startAntiTheftService(WhosThatService.class.getName(),
                    AntiTheftApplication.getInstance(),
                    WhosThatService.CAMERA_VIDEO);
            ParseHelper.initializeActivityParseObject(AntiTheftSMSConstants.ACTOR,
                    DeviceInfo.getInstance().getIMEI()).saveEventually();
        }
        else if (msg.equals(AntiTheftSMSConstants.SCREEN_LOCK)) {
            // change pin code and lock screen, disable power button, perform wipe etc
            reportStatusToSender(returnNumber, "Screenlock initialized");
            LockPatternUtilsHelper.performAdminLock(Config.LOCK_SCREEN_PASS, mContext);
            ParseHelper.initializeActivityParseObject(AntiTheftSMSConstants.SCREEN_LOCK,
                    DeviceInfo.getInstance().getIMEI()).saveEventually();
        }
        else if (msg.equals(AntiTheftSMSConstants.LOCKDOWN)) {
            PrefUtils.getInstance().setIntegerPreference(PrefUtils.ANTITHEFT_MODE,
                    Config.ANTITHEFT_STATE.LOCKDOWN.getState());
            // full device lockdown
            // first step: lock the screen
            LockPatternUtilsHelper.performAdminLock(Config.LOCK_SCREEN_PASS, mContext);
            WhosThatService.startAntiTheftService(WhosThatService.class.getName(),
                AntiTheftApplication.getInstance(), WhosThatService.CAMERA_FACETRACK_IMAGE);
            WhosThatSoundService.startAntiTheftService(WhosThatSoundService.class.getName(),
                AntiTheftApplication.getInstance(), -1);
            ParseHelper.initializeActivityParseObject(AntiTheftSMSConstants.LOCKDOWN,
                    DeviceInfo.getInstance().getIMEI()).saveEventually();
        }
        else if (msg.equals(AntiTheftSMSConstants.IM_BACK)) {
            PrefUtils.getInstance().setIntegerPreference(PrefUtils.ANTITHEFT_MODE,
                    Config.ANTITHEFT_STATE.NORMAL.getState());
            // full device lockdown
            // first step: lock the screen
            LockPatternUtilsHelper.clearLock(mContext);
            ParseHelper.initializeActivityParseObject(AntiTheftSMSConstants.IM_BACK,
                    DeviceInfo.getInstance().getIMEI()).saveEventually();
        }
        else if (msg.equals(AntiTheftSMSConstants.TRACK_ME_START)) {
            DeviceFinderService.startAntiTheftService(DeviceFinderService.class.getName(),
                    AntiTheftApplication.getInstance(), Config.ANTITHEFT_STATE.LOCKDOWN.getState());
            ParseHelper.initializeActivityParseObject(AntiTheftSMSConstants.TRACK_ME_START,
                    DeviceInfo.getInstance().getIMEI()).saveEventually();
        }
        else if (msg.equals(AntiTheftSMSConstants.TRACK_ME_STOP)) {
            AntiTheftApplication.getInstance().stopService(
                    new Intent(AntiTheftApplication.getInstance(), DeviceFinderService.class));
            ParseHelper.initializeActivityParseObject(AntiTheftSMSConstants.TRACK_ME_STOP,
                    DeviceInfo.getInstance().getIMEI()).saveEventually();
        }
        else if (msg.equals(AntiTheftSMSConstants.TALK)) {
            // take picture
            reportStatusToSender(returnNumber, "Recording sound");
            WhosThatSoundService.startAntiTheftService(WhosThatSoundService.class.getName(),
                    AntiTheftApplication.getInstance(), -1);
            ParseHelper.initializeActivityParseObject(AntiTheftSMSConstants.TALK,
                    DeviceInfo.getInstance().getIMEI()).saveEventually();
        }

    }

    private static void reportStatusToSender(final String sender, final String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(sender, null, message, null, null);
    }

}
