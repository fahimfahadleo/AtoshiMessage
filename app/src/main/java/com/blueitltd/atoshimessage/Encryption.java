package com.blueitltd.atoshimessage;

import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryption {

    public String encryption(String a, String b) {
        String AsImplodedString;
        String md5 = hashing(a, "MD5");
        String sha1 = hashing(a, "SHA-1");
        char[] mdarray = md5.toCharArray();
        char[] sha1array = sha1.toCharArray();
        char[] messagearray = b.toCharArray();
        int[] mdintarray = new int[md5.length()];
        int[] sha1intarray = new int[sha1.length()];
        int[] messageintarray = new int[b.length()];
        for (int i = 0; i < mdarray.length; i++) {
            mdintarray[i] = mdarray[i];
        }
        for (int i2 = 0; i2 < sha1array.length; i2++) {
            sha1intarray[i2] = sha1array[i2];
        }
        for (int i3 = 0; i3 < b.length(); i3++) {
            messageintarray[i3] = messagearray[i3];
        }
        int[] messageconvertarray = new int[b.length()];
        int j = 0;
        int k = 0;
        for (int i4 = 0; i4 < b.length(); i4++) {
            if (j == md5.length()) {
                j = 0;
            }
            if (k == sha1.length()) {
                k = 0;
            }
            messageconvertarray[i4] = (char) (messageintarray[i4] + mdintarray[j] + sha1intarray[k]);
            j++;
            k++;
        }
        if (messageconvertarray.length == 0) {
            AsImplodedString = "";
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append(messageconvertarray[0]);
            for (int i5 = 1; i5 < messageconvertarray.length; i5++) {
                sb.append(" ");
                sb.append(messageconvertarray[i5]);
            }
            AsImplodedString = sb.toString();
        }

        Log.e("rawstr",AsImplodedString);






        return AsImplodedString;
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
}
