package com.blueitltd.atoshimessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import static com.blueitltd.atoshimessage.MainActivity.active;
import static com.blueitltd.atoshimessage.MainActivity.setUpData;
import static com.blueitltd.atoshimessage.showMessage.active2;
import static com.blueitltd.atoshimessage.showMessage.setUpData2;

public class SMSRECEIVER extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("receiver","sms received");
        Toast.makeText(context,"sms received",Toast.LENGTH_SHORT).show();

        if(active){

            new CallSms(context);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setUpData();
                }
            },2500);

        }else if(active2){
            new CallSms(context);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setUpData2();
                }
            },2500);
        }
    }
}
