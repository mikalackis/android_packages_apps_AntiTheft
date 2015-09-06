package com.android.antitheft.security;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.android.antitheft.sms.AntiTheftSMSReceiver;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

public class AntiTheftSecurityHelper {
	
	private final String ROOT_ACCESS_DISABLED = "0";
	private final String ROOT_ACCESS_APPS_ONLY = "1";
	private final String ROOT_ACCESS_ADB_ONLY = "2";
	private final String ROOT_ACCESS_APPS_AND_ADB = "3";
	
	private static final String TAG = "AntiTheftSecurityHelper";
	
	/*
	 * if scramble=true, disable power button
	 * if scramble=false, enable power button
	 */
	public static void performPowerSwitch(final boolean scramble){
		if(scramble){
			new ScrewPowerTask().execute("Generic_locked.kl","Generic.kl");
		}
		else{
			new ScrewPowerTask().execute("Generic.kl","Generic.kl");
		}
	}
	
	public static boolean checkSu() throws Exception {
        Process process = Runtime.getRuntime().exec("su");
        DataOutputStream os = new DataOutputStream(
				process.getOutputStream());
		os.writeBytes("exit\n");
		process.waitFor();
        if (process.exitValue() != 255) {
        	return true;
		} else {
			throw new Exception("zero result");
		}
    }
	
	public static void updateSMSReceiverStatus(final boolean enabled, final Context context){
		ComponentName component = new ComponentName(context, AntiTheftSMSReceiver.class);
		int status = context.getPackageManager().getComponentEnabledSetting(component);
		if(status == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
			context.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED , PackageManager.DONT_KILL_APP);
		} else if(status == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
			context.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED , PackageManager.DONT_KILL_APP);
		}
	}
	
	private static byte[] readToEndAsArray(InputStream input) throws IOException {
        DataInputStream dis = new DataInputStream(input);
        byte[] stuff = new byte[1024];
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        int read = 0;
        while ((read = dis.read(stuff)) != -1)
        {
            buff.write(stuff, 0, read);
        }
        input.close();
        return buff.toByteArray();
    }

    private static String readToEnd(InputStream input) throws IOException {
        return new String(readToEndAsArray(input));
    }
	
	private static class ScrewPowerTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
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
    			return false;
    		}

            if (foundSystem && device != null) {
                final String mountDev = device;
                Process process;
                String firstFile = params[0];
                String secondFile = params[1];
                try {
                	Log.i(TAG, "Executing commands");
    				process = Runtime.getRuntime().exec("su");
    				DataOutputStream os = new DataOutputStream(
    						process.getOutputStream());
    				os.writeBytes("mount -o remount,rw " + mountDev + " /system\n");
    				os.writeBytes("cat /sdcard/"+firstFile+" > /system/usr/keylayout/"+secondFile+"\n");
    				os.writeBytes("mount -o remount,ro " + mountDev + " /system\n");
    				os.writeBytes("exit\n");
    				Log.i(TAG, "Wrote last commands");
    				try {
    					process.waitFor();
    					if (process.exitValue() != 255) {
    						return true;
    					} else {
    						return false;
    					}
    				} catch (InterruptedException e) {
    					e.printStackTrace();
    					return false;
    				}
                } catch (IOException e) {
                    return false;
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result){
            	try {
        			IPowerManager pm = IPowerManager.Stub.asInterface(ServiceManager
        					.getService(Context.POWER_SERVICE));
        			pm.reboot(false, null, false);
        		} catch (RemoteException e) {
        			Log.e(TAG, "PowerManager service died!", e);
        			return;
        		}
            }
            else{
            	Log.i(TAG, "ERROR SCREWING POWER");
            }
        }
    }
	
}
