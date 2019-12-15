package com.apps.adrcotfas.burpeebuddy.workout;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LifecycleService;

import com.apps.adrcotfas.burpeebuddy.common.bl.BuddyApplication;
import com.apps.adrcotfas.burpeebuddy.common.timers.PreWorkoutCountdown;
import com.apps.adrcotfas.burpeebuddy.common.bl.Events;
import com.apps.adrcotfas.burpeebuddy.common.bl.NotificationHelper;
import com.apps.adrcotfas.burpeebuddy.common.bl.RepCounter;
import com.apps.adrcotfas.burpeebuddy.common.bl.WorkoutManager;
import com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundPlayer;
import com.apps.adrcotfas.burpeebuddy.common.timers.Timer;
import com.apps.adrcotfas.burpeebuddy.common.utilities.Power;
import com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.TimeUnit;

import static com.apps.adrcotfas.burpeebuddy.common.bl.NotificationHelper.WORKOUT_NOTIFICATION_ID;
import static com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundType.COUNTDOWN;
import static com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundType.COUNTDOWN_LONG;
import static com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundType.REP_COMPLETE;
import static com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundType.REP_COMPLETE_SPECIAL;

public class WorkoutService extends LifecycleService {
    private static final String TAG = "WorkoutService";
    public static boolean isStarted = false;
    private static int PRE_WORKOUT_COUNTDOWN_SECONDS = (int) TimeUnit.SECONDS.toMillis(5);
    private PreWorkoutCountdown preWorkoutCountdown;

    private void onStartWorkout() {
        Log.d(TAG, "onStartWorkout");
        getNotificationHelper().setReps(0);
        getWorkoutManager().start();
        getRepCounter().register(getWorkoutManager());
        Timer.start();
    }

    private void onStop() {
        Log.d(TAG, "onStop");
        isStarted = false;
        preWorkoutCountdown.cancel();
        stopForeground(true);
        stopSelf();
        Timer.stop();
        getMediaPlayer().play(COUNTDOWN_LONG);
        // workaround to make sure that the sound played above is audible
        new Handler().postDelayed(() -> getMediaPlayer().stop(), 1000);
        EventBus.getDefault().post(new Events.FinishedWorkoutEvent());
    }

    @Override
    public synchronized int onStartCommand(final Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        switch (intent.getAction()) {
            case Actions.STOP:
                if (isStarted) {
                    onStop();
                }
                break;
            case Actions.START:
                if (!isStarted) {
                    isStarted = true;
                    getMediaPlayer().init();
                    preWorkoutCountdown = new PreWorkoutCountdown(PRE_WORKOUT_COUNTDOWN_SECONDS);
                    preWorkoutCountdown.start();
                    startForeground(WORKOUT_NOTIFICATION_ID, getNotificationHelper().getBuilder().build());
                    getNotificationHelper().setTitle("Get ready"); //TODO: extract string
                }
                break;
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (isStarted) {
            onStop();
        }
        getRepCounter().unregister();
        getWorkoutManager().resetWorkout();
        super.onDestroy();
    }

    @Subscribe
    public void onMessageEvent(Events.PreWorkoutCountdownTickEvent event) {
        Log.d(TAG, "PreWorkoutCountdownTickEvent: " + event.seconds);
        getNotificationHelper().setElapsedTime(event.seconds);
        if (event.seconds == 0) {
            getMediaPlayer().play(COUNTDOWN_LONG);
            onStartWorkout();
        } else if (event.seconds <= 3) {
            getMediaPlayer().play(COUNTDOWN);
        }
    }

    @Subscribe
    public void onMessageEvent(Events.PreWorkoutCountdownFinished event) {
        Log.d(TAG, "PreWorkoutCountdownFinished");
    }

    @Subscribe
    public void onMessageEvent(Events.TimerTickEvent event) {
        Log.d(TAG, "TimerTickEvent: " + event.seconds + " seconds");
        getNotificationHelper().setElapsedTime(event.seconds);
    }

    @Subscribe
    public void onMessageEvent(Events.RepCompletedEvent event) {
        Log.d(TAG, "RepCompletedEvent: " + event.size + " reps");

        if (SettingsHelper.wakeupEnabled()
                && (event.size % SettingsHelper.getWakeUpInterval() == 0)) {
            Power.turnOnScreen(this);
        }
        if (SettingsHelper.specialSoundEnabled()
                && (event.size % SettingsHelper.getSpecialSoundInterval() == 0)){
            getMediaPlayer().play(REP_COMPLETE_SPECIAL);
        } else if (SettingsHelper.soundEnabled()){
            getMediaPlayer().play(REP_COMPLETE);
        }
        getNotificationHelper().setReps(event.size);
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
}
