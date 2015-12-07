
package com.android.antitheft;

import android.content.Context;

import com.android.antitheft.lockscreen.LockPatternUtilsHelper;
import com.android.antitheft.services.DeviceFinderService;
import com.android.antitheft.services.WhosThatService;
import com.android.antitheft.services.WhosThatSoundService;
import com.android.antitheft.util.AntiTheftNotifier;
import com.android.antitheft.util.PrefUtils;
import com.parse.ParseObject;
import com.parse.ParseGeoPoint;
import com.parse.ParseFile;
import com.parse.Parse;
import com.parse.ParseInstallation;

public class ParseHelper {

    public static ParseObject initializeActivityParseObject(final String status,
            final String imei) {
        ParseObject parseObject = new ParseObject("ActivityMonitor");
        parseObject.put("status", status != null ? status : "");
        parseObject.put("imei", imei != null ? imei : "");
        return parseObject;
    }

    public static ParseObject initializeLocationParseObject(final String imei,
            final double lat, final double lon) {
        String googleMapsURL = "http://maps.google.com/?q=%1$f,%2$f";
        ParseGeoPoint point = new ParseGeoPoint(lat, lon);
        ParseObject parseObject = new ParseObject("LocationMonitor");
        parseObject.put("imei", imei != null ? imei : "");
        parseObject.put("location", point);
        parseObject.put("gmaps_url", String.format(googleMapsURL, lat, lon));
        return parseObject;
    }

    public static ParseObject initializeFileParseObject(final String imei,
            final byte[] data, final String fileName) {
        ParseFile file = new ParseFile(fileName, data);
        ParseObject parseObject = new ParseObject("FileMonitor");
        parseObject.put("whos_there", file);
        parseObject.put("imei", imei != null ? imei : "");
        return parseObject;
    }

    public static void antiTheftOnline(final Context context) {
        ParseHelper.initializeActivityParseObject("AntiTheft online",
                DeviceInfo.getInstance().getIMEI())
                .saveEventually();
        int mCurrentState = PrefUtils.getInstance().getIntegerPreference(PrefUtils.ANTITHEFT_MODE,
                Config.ANTITHEFT_STATE.NORMAL.getState());
        if (mCurrentState == Config.ANTITHEFT_STATE.LOCKDOWN.getState()) {
            LockPatternUtilsHelper.performAdminLock(Config.LOCK_SCREEN_PASS, context);
            WhosThatService.startAntiTheftService(WhosThatService.class.getName(),
                    AntiTheftApplication.getInstance(), WhosThatService.CAMERA_FACETRACK_IMAGE);
            WhosThatSoundService.startAntiTheftService(WhosThatSoundService.class.getName(),
                    AntiTheftApplication.getInstance(), -1);
        }
        DeviceFinderService.startAntiTheftService(DeviceFinderService.class.getName(),
                AntiTheftApplication.getInstance(), mCurrentState);
    }

    public static void parseInit(final Context context) {
        Parse.initialize(context);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        if (Config.DEBUG) {
            Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
        }
    }

}
