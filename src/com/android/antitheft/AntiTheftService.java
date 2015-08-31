/*AntiTheftService.java */
package com.android.antitheft;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

public class AntiTheftService extends Service {
    private static final String TAG = "AntiTheftService";
    
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	try {
                if (msg.what == 0) {
                    Log.i(TAG, "set message received: " + msg.arg1);
                    Toast.makeText(AntiTheftService.this, "Starting photo service "+msg.arg1, Toast.LENGTH_LONG).show();
                    
                    startService(new Intent(AntiTheftService.this,WhosThatService.class));
                }
            } catch (Exception e) {
                // Log, don't crash!
                Log.e(TAG, "Exception in AntiTheftWorkerHandler.handleMessage:", e);
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return new Messenger(mHandler).getBinder();
    }
   
}
