
package com.android.antitheft.sms;

import com.android.antitheft.commands.AntiTheftCommand;
import com.android.antitheft.commands.AntiTheftCommandUtil;

import android.content.Context;
import android.telecom.Log;
import android.telephony.SmsManager;

/**
 * @author mikalackis
 */
public class AntiTheftSMSOperator {

    public static void checkMessage(final Context mContext, final String msg,
            final String returnNumber) {

        Log.i("AntiTheftSMSOperator", "Message: " + msg);
        if (msg.matches(AntiTheftCommandUtil.COMMAND_REGEX)) {
            Log.i("AntiTheftSMSOperator", "Message regex clear!");
            String[] command = msg.split(":");
            AntiTheftCommand atCommand = AntiTheftCommandUtil.getCommandByKey(command[0]);
            if (atCommand != null) {
                Log.i("AntiTheftSMSOperator", "Command found: " + atCommand.getKey());
                atCommand.executeCommand(command[1]);
                reportStatusToSender(returnNumber, "Command " + msg + " executed!");
            }

        }
    }

    private static void reportStatusToSender(final String sender, final String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(sender, null, message, null, null);
    }

}
