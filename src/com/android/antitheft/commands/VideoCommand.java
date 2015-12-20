
package com.android.antitheft.commands;

import com.android.antitheft.AntiTheftApplication;
import com.android.antitheft.services.WhosThatService;

public class VideoCommand extends AntiTheftCommand {

    public VideoCommand(String key, String[] commands, String description) {
        super(key, commands, description);
    }

    @Override
    public void executeCommand(final String action) {
        reportActionToParse(action);
        startAntiTheftService(WhosThatService.class.getName(),
                AntiTheftApplication.getInstance(),
                WhosThatService.CAMERA_VIDEO);
    }

}
