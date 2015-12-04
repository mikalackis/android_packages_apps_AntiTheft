/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.antitheft.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.android.antitheft.R;
import com.android.antitheft.widget.SwitchBar;

import android.app.Activity;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class SettingsActivity extends Activity {

    private static final String LOG_TAG = "SettingsActivity";

    private SwitchBar mSwitchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_main_prefs);

        mSwitchBar = (SwitchBar) findViewById(R.id.switch_bar);
        getFragmentManager().beginTransaction().replace(R.id.main_content,
                new AntiTheftPreferences()).commit();
    }

    public SwitchBar getSwitchBar() {
        return mSwitchBar;
    }

}