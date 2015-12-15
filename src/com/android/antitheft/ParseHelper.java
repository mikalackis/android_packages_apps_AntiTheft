
package com.android.antitheft;

import android.content.Context;
import android.telecom.Log;

import com.android.antitheft.listeners.ParseSaveCallback;
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
import com.parse.ConfigCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;

public class ParseHelper {
    
    private static final String TAG = "ParseHelper";

    private static ParseConfig parseConfig;
    private static long configLastFetchedTime;

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
                .saveEventually(new ParseSaveCallback("AntiTheft online"));
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
        if (Config.DEBUG){
            Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
        }
        else{
            Parse.setLogLevel(Parse.LOG_LEVEL_ERROR);
        }
        String appId = PrefUtils.getInstance().getStringPreference(PrefUtils.PARSE_APP_ID, null);
        String clientKey = PrefUtils.getInstance().getStringPreference(PrefUtils.PARSE_CLIENT_KEY, null);
        if(appId!=null && appId.length()>0 && clientKey!=null && clientKey.length()>0){
            Parse.initialize(context, appId, clientKey);
            ParseInstallation.getCurrentInstallation().saveInBackground();
            AntiTheftNotifier.notifyAntiTheftState(context,true);
        }
        else{
            AntiTheftNotifier.notifyAntiTheftState(context,false);
        }
        
    }
    
    public static void fetchConfigIfNeeded() {
        final long configRefreshInterval = 60 * 60; // 1 hour

        if (parseConfig == null ||
                System.currentTimeMillis() - configLastFetchedTime > configRefreshInterval) {
            // Set the config to current, just to load the cache
            parseConfig = ParseConfig.getCurrentConfig();

            // Set the current time, to flag that the operation started and prevent double fetch
            ParseConfig.getInBackground(new ConfigCallback() {
                @Override
                public void done(ParseConfig parseConfig, ParseException e) {
                    if (e == null) {
                        // Yay, retrieved successfully
                        parseConfig = parseConfig;
                        configLastFetchedTime = System.currentTimeMillis();
                    } else {
                        // Fetch failed, reset the time
                        configLastFetchedTime = 0;
                    }
                }
            });
        }
    }

}
