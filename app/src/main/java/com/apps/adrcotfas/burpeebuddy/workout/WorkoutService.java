package com.apps.adrcotfas.burpeebuddy.workout;

import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.LifecycleService;

import com.apps.adrcotfas.burpeebuddy.common.bl.BuddyApplication;
import com.apps.adrcotfas.burpeebuddy.common.bl.Events;
import com.apps.adrcotfas.burpeebuddy.common.bl.NotificationHelper;
import com.apps.adrcotfas.burpeebuddy.common.bl.WorkoutManager;
import com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundPlayer;
import com.apps.adrcotfas.burpeebuddy.common.utilities.Power;
import com.apps.adrcotfas.burpeebuddy.db.exercisetype.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.exercisetype.ExerciseTypeConverter;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;
import com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import timber.log.Timber;

import static com.apps.adrcotfas.burpeebuddy.common.bl.NotificationHelper.WORKOUT_NOTIFICATION_ID;
import static com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundType.COUNTDOWN;
import static com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundType.COUNTDOWN_LONG;
import static com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundType.REP_COMPLETE;
import static com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundType.REP_COMPLETE_SPECIAL;

public class WorkoutService extends LifecycleService {
    private static final String TAG = "WorkoutService";

    private Bundle mExtras;

    private void onStartWorkout() {
        Timber.tag(TAG).d( "onStartWorkout");

        getNotificationHelper().setReps(0);

        final ExerciseType type = ExerciseTypeConverter.getExerciseTypeFromInt(
                WorkoutFragmentArgs.fromBundle(mExtras).getExerciseType());
        final Goal goal = WorkoutFragmentArgs.fromBundle(mExtras).getGoal();

        getWorkoutManager().start(type, goal);
        getWorkoutManager().getWorkout().totalReps.observe(this, reps -> onRepCompleted(reps));
        getWorkoutManager().getTimer().getElapsedSeconds().observe(this, seconds -> onTimerTick(seconds));
    }

    private void onStopWorkout() {
        Timber.tag(TAG).d( "onStopWorkout");
        getWorkoutManager().getWorkout().state = State.INACTIVE;
        stopInternal();
    }

    private void onFinishedWorkout() {
        // TODO: show finished notification
        Timber.tag(TAG).d( "onFinishedWorkout");
        getWorkoutManager().getWorkout().state = State.FINISHED;
        stopInternal();
    }

    private void stopInternal() {
        getWorkoutManager().getPreWorkoutTimer().cancel();
        getWorkoutManager().getTimer().getElapsedSeconds().removeObservers(this);
        getWorkoutManager().getTimer().stop();
        getMediaPlayer().onWorkoutStop();

        stopForeground(true);
        stopSelf();
    }

    @Override
    public synchronized int onStartCommand(final Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        EventBus.getDefault().register(this);

        getWorkoutManager().reset();
        getWorkoutManager().getWorkout().state = State.ACTIVE;
        mExtras = intent.getExtras();
        getMediaPlayer().init();

        getWorkoutManager().startPreWorkoutTimer();
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
    public void onMessageEvent(Events.PreWorkoutCountdownTickEvent event) {
        getNotificationHelper().setElapsedTime(event.seconds);
        if (event.seconds == 0) {
            getMediaPlayer().play(COUNTDOWN_LONG);
            onStartWorkout();
            //TODO: extract to constants or to Settings
        } else if (event.seconds <= 3) {
            getMediaPlayer().play(COUNTDOWN);
        }
    }

    @Subscribe
    public void onMessageEvent(Events.StopWorkoutEvent event) {
        Timber.tag(TAG).d( "FinishedWorkoutEvent");
        onStopWorkout();
    }

    @Subscribe
    public void onMessageEvent(Events.FinishedWorkoutEvent event) {
        Timber.tag(TAG).d( "FinishedWorkoutEvent");
        onFinishedWorkout();
    }

    public void onTimerTick(int seconds) {
        getNotificationHelper().setElapsedTime(seconds);
    }

    private void onRepCompleted(int reps) {
        if (reps == 0) {
            return;
        }
        if (SettingsHelper.wakeupEnabled()
                && (reps % SettingsHelper.getWakeUpInterval() == 0)) {
            Power.turnOnScreen(this);
        }
        if (SettingsHelper.specialSoundEnabled()
                && (reps % SettingsHelper.getSpecialSoundInterval() == 0)){
            getMediaPlayer().play(REP_COMPLETE_SPECIAL);
        } else if (SettingsHelper.soundEnabled()){
            getMediaPlayer().play(REP_COMPLETE);
        }
        getNotificationHelper().setReps(reps);
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
