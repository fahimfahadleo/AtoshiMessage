package com.blueitltd.atoshimessage;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends AppCompatActivity implements Serializable {


    static ListView listView;
    static Context context;
    static Activity activity;
    CircleImageView sendnewemessage;

    static ArrayList<String> numberlist;
   public static HashMap<String,ArrayList<HashMap<String,ArrayList<HashMap<String,String>>>>> lastmaplist;

    public static boolean active = false;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listview);
        sendnewemessage = findViewById(R.id.newmessage);

        context = getApplicationContext();
        activity = this;


//        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
//        filter.setPriority(2147483647);
//        SMSRECEIVER receiver = new SMSRECEIVER();
//        registerReceiver(receiver, filter);


        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_SMS},
                1);


        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECEIVE_SMS)!=PackageManager.PERMISSION_GRANTED){
            Log.e("showmessage","showmessage");
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECEIVE_SMS},0);
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE},5);
        }




        sendnewemessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View vi = getLayoutInflater().inflate(R.layout.getnumber,null);
                AlertDialog.Builder builder =new AlertDialog.Builder(MainActivity.this);
                builder.setView(vi);
                final AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                final EditText number = vi.findViewById(R.id.number);
                Button send = vi.findViewById(R.id.next);
                Button cancle = vi.findViewById(R.id.cancle);
                cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String numberstr = number.getText().toString();
                        if(TextUtils.isEmpty(numberstr)){
                            number.setError("number empty!");
                        }else {
                            Intent i = new Intent(MainActivity.this,showMessage.class);
                            i.putExtra("name",number.getText().toString());
                            i.setFlags(FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                        }
                    }
                });

            }
        });

    }











    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    setUpData();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your SMS", Toast.LENGTH_SHORT).show();
                    Log.e("permission","permission denied read sms");
                }
                return;
            }
            case 0:{
                Log.e("case 0 ","called");
                if (shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_SMS)) {
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, 0);
                                }
                            };
                }
            }


            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



   public static void setUpData(){

      CallSms callSms = new CallSms(context);

        HashMap<ArrayList<String>, HashMap<String, ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>>>> map = callSms.callsms();

        for(ArrayList<String> s : map.keySet()){
            numberlist = s;
            lastmaplist = map.get(s);
        }

        ListviewAdapter adapter = new ListviewAdapter(context, numberlist);
        listView.setAdapter(adapter);
    }

    public static class ListviewAdapter extends ArrayAdapter<String> {
        ArrayList<String> mylist;

        public ListviewAdapter(Context context, ArrayList<String> users) {
            super(context, 0, users);
            mylist = users;
        }


        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

            View vi = inflater.inflate(R.layout.singlefile, null);

            final TextView name = vi.findViewById(R.id.name);
            TextView time = vi.findViewById(R.id.time);
            final LinearLayout mainlayout = vi.findViewById(R.id.mainlayout);

            name.setText(mylist.get(position));

            long timeinmil =checktime(mylist.get(position),lastmaplist.get(mylist.get(position)));
            Log.e("time",String.valueOf(timeinmil));
            Date date = new Date(timeinmil);
            String yourDesiredDateValue = new SimpleDateFormat("dd/MMM/yyyy \n hh:mm a").format(date);
            time.setText(yourDesiredDateValue);
            mainlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context,showMessage.class);
                    //i.putExtra("map",lastmaplist.get(name.getText().toString()));
                    i.putExtra("name",name.getText().toString());
                    i.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                    activity.finish();

                }
            });
            return vi;
        }
    }

    static long checktime(String name, ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>> check) {
        long lasttime = Long.valueOf(0);

        for(int i = 0;i<check.size();i++){
            HashMap<String,ArrayList<HashMap<String,String>>> tempmap = check.get(i);
            ArrayList<HashMap<String,String>> tempolist = tempmap.get(name);
            if(Long.parseLong(tempolist.get(4).get("date"))>lasttime){
                lasttime =Long.parseLong(tempolist.get(4).get("date"));
            }
        }
        return lasttime;
    }
}