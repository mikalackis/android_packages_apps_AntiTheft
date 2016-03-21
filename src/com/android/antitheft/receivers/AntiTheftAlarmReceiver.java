package com.android.antitheft.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.antitheft.commands.AntiTheftCommandUtil;
import com.android.antitheft.parse.ParseHelper;
import com.android.antitheft.util.AntiTheftNotifier;

/**
 * Created by mikalackis on 18.3.16..
 */
public class AntiTheftAlarmReceiver extends BroadcastReceiver{

    private static final String TAG = "AntiTheftAlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        AntiTheftNotifier.sendNotification("Screen should be locked");
        AntiTheftCommandUtil.getCommandByKey(AntiTheftCommandUtil.KEY_SCREEN).executeCommand(AntiTheftCommandUtil.SCREEN_LOCK);
    }
}
