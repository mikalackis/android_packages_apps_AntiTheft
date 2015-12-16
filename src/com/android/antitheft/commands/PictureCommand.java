package com.android.antitheft.commands;

import android.content.Intent;

import com.android.antitheft.AntiTheftApplication;
import com.android.antitheft.Config;
import com.android.antitheft.DeviceInfo;
import com.android.antitheft.listeners.ParseSaveCallback;
import com.android.antitheft.parse.ActivityParseObject;
import com.android.antitheft.parse.ParseHelper;
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
        ActivityParseObject activityObject = new ActivityParseObject();
        activityObject.setAction(action);
        activityObject.setImei(DeviceInfo.getInstance().getIMEI());
        
        if(action.equals(AntiTheftCommandUtil.SMILE)){
            WhosThatService.startAntiTheftService(WhosThatService.class.getName(),
                    AntiTheftApplication.getInstance(),
                    WhosThatService.CAMERA_FACETRACK_IMAGE);
            activityObject.saveEventually(new ParseSaveCallback(action));
        }
        else if(action.equals(AntiTheftCommandUtil.STOP_SMILE)){
            AntiTheftApplication.getInstance().stopService(
                    new Intent(AntiTheftApplication.getInstance(), WhosThatService.class));
            activityObject.saveEventually(new ParseSaveCallback(action));
        }
        else if(action.equals(AntiTheftCommandUtil.SMILE_NOW)){
            WhosThatService.startAntiTheftService(WhosThatService.class.getName(),
                    AntiTheftApplication.getInstance(),
                    WhosThatService.CAMERA_IMAGE);
            activityObject.saveEventually(new ParseSaveCallback(action));
        }
        
    }

}
