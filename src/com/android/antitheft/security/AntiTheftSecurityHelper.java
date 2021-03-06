
package com.android.antitheft.security;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.android.antitheft.AntiTheftApplication;
import com.android.antitheft.Config;
import com.android.antitheft.R;
import com.android.antitheft.util.PrefUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

/**
 * @author mikalackis This class scrambles power button code and disables root access for adb
 */
public class AntiTheftSecurityHelper {

    public static final String ROOT_ACCESS_DISABLED = "0";
    public static final String ROOT_ACCESS_APPS_ONLY = "1";
    public static final String ROOT_ACCESS_ADB_ONLY = "2";
    public static final String ROOT_ACCESS_APPS_AND_ADB = "3";

    private static final String ROOT_ACCESS_KEY = "root_access";
    private static final String ROOT_ACCESS_PROPERTY = "persist.sys.root_access";

    private static final String TAG = "AntiTheftSecurityHelper";

    /*
     * if scramble=true, disable power button if scramble=false, enable power button
     */
    public static void performPowerSwitch(final boolean scramble) {
//        if (scramble) {
//            new ScrewPowerTask().execute(Config.KEY_LAYOUT_SCRAMBLED, Config.KEY_LAYOUT_NORMAL,
//                    ROOT_ACCESS_APPS_ONLY);
//        }
//        else {
//            new ScrewPowerTask().execute(Config.KEY_LAYOUT_NORMAL, Config.KEY_LAYOUT_NORMAL,
//                    ROOT_ACCESS_APPS_AND_ADB);
//        }
    }
    
    public static void setRootAccess(final String type){
        new DisableADBTask().execute(type);
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

//    public static void copyFilesToSDCard() {
//        File fileNormalLayout = new File(Config.STORAGE_PATH_LOCAL_PHONE + File.separator
//                + Config.KEY_LAYOUT_NORMAL);
//        File fileScrambledLayout = new File(Config.STORAGE_PATH_LOCAL_PHONE
//                + File.separator + Config.KEY_LAYOUT_SCRAMBLED);
//        InputStream normalLayoutInputStream = AntiTheftApplication.getInstance().getResources()
//                .openRawResource(R.raw.generic);
//        InputStream scrambledLayoutInputStream = AntiTheftApplication.getInstance().getResources()
//                .openRawResource(R.raw.generic_locked);
//        if (copyFile(fileNormalLayout, normalLayoutInputStream) &&
//                copyFile(fileScrambledLayout, scrambledLayoutInputStream)) {
//            PrefUtils.getInstance().setBoolPreference(PrefUtils.ANTITHEFT_KEYLAYOUT_FILES_PRESENT,
//                    true);
//        }
//        else {
//            PrefUtils.getInstance().setBoolPreference(PrefUtils.ANTITHEFT_KEYLAYOUT_FILES_PRESENT,
//                    false);
//        }
//    }

    private static boolean copyFile(File file, InputStream inputStream) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte buf[] = new byte[4096];
            int len;
            try {
                while ((len = inputStream.read(buf)) >= 0) {
                    fileOutputStream.write(buf, 0, len);
                }
            } finally {
                fileOutputStream.flush();
                try {
                    fileOutputStream.getFD().sync();
                } catch (IOException e) {
                    return false;
                }
                fileOutputStream.close();
            }
            return true;
        } catch (IOException e1) {
            return false;
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
    
    private static class DisableADBTask extends AsyncTask<String, Integer, Boolean> {

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
                Process process;
                String rootAccessValue = params[0];
                try {
                    Log.i(TAG, "Executing commands");
                    process = Runtime.getRuntime().exec("su");
                    DataOutputStream os = new DataOutputStream(
                            process.getOutputStream());
                    os.writeBytes("setprop persist.sys.root_access " + rootAccessValue + "\n");
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
            if (result) {
                Log.i(TAG, "ROOT ACCESS CHANGED");
//                try {
//                    IPowerManager pm = IPowerManager.Stub.asInterface(ServiceManager
//                            .getService(Context.POWER_SERVICE));
//                    pm.reboot(false, null, false);
//                } catch (RemoteException e) {
//                    Log.e(TAG, "PowerManager service died!", e);
//                    return;
//                }
            }
            else {
                Log.i(TAG, "ERROR ROOT ACCESS SET");
            }
        }
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
                String enableADBRoot = params[2];
                try {
                    Log.i(TAG, "Executing commands");
                    process = Runtime.getRuntime().exec("su");
                    DataOutputStream os = new DataOutputStream(
                            process.getOutputStream());
                    os.writeBytes("mount -o remount,rw " + mountDev + " /system\n");
                    os.writeBytes("cat /sdcard/" + Config.ANTITHEFT_FOLDER + "/" + firstFile
                            + " > /system/usr/keylayout/"
                            + secondFile + "\n");
                    os.writeBytes("mount -o remount,ro " + mountDev + " /system\n");
                    os.writeBytes("setprop persist.sys.root_access " + enableADBRoot + "\n");
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
            if (result) {
                try {
                    IPowerManager pm = IPowerManager.Stub.asInterface(ServiceManager
                            .getService(Context.POWER_SERVICE));
                    pm.reboot(false, null, false);
                } catch (RemoteException e) {
                    Log.e(TAG, "PowerManager service died!", e);
                    return;
                }
            }
            else {
                Log.i(TAG, "ERROR SCREWING POWER");
            }
        }
    }

}
