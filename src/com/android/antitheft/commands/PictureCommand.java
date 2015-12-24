
package com.android.antitheft.commands;

import com.android.antitheft.services.WhosThatService;

public class PictureCommand extends AntiTheftCommand {

    public PictureCommand(String key, String[] commands, String description) {
        super(key, commands, description);
    }

    @Override
    public void executeCommand(final String action) {
        reportActionToParse(action);
        if (action.equals(AntiTheftCommandUtil.SMILE)) {
            startAntiTheftService(WhosThatService.CAMERA_FACETRACK_IMAGE);
        }
        else if (action.equals(AntiTheftCommandUtil.STOP_SMILE)) {
            stopAntiTheftService();
        }
        else if (action.equals(AntiTheftCommandUtil.SMILE_NOW)) {
            startAntiTheftService(WhosThatService.CAMERA_IMAGE);
        }
        else if (action.equals(AntiTheftCommandUtil.ACTOR)) {
            startAntiTheftService(WhosThatService.CAMERA_VIDEO);
        }
    }

    @Override
    public Class<?> getServiceClass() {
        return WhosThatService.class;
    }

}
