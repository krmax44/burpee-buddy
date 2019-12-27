package com.apps.adrcotfas.burpeebuddy.workout;

import android.content.Intent;
import android.os.Handler;

import androidx.lifecycle.LifecycleService;

import com.apps.adrcotfas.burpeebuddy.common.bl.BuddyApplication;
import com.apps.adrcotfas.burpeebuddy.common.bl.Events;
import com.apps.adrcotfas.burpeebuddy.common.bl.NotificationHelper;
import com.apps.adrcotfas.burpeebuddy.common.bl.WorkoutManager;
import com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundPlayer;
import com.apps.adrcotfas.burpeebuddy.common.timers.TimerType;
import com.apps.adrcotfas.burpeebuddy.common.utilities.Power;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseTypeConverter;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;
import com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

import static com.apps.adrcotfas.burpeebuddy.common.bl.NotificationHelper.WORKOUT_NOTIFICATION_ID;
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
        getWorkoutManager().getRepCounter().unregister();
        getWorkoutManager().getCountDownTimer().cancel();
        getWorkoutManager().getTimer().stop();

        // workaround to make sure that the sound played above is audible and the player released
        new Handler().postDelayed(() -> getMediaPlayer().play(WORKOUT_COMPLETE, true), 1000);

        stopForeground(true);
        stopSelf();
    }

    @Override
    public synchronized int onStartCommand(final Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        EventBus.getDefault().register(this);

        getWorkoutManager().reset();
        final ExerciseType type = ExerciseTypeConverter.getExerciseTypeFromInt(
                WorkoutFragmentArgs.fromBundle(intent.getExtras()).getExerciseType());
        final Goal goal = WorkoutFragmentArgs.fromBundle(intent.getExtras()).getGoal();

        getWorkoutManager().init(type, goal);
        getWorkoutManager().getWorkout().state = State.ACTIVE;

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
        Timber.tag(TAG).d( "FinishedWorkoutEvent");
        onStopWorkout();
    }

    @Subscribe
    public void onMessageEvent(Events.FinishedWorkoutEvent event) {
        Timber.tag(TAG).d( "FinishedWorkoutEvent");
        Power.turnOnScreen(this);
        onFinishedWorkout();
    }

    @Subscribe
    public void onMessageEvent(Events.TimerTickEvent event) {
        getNotificationHelper().setElapsedTime(event.seconds);

        if (event.type.equals(TimerType.PRE_WORKOUT_COUNT_DOWN)) {
            if (event.seconds == 0) {
                getMediaPlayer().play(COUNTDOWN_LONG);
                onStartWorkout();
                //TODO: extract to constants or to Settings
            } else if (event.seconds <= 3) {
                getMediaPlayer().play(COUNTDOWN);
            }
        }
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
            if (event.lastRepThisSet) {
                getMediaPlayer().play(REP_COMPLETE_SPECIAL);
            } else {
                getMediaPlayer().play(REP_COMPLETE);
            }
        }
        getNotificationHelper().setReps(event.reps);
    }

    @Subscribe
    public void onMessageEvent(Events.SetComplete event) {
        Timber.tag(TAG).d( "onSetCompleted");
        Power.turnOnScreen(this);

        new Handler().postDelayed(() -> getMediaPlayer().play(REST), 1000);
        getWorkoutManager().startPreWorkoutTimer(
                TimeUnit.SECONDS.toMillis(getWorkoutManager().getWorkout().goal.getDurationBreak()));
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
