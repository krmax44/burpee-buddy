package com.apps.adrcotfas.burpeebuddy.workout;

import android.content.Intent;
import android.os.Handler;

import androidx.lifecycle.LifecycleService;

import com.apps.adrcotfas.burpeebuddy.common.bl.BuddyApplication;
import com.apps.adrcotfas.burpeebuddy.common.bl.MediaPlayer;
import com.apps.adrcotfas.burpeebuddy.common.bl.PreWorkoutCountdown;
import com.apps.adrcotfas.burpeebuddy.common.bl.Events;
import com.apps.adrcotfas.burpeebuddy.common.bl.NotificationHelper;
import com.apps.adrcotfas.burpeebuddy.common.bl.RepCounter;
import com.apps.adrcotfas.burpeebuddy.common.bl.WorkoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.TimeUnit;

public class WorkoutService extends LifecycleService {

    public static boolean isStarted = false;
    private static long PRE_WORKOUT_COUNTDOWN_SECONDS = TimeUnit.SECONDS.toMillis(5);

    private void onStartWorkout() {
        isStarted = true;
        getWorkoutManager().start();
        getRepCounter().register(getWorkoutManager());
    }

    private void onStop() {
        isStarted = false;
        stopForeground(true);
        stopSelf();
    }

    @Override
    public synchronized int onStartCommand(final Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        switch (intent.getAction()) {
            case "STOP":
                onStop();
                break;
            case "START":
                PreWorkoutCountdown timer = new PreWorkoutCountdown(PRE_WORKOUT_COUNTDOWN_SECONDS);
                timer.start();
                startForeground(42, getNotificationHelper().getInProgressBuilder().build()); //todo: extract constant
                break;
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        onStop();
        getRepCounter().unregister();
        getWorkoutManager().resetWorkout();
        super.onDestroy();
    }

    @Subscribe
    public void onMessageEvent(Events.PreWorkoutCountdownTickEvent event) {
        getMediaPlayer().play();
    }

    @Subscribe
    public void onMessageEvent(Events.PreWorkoutCountdownFinished event) {
        onStartWorkout();
    }

    @Subscribe
    public void onMessageEvent(Events.RepCompletedEvent event) {
        getNotificationHelper().updateNotificationProgress(
                String.valueOf(event.size));
        //new Handler().postDelayed(() -> getMediaPlayer().play(), 1000);
        getMediaPlayer().play();
    }














    private NotificationHelper getNotificationHelper() {
        return BuddyApplication.getNotificationHelper();
    }

    private WorkoutManager getWorkoutManager() {
        return BuddyApplication.getWorkoutManager();
    }

    private RepCounter getRepCounter() {
        return BuddyApplication.getRepCounter();
    }

    private MediaPlayer getMediaPlayer() {
        return BuddyApplication.getMediaPlayer();
    }
}
