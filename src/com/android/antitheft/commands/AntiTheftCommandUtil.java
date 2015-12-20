
package com.android.antitheft.commands;

import java.util.HashMap;
import java.util.regex.Pattern;

public class AntiTheftCommandUtil {

    public static final String COMMAND_REGEX = "^\\b\\w{3}\\b:\\w+$";// ex: loc:track, img:actor

    public static final String KEY_IMAGE = "img";
    public static final String KEY_LOCATION = "loc";
    public static final String KEY_SOUND = "snd";
    public static final String KEY_THEFT = "tft";
    public static final String KEY_SCREEN = "scr";
    
    // sound commands
    public static final String TALK = "talk";
    public static final String TALK_STOP = "talk_stop";
    
    // location commands
    public static final String WHERE = "where"; // report my location now
    public static final String TRACK_ME_START = "track"; // start tracking my location
    public static final String TRACK_ME_STOP = "track_stop"; // stop tracking my location
    
    // image commands
    public static final String SMILE = "smile"; // face track take photo
    public static final String SMILE_NOW = "smile_now"; // instant photo
    public static final String STOP_SMILE = "stop_smile"; // stop face track
    public static final String ACTOR = "actor"; // start recording video

    // theft commands
    public static final String LOCKDOWN = "byebye"; // full device lockdown
    public static final String IM_BACK = "leboss"; // full device unlock
    public static final String SCREEN_LOCK = "lockscreen"; // lock my screen with pwd
    

    private static HashMap<String, AntiTheftCommand> mCommandMap = new HashMap<>();

    static {
        mCommandMap.put(KEY_IMAGE, new PictureCommand(KEY_IMAGE, new String[] {
                SMILE, SMILE_NOW, STOP_SMILE, ACTOR
        }, "Image tracking command"));
        mCommandMap.put(KEY_LOCATION, new DeviceFinderCommand(KEY_LOCATION, new String[] {
                TRACK_ME_START, TRACK_ME_STOP, WHERE
        }, "GPS location"));
        mCommandMap.put(KEY_THEFT, new TheftModeCommand(KEY_THEFT, new String[] {
                LOCKDOWN, IM_BACK
        }, "Theft mode"));
        mCommandMap.put(KEY_SCREEN, new ScreenLockCommand(KEY_SCREEN, new String[] {
            SCREEN_LOCK
        }, "Locks screen with predifined pin"));
        mCommandMap.put(KEY_SOUND, new TalkCommand(KEY_SOUND, new String[] {
                TALK, TALK_STOP
        }, "Talk to me please, let me hear your voice"));
    }
    
    public static AntiTheftCommand getCommandByKey(final String key){
        return mCommandMap.get(key);
    }
    
    public static HashMap<String, AntiTheftCommand> getAllCommands(){
        return mCommandMap;
    }

    public static final String[] CONTROLS = new String[] {
            SMILE.toString(),
            SMILE_NOW.toString(),
            STOP_SMILE.toString(),
            ACTOR.toString(),
            WHERE.toString(),
            TRACK_ME_START.toString(),
            TRACK_ME_STOP.toString(),
            LOCKDOWN.toString(),
            IM_BACK.toString(),
            SCREEN_LOCK.toString(),
            TALK.toString(),
            TALK_STOP.toString()
    };

    public void executeBatchCommands() {

    }

}
