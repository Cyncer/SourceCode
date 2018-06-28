package com.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Created by zlinux on 30/3/16.
 */
public class PermissionUtils {

    public static boolean verifyAllPermissions(int[] permissions) {
        for (int result : permissions) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean hasSelfPermission(Activity activity, String[] permissions) {
        if (!isMNCBuildVersion()) {
            return true;
        }

        // Verify that all the permissions.
        for (String permission : permissions) {
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    public static boolean isMNCBuildVersion() {

        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;

    }


}
