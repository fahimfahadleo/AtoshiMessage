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
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.blueitltd.atoshimessage.MainActivity.lastmaplist;

public class showMessagedialogue extends AppCompatActivity {
    static ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>> mainmap;
    static String title;
    TextView titleview;
    static HashMap<String, HashMap<String, String>> sortbytime;
    static ListView listView;
    static TextInputEditText typesomething, typepassword;
    ImageButton sendButton, setPassword, showpassword;
    Encryption encryption;
    Decryption decryption;
    View layout;
    RelativeLayout primaryLayout;
    DatabaseHelper helper;

    static String password = null;

    public static boolean active2 = false;
    String background,chatheadicon;
    static String textcolor;

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


    StringBuilder finalmessage;
    TextView textwatcher;

    boolean isSend = false;
    static String phonenumber;
    static SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_message_dialogue);
        titleview = findViewById(R.id.title);
        listView = findViewById(R.id.listview);
        typesomething = findViewById(R.id.typemessage);
        sendButton = findViewById(R.id.sendbutton);
        layout = findViewById(R.id.security);
        typepassword = layout.findViewById(R.id.typepassword);
        showpassword = layout.findViewById(R.id.showpassword);
        setPassword = layout.findViewById(R.id.setpassword);
        textwatcher = findViewById(R.id.textwatcher);
        swipeRefreshLayout = findViewById(R.id.swipelayout);
        primaryLayout = findViewById(R.id.primarylayout);

        helper = new DatabaseHelper(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new CallSms(showMessagedialogue.this);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        setUpData2();
                    }
                }, 2000);


            }

        });

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
                    typepassword.setText("");


                    listView.setAdapter(null);

                    if (time != null && time.length != 0) {
                        ListviewAdapter adapter = new ListviewAdapter(context, time);
                        listView.setAdapter(adapter);
                    }

                }
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(showMessagedialogue.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 5);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 10);
        }

        typesomething.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.length()!=0){
                    if(charSequence.length()>70){
                        textwatcher.setTextColor(getResources().getColor(R.color.colorPrimary));
                    }else {
                        textwatcher.setTextColor(getResources().getColor(R.color.ashcolor));
                    }
                    textwatcher.setText(String.valueOf(charSequence.length()));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                messagees = typesomething.getText().toString();
                if (TextUtils.isEmpty(messagees)) {
                    typesomething.setError("Write a message!");
                    typesomething.requestFocus();
                } else if (password == null || password.equals("") || password.length() < 4) {
                    typepassword.setError("Password empty or too short!");
                    typepassword.requestFocus();
                } else {
                  String  encryptedpass = encryption.encryption(password, messagees);

                    finalmessage = new StringBuilder();





                    Log.e("enc",encryptedpass);
                    String [] somestr = encryptedpass.split(" ");
                    for (String s : somestr) {
                        String str = Character.toString((char) Integer.parseInt(s));
                        finalmessage.append(str);

                    }

                    typesomething.setText(null);

                    if (ContextCompat.checkSelfPermission(showMessagedialogue.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(showMessagedialogue.this, new String[]{Manifest.permission.SEND_SMS}, 0);
                    } else {
                        Log.e("dhukche","first else e");
                        SendSMS(phonenumber, finalmessage.toString());

                    }
                }
            }
        });


        sortbytime = new HashMap<>();
        Intent i = getIntent();
        title = i.getStringExtra("name");
        phonenumber = i.getStringExtra("number");

        if(phonenumber!=null){
            if(phonenumber.contains("+88")){
                phonenumber = phonenumber.replace("+88","");
            }
            if(phonenumber.contains(" ")){
                phonenumber =phonenumber.replace(" ","");
            }
            if(phonenumber.contains("-")){
                phonenumber = phonenumber.replace("-","");
            }

        }


        titleview.setText(title);

        Cursor c = helper.getbackgroundData(phonenumber);
        Log.e("phonenumber",phonenumber);

        if (c.moveToFirst()) {
            Log.e("cursor","curso    dsfadsfr");
            do {
                background = c.getString(c.getColumnIndex("back"));
                textcolor = c.getString(c.getColumnIndex("textcolor"));
                chatheadicon = c.getString(c.getColumnIndex("chathead"));


                Log.e(background,textcolor);
                Log.e("chathead",chatheadicon);
                // do what ever you want here
            } while (c.moveToNext());
        }else {
            Log.e("called","called");
            helper.insertBackground(phonenumber, "bg1", "black", "icon1");
            background = "bg1";
            textcolor = "black";
            chatheadicon = "icon1";
        }




        switch (background) {
            case "bg1":
                primaryLayout.setBackgroundColor(getResources().getColor(R.color.background));
                break;
            case "bg2":
                primaryLayout.setBackground(getResources().getDrawable(R.drawable.orangebg));
                break;
            case "bg3":
                primaryLayout.setBackground(getResources().getDrawable(R.drawable.background3));
                break;
            case "bg4":
                primaryLayout.setBackground(getResources().getDrawable(R.drawable.gradiantbgfour));
                break;
            case "bg5":
                primaryLayout.setBackground(getResources().getDrawable(R.drawable.gradiantbgfive));
                break;
            case "bg6":
                primaryLayout.setBackground(getResources().getDrawable(R.drawable.gradiantbgsix));
                break;
            case "bg7":
                primaryLayout.setBackground(getResources().getDrawable(R.drawable.bg7));
                break;
            case "bg8":
                primaryLayout.setBackground(getResources().getDrawable(R.drawable.bg8));
                break;
            case "bg9":
                primaryLayout.setBackground(getResources().getDrawable(R.drawable.bg9));
                break;
            case "bg10":
                primaryLayout.setBackground(getResources().getDrawable(R.drawable.bg10));
                break;
        }

        setUpData2();

    }
    String messagees;
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

                    Log.e("dhukche","first else e");
                    if (finalmessage.toString().length()>0) {
                        SendSMS(phonenumber, finalmessage.toString());
                    }


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(showMessagedialogue.this, "Permission denied to read your SMS", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    SubscriptionInfo simcardid = null;
    SubscriptionInfo simInfo1, simInfo2;


    public static void setUpData2() {
        HashMap<ArrayList<String>, HashMap<String, ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>>>> map = CallSms.getmessage();
        for (ArrayList<String> s : map.keySet()) {
            lastmaplist = map.get(s);
        }
        if (lastmaplist != null && lastmaplist.containsKey(phonenumber)) {
            mainmap = lastmaplist.get(phonenumber);
        }
        if (mainmap != null && mainmap.size() > 0) {


            Log.e("mainmap",mainmap.toString());
            sortByTime(mainmap);
            ListviewAdapter adapter = new ListviewAdapter(context, time);
            listView.setAdapter(adapter);
        }
    }

    static String[] time;
    static HashMap<String, String> tempmap;

    static void sortByTime(ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>> mainmap) {
        time = new String[mainmap.size()];
        for (int i = 0; i < mainmap.size(); i++) {

            Log.e("phonenumber",phonenumber);



            time[i] = mainmap.get(i).get(phonenumber).get(4).get("date");
            tempmap = new HashMap<>();
            tempmap.put("date", mainmap.get(i).get(phonenumber).get(4).get("date"));
            tempmap.put("type", mainmap.get(i).get(phonenumber).get(9).get("type"));
            tempmap.put("body", mainmap.get(i).get(phonenumber).get(12).get("body"));
            tempmap.put("seen", mainmap.get(i).get(phonenumber).get(16).get("seen"));
            tempmap.put("status", mainmap.get(i).get(phonenumber).get(8).get("status"));

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

            switch (textcolor) {
                case "black":
                    tv.setTextColor(context.getResources().getColor(R.color.black));
                    break;
                case "red":
                    tv.setTextColor(context.getResources().getColor(R.color.red));
                    break;
                case "white":
                    tv.setTextColor(context.getResources().getColor(R.color.white));
                    break;
                case "green":
                    tv.setTextColor(context.getResources().getColor(R.color.green));
                    break;
            }


            String pass = password;

            if (TextUtils.isEmpty(pass)) {
                tv.setText(sortbytime.get(mylist[position]).get("body"));
            } else {

                String Decrypte = decryption.decryption(pass, sortbytime.get(mylist[position]).get("body"));

                tv.setText(Decrypte);
            }

            return vi;
        }
    }

    @Override
    public void onBackPressed() {

        if(isSend){
            startActivity(new Intent(this,MainActivity.class));
        }else {
            super.onBackPressed();
        }

    }


    private void SendSMS(final String phoneNumber, final String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        Log.e("message",message);

        final PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);

        final PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        new CallSms(showMessagedialogue.this);
                        isSend = true;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setUpData2();
                            }
                        },2500);


                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure, Probably your Sms is too long for this app.",
                                Toast.LENGTH_SHORT).show();
                        typesomething.setText(messagees);
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        typesomething.setText(messagees);
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        typesomething.setText(messagees);
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        typesomething.setText(messagees);
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
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


        SubscriptionManager localSubscriptionManager = SubscriptionManager.from(showMessagedialogue.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            if (localSubscriptionManager.getActiveSubscriptionInfoCount() > 1) {
                List localList = localSubscriptionManager.getActiveSubscriptionInfoList();
                simInfo1 = (SubscriptionInfo) localList.get(0);
                simInfo2 = (SubscriptionInfo) localList.get(1);

                if (simcardid != null) {
                    SmsManager.getSmsManagerForSubscriptionId(simcardid.getSubscriptionId()).sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(showMessagedialogue.this);
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


                Log.e("messagelength",phoneNumber);
                manager.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);

            }
            return;
        } else {
            ActivityCompat.requestPermissions(showMessagedialogue.this, new String[]{Manifest.permission.SEND_SMS}, 0);

        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setUpData2();
            }
        }, 3000);
    }




        public ArrayList<String> splitEqually(String text, int size) {
        // Give the list the right capacity to start with. You could use an array
        // instead if you wanted.
        ArrayList<String> ret = new ArrayList<>();


        for (int start = 0; start < text.length(); start += size) {
            ret.add(text.substring(start, Math.min(text.length(), start + size)));
        }
        return ret;
    }


}