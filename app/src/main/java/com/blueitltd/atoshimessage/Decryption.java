package com.blueitltd.atoshimessage;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Decryption {
    Context context;

    public Decryption(Context context) {
        this.context = context;
    }

    public String hashing(String s, String mat) {
        try {
            MessageDigest digest = MessageDigest.getInstance(mat);
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();
            StringBuffer hexString = new StringBuffer();
            for (byte b : messageDigest) {
                hexString.append(Integer.toHexString(b & 255));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.e("dhukche","ekhane1");
            return "";
        }
    }

    /* access modifiers changed from: private */
    public String decryption(String a, String b) {

        Log.e("body",b);

            String md5 = hashing(a, "MD5");
            String sha1 = hashing(a, "SHA-1");
            char[] mdarray = md5.toCharArray();
            char[] sha1array = sha1.toCharArray();
            int[] mdintarray = new int[md5.length()];
            int[] sha1intarray = new int[sha1.length()];
            StringBuilder something  = new StringBuilder();
            char [] mainstr = b.toCharArray();
            for(int i = 0;i<mainstr.length;i++){
                something.append(mainstr[i]).append(" ");
            }

            String[] hati = something.toString().split(" ");


            for (int i = 0; i < mdarray.length; i++) {
                mdintarray[i] = mdarray[i];
            }
            for (int i2 = 0; i2 < sha1array.length; i2++) {
                sha1intarray[i2] = sha1array[i2];
            }
            int j = 0;
            int k = 0;
            for (int i3 = 0; i3 < hati.length; i3++) {
                if (j == md5.length()) {
                    j = 0;
                }
                if (k == sha1.length()) {
                    k = 0;
                }
                try{
                    hati[i3] = Character.toString((char) (((int)(hati[i3].charAt(0)) - mdintarray[j]) - sha1intarray[k]));
                    j++;
                    k++;
                }catch (NumberFormatException e){
                    // Toast.makeText(context,"Invalid Input",Toast.LENGTH_SHORT).show();
                    Log.e("dhukche","ekhane2");
                     return "";
                }

            }
            if (hati.length == 0) {
                Log.e("dhukche","ekhane3");
                return "";
            }
            StringBuffer sb = new StringBuffer();
            sb.append(hati[0]);
            for (int i4 = 1; i4 < hati.length; i4++) {
                sb.append("");
                sb.append(hati[i4]);
            }
            return sb.toString();


    }
}
