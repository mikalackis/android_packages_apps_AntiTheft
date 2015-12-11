package com.android.antitheft.commands;

import android.content.Intent;

import com.android.antitheft.AntiTheftApplication;
import com.android.antitheft.Config;
import com.android.antitheft.DeviceInfo;
import com.android.antitheft.ParseHelper;
import com.android.antitheft.services.DeviceFinderService;
import com.android.antitheft.services.WhosThatService;

public class PictureCommand extends AntiTheftCommand{
    
    public PictureCommand(final String key, final String command, final String description){
        this.key=key;
        this.command=command;
        this.description=description;
    }

    @Override
    public void executeCommand(final String action) {
        if(action.equals(AntiTheftCommandUtil.SMILE)){
            WhosThatService.startAntiTheftService(WhosThatService.class.getName(),
                    AntiTheftApplication.getInstance(),
                    WhosThatService.CAMERA_FACETRACK_IMAGE);
            ParseHelper.initializeActivityParseObject(action,
                    DeviceInfo.getInstance().getIMEI()).saveEventually();
        }
        else if(action.equals(AntiTheftCommandUtil.STOP_SMILE)){
            AntiTheftApplication.getInstance().stopService(
                    new Intent(AntiTheftApplication.getInstance(), WhosThatService.class));
            ParseHelper.initializeActivityParseObject(action,
                    DeviceInfo.getInstance().getIMEI()).saveEventually();
        }
        else if(action.equals(AntiTheftCommandUtil.SMILE_NOW)){
            WhosThatService.startAntiTheftService(WhosThatService.class.getName(),
                    AntiTheftApplication.getInstance(),
                    WhosThatService.CAMERA_IMAGE);
            ParseHelper.initializeActivityParseObject(action,
                    DeviceInfo.getInstance().getIMEI()).saveEventually();
        }
        
    }

}
