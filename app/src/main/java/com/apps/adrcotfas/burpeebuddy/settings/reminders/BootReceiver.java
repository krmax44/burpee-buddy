package com.apps.adrcotfas.burpeebuddy.settings.reminders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.apps.adrcotfas.burpeebuddy.common.BuddyApplication;

import timber.log.Timber;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null || intent == null) return;
        if (intent.getAction() == null) return;

        try {
            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                Timber.tag(TAG).d("onBootComplete");
                BuddyApplication.getInstance().getReminderHelper().scheduleNotification();
            }
        }
        catch (RuntimeException e) {
            Timber.tag(TAG).wtf("Could not process intent");
        }
    }
}
