package com.blueitltd.atoshimessage;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.blueitltd.atoshimessage.MainActivity.lastmaplist;

public class showMessage extends AppCompatActivity {
    static ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>> mainmap;
    static String title;
    TextView titleview;
    static HashMap<String, HashMap<String, String>> sortbytime;
    static ListView listView;
    static EditText typesomething, typepassword;
    ImageButton sendButton, setPassword, showpassword;
    Encryption encryption;
    Decryption decryption;

    static String password = null;

    public static boolean active2 = false;

    @Override
    public void onStart() {
        super.onStart();
        active2 = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active2 = false;
    }

    static Context context;


    String encryptedpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_message);
        titleview = findViewById(R.id.title);
        listView = findViewById(R.id.listview);
        typesomething = findViewById(R.id.typemessage);
        sendButton = findViewById(R.id.sendbutton);
        typepassword = findViewById(R.id.typepassword);
        showpassword = findViewById(R.id.showpassword);
        setPassword = findViewById(R.id.setpassword);
        context = getApplicationContext();
        password = "";

        encryption = new Encryption();
        decryption = new Decryption(context);

        showpassword.setBackgroundColor(getResources().getColor(R.color.red));
        setPassword.setBackgroundColor(getResources().getColor(R.color.red));


        showpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorDrawable d = (ColorDrawable) showpassword.getBackground();
                if (d.getColor() == new ColorDrawable(getResources().getColor(R.color.red)).getColor()) {
                    typepassword.setTransformationMethod(null);
                    showpassword.setBackgroundColor(getResources().getColor(R.color.green));
                } else {
                    typepassword.setTransformationMethod(new PasswordTransformationMethod());
                    showpassword.setBackgroundColor(getResources().getColor(R.color.red));
                }

            }
        });

        setPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorDrawable d = (ColorDrawable) setPassword.getBackground();
                if (d.getColor() == new ColorDrawable(getResources().getColor(R.color.red)).getColor()) {
                    password = typepassword.getText().toString();
                    setPassword.setBackgroundColor(getResources().getColor(R.color.green));

                    listView.setAdapter(null);

                    if (time != null && time.length != 0) {
                        ListviewAdapter adapter = new ListviewAdapter(context, time);
                        listView.setAdapter(adapter);
                    }


                } else {
                    password = "";
                    setPassword.setBackgroundColor(getResources().getColor(R.color.red));


                    listView.setAdapter(null);

                    if (time != null && time.length != 0) {
                        ListviewAdapter adapter = new ListviewAdapter(context, time);
                        listView.setAdapter(adapter);
                    }

                }
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(showMessage.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 5);
        }

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECEIVE_SMS)!=PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECEIVE_SMS},10);
        }
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = typesomething.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    typesomething.setError("Write a message!");
                    typesomething.requestFocus();
                } else if (password == null || password.equals("") || password.length() < 4) {
                    typepassword.setError("Password empty or too short!");
                    typepassword.requestFocus();
                } else {
                    encryptedpass = encryption.encryption(password, message);

                    typesomething.setText(null);

                    if (ContextCompat.checkSelfPermission(showMessage.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(showMessage.this, new String[]{Manifest.permission.SEND_SMS}, 0);
                    } else {
                        SendSMS(title, encryptedpass);
                    }
                }
            }
        });


        sortbytime = new HashMap<>();
        Intent i = getIntent();
        title = i.getStringExtra("name");
        titleview.setText(title);

        setUpData2();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 0: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if(encryptedpass!=null){
                        SendSMS(title, encryptedpass);
                    }


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(showMessage.this, "Permission denied to read your SMS", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    SubscriptionInfo simcardid = null;
    SubscriptionInfo simInfo1, simInfo2;


    public static void setUpData2() {
        Toast.makeText(context,"Called",Toast.LENGTH_SHORT).show();
        CallSms callSms = new CallSms(context);
        HashMap<ArrayList<String>, HashMap<String, ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>>>> map = callSms.callsms();
        for(ArrayList<String> s : map.keySet()){
            lastmaplist = map.get(s);
        }
        if(lastmaplist!=null && lastmaplist.containsKey(title)){
            mainmap = lastmaplist.get(title);
        }
        if(mainmap!=null){
            sortByTime();
            ListviewAdapter adapter = new ListviewAdapter(context, time);
            listView.setAdapter(adapter);
        }
    }
    static String[] time;
    static HashMap<String, String> tempmap;
    static void sortByTime() {
        time = new String[mainmap.size()];
        for (int i = 0; i < mainmap.size(); i++) {
            time[i] = mainmap.get(i).get(title).get(4).get("date");
            tempmap = new HashMap<>();
            tempmap.put("date", mainmap.get(i).get(title).get(4).get("date"));
            tempmap.put("type", mainmap.get(i).get(title).get(9).get("type"));
            tempmap.put("body", mainmap.get(i).get(title).get(12).get("body"));
            tempmap.put("seen", mainmap.get(i).get(title).get(16).get("seen"));
            tempmap.put("status", mainmap.get(i).get(title).get(8).get("status"));

            sortbytime.put(time[i], tempmap);
        }
        Arrays.sort(time);
    }
    public static class ListviewAdapter extends ArrayAdapter<String> {
        String[] mylist;
        Decryption decryption;

        public ListviewAdapter(Context context, String[] users) {
            super(context, 0, users);
            mylist = users;
            decryption = new Decryption(context);
        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View vi;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            if (sortbytime.get(mylist[position]).get("type").equals("1")) {
                vi = inflater.inflate(R.layout.bubblestart, null);
            } else {
                vi = inflater.inflate(R.layout.bubbleend, null);
            }
            TextView tv = vi.findViewById(R.id.text);

            String pass = password;

            if(TextUtils.isEmpty(pass)){
                tv.setText(sortbytime.get(mylist[position]).get("body"));
            }else {
                String Decrypte=decryption.decryption(pass, sortbytime.get(mylist[position]).get("body"));
                tv.setText(Decrypte);
            }

            return vi;
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }














    private void SendSMS(final String phoneNumber, final String message)
    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        final PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,new Intent(SENT), 0);

        final PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        setUpData2();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));








        SubscriptionManager localSubscriptionManager = SubscriptionManager.from(showMessage.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            if (localSubscriptionManager.getActiveSubscriptionInfoCount() > 1) {
                List localList = localSubscriptionManager.getActiveSubscriptionInfoList();
                simInfo1 = (SubscriptionInfo) localList.get(0);
                simInfo2 = (SubscriptionInfo) localList.get(1);

                if (simcardid != null) {
                    SmsManager.getSmsManagerForSubscriptionId(simcardid.getSubscriptionId()).sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(showMessage.this);
                    builder.setTitle("Choose Sim Card");
                    builder.setMessage("Choose Your Default Sim Card.");
                    builder.setPositiveButton("SIM 1", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            simcardid = simInfo1;
                            SmsManager.getSmsManagerForSubscriptionId(simInfo1.getSubscriptionId()).sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
                        }
                    });
                    builder.setNegativeButton("SIM 2", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            simcardid = simInfo2;
                            SmsManager.getSmsManagerForSubscriptionId(simInfo2.getSubscriptionId()).sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
                        }
                    });

                    builder.create();
                    builder.show();
                }

            } else {
                SmsManager manager = SmsManager.getDefault();
                manager.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);

            }
            return;
        }else {

        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setUpData2();
            }
        },10000);


    }






}