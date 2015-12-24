
package com.android.antitheft.commands;

import com.android.antitheft.services.WhosThatSoundService;

public class TalkCommand extends AntiTheftCommand {

    public TalkCommand(String key, String[] commands, String description) {
        super(key, commands, description);
    }

    @Override
    public void executeCommand(final String action) {
        reportActionToParse(action);
        if (action.equals(AntiTheftCommandUtil.TALK)) {
            startAntiTheftService(-1);
        }
        else if (action.equals(AntiTheftCommandUtil.TALK_STOP)) {
            stopAntiTheftService();
        }
    }

    @Override
    public Class<?> getServiceClass() {
        return WhosThatSoundService.class;
    }

}
