
package com.android.antitheft.widget;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author mikalackis Helper class for storing shared prefferences
 */
public class SwitchBar {

    public static final String SHARED_PREFS = "antitheft";

    /** keys */
    public static final String ANTITHEFT_ENABLED = "antitheft_enabled";

    public static final String ANTITHEFT_MODE = "antitheft_mode";

    private static Context mContext;

    static private SwitchBar instance;

    static public void init(final Context ctx) {
        if (null == instance) {
            instance = new SwitchBar(ctx);
        }
    }

    static public SwitchBar getInstance() {
        return instance;
    }

    private SwitchBar(final Context ctx) {
        mContext = ctx;
    }

    public Integer getIntegerPreference(final String key, final int defaultValue) {
        final SharedPreferences settings = mContext.getSharedPreferences(SHARED_PREFS,
                Context.MODE_PRIVATE);
        return settings.getInt(key, defaultValue);
    }

    public void setIntegerPreference(final String key, final Integer value) {
        final SharedPreferences settings = mContext.getSharedPreferences(SHARED_PREFS,
                Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public long getLongPreference(final String key, final long defaultValue) {
        final SharedPreferences settings = mContext.getSharedPreferences(SHARED_PREFS,
                Context.MODE_PRIVATE);
        return settings.getLong(key, defaultValue);
    }

    public void setLongPreference(final String key, final long value) {
        final SharedPreferences settings = mContext.getSharedPreferences(SHARED_PREFS,
                Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public boolean getBoolPreference(final String key, final boolean defaultValue) {
        final SharedPreferences settings = mContext.getSharedPreferences(SHARED_PREFS,
                Context.MODE_PRIVATE);
        return settings.getBoolean(key, defaultValue);
    }

    public void setBoolPreference(final String key, final boolean value) {
        final SharedPreferences settings = mContext.getSharedPreferences(SHARED_PREFS,
                Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public String getStringPreference(final String key, final String defaultValue) {
        final SharedPreferences settings = mContext.getSharedPreferences(SHARED_PREFS,
                Context.MODE_PRIVATE);
        return settings.getString(key, defaultValue);
    }

    public void setStringPreference(final String key, final String value) {
        final SharedPreferences settings = mContext.getSharedPreferences(SHARED_PREFS,
                Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

}
