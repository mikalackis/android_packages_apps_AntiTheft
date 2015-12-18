
package com.android.antitheft.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.android.antitheft.commands.AntiTheftCommandUtil;


public abstract class AntiTheftService extends Service {

    public static final String SERVICE_PARAM = "service_param";

    protected static String TAG;

    private static PowerManager.WakeLock sWakeLock;

    public static void startAntiTheftService(final String serviceName, final Context context,
            final int state) {
        Class<?> serviceClass;
        try {
            serviceClass = Class.forName(serviceName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (sWakeLock == null) {
            PowerManager pm = (PowerManager)
                    context.getSystemService(Context.POWER_SERVICE);
            sWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, serviceName);
        }
        if (!sWakeLock.isHeld()) {
            sWakeLock.acquire();
        }
        TAG = serviceName;
        
        Intent intent = new Intent(context, serviceClass);
        intent.putExtra(SERVICE_PARAM, state);
        context.startService(intent);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        if (sWakeLock != null && sWakeLock.isHeld()) {
            Log.i(TAG, "sWakeLock existing");
            Toast.makeText(this, "Wake lock released", Toast.LENGTH_LONG).show();
            sWakeLock.release();
        }
    }

}
