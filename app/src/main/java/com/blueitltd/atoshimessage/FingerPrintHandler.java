package com.blueitltd.atoshimessage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.widget.Toast;

public class FingerPrintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;
    DatabaseHelper helper;
    String number,name;

    public FingerPrintHandler(Context context){

        this.context = context;
        helper = new DatabaseHelper(context);

    }
    public FingerPrintHandler(Context context,String name,String number){
        this.context = context;
        helper = new DatabaseHelper(context);
        this.number = number;
        this.name = name;
    }

    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject){

        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);

    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {

        //this.update("There was an Auth Error. " + errString, false);

    }

    @Override
    public void onAuthenticationFailed() {

        this.update("Auth Failed. ", false);

    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

        this.update("Error: " + helpString, false);

    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        this.result = result;

        this.update("You can now access the app.", true);

    }

    private void update(String s, boolean b) {
        if (b){

            if(context instanceof RegisterYourself){
                helper.insertContact(result.getCryptoObject().toString(),true);
                Variables.isActive = true;
                context.startActivity(new Intent(context,MainActivity.class));
                ((Activity)context).finish();
                Variables.isDown = false;
            }else if(context instanceof PasswordActivity){
                helper.updateContact(1,result.getCryptoObject().toString());
                Variables.isActive = true;
                context.startActivity(new Intent(context,MainActivity.class));
                ((Activity)context).finish();
                Variables.isDown = false;
            }else if(context instanceof PasswordActivitydialogue){
                Intent i = new Intent(context,showMessagedialogue.class);
                i.putExtra("name",name);
                i.putExtra("number",number);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                ((Activity)context).finish();
            }


        }else {
            Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
        }
    }

    FingerprintManager.AuthenticationResult result;
}



