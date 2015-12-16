package com.android.antitheft.commands;


import java.util.HashMap;

public class AntiTheftCommandUtil {
    
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
        COMMAND_MAP.put(SMILE, new PictureCommand(SMILE, SMILE, "Facetracking and photo when located"));
        COMMAND_MAP.put(SMILE_NOW, new PictureCommand(SMILE_NOW, SMILE_NOW, "Smile and wave: youre on a photo!"));
        COMMAND_MAP.put(STOP_SMILE, new PictureCommand(STOP_SMILE, STOP_SMILE, "Stops face tracking if started"));
        COMMAND_MAP.put(ACTOR, new VideoCommand(ACTOR, ACTOR, "Camera rolling... ACTION!"));
        COMMAND_MAP.put(WHERE, new DeviceFinderCommand(WHERE, WHERE, "Instant GPS location"));
        COMMAND_MAP.put(TRACK_ME_START, new DeviceFinderCommand(TRACK_ME_START,TRACK_ME_START,"Continuous GPS tracking"));
        COMMAND_MAP.put(TRACK_ME_STOP, new DeviceFinderCommand(TRACK_ME_STOP,TRACK_ME_STOP,"Stops continuous GPS tracking"));
        COMMAND_MAP.put(LOCKDOWN, new TheftModeCommand(LOCKDOWN,LOCKDOWN,"Lockdown baby, bye bye"));
        COMMAND_MAP.put(IM_BACK, new TheftModeCommand(IM_BACK,IM_BACK,"Le boss is back ;)"));
        COMMAND_MAP.put(SCREEN_LOCK, new ScreenLockCommand(SCREEN_LOCK,SCREEN_LOCK,"Locks screen with predifined pin"));
        COMMAND_MAP.put(TALK, new TalkCommand(TALK,TALK,"Talk to me please, let me hear your voice"));
        COMMAND_MAP.put(TALK_STOP, new TalkCommand(TALK_STOP,TALK_STOP,"Stop talking, its anoying"));
    }
    
    public void executeBatchCommands(){
        
    }
    
}
