
package com.android.antitheft.commands;

import android.content.ContentResolver;
import android.provider.ArielSettings;

import com.android.antitheft.AntiTheftApplication;
import com.android.antitheft.Config;
import com.android.antitheft.lockscreen.LockPatternUtilsHelper;
import com.android.antitheft.security.AntiTheftSecurityHelper;
import com.android.antitheft.util.PrefUtils;

public class TheftModeCommand extends AntiTheftCommand {

    public TheftModeCommand(String key, String[] commands, String description) {
        super(key, commands, description);
    }

    @Override
    public void executeCommand(final String action) {
        reportActionToParse(action);
        ContentResolver resolver = AntiTheftApplication.getInstance().getContentResolver();
        if (action.equals(AntiTheftCommandUtil.LOCKDOWN)) {
            ArielSettings.Secure.putInt(resolver, ArielSettings.Secure.ARIEL_SYSTEM_STATUS, Config.ANTITHEFT_STATE.THEFT.getState());
            // full device lockdown
            // first step: lock the screen
            LockPatternUtilsHelper.performAdminLock(Config.LOCK_SCREEN_PASS,
                    AntiTheftApplication.getInstance());
            // start face track service
            AntiTheftCommandUtil.getCommandByKey(AntiTheftCommandUtil.KEY_IMAGE).executeCommand(
                    AntiTheftCommandUtil.SMILE);
            // start sound recorder service
            AntiTheftCommandUtil.getCommandByKey(AntiTheftCommandUtil.KEY_SOUND).executeCommand(
                    AntiTheftCommandUtil.TALK);
            // start location updates
            AntiTheftCommandUtil.getCommandByKey(AntiTheftCommandUtil.KEY_LOCATION).executeCommand(
                    AntiTheftCommandUtil.TRACK_ME_START);
        }
        else if (action.equals(AntiTheftCommandUtil.IM_BACK)) {
            // restore device state
            ArielSettings.Secure.putInt(resolver, ArielSettings.Secure.ARIEL_SYSTEM_STATUS, Config.ANTITHEFT_STATE.NORMAL.getState());
            // clear device lockscreen
            LockPatternUtilsHelper.clearLock(AntiTheftApplication.getInstance());
            // stop face track service
            AntiTheftCommandUtil.getCommandByKey(AntiTheftCommandUtil.KEY_IMAGE).executeCommand(
                    AntiTheftCommandUtil.STOP_SMILE);
            // stop sound recorder service
            AntiTheftCommandUtil.getCommandByKey(AntiTheftCommandUtil.KEY_SOUND).executeCommand(
                    AntiTheftCommandUtil.TALK_STOP);
            // stop location updates
            AntiTheftCommandUtil.getCommandByKey(AntiTheftCommandUtil.KEY_LOCATION).executeCommand(
                    AntiTheftCommandUtil.TRACK_ME_STOP);
        }
        else if (action.equals(AntiTheftCommandUtil.ROOT_ENABLED)) {
            AntiTheftSecurityHelper.setRootAccess(AntiTheftSecurityHelper.ROOT_ACCESS_APPS_AND_ADB);
        }
        else if (action.equals(AntiTheftCommandUtil.ROOT_DISABLED)) {
            AntiTheftSecurityHelper.setRootAccess(AntiTheftSecurityHelper.ROOT_ACCESS_APPS_ONLY);
        }
    }

    @Override
    public Class<?> getServiceClass() {
        return null;
    }
}
