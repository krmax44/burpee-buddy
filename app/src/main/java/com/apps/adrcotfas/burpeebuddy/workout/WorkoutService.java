package com.apps.adrcotfas.burpeebuddy.workout;

import android.content.Intent;

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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.TimeUnit;

import static com.apps.adrcotfas.burpeebuddy.common.bl.NotificationHelper.WORKOUT_NOTIFICATION_ID;
import static com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundType.COUNTDOWN;
import static com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundType.COUNTDOWN_LONG;
import static com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundType.REP_COMPLETE;
import static com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundType.REP_COMPLETE_SPECIAL;

public class WorkoutService extends LifecycleService {

    public static boolean isStarted = false;
    private static long PRE_WORKOUT_COUNTDOWN_SECONDS = TimeUnit.SECONDS.toMillis(5);
    private PreWorkoutCountdown preWorkoutCountdown;

    private void onStartWorkout() {
        getWorkoutManager().start();
        getRepCounter().register(getWorkoutManager());
        Timer.start();
    }

    private void onStop() {
        isStarted = false;
        preWorkoutCountdown.cancel();
        stopForeground(true);
        stopSelf();
        Timer.stop();
    }

    @Override
    public synchronized int onStartCommand(final Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        switch (intent.getAction()) {
            case Actions.STOP:
                onStop();
                break;
            case Actions.START:
                isStarted = true;
                preWorkoutCountdown = new PreWorkoutCountdown(PRE_WORKOUT_COUNTDOWN_SECONDS);
                preWorkoutCountdown.start();
                startForeground(WORKOUT_NOTIFICATION_ID, getNotificationHelper().getBuilder().build()); //todo: extract constant
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
        getNotificationHelper().setSubtext("Get ready"); //TODO: extract string
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
    public void onMessageEvent(Events.TimerTickEvent event) {
        final int reps = getWorkoutManager().getWorkout().reps.size();
        getNotificationHelper().setRepsAndElapsedTime(reps, event.seconds);
    }

    @Subscribe
    public void onMessageEvent(Events.RepCompletedEvent event) {
        // TODO: extract to preferences
        if (event.size % 20 == 0) {
            // TODO: give warning to users of S10 and other similar phones
            // proximity sensor does not work when the screen is on
            Power.turnOnScreen(this);
        } else if (event.size % 10 == 0){
            getMediaPlayer().play(REP_COMPLETE_SPECIAL);
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
}
