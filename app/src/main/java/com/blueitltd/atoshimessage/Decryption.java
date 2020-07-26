package com.blueitltd.atoshimessage;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
            return "";
        }
    }

    /* access modifiers changed from: private */
    public String decryption(String a, String b) {



            String md5 = hashing(a, "MD5");
            String sha1 = hashing(a, "SHA-1");
            char[] mdarray = md5.toCharArray();
            char[] sha1array = sha1.toCharArray();
            int[] mdintarray = new int[md5.length()];
            int[] sha1intarray = new int[sha1.length()];
            byte[] decodeValue = null;
            try {
                decodeValue = Base64.decode(b, 0);
            } catch (IllegalArgumentException e) {
              //  Toast.makeText(context, "Invalid Input", Toast.LENGTH_SHORT).show();
            }
            String[] hati = new String(decodeValue).split(" ");
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
                    hati[i3] = Character.toString((char) ((Integer.parseInt(hati[i3]) - mdintarray[j]) - sha1intarray[k]));
                    j++;
                    k++;
                }catch (NumberFormatException e){
                    // Toast.makeText(context,"Invalid Input",Toast.LENGTH_SHORT).show();
                     return "";
                }

            }
            if (hati.length == 0) {
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
