
package com.android.antitheft;

import android.content.ContentResolver;
import android.os.Environment;
import android.provider.Settings;

/**
 * @author mikalackis
 */
public class Config {

    public static final boolean DEBUG = true;

    public static final String CONFIG_CHANNEL_UPDATE="config_%s";

    public static final String LOCK_SCREEN_PASS = "6969";

    public static final String ANTITHEFT_FOLDER = "AntiTheft";

    public static final String STORAGE_PATH_LOCAL_PHONE = Environment.getExternalStorageDirectory()
            .toString() + "/" + ANTITHEFT_FOLDER;
    
    public static enum ANTITHEFT_STATE {
        NORMAL(0),
        LOCK(1),
        THEFT(2);

        private int mState;

        private ANTITHEFT_STATE(final int state) {
            this.mState = state;
        }

        public int getState() {
            return mState;
        }
    }

}
