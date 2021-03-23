package com.blueitltd.atoshimessage;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class CallSms implements Serializable {
    static Context context;
    static HashMap<ArrayList<String>, HashMap<String, ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>>>> var = new HashMap<>();

    public CallSms(Context context) {
        CallSms.context = context;
        callsms();
    }

    public static void callsms() {



        HashMap<String, HashMap<String, String>> tempmap = new HashMap<>();
        ArrayList<String> numberlist = new ArrayList<>();
        HashMap<String, ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>>> lastmaplist = new HashMap<>();
        HashMap<ArrayList<String>, HashMap<String, ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>>>> map = new HashMap<>();
        HashMap<Integer, HashMap<String, HashMap<String, String>>> list = new HashMap<>();
        int ikasdjflk = 0;
        var.clear();
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms"), null, null, null, null);
        if (cursor.moveToFirst()) { // must check the result to prevent exception
            Log.e("check", "checking");
            do {
                tempmap = new HashMap<>();
             //   String msgData = "";
                for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
                    HashMap<String, String> tmp = new HashMap<>();
                    tmp.put(cursor.getColumnName(idx), cursor.getString(idx) == null ? "" : cursor.getString(idx));
                    tempmap.put(String.valueOf(idx), tmp);
                  //  msgData = msgData + " " + cursor.getColumnName(idx) + ":" + (cursor.getString(idx) == null ? "" : cursor.getString(idx));
                }
                list.put(ikasdjflk, tempmap);

                String temp;
                if(tempmap.get("2").get("address").contains("+88")){
                    temp = tempmap.get("2").get("address").replace("+88", "");
                }else {
                    temp = tempmap.get("2").get("address");
                }

                if(temp.contains(" ")){
                    temp = temp.replace(" ","");
                }
                if(temp.contains("-")){
                    temp.replace("-","");
                }



                if (!numberlist.contains(temp)) {
                    numberlist.add(temp);
                }


                ikasdjflk++;
            } while (cursor.moveToNext());
        } else {
            Toast.makeText((context), "Your Inbox is Empty!", Toast.LENGTH_LONG).show();
        }
        cursor.close();
        Log.e("int_value", ikasdjflk + "");

        for (int position = 0; position < numberlist.size(); position++) {
            ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>> lastmap = new ArrayList();
            for (int i = 0; i < list.size(); i++) {


                String temp;
                if(list.get(i).get("2").get("address").contains("+88")){
                    temp = list.get(i).get("2").get("address").replace("+88", "");
                }else {
                   temp = list.get(i).get("2").get("address");
                }

                if(temp.contains(" ")){
                    temp = temp.replace(" ","");
                }
                if(temp.contains("-")){
                    temp.replace("-","");
                }

                String tempvalue = temp;

                ArrayList<HashMap<String, String>> tempmapholder = new ArrayList<>();
                for (int p = 0; p < list.get(i).size(); p++) {
                    tempmapholder.add(list.get(i).get(String.valueOf(p)));
                }
                if (tempvalue.equals(numberlist.get(position))) {
                    HashMap<String, ArrayList<HashMap<String, String>>> temo = new HashMap<>();
                    temo.put((numberlist.get(position)), tempmapholder);
                    lastmap.add(temo);
                }
            }
            lastmaplist.put(numberlist.get(position), lastmap);
        }
        map.put(numberlist, lastmaplist);
        var = map;

    }

    public static HashMap<ArrayList<String>, HashMap<String, ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>>>> getmessage() {
        return var;
    }
}
