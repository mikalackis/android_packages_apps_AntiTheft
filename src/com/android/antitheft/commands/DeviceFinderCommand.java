package com.android.antitheft.commands;

import android.content.Intent;

import com.android.antitheft.AntiTheftApplication;
import com.android.antitheft.Config;
import com.android.antitheft.DeviceInfo;
import com.android.antitheft.listeners.ParseSaveCallback;
import com.android.antitheft.parse.ActivityParseObject;
import com.android.antitheft.parse.ParseHelper;
import com.android.antitheft.services.DeviceFinderService;

public class DeviceFinderCommand extends AntiTheftCommand{
    
    public DeviceFinderCommand(final String key, final String command, final String description){
        this.key=key;
        this.command=command;
        this.description=description;
    }

    @Override
    public void executeCommand(final String action) {
        ActivityParseObject activityObject = new ActivityParseObject();
        activityObject.setAction(action);
        activityObject.setImei(DeviceInfo.getInstance().getIMEI());
        activityObject.saveEventually(new ParseSaveCallback(action));
        int state = -1;
        if(action.equals(AntiTheftCommandUtil.TRACK_ME_STOP)){
            AntiTheftApplication.getInstance().stopService(
                    new Intent(AntiTheftApplication.getInstance(), DeviceFinderService.class));
            activityObject.saveEventually(new ParseSaveCallback(action));
        }
        else if(action.equals(AntiTheftCommandUtil.WHERE)){
            state = Config.ANTITHEFT_STATE.NORMAL.getState();
        }
        else if(action.equals(AntiTheftCommandUtil.TRACK_ME_START) || action.equals(AntiTheftCommandUtil.LOCKDOWN)){
            state = Config.ANTITHEFT_STATE.LOCKDOWN.getState();
            activityObject.saveEventually(new ParseSaveCallback(action));
        }
        DeviceFinderService.startAntiTheftService(DeviceFinderService.class.getName(),
                AntiTheftApplication.getInstance(), state);
    }

}
