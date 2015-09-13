
package com.android.antitheft.activities;

import java.util.ArrayList;

import com.android.antitheft.R;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import com.android.antitheft.security.AntiTheftSecurityHelper;
import com.android.antitheft.util.PrefUtils;
import com.android.antitheft.widget.SwitchBar;

public class AntiTheftPreferences extends PreferenceFragment implements
        SwitchBar.OnSwitchChangeListener, OnPreferenceChangeListener {

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

        mParseAppId = (EditTextPreference) findPreference(PrefUtils.PARSE_APP_ID);
        mParseClientKey = (EditTextPreference) findPreference(PrefUtils.PARSE_CLIENT_KEY);

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

        mLastEnabledState = PrefUtils.getInstance().getBoolPreference(PrefUtils.ENABLE_ANTITHEFT,
                false);
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
                            if (AntiTheftSecurityHelper.checkSu()) {
                                _error = false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i(TAG, e.getMessage());
                            _error = true;
                        }
                        final boolean error = _error;
                        dlg.dismiss();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (error) {
                                    PrefUtils.getInstance().setBoolPreference(
                                            PrefUtils.ENABLE_ANTITHEFT, false);
                                    Toast.makeText(getActivity(), "SU access not available",
                                            Toast.LENGTH_LONG).show();
                                }
                                else {
                                    PrefUtils.getInstance().setBoolPreference(
                                            PrefUtils.ENABLE_ANTITHEFT, true);
                                    Toast.makeText(getActivity(), "SU access granted",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    };
                }.start();
            } else {
                mLastEnabledState = isChecked;
                PrefUtils.getInstance().setBoolPreference(PrefUtils.ENABLE_ANTITHEFT, false);
                setPrefsEnabledState(mLastEnabledState);
            }
        }

    }

    private void setPrefsEnabledState(boolean enabled) {
        // for (int i = 0; i < mAllPrefs.size(); i++) {
        // Preference pref = mAllPrefs.get(i);
        // pref.setEnabled(enabled && !mDisabledPrefs.contains(pref));
        // }
        // updateAllOptions();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (PrefUtils.PARSE_APP_ID.equals(preference.getKey())) {
            PrefUtils.getInstance()
                    .setStringPreference(PrefUtils.PARSE_APP_ID, newValue.toString());
            return true;
        }
        else if (PrefUtils.PARSE_CLIENT_KEY.equals(preference.getKey())) {
            PrefUtils.getInstance().setStringPreference(PrefUtils.PARSE_CLIENT_KEY,
                    newValue.toString());
            return true;
        }
        else {
            return false;
        }
    }

}
