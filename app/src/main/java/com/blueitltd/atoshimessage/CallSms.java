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
     Context context;

     HashMap<String,HashMap<String, String>> tempmap;
     HashMap<Integer,HashMap<String,HashMap<String,String>>>list;
     int ikasdjflk = 0;
      ArrayList<String> numberlist;
     HashMap<String,ArrayList<HashMap<String,ArrayList<HashMap<String,String>>>>> lastmaplist;
     HashMap<ArrayList<String>,HashMap<String,ArrayList<HashMap<String,ArrayList<HashMap<String,String>>>>>> map;

    public CallSms(Context context){
        this.context = context;
    }

    public HashMap<ArrayList<String>,HashMap<String,ArrayList<HashMap<String,ArrayList<HashMap<String,String>>>>>> callsms(){
        tempmap = new HashMap<>();
        numberlist = new ArrayList<>();
        lastmaplist = new HashMap<>();
        map = new HashMap<>();

        list = new HashMap<>();
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms"), null, null, null, null);

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            Log.e("check", "checking");
            do {
                tempmap = new HashMap<>();
                String msgData = "";
                for (int idx = 0; idx < cursor.getColumnCount(); idx++) {

                    HashMap<String,String> tmp =  new HashMap<>();
                    tmp.put(cursor.getColumnName(idx), cursor.getString(idx) == null ? "" : cursor.getString(idx));
                    tempmap.put(String.valueOf(idx),tmp);
                    msgData = msgData + " " + cursor.getColumnName(idx) + ":" + (cursor.getString(idx) == null ? "" : cursor.getString(idx));
                }
                list.put(ikasdjflk,tempmap);
                ikasdjflk++;
            } while (cursor.moveToNext());
        } else {
            Toast.makeText((context), "Your Inbox is Empty!", Toast.LENGTH_LONG).show();
        }
        cursor.close();
        Log.e("int_value",ikasdjflk+"");
        for(int i = 0;i<list.size();i++){
            Log.e("tag "+i,list.get(i).toString());
            String address = list.get(i).get("2").get("address");
            if(address.contains("+88")){
                address = address.replace("+88","");
            }
            if(!numberlist.contains(address)){
                numberlist.add(address);
            }
        }
        for(int position = 0;position<numberlist.size();position++){
            ArrayList<HashMap<String,ArrayList<HashMap<String,String>>>> lastmap = new ArrayList();
            for(int i = 0;i<list.size();i++){
                String tempvalue = list.get(i).get("2").get("address").contains("+88")? list.get(i).get("2").get("address").replace("+88",""): list.get(i).get("2").get("address");
                ArrayList<HashMap<String,String>> tempmapholder = new ArrayList<>();
                for(int p = 0;p<list.get(i).size();p++){
                    tempmapholder.add(list.get(i).get(String.valueOf(p)));
                }

                if(tempvalue.equals(numberlist.get(position))){
                    HashMap<String, ArrayList<HashMap<String, String>>> temo = new HashMap<>();
                    temo.put((numberlist.get(position)),tempmapholder);
                    lastmap.add(temo);
                }
            }
            lastmaplist.put(numberlist.get(position),lastmap);
        }

        map.put(numberlist,lastmaplist);
        return map;
    }

}
