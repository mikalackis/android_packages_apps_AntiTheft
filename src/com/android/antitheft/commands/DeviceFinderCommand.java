
package com.android.antitheft.commands;

import android.content.Intent;

import com.android.antitheft.AntiTheftApplication;
import com.android.antitheft.Config;
import com.android.antitheft.services.DeviceFinderService;

public class DeviceFinderCommand extends AntiTheftCommand {

    public DeviceFinderCommand(String key, String[] commands, String description) {
        super(key, commands, description);
    }

    @Override
    public void executeCommand(final String action) {
        reportActionToParse(action);
        int state = -1;
        if (action.equals(AntiTheftCommandUtil.TRACK_ME_STOP)) {
            stopAntiTheftService();
        }
        else if (action.equals(AntiTheftCommandUtil.WHERE)) {
            state = Config.ANTITHEFT_STATE.NORMAL.getState();
            startAntiTheftService(state);
        }
        else if (action.equals(AntiTheftCommandUtil.TRACK_ME_START)
                || action.equals(AntiTheftCommandUtil.LOCKDOWN)) {
            state = Config.ANTITHEFT_STATE.THEFT.getState();
            startAntiTheftService(state);
        }
    }

    @Override
    public Class<?> getServiceClass() {
        return DeviceFinderService.class;
    }

}
