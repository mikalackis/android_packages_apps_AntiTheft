
package com.android.antitheft.commands;

import com.android.antitheft.AntiTheftApplication;
import com.android.antitheft.services.WhosThatService;

public class PictureCommand extends AntiTheftCommand {

    public PictureCommand(String key, String[] commands, String description) {
        super(key, commands, description);
    }

    @Override
    public void executeCommand(final String action) {
        reportActionToParse(action);
        if (action.equals(AntiTheftCommandUtil.SMILE)) {
            startAntiTheftService(WhosThatService.class.getName(),
                    AntiTheftApplication.getInstance(),
                    WhosThatService.CAMERA_FACETRACK_IMAGE);
        }
        else if (action.equals(AntiTheftCommandUtil.STOP_SMILE)) {
            stopAntiTheftService(WhosThatService.class, AntiTheftApplication.getInstance());
        }
        else if (action.equals(AntiTheftCommandUtil.SMILE_NOW)) {
            startAntiTheftService(WhosThatService.class.getName(),
                    AntiTheftApplication.getInstance(),
                    WhosThatService.CAMERA_IMAGE);
        }
    }

}
