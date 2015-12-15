
package com.android.antitheft.listeners;

import android.widget.Toast;

import com.android.antitheft.eventbus.StatusUpdateEvent;
import com.android.antitheft.util.PrefUtils;
import com.parse.ParseException;
import com.parse.SaveCallback;
import de.greenrobot.event.EventBus;

public class ParseSaveCallback implements SaveCallback {
    
    private EventBus mBus = EventBus.getDefault(); 

    private String mAction;

    public ParseSaveCallback(final String action) {
        mAction = action;
    }

    @Override
    public void done(ParseException e) {
        if (e == null) {
            long time=System.currentTimeMillis();
            PrefUtils.getInstance().setStringPreference(PrefUtils.PARSE_LAST_UPDATE_TIME,
                    time+":"+mAction);
            mBus.post(new StatusUpdateEvent(time, mAction));
        } else {
            PrefUtils.getInstance().setStringPreference(PrefUtils.PARSE_LAST_UPDATE_TIME, -1+":"+mAction);
            mBus.post(new StatusUpdateEvent(-1, mAction));
        }
    }

}
