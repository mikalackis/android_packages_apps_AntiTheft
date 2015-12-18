
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
import com.android.antitheft.services.WhosThatSoundService;

public class TalkCommand extends AntiTheftCommand {

    public TalkCommand(String key, String[] commands, String description) {
        super(key, commands, description);
    }

    @Override
    public void executeCommand(final String action) {
        reportActionToParse(action);
        if (action.equals(AntiTheftCommandUtil.TALK)) {
            AntiTheftService.startAntiTheftService(WhosThatSoundService.class.getName(),
                    AntiTheftApplication.getInstance(), -1);
        }
        else if (action.equals(AntiTheftCommandUtil.TALK_STOP)) {
            AntiTheftApplication.getInstance().stopService(
                    new Intent(AntiTheftApplication.getInstance(), WhosThatSoundService.class));
        }

    }

}
