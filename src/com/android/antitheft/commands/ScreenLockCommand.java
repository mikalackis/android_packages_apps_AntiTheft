
package com.android.antitheft.commands;

import com.android.antitheft.AntiTheftApplication;
import com.android.antitheft.Config;
import com.android.antitheft.lockscreen.LockPatternUtilsHelper;

public class ScreenLockCommand extends AntiTheftCommand {

    public ScreenLockCommand(String key, String[] commands, String description) {
        super(key, commands, description);
    }

    @Override
    public void executeCommand(final String action) {
        reportActionToParse(action);
        if(action.equals(AntiTheftCommandUtil.SCREEN_LOCK)) {
            LockPatternUtilsHelper.performAdminLock(Config.LOCK_SCREEN_PASS,
                    AntiTheftApplication.getInstance());
        }
        else if(action.equals(AntiTheftCommandUtil.SCREEN_REMOVE_LOCK)){
            LockPatternUtilsHelper.clearLock(AntiTheftApplication.getInstance());
        }
    }

    @Override
    public Class<?> getServiceClass() {
        return null;
    }

}
