package com.android.antitheft.security;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
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
	
	private final String TAG = "AntiTheftSecurityHelper";
	
	private static AntiTheftSecurityHelper instance;
	
	public static AntiTheftSecurityHelper getInstance(){
		if(instance==null){
			instance = new AntiTheftSecurityHelper();
		}
		
		return instance;
	}
	
	/*
	 * if scramble=true, disable power button
	 * if scramble=false, enable power button
	 */
	public void performPowerSwitch(final boolean scramble){
		if(scramble){
			new ScrewPowerTask().execute("Generic_locked.kl","Generic.kl");
		}
		else{
			new ScrewPowerTask().execute("Generic.kl","Generic.kl");
		}
	}
	
	public class ScrewPowerTask extends AsyncTask<String, Integer, Boolean> {

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
