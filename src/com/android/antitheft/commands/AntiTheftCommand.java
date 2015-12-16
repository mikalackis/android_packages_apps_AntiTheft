package com.android.antitheft.commands;

public abstract class AntiTheftCommand {
    
    protected String key;
    protected String command;
    protected String description;
    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public abstract void executeCommand(final String action);
    
    //public abstract void stopSelf();

}
