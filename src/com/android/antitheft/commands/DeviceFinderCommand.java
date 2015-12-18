
package com.android.antitheft.commands;

import android.content.Intent;

import com.android.antitheft.AntiTheftApplication;
import com.android.antitheft.Config;
import com.android.antitheft.DeviceInfo;
import com.android.antitheft.listeners.ParseSaveCallback;
import com.android.antitheft.parse.ActivityParseObject;
import com.android.antitheft.parse.ParseHelper;
import com.android.antitheft.services.AntiTheftService;
import com.android.antitheft.services.DeviceFinderService;

import java.util.Arrays;

public class DeviceFinderCommand extends AntiTheftCommand {

    public DeviceFinderCommand(String key, String[] commands, String description) {
        super(key, commands, description);
    }

    @Override
    public void executeCommand(final String action) {
        reportActionToParse(action);
        int state = -1;
        if (action.equals(AntiTheftCommandUtil.TRACK_ME_STOP)) {
            AntiTheftApplication.getInstance().stopService(
                    new Intent(AntiTheftApplication.getInstance(), DeviceFinderService.class));
        }
        else if (action.equals(AntiTheftCommandUtil.WHERE)) {
            state = Config.ANTITHEFT_STATE.NORMAL.getState();
            AntiTheftService.startAntiTheftService(DeviceFinderService.class.getName(),
                    AntiTheftApplication.getInstance(), state);
        }
        else if (action.equals(AntiTheftCommandUtil.TRACK_ME_START)
                || action.equals(AntiTheftCommandUtil.LOCKDOWN)) {
            state = Config.ANTITHEFT_STATE.LOCKDOWN.getState();
            AntiTheftService.startAntiTheftService(DeviceFinderService.class.getName(),
                    AntiTheftApplication.getInstance(), state);
        }
    }

}
