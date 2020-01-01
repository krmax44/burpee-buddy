package com.apps.adrcotfas.burpeebuddy.workout;

import android.content.Intent;
import android.os.Handler;

import androidx.lifecycle.LifecycleService;

import com.apps.adrcotfas.burpeebuddy.common.BuddyApplication;
import com.apps.adrcotfas.burpeebuddy.common.Events;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.workout.manager.NotificationHelper;
import com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundPlayer;
import com.apps.adrcotfas.burpeebuddy.common.timers.TimerType;
import com.apps.adrcotfas.burpeebuddy.common.utilities.Power;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;
import com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper;
import com.apps.adrcotfas.burpeebuddy.workout.manager.State;
import com.apps.adrcotfas.burpeebuddy.workout.manager.WorkoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

import static com.apps.adrcotfas.burpeebuddy.workout.manager.NotificationHelper.WORKOUT_NOTIFICATION_ID;
import static com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundType.COUNTDOWN;
import static com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundType.COUNTDOWN_LONG;
import static com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundType.REP_COMPLETE;
import static com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundType.REP_COMPLETE_SPECIAL;
import static com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundType.REST;
import static com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundType.WORKOUT_COMPLETE;

public class WorkoutService extends LifecycleService {
    private static final String TAG = "WorkoutService";
    private static int PRE_WORKOUT_COUNTDOWN_SECONDS = (int) TimeUnit.SECONDS.toMillis(5);

    private void onStartWorkout() {
        Timber.tag(TAG).d( "onStartWorkout");
        getNotificationHelper().setReps(0);
        getWorkoutManager().start();
    }

    private void stopInternal() {
        // workaround to make sure that the sound played above is audible and the player released
        new Handler().postDelayed(() -> getMediaPlayer().play(WORKOUT_COMPLETE, true), 1000);

        getWorkoutManager().stop();
        stopForeground(true);
        stopSelf();
    }

    @Override
    public synchronized int onStartCommand(final Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        EventBus.getDefault().register(this);

        final Exercise exercise = WorkoutFragmentArgs.fromBundle(intent.getExtras()).getExercise();
        final Goal goal = WorkoutFragmentArgs.fromBundle(intent.getExtras()).getGoal();

        getWorkoutManager().init(exercise, goal);

        getMediaPlayer().init();

        getWorkoutManager().startPreWorkoutTimer(PRE_WORKOUT_COUNTDOWN_SECONDS);
        startForeground(WORKOUT_NOTIFICATION_ID, getNotificationHelper().getBuilder().build());
        getNotificationHelper().setTitle("Get ready"); //TODO: extract string

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Timber.tag(TAG).d( "onDestroy");
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onMessageEvent(Events.StopWorkoutEvent event) {
        Timber.tag(TAG).d( "StopWorkoutEvent");
        stopInternal();
    }

    @Subscribe
    public void onMessageEvent(Events.FinishedWorkoutEvent event) {
        Timber.tag(TAG).d( "FinishedWorkoutEvent");
        Power.turnOnScreen(this);
        stopInternal();
        getWorkoutManager().getWorkout().state = State.WORKOUT_FINISHED;
    }

    @Subscribe
    public void onMessageEvent(Events.TimerTickEvent event) {
        getNotificationHelper().setElapsedTime(event.seconds);

        if (event.type.equals(TimerType.PRE_WORKOUT_COUNT_DOWN)) {
            if (event.seconds == 0) {
                getMediaPlayer().play(COUNTDOWN_LONG);
            } else if (event.seconds <= 3) {
                getMediaPlayer().play(COUNTDOWN);
            }
        }
    }

    @Subscribe
    public void onMessageEvent(Events.PreWorkoutCountdownFinished event) {
        onStartWorkout();
    }

    @Subscribe
    public void onMessageEvent(Events.RepComplete event) {
        if (event.reps == 0) {
            return;
        }
        if (SettingsHelper.wakeupEnabled()
                && (event.reps  % SettingsHelper.getWakeUpInterval() == 0)) {
            Power.turnOnScreen(this);
        }
        if (SettingsHelper.soundEnabled()){
            getMediaPlayer().play(REP_COMPLETE);
        }
        getNotificationHelper().setReps(event.reps);
    }

    @Subscribe
    public void onMessageEvent(Events.SetFinished event) {
        if (SettingsHelper.soundEnabled()){
            getMediaPlayer().play(REP_COMPLETE_SPECIAL);
        }
    }

    @Subscribe
    public void onMessageEvent(Events.StartBreak event) {
        Timber.tag(TAG).d( "onSetCompleted");
        Power.turnOnScreen(this);

        new Handler().postDelayed(() -> getMediaPlayer().play(REST), 1000);
        getWorkoutManager().getWorkout().state = State.BREAK_ACTIVE;
        getWorkoutManager().startPreWorkoutTimer(
                TimeUnit.SECONDS.toMillis(event.duration));
    }

    @Subscribe
    public void onMessageEvent(Events.ToggleWorkoutEvent event) {
        getWorkoutManager().toggle();
    }


    private NotificationHelper getNotificationHelper() {
        return BuddyApplication.getNotificationHelper();
    }

    private WorkoutManager getWorkoutManager() {
        return BuddyApplication.getWorkoutManager();
    }

    private SoundPlayer getMediaPlayer() {
        return BuddyApplication.getMediaPlayer();
    }
}
