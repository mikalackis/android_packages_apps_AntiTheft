
package com.android.antitheft.listeners;

import android.widget.Toast;

import com.android.antitheft.eventbus.StatusUpdateEvent;
import com.android.antitheft.util.PrefUtils;
import com.parse.ParseException;
import com.parse.SaveCallback;
import de.greenrobot.event.EventBus;
import com.google.gson.Gson;

public class ParseSaveCallback implements SaveCallback {
    
    private EventBus mBus = EventBus.getDefault(); 

    private String mAction;

    public ParseSaveCallback(final String action) {
        mAction = action;
    }

    @Override
    public void done(ParseException e) {
        StatusUpdateEvent event = new StatusUpdateEvent(-1, mAction);
        Gson gson = new Gson();
        if (e == null) {
            event.setTime(System.currentTimeMillis());
            String eventJson = gson.toJson(event);
            PrefUtils.getInstance().setStringPreference(PrefUtils.PARSE_LAST_UPDATE_EVENT,eventJson);
            mBus.post(event);
        } else {
            String eventJson = gson.toJson(event);
            PrefUtils.getInstance().setStringPreference(PrefUtils.PARSE_LAST_UPDATE_EVENT, eventJson);
            mBus.post(event);
        }
    }

}
