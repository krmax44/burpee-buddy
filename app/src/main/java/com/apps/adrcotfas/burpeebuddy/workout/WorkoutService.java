package com.apps.adrcotfas.burpeebuddy.workout;

import android.content.Intent;
import android.os.PowerManager;

import androidx.lifecycle.LifecycleService;

import com.apps.adrcotfas.burpeebuddy.common.bl.BuddyApplication;
import com.apps.adrcotfas.burpeebuddy.common.bl.PreWorkoutCountdown;
import com.apps.adrcotfas.burpeebuddy.common.bl.Events;
import com.apps.adrcotfas.burpeebuddy.common.bl.NotificationHelper;
import com.apps.adrcotfas.burpeebuddy.common.bl.RepCounter;
import com.apps.adrcotfas.burpeebuddy.common.bl.WorkoutManager;
import com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundPlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.TimeUnit;

import static com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundType.COUNTDOWN;
import static com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundType.COUNTDOWN_LONG;
import static com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundType.REP_COMPLETE;
import static com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundType.REP_COMPLETE_SPECIAL;

public class WorkoutService extends LifecycleService {

    public static boolean isStarted = false;
    private static long PRE_WORKOUT_COUNTDOWN_SECONDS = TimeUnit.SECONDS.toMillis(5);

    private void onStartWorkout() {
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
                isStarted = true;
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
        if (event.seconds == 0) {
            getMediaPlayer().play(COUNTDOWN_LONG);
        } else if (event.seconds <= 3) {
            getMediaPlayer().play(COUNTDOWN);
        }
    }

    @Subscribe
    public void onMessageEvent(Events.PreWorkoutCountdownFinished event) {
        onStartWorkout();
    }

    @Subscribe
    public void onMessageEvent(Events.RepCompletedEvent event) {
        getNotificationHelper().updateNotificationProgress(
                String.valueOf(event.size));
        // TODO: extract to preferences
        if (event.size % 5 == 0) {
            getMediaPlayer().play(REP_COMPLETE_SPECIAL);
            turnOnScreen();
        } else {
            getMediaPlayer().play(REP_COMPLETE);
        }
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

    private SoundPlayer getMediaPlayer() {
        return BuddyApplication.getMediaPlayer();
    }

    private void turnOnScreen() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                WorkoutService.class.getName());
        wakeLock.acquire(0);
    }
}
