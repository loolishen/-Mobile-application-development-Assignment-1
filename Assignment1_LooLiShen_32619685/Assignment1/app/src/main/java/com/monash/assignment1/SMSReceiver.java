package com.monash.assignment1;

import static androidx.core.content.ContextCompat.RECEIVER_EXPORTED;
import static androidx.core.content.ContextCompat.registerReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);

        String message = null;
        for (int i = 0; i < messages.length; i++) {
            SmsMessage currentMessage = messages[i];
            message = currentMessage.getDisplayMessageBody();
        }

        /*
         * For each new message, send a broadcast which contains the new message to MainActivity
         * The MainActivity has to tokenize the new message and update the UI
         * */
        Intent msgIntent = new Intent();
        msgIntent.setAction(SMS_FILTER);
        msgIntent.putExtra(SMS_MSG_KEY, message);
        context.sendBroadcast(msgIntent);

    }

    // used as a channel to broadcast the message
    // any application aware of this channel can listen to the broadcasts
    public static final String SMS_FILTER = "SMS_FILTER";

    // within the broadcast, we would like to send information
    // and this will be the key to identify that information, in this case the SMS message
    public static final String SMS_MSG_KEY = "SMS_MSG_KEY";


}