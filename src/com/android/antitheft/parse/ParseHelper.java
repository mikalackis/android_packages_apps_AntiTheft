
package com.android.antitheft.parse;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

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
import com.android.antitheft.util.PubNubManager;
import com.parse.ParseObject;
import com.parse.ParseGeoPoint;
import com.parse.ParseFile;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ConfigCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.GetCallback;
import com.parse.SaveCallback;


public class ParseHelper {

    private static final String TAG = "ParseHelper";

    public static final String GOOGLE_MAPS_URL = "http://maps.google.com/?q=%1$f,%2$f";

    public static void antiTheftOnline(final Context context) {
        ActivityParseObject activityObject = new ActivityParseObject();
        activityObject.setAction("AntiTheft online");
        activityObject.setImei(DeviceInfo.getInstance().getIMEI());
        activityObject.saveInBackground(new ParseSaveCallback("AntiTheft online"));
        if (DeviceInfo.getInstance().getArielSystemStatus() == Config.ANTITHEFT_STATE.THEFT.getState()) {
            AntiTheftCommandUtil.getCommandByKey(AntiTheftCommandUtil.KEY_THEFT).executeCommand(AntiTheftCommandUtil.LOCKDOWN);
        } else {
            AntiTheftCommandUtil.getCommandByKey(AntiTheftCommandUtil.KEY_LOCATION).executeCommand(AntiTheftCommandUtil.WHERE);
        }

        getDeviceConfiguration();
    }

    private static void getDeviceConfiguration(){
        //first, fetch local deviceconfiguration
        ParseQuery<DeviceConfiguration> localQuery = ParseQuery.getQuery(DeviceConfiguration.class);
        localQuery.fromLocalDatastore();
        localQuery.getInBackground(DeviceInfo.getInstance().getIMEI(), new GetCallback<DeviceConfiguration>() {
            @Override
            public void done(DeviceConfiguration deviceConfiguration, ParseException e) {
                if (e == null) {
                    Log.i(TAG, "*******************************************");
                    Log.i(TAG, "Retrieved DeviceConfiguration from localdatastore");
                    Log.i(TAG, "clientId=" + deviceConfiguration.getClientId());
                    Log.i(TAG, "masterDevice=" + deviceConfiguration.isMasterDevice());
                    Log.i(TAG, "*******************************************");
                    DeviceInfo.getInstance().setDeviceConfiguration(deviceConfiguration);
                    PubNubManager.getInstance().preparePubNub();
                } else {
                    Log.i(TAG, "DeviceConfiguration error");
                    e.printStackTrace();
                }
            }
        });

        getConfigFromServer();

    }

    public static void getConfigFromServer(){
        // now get the one from the server
        Log.i(TAG, "Searching for config, deviceID: " + DeviceInfo.getInstance().getIMEI());
        if (DeviceInfo.getInstance().getIMEI() != null && DeviceInfo.getInstance().getIMEI().length() > 0) {
            ParseQuery<DeviceConfiguration> query = ParseQuery.getQuery(DeviceConfiguration.class);
            query.getInBackground(DeviceInfo.getInstance().getIMEI(), new GetCallback<DeviceConfiguration>() {
                public void done(DeviceConfiguration deviceConfiguration, ParseException e) {
                    if (e == null) {
                        Log.i(TAG, "*******************************************");
                        Log.i(TAG, "Retrieved DeviceConfiguration");
                        Log.i(TAG, "clientId=" + deviceConfiguration.getClientId());
                        Log.i(TAG, "masterDevice=" + deviceConfiguration.isMasterDevice());
                        Log.i(TAG, "*******************************************");
                        deviceConfiguration.pinInBackground("AntiTheft",
                                new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                    }
                                });
                        DeviceInfo.getInstance().setDeviceConfiguration(deviceConfiguration);
                        PubNubManager.getInstance().preparePubNub();
                    } else {
                        Log.i(TAG, "DeviceConfiguration error");
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void parseInit(final Context context) {
        if (Config.DEBUG) {
            Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
        } else {
            Parse.setLogLevel(Parse.LOG_LEVEL_ERROR);
        }
        // init parse Objects
        ParseObject.registerSubclass(ActivityParseObject.class);
        ParseObject.registerSubclass(FileParseObject.class);
        ParseObject.registerSubclass(LocationParseObject.class);
        ParseObject.registerSubclass(DeviceConfiguration.class);

        Parse.initialize(new Parse.Configuration.Builder(context)
                .applicationId(context.getString(R.string.parse_app_id))
                .clientKey(null)
                .server("http://arielparseserver-main.us-east-1.elasticbeanstalk.com/parse/")
                .enableLocalDataStore()
                .build());
        ParseInstallation.getCurrentInstallation().saveInBackground();

    }

}
