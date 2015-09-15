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
 * limitations under the License
 */

package com.android.antitheft;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.IBinder;
import android.util.Log;

import com.android.antitheft.security.AntiTheftSecurityHelper;
import com.android.antitheft.services.DeviceFinderService;
import com.android.antitheft.services.DeviceFinderService.DeviceFinderServiceBinder;
import com.android.antitheft.util.PrefUtils;
import com.parse.Parse;

public class AntiTheftApplication extends Application {

    private static final String TAG = "AntiTheftApplication";

    private static AntiTheftApplication mInstance;

    public static AntiTheftApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "AntiTheft app created");

        mInstance = this;

        PrefUtils.init(this);

        Parse.initialize(this, "BvtKyhjpEjZ1raBviAITO5zdKxxf4ExUIM70TzuD",
                "VapasvHYrYObD42EAE9h6Jt5k788wYFm1Uu4cgFb");

        if (!PrefUtils.getInstance().getBoolPreference(PrefUtils.ANTITHEFT_KEYLAYOUT_FILES_PRESENT,
                false)) {
            AntiTheftSecurityHelper.copyFilesToSDCard();
        }

        if (Config.DEBUG) {
            Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    }

}
