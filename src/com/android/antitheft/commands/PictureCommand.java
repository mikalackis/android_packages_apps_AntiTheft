
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

public class PictureCommand extends AntiTheftCommand {

    public PictureCommand(String key, String[] commands, String description) {
        super(key, commands, description);
    }

    @Override
    public void executeCommand(final String action) {
        reportActionToParse(action);
        if (action.equals(AntiTheftCommandUtil.SMILE)) {
            AntiTheftService.startAntiTheftService(WhosThatService.class.getName(),
                    AntiTheftApplication.getInstance(),
                    WhosThatService.CAMERA_FACETRACK_IMAGE);
        }
        else if (action.equals(AntiTheftCommandUtil.STOP_SMILE)) {
            AntiTheftApplication.getInstance().stopService(
                    new Intent(AntiTheftApplication.getInstance(), WhosThatService.class));
        }
        else if (action.equals(AntiTheftCommandUtil.SMILE_NOW)) {
            AntiTheftService.startAntiTheftService(WhosThatService.class.getName(),
                    AntiTheftApplication.getInstance(),
                    WhosThatService.CAMERA_IMAGE);
        }
    }

}
