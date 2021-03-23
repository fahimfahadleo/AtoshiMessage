package com.blueitltd.atoshimessage;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends AppCompatActivity implements Serializable, LifecycleObserver {


    static ListView listView;
    static Context context;
    static Activity activity;
    ImageView sendnewemessage, search, options;
    LinearLayout searchlayout;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    CardView settingsview, helpview, contactusview;
    TextView settings, help, contactus;

    DatabaseHelper helper;
    static ArrayList<String> numberlist = new ArrayList<>();
    public static HashMap<String, ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>>> lastmaplist = new HashMap<>();


    public static boolean active = false;

    @Override
    public void onStart() {
        super.onStart();
        active = true;

        if (!Variables.isActive) {
            Cursor cursor = helper.getData(1);

            if (cursor != null && cursor.getCount() > 0) {
                Log.e("check", "login");
                startActivity(new Intent(this, PasswordActivity.class));
            } else {
                Log.e("check", "register");
                startActivity(new Intent(this, RegisterYourself.class));
            }
            finish();
        }

    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (doubleBackToExitPressedOnce) {
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackgrounded() {
        Variables.isDown = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForegrounded() {
        if (Variables.isDown) {
            startActivity(new Intent(this, PasswordActivity.class));
            finish();
        }
    }

    boolean isActive = false;
    TextInputEditText searchpeople;
    ArrayList<String> searched;
    static RelativeLayout primarylayout;
    static ArrayList<String> namelist;
    static SwipeRefreshLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listview);
        sendnewemessage = findViewById(R.id.newmessage);
        search = findViewById(R.id.search);
        searchlayout = findViewById(R.id.searchlayout);
        searchlayout.setVisibility(View.GONE);
        searchpeople = findViewById(R.id.searchpeople);
        searched = new ArrayList<>();
        options = findViewById(R.id.options);

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_view);

        settings = navigationView.findViewById(R.id.settings);
        contactus = navigationView.findViewById(R.id.contactus);
        help = navigationView.findViewById(R.id.help);

        settingsview = navigationView.findViewById(R.id.settingsview);
        helpview = navigationView.findViewById(R.id.helpview);
        contactusview = navigationView.findViewById(R.id.contactusview);
        primarylayout = findViewById(R.id.primarylayout);
        layout = findViewById(R.id.swipelayout);
        namelist = new ArrayList<>();
        context = getApplicationContext();
        activity = this;
        helper = new DatabaseHelper(this);






        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new CallSms(MainActivity.this);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layout.setRefreshing(false);
                        setUpData();
                    }
                }, 2000);


            }
        });


        settingsview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });


        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isActive) {
                    TransitionManager.beginDelayedTransition(searchlayout,new AutoTransition());
                    TransitionManager.beginDelayedTransition(listView,new AutoTransition());
                    searchlayout.setVisibility(View.GONE);
                    isActive = false;
                } else {
                    TransitionManager.beginDelayedTransition(listView,new AutoTransition());
                    TransitionManager.beginDelayedTransition(searchlayout,new AutoTransition());
                    searchlayout.setVisibility(View.VISIBLE);
                    isActive = true;
                }
            }
        });





        Cursor cursor = helper.getData(1);
        if (cursor.moveToFirst()) {
            do {
                String background = cursor.getString(cursor.getColumnIndex("background"));
                String textcolor = cursor.getString(cursor.getColumnIndex("textcolor"));


                Variables.backgroundcolor = background;
                Variables.textbackgroundcolor = textcolor;


                // do what ever you want here
            } while (cursor.moveToNext());
        }
        cursor.close();


        switch (Variables.backgroundcolor) {
            case "bg1":
                primarylayout.setBackgroundColor(getResources().getColor(R.color.background));
                break;
            case "bg2":
                primarylayout.setBackground(getResources().getDrawable(R.drawable.orangebg));
                break;
            case "bg3":
                primarylayout.setBackground(getResources().getDrawable(R.drawable.background3));
                break;
            case "bg4":
                primarylayout.setBackground(getResources().getDrawable(R.drawable.gradiantbgfour));
                break;
            case "bg5":
                primarylayout.setBackground(getResources().getDrawable(R.drawable.gradiantbgfive));
                break;
            case "bg6":
                primarylayout.setBackground(getResources().getDrawable(R.drawable.gradiantbgsix));
                break;
            case "bg7":
                primarylayout.setBackground(getResources().getDrawable(R.drawable.bg7));
                break;
            case "bg8":
                primarylayout.setBackground(getResources().getDrawable(R.drawable.bg8));
                break;
            case "bg9":
                primarylayout.setBackground(getResources().getDrawable(R.drawable.bg9));
                break;
            case "bg10":
                primarylayout.setBackground(getResources().getDrawable(R.drawable.bg10));
                break;
        }


        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE}, 1);


        if(!Variables.canDrawOverlays(this)){
            requestPermission(OVERLAY_PERMISSION_REQ_CODE_CHATHEAD);
        }


        searchpeople.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searched = new ArrayList<>();
                if (charSequence.length() != 0) {
                    for (String s : numberlist) {
                        if (s.contains(charSequence.toString())) {


                            searched.add(s);
                        }
                    }


                    ListviewAdapter adapter = new ListviewAdapter(context, searched);
                    listView.setAdapter(adapter);
                } else {
                    setUpData();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        sendnewemessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(i, 10);


                Variables.isDown = false;

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.

                setUpData();
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(MainActivity.this, "Permission denied to read your SMS", Toast.LENGTH_SHORT).show();
            }
            return;


            // other 'case' lines to check for other
            // permissions this app might request
        }
    }




    public static void setUpData() {

        HashMap<ArrayList<String>, HashMap<String, ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>>>> map = CallSms.getmessage();

        for (ArrayList<String> s : map.keySet()) {
            numberlist = s;
            lastmaplist = map.get(s);
        }


        for (String s : numberlist) {
            if (contactExists(context, s)) {
                namelist.add(getContactName(s, context));
            } else {
                namelist.add(s);
            }
        }

        ListviewAdapter adapter = new ListviewAdapter(context, numberlist);

        listView.setAdapter(adapter);

    }

    static String iconvalue;
    public static class ListviewAdapter extends ArrayAdapter<String> {
        ArrayList<String> mylist;

        public ListviewAdapter(Context context, ArrayList<String> users) {
            super(context, 0, users);
            mylist = users;


            Log.e("Mulist",mylist.toString());
        }


        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

            View vi = inflater.inflate(R.layout.singlemessagefile, null);

            final TextView name = vi.findViewById(R.id.name);
            TextView time = vi.findViewById(R.id.time);
            final LinearLayout mainlayout = vi.findViewById(R.id.mainlayout);





            switch (Variables.textbackgroundcolor) {
                case "black":
                    name.setTextColor(context.getResources().getColor(R.color.black));
                    time.setTextColor(context.getResources().getColor(R.color.black));
                    break;
                case "red":
                    name.setTextColor(context.getResources().getColor(R.color.red));
                    time.setTextColor(context.getResources().getColor(R.color.red));
                    break;
                case "white":
                    name.setTextColor(context.getResources().getColor(R.color.white));
                    time.setTextColor(context.getResources().getColor(R.color.white));
                    break;
                case "green":
                    name.setTextColor(context.getResources().getColor(R.color.green));
                    time.setTextColor(context.getResources().getColor(R.color.green));
                    break;
            }


            if (contactExists(context, mylist.get(position))) {
                name.setText(getContactName(mylist.get(position), context));
            } else {
                name.setText(mylist.get(position));
            }


            Log.e("lastmaplist",lastmaplist.toString());

            long timeinmil = checktime(mylist.get(position), lastmaplist.get(mylist.get(position)));
            Log.e("time", String.valueOf(timeinmil));
            Date date = new Date(timeinmil);
            String yourDesiredDateValue = new SimpleDateFormat("dd/MMM/yyyy \n hh:mm a").format(date);
            time.setText(yourDesiredDateValue);
            mainlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, showMessage.class);
                    i.putExtra("name", name.getText().toString());
                    i.putExtra("number", mylist.get(position));
                    i.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);

                }
            });


            mainlayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    DatabaseHelper helper = new DatabaseHelper(context);

                    String number = mylist.get(position);

                    if (number != null) {
                        if (number.contains("+88")) {
                            number = number.replace("+88", "");
                        }
                        if (number.contains(" ")) {
                            number = number.replace(" ", "");
                        }
                        if (number.contains("-")) {
                            number = number.replace("-", "");
                        }

                    }
                    Cursor c = helper.getbackgroundData(number);
                    if (c.moveToFirst()) {
                        Log.e("cursor","cursor");
                        do {

                            iconvalue = c.getString(c.getColumnIndex("chathead"));
                            // do what ever you want here
                        } while (c.moveToNext());
                    }else {
                        Log.e("called","called");
                        helper.insertBackground(number, "bg1", "black", "icon1");

                        iconvalue = "icon1";
                    }



                    if(Variables.canDrawOverlays(context)) {
                        Intent i = new Intent(context,ChatHeadService.class);
                        i.putExtra("name", name.getText().toString());
                        i.putExtra("number", mylist.get(position));
                        i.putExtra("icon",iconvalue);
                        context.startService(i);
                        activity.finish();
                    }

                    return true;
                }
            });
            return vi;
        }
    }

    static long checktime(String name, ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>> check) {
        long lasttime = 0L;
        Log.e("check",check.toString());

        for (int i = 0; i < check.size(); i++) {
            HashMap<String, ArrayList<HashMap<String, String>>> tempmap = check.get(i);
            ArrayList<HashMap<String, String>> tempolist = tempmap.get(name);
            Log.e("tempolist",tempolist.toString());
            Log.e("date",tempolist.get(4).get("date"));


            if (Long.parseLong(tempolist.get(4).get("date")) > lasttime) {
                lasttime = Long.parseLong(tempolist.get(4).get("date"));
            }
        }
        return lasttime;
    }


    public static String getContactName(final String phoneNumber, Context context) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName = "";
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }

    public static boolean contactExists(Context context, String number) {
        /// number is the phone number
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                cur.close();
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }

    public static void changeBg(String bg) {
        if (bg.equals("bg1")) {
            primarylayout.setBackgroundColor(context.getResources().getColor(R.color.background));
        } else if (bg.equals("bg2")) {
            primarylayout.setBackground(context.getResources().getDrawable(R.drawable.orangebg));
        } else if (bg.equals("bg3")) {
            primarylayout.setBackground(context.getResources().getDrawable(R.drawable.background3));
        } else if (bg.equals("bg4")) {
            primarylayout.setBackground(context.getResources().getDrawable(R.drawable.gradiantbgfour));
        } else if (bg.equals("bg5")) {
            primarylayout.setBackground(context.getResources().getDrawable(R.drawable.gradiantbgfive));
        } else if (bg.equals("bg6")) {
            primarylayout.setBackground(context.getResources().getDrawable(R.drawable.gradiantbgsix));
        } else if (bg.equals("bg7")) {
            primarylayout.setBackground(context.getResources().getDrawable(R.drawable.bg7));
        } else if (bg.equals("bg8")) {
            primarylayout.setBackground(context.getResources().getDrawable(R.drawable.bg8));
        } else if (bg.equals("bg9")) {
            primarylayout.setBackground(context.getResources().getDrawable(R.drawable.bg9));
        } else if (bg.equals("bg10")) {
            primarylayout.setBackground(context.getResources().getDrawable(R.drawable.bg10));
        }

    }

    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD = 1234;
    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG = 5678;

    private static void showChatHeadMsg(  ){

        String str = "You have a new Message";

        Intent it = new Intent(context, ChatHeadService.class);
        it.putExtra(Variables.EXTRA_MSG, str);
        context.startService(it);
    }

    private void needPermissionDialog(final int requestCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("You need to allow permission");
        builder.setPositiveButton("OK",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        requestPermission(requestCode);
                    }
                });
        builder.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        builder.setCancelable(false);
        builder.show();
    }





    private void requestPermission(int requestCode){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10 && resultCode == RESULT_OK) {

            Variables.isDown = false;
            // Get the URI and query the content provider for the phone number
            Uri contactUri = data.getData();
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor cursor = getApplicationContext().getContentResolver().query(contactUri, projection,
                    null, null, null);

            // If the cursor returned is valid, get the phone number
            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberIndex);


                Intent i = new Intent(MainActivity.this, showMessage.class);
                i.putExtra("name", getContactName(number, this));
                i.putExtra("number", number);

                i.setFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

            }

            cursor.close();
        } else {

            Variables.isDown = false;

        }




        if (requestCode == OVERLAY_PERMISSION_REQ_CODE_CHATHEAD) {
            if (!Variables.canDrawOverlays(MainActivity.this)) {
                needPermissionDialog(requestCode);
            }

        }else if(requestCode == OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG){
            if (!Variables.canDrawOverlays(MainActivity.this)) {
                needPermissionDialog(requestCode);
            }

        }

    }





}