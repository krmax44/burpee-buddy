package com.apps.adrcotfas.burpeebuddy.common.bl;

import android.app.Application;

public class BuddyApplication extends Application {

    private static BuddyApplication INSTANCE;
    private static WorkoutManager mWorkoutManager;
    private static RepCounter mRepCounter;
    private static NotificationHelper mNotificationHelper;
    private static MediaPlayer mMediaPlayer;

    public static NotificationHelper getNotificationHelper() {
        return mNotificationHelper;
    }

    public static RepCounter getRepCounter() {
        return mRepCounter;
    }

    public static WorkoutManager getWorkoutManager() {
        return mWorkoutManager;
    }

    public static MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public static BuddyApplication getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        mWorkoutManager = new WorkoutManager();
        mRepCounter = new RepCounter(this);
        mNotificationHelper = new NotificationHelper(this);
        mMediaPlayer = new MediaPlayer(this);
    }
}
