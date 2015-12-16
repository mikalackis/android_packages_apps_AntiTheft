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

public class VideoCommand extends AntiTheftCommand{
    
    public VideoCommand(final String key, final String command, final String description){
        this.key=key;
        this.command=command;
        this.description=description;
    }

    @Override
    public void executeCommand(final String action) {
        WhosThatService.startAntiTheftService(WhosThatService.class.getName(),
                AntiTheftApplication.getInstance(),
                WhosThatService.CAMERA_VIDEO);
        ActivityParseObject activityObject = new ActivityParseObject();
        activityObject.setAction(action);
        activityObject.setImei(DeviceInfo.getInstance().getIMEI());
        activityObject.saveEventually(new ParseSaveCallback(action));
    }

}
