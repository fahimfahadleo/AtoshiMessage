package com.blueitltd.atoshimessage;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class MyDialogue extends Activity {
    public static boolean active = false;
    public static Activity myDialog;

    EditText edt;
    Button btn;
    View top;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialogue);

        edt = (EditText) findViewById(R.id.dialog_edt);
        btn = (Button) findViewById(R.id.dialog_btn);
        top = (View)findViewById(R.id.dialog_top);

        myDialog = MyDialogue.this;



        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String str = edt.getText().toString();
                if(str.length() > 0){
//					ChatHeadService.showMsg(MyDialog.this, str);
                    Intent it = new Intent(MyDialogue.this, ChatHeadService.class);
                    it.putExtra(Variables.EXTRA_MSG, str);
                    startService(it);
                }
            }
        });


        top.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });

    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        active = true;
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        active = false;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        active = false;
    }



}