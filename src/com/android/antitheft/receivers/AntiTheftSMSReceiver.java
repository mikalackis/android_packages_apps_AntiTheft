
package com.android.antitheft.receivers;

import com.android.antitheft.sms.AntiTheftSMSOperator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class AntiTheftSMSReceiver extends BroadcastReceiver {

    private final String TAG = "SMSReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Bundle pudsBundle = intent.getExtras();
        Object[] pdus = (Object[]) pudsBundle.get("pdus");
        SmsMessage messages = SmsMessage.createFromPdu((byte[]) pdus[0]);
        Log.i(TAG, messages.getMessageBody());
        AntiTheftSMSOperator operator = new AntiTheftSMSOperator(context);
        operator.checkMessage(messages.getMessageBody(), messages.getOriginatingAddress());
    }

}
