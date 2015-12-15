package com.android.antitheft.eventbus;

public class StatusUpdateEvent {
    
    private long mTime;
    private String mAction;
    
    public StatusUpdateEvent(long mTime, String mAction) {
        super();
        this.mTime = mTime;
        this.mAction = mAction;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long mTime) {
        this.mTime = mTime;
    }

    public String getAction() {
        return mAction;
    }

    public void setAction(String mAction) {
        this.mAction = mAction;
    }

}
