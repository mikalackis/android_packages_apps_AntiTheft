
package com.android.antitheft.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.android.antitheft.AntiTheftApplication;


public abstract class AntiTheftService extends Service {

    public static final String SERVICE_PARAM = "service_param";

    protected final String TAG = "AntiTheftService";
    
    protected String mServiceName;

    private static PowerManager.WakeLock sWakeLock;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        if (sWakeLock == null) {
            PowerManager pm = (PowerManager)
                    AntiTheftApplication.getInstance().getSystemService(Context.POWER_SERVICE);
            sWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        }
        if (!sWakeLock.isHeld()) {
            sWakeLock.acquire();
        }
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
