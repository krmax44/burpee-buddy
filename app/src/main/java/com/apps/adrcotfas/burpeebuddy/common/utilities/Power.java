package com.apps.adrcotfas.burpeebuddy.common.utilities;

import android.content.Context;
import android.os.PowerManager;

import static android.content.Context.POWER_SERVICE;

public class Power {

    public static void turnOnScreen(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                Power.class.getName());
        wakeLock.acquire(0);
        wakeLock.release();
    }

    public static boolean isScreenOn(Context context) {
        return ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).isScreenOn();
    }
}
