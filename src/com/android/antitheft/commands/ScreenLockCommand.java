
package com.android.antitheft.commands;

import android.content.Intent;

import com.android.antitheft.AntiTheftApplication;
import com.android.antitheft.Config;
import com.android.antitheft.DeviceInfo;
import com.android.antitheft.listeners.ParseSaveCallback;
import com.android.antitheft.lockscreen.LockPatternUtilsHelper;
import com.android.antitheft.parse.ActivityParseObject;
import com.android.antitheft.parse.ParseHelper;
import com.android.antitheft.services.DeviceFinderService;
import com.android.antitheft.services.WhosThatService;

public class ScreenLockCommand extends AntiTheftCommand {

    public ScreenLockCommand(final String key, final String command, final String description) {
        this.key = key;
        this.command = command;
        this.description = description;
    }

    @Override
    public void executeCommand(final String action) {
        LockPatternUtilsHelper.performAdminLock(Config.LOCK_SCREEN_PASS,
                AntiTheftApplication.getInstance());
        ActivityParseObject activityObject = new ActivityParseObject();
        activityObject.setAction(action);
        activityObject.setImei(DeviceInfo.getInstance().getIMEI());
        activityObject.saveEventually(new ParseSaveCallback(action));
    }

}
