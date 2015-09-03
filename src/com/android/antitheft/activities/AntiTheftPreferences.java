package com.android.antitheft.activities;

import java.util.ArrayList;

import com.android.antitheft.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.app.backup.IBackupManager;
import android.content.Context;
import android.os.Bundle;
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
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.android.antitheft.widget.SwitchBar;

public class AntiTheftPreferences extends PreferenceFragment implements SwitchBar.OnSwitchChangeListener, OnPreferenceChangeListener{
	
	private static final String TAG = "AntiTheftPreferences";
	
	public static final String PREF_FILE = "antitheft";
	
	public static final String ENABLE_ANTITHEFT = "enable_antitheft";
	
    private static final String PARSE_APP_ID = "antitheft_parse_app_id";
    private static final String PARSE_CLIENT_KEY = "antitheft_parse_client_key";
	
	private SwitchBar mSwitchBar;
	
	private boolean mLastEnabledState;
	
	private EditTextPreference mParseAppId;
	private EditTextPreference mParseClientKey;
	
	private final ArrayList<Preference> mAllPrefs = new ArrayList<Preference>();
	
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.antitheft_prefs);
        
        mParseAppId = (EditTextPreference)findPreference(PARSE_APP_ID);
        mParseClientKey = (EditTextPreference)findPreference(PARSE_CLIENT_KEY);
        
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

        mLastEnabledState = getActivity().getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        		.getBoolean(ENABLE_ANTITHEFT, false);
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
            	getActivity().getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE).edit()
                .putBoolean(ENABLE_ANTITHEFT, true)
                .apply();
            } else {
                mLastEnabledState = isChecked;
                getActivity().getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE).edit()
                .putBoolean(ENABLE_ANTITHEFT, true)
                .apply();
                setPrefsEnabledState(mLastEnabledState);

                getActivity().getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE).edit()
                .putBoolean(ENABLE_ANTITHEFT, false)
                .apply();
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
		if (PARSE_APP_ID.equals(preference.getKey())) {
			getActivity().getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE).edit()
            .putString(PARSE_APP_ID, newValue.toString())
            .apply();
			return true;
		}
		else if(PARSE_CLIENT_KEY.equals(preference.getKey())){
			getActivity().getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE).edit()
            .putString(PARSE_CLIENT_KEY, newValue.toString())
            .apply();
			return true;
		}
		else {
			return false;
		}
	}

}
