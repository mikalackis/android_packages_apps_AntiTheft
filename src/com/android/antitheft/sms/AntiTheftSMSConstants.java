
package com.android.antitheft.sms;

/**
 * @author mikalackis
 */
public class AntiTheftSMSConstants {

    // take picture command
    public static final String SMILE = "smile";
 // take picture command
    public static final String SMILE_NOW = "smile_now";
    // stop picture command
    public static final String STOP_SMILE = "stop_smile";
    // take video command
    public static final String ACTOR = "actor";
    // give me my location
    public static final String WHERE = "where";
    // constant location tracking start
    public static final String TRACK_ME_START = "track";
    // constant location tracking stop
    public static final String TRACK_ME_STOP = "track_stop";
    // full device lockdown
    public static final String LOCKDOWN = "byebye";
    // enable device
    public static final String IM_BACK = "leboss";
    // disable power button
    public static final String SCREW_POWER = "screw_power";
    // enable power button
    public static final String UNSCREW_POWER = "unscrew_power";
    // lock screen with pwd
    public static final String SCREEN_LOCK = "lockscreen";
    // record voice
    public static final String TALK = "talk";
    // record voice
    public static final String TALK_STOP = "talk_stop";
    
    public static enum SMS_CONSTANTS {
        SMILE,
        SMILE_NOW,
        STOP_SMILE,
        ACTOR,
        WHERE,
        TRACK_ME_START,
        TRACK_ME_STOP,
        LOCKDOWN,
        IM_BACK,
        SCREEN_LOCK,
        TALK,
        TALK_STOP,
        SCREW_POWER,
        UNSCREW_POWER
    }

}
