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
    
    // take picture command
    public static final String SMILE = "smile"; // face track take photo
 // take picture command
    public static final String SMILE_NOW = "smile_now"; // instant photo
    // stop picture command
    public static final String STOP_SMILE = "stop_smile"; // stop face track
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
    // lock screen with pwd
    public static final String SCREEN_LOCK = "lockscreen";
    // record voice
    public static final String TALK = "talk";
    // record voice
    public static final String TALK_STOP = "talk_stop";
    
    public static HashMap<String,AntiTheftCommand> COMMAND_MAP = new HashMap<>();
    
    public static final String[] CONTROLS = new String[]{
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
    
    public static void initCommands(){
        COMMAND_MAP.clear();
        COMMAND_MAP.put(KEY_IMAGE, new PictureCommand(KEY_IMAGE, new String[]{SMILE, SMILE_NOW, STOP_SMILE, ACTOR},"Image tracking command"));
        COMMAND_MAP.put(KEY_LOCATION, new DeviceFinderCommand(KEY_LOCATION, new String[]{TRACK_ME_START, TRACK_ME_STOP,WHERE}, "GPS location"));
        COMMAND_MAP.put(KEY_THEFT, new TheftModeCommand(KEY_THEFT,new String[]{LOCKDOWN, IM_BACK},"Theft mode"));
        COMMAND_MAP.put(KEY_SCREEN, new ScreenLockCommand(KEY_SCREEN,new String[]{SCREEN_LOCK},"Locks screen with predifined pin"));
        COMMAND_MAP.put(KEY_SOUND, new TalkCommand(KEY_SOUND, new String[]{TALK, TALK_STOP},"Talk to me please, let me hear your voice"));
    }
    
    public void executeBatchCommands(){
        
    }
    
}
