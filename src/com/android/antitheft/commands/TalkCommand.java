
package com.android.antitheft.commands;

import android.content.Intent;

import com.android.antitheft.AntiTheftApplication;
import com.android.antitheft.Config;
import com.android.antitheft.DeviceInfo;
import com.android.antitheft.ParseHelper;
import com.android.antitheft.services.DeviceFinderService;
import com.android.antitheft.services.WhosThatSoundService;

public class TalkCommand extends AntiTheftCommand {
    
    public TalkCommand(final String key, final String command, final String description){
        this.key=key;
        this.command=command;
        this.description=description;
    }

    @Override
    public void executeCommand(final String action) {
        WhosThatSoundService.startAntiTheftService(WhosThatSoundService.class.getName(),
                AntiTheftApplication.getInstance(), -1);
        ParseHelper.initializeActivityParseObject(action,
                DeviceInfo.getInstance().getIMEI()).saveEventually();
    }

}
