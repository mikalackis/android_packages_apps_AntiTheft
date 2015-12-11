package com.android.antitheft.commands;

import android.content.Intent;

import com.android.antitheft.AntiTheftApplication;
import com.android.antitheft.Config;
import com.android.antitheft.DeviceInfo;
import com.android.antitheft.ParseHelper;
import com.android.antitheft.services.DeviceFinderService;

public class DeviceFinderCommand extends AntiTheftCommand{
    
    public DeviceFinderCommand(final String key, final String command, final String description){
        this.key=key;
        this.command=command;
        this.description=description;
    }

    @Override
    public void executeCommand(final String action) {
        if(action.equals(AntiTheftCommandUtil.WHERE)){
            DeviceFinderService.startAntiTheftService(DeviceFinderService.class.getName(),
                    AntiTheftApplication.getInstance(), Config.ANTITHEFT_STATE.NORMAL.getState());
            ParseHelper.initializeActivityParseObject(action,
                    DeviceInfo.getInstance().getIMEI()).saveEventually();
        }
        else if(action.equals(AntiTheftCommandUtil.TRACK_ME_START)){
            DeviceFinderService.startAntiTheftService(DeviceFinderService.class.getName(),
                    AntiTheftApplication.getInstance(), Config.ANTITHEFT_STATE.LOCKDOWN.getState());
            ParseHelper.initializeActivityParseObject(action,
                    DeviceInfo.getInstance().getIMEI()).saveEventually();
        }
        else if(action.equals(AntiTheftCommandUtil.TRACK_ME_STOP)){
            AntiTheftApplication.getInstance().stopService(
                    new Intent(AntiTheftApplication.getInstance(), DeviceFinderService.class));
            ParseHelper.initializeActivityParseObject(action,
                    DeviceInfo.getInstance().getIMEI()).saveEventually();
        }
    }

}
