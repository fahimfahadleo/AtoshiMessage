package com.blueitltd.atoshimessage;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.Objects;

import static com.blueitltd.atoshimessage.MainActivity.active;
import static com.blueitltd.atoshimessage.MainActivity.setUpData;
import static com.blueitltd.atoshimessage.showMessage.active2;
import static com.blueitltd.atoshimessage.showMessage.setUpData2;

public class SMSRECEIVER extends BroadcastReceiver {
    private static final String TAG =
            SMSRECEIVER.class.getSimpleName();
    public static final String pdu_type = "pdus";


//    @Override
//    public void onReceive(Context context, Intent intent) {
//        // Get the SMS message.
//        Bundle bundle = intent.getExtras();
//        SmsMessage[] msgs;
//        String strMessage = "";
//        String format = bundle.getString("format");
//        // Retrieve the SMS message received.
//        Object[] pdus = (Object[]) bundle.get(pdu_type);
//        if (pdus != null) {
//            // Check the Android version.
//            boolean isVersionM =
//                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
//            // Fill the msgs array.
//            msgs = new SmsMessage[pdus.length];
//            for (int i = 0; i < msgs.length; i++) {
//                // Check Android version and use appropriate createFromPdu.
//                if (isVersionM) {
//                    // If Android version M or newer:
//                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
//                } else {
//                    // If Android version L or older:
//                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
//                }
//                // Build the message to show.
//                strMessage += "SMS from " + msgs[i].getOriginatingAddress();
//                strMessage += " :" + msgs[i].getMessageBody() + "\n";
//                // Log and display the SMS message.
//                Log.d(TAG, "onReceive: " + strMessage);
//                Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();
//            }
//        }
//    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("receiver","sms received");
        Toast.makeText(context,"sms received",Toast.LENGTH_SHORT).show();

        if(active){
            Toast.makeText(context,"MainActivity is active",Toast.LENGTH_SHORT).show();
            setUpData();
        }else if(active2){
            Toast.makeText(context,"showmessage is active",Toast.LENGTH_SHORT).show();
            setUpData2();
        }else {
            Toast.makeText(context,"noActivity is active",Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context,MainActivity.class));
        }
    }
}
