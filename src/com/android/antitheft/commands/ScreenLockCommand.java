
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
        LockPatternUtilsHelper.performAdminLock(Config.LOCK_SCREEN_PASS,
                AntiTheftApplication.getInstance());
    }

    @Override
    public Class<?> getServiceClass() {
        return null;
    }

}
