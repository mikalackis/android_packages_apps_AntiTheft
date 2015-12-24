
package com.android.antitheft.commands;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.android.antitheft.AntiTheftApplication;
import com.android.antitheft.DeviceInfo;
import com.android.antitheft.listeners.ParseSaveCallback;
import com.android.antitheft.parse.ActivityParseObject;
import com.android.antitheft.services.AntiTheftService;
import com.android.antitheft.services.DeviceFinderService;

import java.util.ArrayList;

public abstract class AntiTheftCommand {

    protected String key;
    protected String[] commands;
    protected String description;

    public AntiTheftCommand(final String key, final String[] commands, final String description) {
        this.key = key;
        this.commands = commands;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String[] getCommands() {
        return commands;
    }

    public void setCommands(String[] commands) {
        this.commands = commands;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param action Report action being executed to Parse
     */
    protected void reportActionToParse(final String action) {
        ActivityParseObject activityObject = new ActivityParseObject();
        activityObject.setAction(action);
        activityObject.setImei(DeviceInfo.getInstance().getIMEI());
        activityObject.saveEventually(new ParseSaveCallback(action));
    }

    protected void startAntiTheftService(final int state) {
        Intent intent = new Intent(AntiTheftApplication.getInstance(), getServiceClass());
        intent.putExtra(AntiTheftService.SERVICE_PARAM, state);
        AntiTheftApplication.getInstance().startService(intent);
    }

    protected void stopAntiTheftService() {
        AntiTheftApplication.getInstance().stopService(
                new Intent(AntiTheftApplication.getInstance(), getServiceClass()));
    }

    public abstract void executeCommand(final String action);

    public abstract Class<?> getServiceClass();

    // public abstract void stopSelf();

}
