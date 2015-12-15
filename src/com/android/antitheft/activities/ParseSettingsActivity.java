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
import com.android.antitheft.util.PrefUtils;
import com.android.antitheft.widget.SwitchBar;

import android.app.Activity;
import android.content.Context;

import android.os.Bundle;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ParseSettingsActivity extends Activity {

    private static final String TAG = "ParseSettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.parse_settings_activity);
        
        final EditText parseAppId = (EditText) findViewById(R.id.parse_app_id);
        
        final EditText parseClientKey = (EditText) findViewById(R.id.parse_client_key);
        
        Button btnUpdateParse = (Button)findViewById(R.id.btn_update_parse_setup);
        btnUpdateParse.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                String savedClientKey = PrefUtils.getInstance().getStringPreference(PrefUtils.PARSE_CLIENT_KEY, "");
                String savedAppId = PrefUtils.getInstance().getStringPreference(PrefUtils.PARSE_APP_ID, "");
                String newClientKey = parseClientKey.getText().toString();
                String newAppId = parseAppId.getText().toString();
                if(!newAppId.equals(savedAppId) && !newClientKey.equals(savedClientKey)){
                    PrefUtils.getInstance().setStringPreference(PrefUtils.PARSE_APP_ID, newAppId);
                    PrefUtils.getInstance().setStringPreference(PrefUtils.PARSE_CLIENT_KEY, newClientKey);
                    Toast.makeText(ParseSettingsActivity.this, "Your device will now reboot!", Toast.LENGTH_LONG).show();
                    //PrefUtils.getInstance().setBoolPreference(PrefUtils.PARSE_CONFIG_CHANGED, true);
                    try {
                        IPowerManager pm = IPowerManager.Stub.asInterface(ServiceManager
                                .getService(Context.POWER_SERVICE));
                        pm.reboot(false, null, false);
                    } catch (RemoteException e) {
                        Log.e(TAG, "PowerManager service died!", e);
                        return;
                    }
                }
            }
        });

    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
