package com.apps.adrcotfas.burpeebuddy.common.utilities;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import androidx.appcompat.app.AppCompatActivity;

import static android.content.Context.POWER_SERVICE;

public class Power {

    private  static final int REQUEST_ENABLE = 0;

    public static void turnOnScreen(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                Power.class.getName());
        wakeLock.acquire(0);
    }

    public static void lockScreen(AppCompatActivity activity) {
        ComponentName adminComponent = new ComponentName(
                activity,
                android.app.admin.DeviceAdminReceiver.class);
        DevicePolicyManager devicePolicyManager =
                (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);

        if (!devicePolicyManager.isAdminActive(adminComponent)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
            activity.startActivityForResult(intent, REQUEST_ENABLE);
        } else {
            devicePolicyManager.lockNow();
        }
    }

    public static boolean isScreenOn(Context context) {
        return ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).isScreenOn();
    }
}
