
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
import com.android.antitheft.services.WhosThatSoundService;
import com.android.antitheft.util.PrefUtils;

public class TheftModeCommand extends AntiTheftCommand {

    public TheftModeCommand(String key, String[] commands, String description) {
        super(key, commands, description);
    }

    @Override
    public void executeCommand(final String action) {
        reportActionToParse(action);
        if (action.equals(AntiTheftCommandUtil.LOCKDOWN)) {
            PrefUtils.getInstance().setIntegerPreference(PrefUtils.ANTITHEFT_MODE,
                    Config.ANTITHEFT_STATE.LOCKDOWN.getState());
            // full device lockdown
            // first step: lock the screen
            LockPatternUtilsHelper.performAdminLock(Config.LOCK_SCREEN_PASS,
                    AntiTheftApplication.getInstance());
            // start face track service
            AntiTheftCommandUtil.getCommandByKey(AntiTheftCommandUtil.KEY_IMAGE).executeCommand(AntiTheftCommandUtil.SMILE);
            // start sound recorder service
            AntiTheftCommandUtil.getCommandByKey(AntiTheftCommandUtil.KEY_SOUND).executeCommand(AntiTheftCommandUtil.TALK);
            // start location updates
            AntiTheftCommandUtil.getCommandByKey(AntiTheftCommandUtil.KEY_LOCATION).executeCommand(AntiTheftCommandUtil.TRACK_ME_START);
        }
        else if (action.equals(AntiTheftCommandUtil.IM_BACK)) {
            // restore device state
            PrefUtils.getInstance().setIntegerPreference(PrefUtils.ANTITHEFT_MODE,
                    Config.ANTITHEFT_STATE.NORMAL.getState());
            // clear device lockscreen
            LockPatternUtilsHelper.clearLock(AntiTheftApplication.getInstance());
            // stop face track service
            AntiTheftCommandUtil.getCommandByKey(AntiTheftCommandUtil.KEY_IMAGE).executeCommand(AntiTheftCommandUtil.STOP_SMILE);
            // stop sound recorder service
            AntiTheftCommandUtil.getCommandByKey(AntiTheftCommandUtil.KEY_SOUND).executeCommand(AntiTheftCommandUtil.TALK_STOP);
            // stop location updates
            AntiTheftCommandUtil.getCommandByKey(AntiTheftCommandUtil.KEY_LOCATION).executeCommand(AntiTheftCommandUtil.TRACK_ME_STOP);

        }
    }
}
