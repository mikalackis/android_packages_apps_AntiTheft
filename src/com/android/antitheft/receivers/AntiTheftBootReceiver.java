
package com.android.antitheft.receivers;

import com.android.antitheft.parse.ParseHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AntiTheftBootReceiver extends BroadcastReceiver {

    private static final String TAG = "AntiTheftApplication";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Boot received, should send data ");
        ParseHelper.antiTheftOnline(context);
    }

}
