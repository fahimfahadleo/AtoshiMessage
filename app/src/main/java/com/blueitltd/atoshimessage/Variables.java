package com.blueitltd.atoshimessage;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

public class Variables {
    public static boolean isActive = false;
    public static boolean isDown = false;
    public static String backgroundcolor = "bg1";
    public static String textbackgroundcolor = "white";

    public static String LogTag = "henrytest";
    public static String EXTRA_MSG = "extra_msg";


    public static boolean canDrawOverlays(Context context) {
        return Settings.canDrawOverlays(context);
    }


}
