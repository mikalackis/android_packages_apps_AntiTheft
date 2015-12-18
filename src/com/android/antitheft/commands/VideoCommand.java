
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
import com.android.antitheft.services.WhosThatService;

public class VideoCommand extends AntiTheftCommand {

    public VideoCommand(String key, String[] commands, String description) {
        super(key, commands, description);
    }

    @Override
    public void executeCommand(final String action) {
        reportActionToParse(action);
        AntiTheftService.startAntiTheftService(WhosThatService.class.getName(),
                AntiTheftApplication.getInstance(),
                WhosThatService.CAMERA_VIDEO);
    }

}
