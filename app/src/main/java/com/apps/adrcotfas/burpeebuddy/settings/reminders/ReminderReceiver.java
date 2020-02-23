package com.apps.adrcotfas.burpeebuddy.settings.reminders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import timber.log.Timber;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String TAG = "ReminderReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.tag(TAG).d("onReceive");
        ReminderHelper.notifyReminder(context);
    }
}
