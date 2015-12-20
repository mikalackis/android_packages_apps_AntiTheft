
package com.android.antitheft.parse;

import android.content.Context;
import android.telecom.Log;

import com.android.antitheft.AntiTheftApplication;
import com.android.antitheft.Config;
import com.android.antitheft.DeviceInfo;
import com.android.antitheft.Config.ANTITHEFT_STATE;
import com.android.antitheft.R;
import com.android.antitheft.commands.AntiTheftCommandUtil;
import com.android.antitheft.commands.TheftModeCommand;
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

    public static final String GOOGLE_MAPS_URL = "http://maps.google.com/?q=%1$f,%2$f";

    public static void antiTheftOnline(final Context context) {
        ActivityParseObject activityObject = new ActivityParseObject();
        activityObject.setAction("AntiTheft online");
        activityObject.setImei(DeviceInfo.getInstance().getIMEI());
        activityObject.saveInBackground(new ParseSaveCallback("AntiTheft online"));
        int mCurrentState = PrefUtils.getInstance().getIntegerPreference(PrefUtils.ANTITHEFT_MODE,
                Config.ANTITHEFT_STATE.NORMAL.getState());
        if (mCurrentState == Config.ANTITHEFT_STATE.LOCKDOWN.getState()) {
            AntiTheftCommandUtil.getCommandByKey(AntiTheftCommandUtil.KEY_THEFT).executeCommand(AntiTheftCommandUtil.LOCKDOWN);
        }
        else{
            AntiTheftCommandUtil.getCommandByKey(AntiTheftCommandUtil.KEY_LOCATION).executeCommand(AntiTheftCommandUtil.WHERE);
        }
    }

    public static void parseInit(final Context context) {
        if (Config.DEBUG) {
            Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
        }
        else {
            Parse.setLogLevel(Parse.LOG_LEVEL_ERROR);
        }
        // init parse Objects
        ParseObject.registerSubclass(ActivityParseObject.class);
        ParseObject.registerSubclass(FileParseObject.class);
        ParseObject.registerSubclass(LocationParseObject.class);

        String appId = PrefUtils.getInstance().getStringPreference(PrefUtils.PARSE_APP_ID, context.getString(R.string.parse_app_id));
        String clientKey = PrefUtils.getInstance().getStringPreference(PrefUtils.PARSE_CLIENT_KEY,
                context.getString(R.string.parse_client_key));
        if (appId != null && appId.length() > 0 && clientKey != null && clientKey.length() > 0) {
            Parse.initialize(context, appId, clientKey);
            ParseInstallation.getCurrentInstallation().saveInBackground();
            AntiTheftNotifier.notifyAntiTheftState(context, true);
        }
        else {
            AntiTheftNotifier.notifyAntiTheftState(context, false);
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
