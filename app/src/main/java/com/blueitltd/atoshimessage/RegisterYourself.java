package com.blueitltd.atoshimessage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class RegisterYourself extends AppCompatActivity {


    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    LinearLayout fingerprintview, passwordview;
    TextInputEditText password, confirmpassword;
    MaterialButton proceed;
    DatabaseHelper databaseHelper;
    static TextView errorview;

    private KeyStore keyStore;
    private Cipher cipher;
    private String KEY_NAME = "AndroidKey";
    TextView passwordregister;
    boolean isFinger = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_yourself);
        fingerprintview = findViewById(R.id.fingerprintview);
        passwordview = findViewById(R.id.passwordview);
        password = findViewById(R.id.password);
        confirmpassword = findViewById(R.id.confirmpassword);
        proceed = findViewById(R.id.proceedbutton);
        errorview = findViewById(R.id.errortext);
        errorview.setVisibility(View.INVISIBLE);
        passwordregister = findViewById(R.id.passwordregister);
        passwordregister.setVisibility(View.INVISIBLE);
        databaseHelper = new DatabaseHelper(this);
        new CallSms(this);


        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String passwordstr = password.getText().toString();
                String confirmpasswordstr = confirmpassword.getText().toString();

                if (TextUtils.isEmpty(passwordstr)) {
                    password.setError("Field Can Not Be Empty!");
                    password.requestFocus();
                } else if (TextUtils.isEmpty(confirmpasswordstr)) {
                    confirmpassword.setError("Field Can Not Be Empty!");
                    confirmpassword.requestFocus();
                } else if (!passwordstr.equals(confirmpasswordstr)) {
                    confirmpassword.setError("Password Did Not Match!");
                    confirmpassword.requestFocus();
                } else {

                    if (databaseHelper.insertContact(md5(passwordstr), false)) {
                        Toast.makeText(RegisterYourself.this, "Data Saved Successfully!", Toast.LENGTH_SHORT).show();
                    }
                    Variables.isActive = true;
                    startActivity(new Intent(RegisterYourself.this, MainActivity.class));
                    finish();
                    Variables.isDown = false;
                }

            }
        });
        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (fingerprintManager == null) {
            isFinger = false;
        }


        if (isFinger) {
            if (!fingerprintManager.isHardwareDetected()) {
                fingerprintview.setVisibility(View.INVISIBLE);
                passwordview.setVisibility(View.VISIBLE);
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.USE_FINGERPRINT}, 0);
            } else if (!keyguardManager.isKeyguardSecure()) {
                errorview.setVisibility(View.VISIBLE);
                errorview.setText("You Must Use Pattern or Password Or Fingerprint to Lock your Phone in order to use this Application. ");
                fingerprintview.setVisibility(View.INVISIBLE);
                passwordview.setVisibility(View.INVISIBLE);
            } else if (fingerprintManager.isHardwareDetected() && !fingerprintManager.hasEnrolledFingerprints()) {
                errorview.setVisibility(View.VISIBLE);
                errorview.setText("Add Ablest one Fingerprint to use Fingerprint Unlocker or Use Password Instead.");
                fingerprintview.setVisibility(View.INVISIBLE);
                passwordview.setVisibility(View.VISIBLE);
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED) {
                fingerprintview.setVisibility(View.VISIBLE);
                passwordview.setVisibility(View.INVISIBLE);

                passwordregister.setVisibility(View.VISIBLE);

                generateKey();
                if (cipherInit()) {
                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    FingerPrintHandler fingerprintHandler = new FingerPrintHandler(this);
                    fingerprintHandler.startAuth(fingerprintManager, cryptoObject);
                }
            } else {
                fingerprintview.setVisibility(View.VISIBLE);
                passwordview.setVisibility(View.INVISIBLE);
                passwordregister.setVisibility(View.VISIBLE);


                generateKey();
                if (cipherInit()) {
                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    FingerPrintHandler fingerprintHandler = new FingerPrintHandler(this);
                    fingerprintHandler.startAuth(fingerprintManager, cryptoObject);
                }
            }
        } else {
            fingerprintview.setVisibility(View.INVISIBLE);
            passwordview.setVisibility(View.VISIBLE);
            passwordregister.setVisibility(View.INVISIBLE);
        }

        passwordregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (passwordregister.getText().equals("Use Finger Print")) {
                    isFinger = true;
                    fingerprintview.setVisibility(View.VISIBLE);
                    passwordregister.setText("Use Password");
                    passwordview.setVisibility(View.INVISIBLE);
                } else {
                    isFinger = false;
                    fingerprintview.setVisibility(View.INVISIBLE);
                    passwordregister.setText("Use Finger Print");
                    passwordview.setVisibility(View.VISIBLE);
                }
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                passwordview.setVisibility(View.VISIBLE);
            } else {

                Toast.makeText(this, "Permission Denied To Use Fingerprint!", Toast.LENGTH_SHORT).show();
                fingerprintview.setVisibility(View.INVISIBLE);
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
            Log.e("error1", e.getMessage());
            throw new RuntimeException("Failed to get Cipher", e);
        }
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            Log.e("error2", e.getMessage());
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            Log.e("error3", e.getMessage());
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }


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

}