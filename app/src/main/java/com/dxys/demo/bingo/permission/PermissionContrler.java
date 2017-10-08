package com.dxys.demo.bingo.permission;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.view.View;

/**
 * Created by dxys on 17/10/6.
 */


public class PermissionContrler {

    public static final String PERMISSION[] = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static boolean isHasMermission(Activity activity, int requestCode) {
        if (ActivityCompat.checkSelfPermission(activity, PERMISSION[requestCode]) == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }

    public static void requestPermission(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{PERMISSION[requestCode]}, requestCode);
    }

    public static boolean onRequestResualt(int requestCode, int[] granResualt) {
        return granResualt[0] == PackageManager.PERMISSION_GRANTED;
    }
}

