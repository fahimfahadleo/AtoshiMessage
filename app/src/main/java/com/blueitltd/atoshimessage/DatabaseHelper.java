package com.blueitltd.atoshimessage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "IMessage.db";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table password " + "(id integer primary key, pass text,isfinger text,background text,textcolor text)"
        );

        db.execSQL(
                "create table background " + "(phone BIGINT primary key, back text, textcolor text, chathead text)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS password");
        db.execSQL("DROP TABLE IF EXISTS background");
        onCreate(db);
    }




    public boolean insertBackground(String phone,String background, String textcolor,String chatheadicon){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("phone",phone);
        contentValues.put("back",background);
        contentValues.put("textcolor",textcolor);
        contentValues.put("chathead",chatheadicon);
        db.insert("background", null, contentValues);
        return true;
    }
    public boolean updatebackgroundbg(String phone,String bg){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("back",bg);
        db.update("background",contentValues,"phone = ? ", new String[] {phone});
        return true;
    }

    public boolean updatetextbackgroundbg(String phone,String textcolor){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("textcolor",textcolor);

        db.update("background",contentValues,"phone = ? ", new String[] { phone });

        return true;
    }



    public boolean updatechatheadiconbackgroundbg(String chatheadicon,String phone){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("chathead",chatheadicon);

        db.update("background",contentValues,"phone = ? ", new String[] {phone});

        return true;
    }


    public Cursor getbackgroundData(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();

        //return db.query("background", new String[]{phone}, null, null, null, null, null);

        return db.rawQuery( "select * from background where phone="+phone+"", null );
    }



    public boolean insertContact (String pass,boolean isfinger) {

        Log.e("data","insarted");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("pass",pass);
        contentValues.put("isfinger",isfinger);
        contentValues.put("background","bg1");
        contentValues.put("textcolor","white");
        db.insert("password", null, contentValues);

        return true;
    }
    public boolean updatebg(String bg){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("background",bg);

        db.update("password",contentValues,"id = ? ", new String[] { Integer.toString(1) });

        return true;
    }

    public boolean updatetextbg(String textcolor){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("textcolor",textcolor);

        db.update("password",contentValues,"id = ? ", new String[] { Integer.toString(1) });

        return true;
    }


    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "select * from password where id="+id+"", null );
    }


    public boolean updateContact (Integer id, String pass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("pass",pass);
        db.update("password", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

}