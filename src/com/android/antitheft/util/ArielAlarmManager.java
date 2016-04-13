package com.android.antitheft.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.android.antitheft.AntiTheftApplication;
import com.android.antitheft.receivers.AntiTheftAlarmReceiver;

import java.util.Date;

/**
 * Created by mikalackis on 18.3.16..
 */
public class ArielAlarmManager {

    private AlarmManager mAlarmManager;

    private static ArielAlarmManager mInstance;

    private PendingIntent mPendingIntent;

    public ArielAlarmManager(){
        mAlarmManager = (AlarmManager) AntiTheftApplication.getInstance().getSystemService(Context.ALARM_SERVICE);
    }

    public static ArielAlarmManager getInstance(){
        if(mInstance==null){
            mInstance=new ArielAlarmManager();
        }
        return mInstance;
    }

    private PendingIntent getPendingIntent(){
        if(mPendingIntent==null) {
            Intent intent = new Intent(AntiTheftApplication.getInstance(), AntiTheftAlarmReceiver.class);
            mPendingIntent = PendingIntent.getBroadcast(AntiTheftApplication.getInstance(), 0, intent, 0);
        }
        return mPendingIntent;
    }

    public void setAlarm(final Date when){
        cancelAlarm();
        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP,when.getTime(),getPendingIntent());
        AntiTheftNotifier.sendNotification("Ariel lease ends at: "+when);
    }

    public void cancelAlarm(){
        mAlarmManager.cancel(getPendingIntent());
    }

}
