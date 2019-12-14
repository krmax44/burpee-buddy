package com.apps.adrcotfas.burpeebuddy.common.bl;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.navigation.NavDeepLinkBuilder;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundPlayer;

public class BuddyApplication extends Application {

    private static BuddyApplication INSTANCE;
    private static WorkoutManager mWorkoutManager;
    private static RepCounter mRepCounter;
    private static NotificationHelper mNotificationHelper;
    private static SoundPlayer mMediaPlayer;

    private static SharedPreferences mPrivatePreferences;

    public static NotificationHelper getNotificationHelper() {
        return mNotificationHelper;
    }

    public static RepCounter getRepCounter() {
        return mRepCounter;
    }

    public static WorkoutManager getWorkoutManager() {
        return mWorkoutManager;
    }

    public static SoundPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public static BuddyApplication getInstance() {
        return INSTANCE;
    }

    public static SharedPreferences getPrivatePreferences() {
        return mPrivatePreferences;
    }

    public static PendingIntent getNavigationIntent(Context context, int destId) {
        return new NavDeepLinkBuilder(context)
                .setGraph(R.navigation.navigation_graph)
                .setDestination(destId)
                .createPendingIntent();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        mWorkoutManager = new WorkoutManager();
        mRepCounter = new RepCounter(this);
        mNotificationHelper = new NotificationHelper(this);
        mMediaPlayer = new SoundPlayer(this);

        mPrivatePreferences =
                getSharedPreferences(getPackageName() + "_private_preferences", MODE_PRIVATE);
    }
}
