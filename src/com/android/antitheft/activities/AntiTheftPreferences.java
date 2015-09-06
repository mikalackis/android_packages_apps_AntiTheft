package com.android.antitheft.activities;

import java.util.ArrayList;

import com.android.antitheft.AntiTheftApplication;
import com.android.antitheft.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.app.backup.IBackupManager;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.android.antitheft.security.AntiTheftSecurityHelper;
import com.android.antitheft.util.SharedPreferencesUtility;
import com.android.antitheft.widget.SwitchBar;

public class AntiTheftPreferences extends PreferenceFragment implements SwitchBar.OnSwitchChangeListener, OnPreferenceChangeListener{
	
	private static final String TAG = "AntiTheftPreferences";
	
	private SwitchBar mSwitchBar;
	
	private boolean mLastEnabledState;
	
	private EditTextPreference mParseAppId;
	private EditTextPreference mParseClientKey;
	
	private final ArrayList<Preference> mAllPrefs = new ArrayList<Preference>();
	
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.antitheft_prefs);
        
        mParseAppId = (EditTextPreference)findPreference(SharedPreferencesUtility.PARSE_APP_ID);
        mParseClientKey = (EditTextPreference)findPreference(SharedPreferencesUtility.PARSE_CLIENT_KEY);
        
    }
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final SettingsActivity activity = (SettingsActivity) getActivity();
        mSwitchBar = activity.getSwitchBar();

        mSwitchBar.addOnSwitchChangeListener(this);
    }
	
	@Override
    public void onResume() {
        super.onResume();

        mLastEnabledState = SharedPreferencesUtility.getInstance().getBoolPreference(SharedPreferencesUtility.ENABLE_ANTITHEFT,false);
        mSwitchBar.setChecked(mLastEnabledState);
        setPrefsEnabledState(mLastEnabledState);

        mSwitchBar.show();
    }

	@Override
	public void onSwitchChanged(Switch switchView, boolean isChecked) {
		if (switchView != mSwitchBar.getSwitch()) {
            return;
        }
        if (isChecked != mLastEnabledState) {
            if (isChecked) {
            	final ProgressDialog dlg = new ProgressDialog(getActivity());
                dlg.setTitle("SU check");
                dlg.setMessage("SU check in progress...");
                dlg.setIndeterminate(true);
                dlg.show();
                new Thread() {
                    public void run() {
                        boolean _error = false;
                        try {
                        	if(AntiTheftSecurityHelper.checkSu()){
                        		_error=false;
                        	}
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            Log.i(TAG,e.getMessage());
                            _error = true;
                        }
                        final boolean error = _error;
                        dlg.dismiss();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (error) {
                                	SharedPreferencesUtility.getInstance().setBoolPreference(SharedPreferencesUtility.ENABLE_ANTITHEFT,false);
                                	AntiTheftApplication.getInstance().unregisterReceivers();
                        			Toast.makeText(getActivity(),"SU access not available",Toast.LENGTH_LONG).show();
                                }
                                else {
                                	SharedPreferencesUtility.getInstance().setBoolPreference(SharedPreferencesUtility.ENABLE_ANTITHEFT,true);
                                	AntiTheftApplication.getInstance().registerReceivers();
                                	Toast.makeText(getActivity(),"SU access granted",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    };
                }.start();
            } else {
                mLastEnabledState = isChecked;
                SharedPreferencesUtility.getInstance().setBoolPreference(SharedPreferencesUtility.ENABLE_ANTITHEFT,false);
                AntiTheftApplication.getInstance().unregisterReceivers();
                setPrefsEnabledState(mLastEnabledState);
            }
        }
		
	}
	
	private void setPrefsEnabledState(boolean enabled) {
//        for (int i = 0; i < mAllPrefs.size(); i++) {
//            Preference pref = mAllPrefs.get(i);
//            pref.setEnabled(enabled && !mDisabledPrefs.contains(pref));
//        }
//        updateAllOptions();
    }

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (SharedPreferencesUtility.PARSE_APP_ID.equals(preference.getKey())) {
			SharedPreferencesUtility.getInstance().setStringPreference(SharedPreferencesUtility.PARSE_APP_ID,newValue.toString());
			return true;
		}
		else if(SharedPreferencesUtility.PARSE_CLIENT_KEY.equals(preference.getKey())){
			SharedPreferencesUtility.getInstance().setStringPreference(SharedPreferencesUtility.PARSE_CLIENT_KEY,newValue.toString());
			return true;
		}
		else {
			return false;
		}
	}

}
