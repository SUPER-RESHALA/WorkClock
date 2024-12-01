package com.simurg.workclock.permissions;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class PermissionsManager {
//    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
//    public static final int REQUEST_READ_EXTERNAL_STORAGE=2;
//    public static final int REQUEST_CHANGE_WIFI_STATE=3;
//    public static final int REQUEST_ACCESS_WIFI_STATE=4;
//    public static final int REQUEST_ACCESS_NETWORK_STATE=5;
//    public static final int REQUEST_INTERNET=6;
//    public static final int REQUEST_CODE_PERNISSIONS=7;
    private static final String[] allPermissions= new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET
    };
    public static boolean isAllPermissionsGranted(Context context){
        for (String permission: allPermissions){
            if (ActivityCompat.checkSelfPermission(context,permission)!=PackageManager.PERMISSION_GRANTED){
                Log.e("PermissionManager","Разрешения на "+ permission + " не получено");
                return false;
            }
        }
        return true;
    }

}

