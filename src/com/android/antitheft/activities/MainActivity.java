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
import com.android.antitheft.eventbus.StatusUpdateEvent;
import com.android.antitheft.fragment.ControlFragment;
import com.android.antitheft.fragment.StatusFragment;
import com.android.antitheft.widget.SwitchBar;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import de.greenrobot.event.EventBus;

public class MainActivity extends Activity {

    private static final String LOG_TAG = "MainActivity";
    
    private EventBus mBus = EventBus.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        getFragmentManager().beginTransaction().replace(R.id.status_fragment,
                StatusFragment.createFragment()).commit();
        getFragmentManager().beginTransaction().replace(R.id.control_fragment,
                ControlFragment.createFragment()).commit();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        mBus.register(this);
    }

    @Override
    public void onStop() {
        mBus.unregister(this);
        super.onStop();
    }

    public void onEventMainThread(StatusUpdateEvent event){
        StatusFragment statusFrag = (StatusFragment) getFragmentManager().findFragmentById(R.id.status_fragment);
        if(statusFrag!=null){
            statusFrag.updateStatusData(event);
        }
        else{
            Toast.makeText(this, "StatusFragment null", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.parse_settings:{
                Intent settingsIntent = new Intent(MainActivity.this,ParseSettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
