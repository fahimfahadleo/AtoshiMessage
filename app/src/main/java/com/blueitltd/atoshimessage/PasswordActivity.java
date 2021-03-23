package com.blueitltd.atoshimessage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import pl.droidsonroids.gif.GifImageView;

public class PasswordActivity extends AppCompatActivity {

    GifImageView imageView;
    TextInputEditText passwordfield;
    MaterialButton proceed;
    LinearLayout passlayout;

    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;

    private KeyStore keyStore;
    private Cipher cipher;
    private String KEY_NAME = "AndroidKey";
    DatabaseHelper helper;
    ImageView iv;
    TextView passwordlogin;


    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) hexString.append(Integer.toHexString(0xFF & b));
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


//    public void splitEqually(String text, int size) {
//        // Give the list the right capacity to start with. You could use an array
//        // instead if you wanted.
//        ArrayList<String> ret = new ArrayList<>();
//
//
//        for (int start = 0; start < text.length(); start += size) {
//            ret.add(text.substring(start, Math.min(text.length(), start + size)));
//        }
//        list = ret;
//    }

    ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        imageView = findViewById(R.id.gifview);
        passlayout = findViewById(R.id.passwordfieldlayout);
        passwordfield = findViewById(R.id.passwordfield);
        proceed = findViewById(R.id.proceedbutton);
        helper = new DatabaseHelper(this);
        passwordlogin = findViewById(R.id.passwordregister);
        passwordlogin.setVisibility(View.INVISIBLE);
        list = new ArrayList<>();



//        Log.e("value",String.valueOf(Character.toChars(Integer.parseInt("অ", 16))));

        char characterToCheck = 'Í';

        int unicode = (int) characterToCheck;

        Log.e("value",String.valueOf(unicode));
  



        passwordlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passwordlogin.getText().equals("Use Finger Print")){
                    passlayout.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    passwordlogin.setText("Use Password");
                }else {
                    passlayout.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.INVISIBLE);
                    passwordlogin.setText("Use Finger Print");
                }
            }
        });




        new CallSms(this);




        iv = findViewById(R.id.imageview);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PasswordActivity.this, "clicked", Toast.LENGTH_SHORT).show();
            }
        });


        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String passwordstr = passwordfield.getText().toString();
                if(TextUtils.isEmpty(passwordstr)){
                    passwordfield.setError("Password Can Not Be Empty!");
                    passwordfield.requestFocus();
                }else {
                    Cursor cursor =helper.getData(1);
                    if (cursor.moveToFirst()){
                        do{
                            String data = cursor.getString(cursor.getColumnIndex("pass"));

                            if(data.equals(md5(passwordstr))){
                                Variables.isActive = true;
                                startActivity(new Intent(PasswordActivity.this,MainActivity.class));
                                finish();
                                Variables.isDown = false;
                            }
                            // do what ever you want here
                        }while(cursor.moveToNext());
                    }
                    cursor.close();
                }
            }
        });

        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        if(fingerprintManager!=null){
            if(!fingerprintManager.isHardwareDetected()){
                imageView.setVisibility(View.INVISIBLE);
                passlayout.setVisibility(View.VISIBLE);
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.USE_FINGERPRINT},0);
            } else if (!keyguardManager.isKeyguardSecure()){
                imageView.setVisibility(View.INVISIBLE);
                passlayout.setVisibility(View.INVISIBLE);
            } else if (!fingerprintManager.hasEnrolledFingerprints()){
                imageView.setVisibility(View.INVISIBLE);
                passlayout.setVisibility(View.VISIBLE);
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED){
                imageView.setVisibility(View.VISIBLE);
                passlayout.setVisibility(View.INVISIBLE);
                generateKey();
                if (cipherInit()){
                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    FingerPrintHandler fingerprintHandler = new FingerPrintHandler(this);
                    fingerprintHandler.startAuth(fingerprintManager, cryptoObject);
                }
            }else {
                imageView.setVisibility(View.VISIBLE);
                passlayout.setVisibility(View.INVISIBLE);
                generateKey();
                if (cipherInit()){
                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    FingerPrintHandler fingerprintHandler = new FingerPrintHandler(this);
                    fingerprintHandler.startAuth(fingerprintManager, cryptoObject);
                }
            }
        }else {
            passlayout.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);
        }


        Cursor c =helper.getData(1);
        if (c.moveToFirst()){
            do{
                String data = c.getString(c.getColumnIndex("isfinger"));

                if(data.equals("0")){
                    Log.e("called","called");
                    passwordlogin.setVisibility(View.INVISIBLE);
                }else {
                    if(fingerprintManager!=null){
                        passwordlogin.setVisibility(View.VISIBLE);
                        passwordlogin.setText("Use Password");
                    }

                }
                // do what ever you want here
            }while(c.moveToNext());
        }
        c.close();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                passlayout.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.INVISIBLE);
            }
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    private void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (KeyStoreException | IOException | CertificateException
                | NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            Log.e("error1",e.getMessage());
            throw new RuntimeException("Failed to get Cipher", e);
        }
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            Log.e("error2",e.getMessage());
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            Log.e("error3",e.getMessage());
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }
}