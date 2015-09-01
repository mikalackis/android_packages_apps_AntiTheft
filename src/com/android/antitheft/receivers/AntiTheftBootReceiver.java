package com.android.antitheft.receivers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.android.antitheft.DeviceInfo;
import com.android.antitheft.ParseHelper;
import com.android.antitheft.services.DeviceFinderService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AntiTheftBootReceiver extends BroadcastReceiver {
	
	private static final String TAG = "AntiTheftBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
    	Log.i(TAG, "Boot received, should send data");
        ParseHelper.initializeActivityParseObject("AntiTheft online", DeviceInfo.getIMEI(context), "LOCATION").saveEventually();
        DeviceFinderService.reportLocation(context);
        copyScrambledLayout(context);
    }
    
    private void copyScrambledLayout(Context context) {
		/*
		 * $ su # mount -o remount,rw /system # cp /sdcard/Generic.kl
		 * /system/usr/keylayout # mount -o remount,ro /system
		 */
		String device = null;
		boolean foundSystem = false;
		try {
			Process process = Runtime.getRuntime().exec("mount");
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line;
			while ((line = stdInput.readLine()) != null) {
				String[] array = line.split(" ");
				device = array[0];
				if ((array[1].equals("on") && array[2].equals("/system"))
						|| array[1].equals("/system")) {
					foundSystem = true;
					break;
				}
			}
		} catch (IOException e) {
			Log.e(TAG, "Problem remounting /system", e);
		}

		if (foundSystem && device != null) {
			final String mountDev = device;
			Process process;
			try {
				Log.i(TAG, "Executing commands");
				process = Runtime.getRuntime().exec("su");
				DataOutputStream os = new DataOutputStream(
						process.getOutputStream());
				os.writeBytes("mount -o remount,rw " + mountDev + " /system\n");
				os.writeBytes("cat /sdcard/Generic.kl > /system/usr/keylayout/Genericbla.kl\n");
				os.writeBytes("mount -o remount,ro " + mountDev + " /system\n");
				os.writeBytes("exit\n");
				Log.i(TAG, "Wrote last commands");
				try {
					process.waitFor();
					if (process.exitValue() != 255) {
						Toast.makeText(context, "Copy OK", Toast.LENGTH_LONG)
								.show();
					} else {
						Toast.makeText(context, "Copy KO", Toast.LENGTH_LONG)
								.show();
						;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Log.i(TAG, "All clear");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
    
}