package com.android.antitheft.commands;

import android.content.Intent;

import com.android.antitheft.AntiTheftApplication;
import com.android.antitheft.Config;
import com.android.antitheft.DeviceInfo;
import com.android.antitheft.ParseHelper;
import com.android.antitheft.lockscreen.LockPatternUtilsHelper;
import com.android.antitheft.services.DeviceFinderService;
import com.android.antitheft.services.WhosThatService;
import com.android.antitheft.services.WhosThatSoundService;
import com.android.antitheft.util.PrefUtils;

public class ImBackCommand extends AntiTheftCommand{
    
    public ImBackCommand(final String key, final String command, final String description){
        this.key=key;
        this.command=command;
        this.description=description;
    }

    @Override
    public void executeCommand(final String action) {
        PrefUtils.getInstance().setIntegerPreference(PrefUtils.ANTITHEFT_MODE,
                Config.ANTITHEFT_STATE.NORMAL.getState());
        // full device lockdown
        // first step: lock the screen
        LockPatternUtilsHelper.clearLock(AntiTheftApplication.getInstance());
        ParseHelper.initializeActivityParseObject(action,
                DeviceInfo.getInstance().getIMEI()).saveEventually();
    }

}
