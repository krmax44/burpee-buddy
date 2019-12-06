package com.apps.adrcotfas.burpeebuddy.common.bl;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.workout.WorkoutActivity;

public class NotificationHelper extends ContextWrapper {

    private static final String BUDDY_NOTIFICATION = "buddy.notification";

    private final NotificationManager mManager;
    private final NotificationCompat.Builder mBuilder;

    public NotificationHelper(Context context) {
        super(context);
        mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            initChannels();
        }
        mBuilder = new NotificationCompat.Builder(this, BUDDY_NOTIFICATION)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(createActivityIntent())
                .setOngoing(true)
                .setShowWhen(false);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void initChannels() {
        NotificationChannel channelInProgress = new NotificationChannel(
                BUDDY_NOTIFICATION, "notifications",
                NotificationManager.IMPORTANCE_LOW);
        channelInProgress.setBypassDnd(true);
        channelInProgress.setShowBadge(true);
        channelInProgress.setSound(null, null);
        mManager.createNotificationChannel(channelInProgress);
    }

    private PendingIntent createActivityIntent() {
        Intent openMainIntent = new Intent(this, WorkoutActivity.class);
        return PendingIntent.getActivity(this, 0, openMainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public NotificationCompat.Builder getInProgressBuilder() {
        mBuilder.setContentTitle("DummyTitle")
                .setContentText("DummyContent");

        return mBuilder;
    }

    public void updateNotificationProgress(String value) {
        mManager.notify(42,
                getInProgressBuilder()
                        .setOnlyAlertOnce(true)
                        .setContentText(value)
                        .build());
    }

    public void clearNotification() {
        mManager.cancelAll();
    }
}
