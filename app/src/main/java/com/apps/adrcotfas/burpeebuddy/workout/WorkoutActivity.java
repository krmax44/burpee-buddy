package com.apps.adrcotfas.burpeebuddy.workout;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.apps.adrcotfas.burpeebuddy.common.BaseActivity;
import com.apps.adrcotfas.burpeebuddy.common.bl.Events;
import com.apps.adrcotfas.burpeebuddy.common.utilities.Power;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class WorkoutActivity extends BaseActivity implements WorkoutViewMvc.Listener {

    private static final String TAG = "WorkoutActivity";
    WorkoutViewMvc mViewMvc;

    public static void start(Context context, int workoutType) {
        Intent intent = new Intent(context, WorkoutActivity.class);
        //TODO later intent.putExtra(EXTRA_WORKOUT_TYPE, workoutType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewMvc = getCompositionRoot().getViewMvcFactory().getWorkoutViewMvc(null);
        setContentView(mViewMvc.getRootView());
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewMvc.registerListener(this);
        if (!WorkoutService.isStarted) {
            startWorkout();
        }
    }

    // TODO: unregister from certain events like update countdown timer event in onStop
    // because we're working with the display off

    @Override
    protected void onDestroy() {
        mViewMvc.unregisterListener(this);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onStopButtonClicked() {
        //TODO open finished dialog, save to ROOM etc
        stopWorkout();
    }

    private void startWorkout() {
        Intent startIntent = new Intent(WorkoutActivity.this, WorkoutService.class);
        startIntent.setAction(Actions.START);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(startIntent);
        } else {
            startService(startIntent);
        }
    }

    private void stopWorkout() {
        Intent stopIntent = new Intent(WorkoutActivity.this, WorkoutService.class);
        stopIntent.setAction(Actions.STOP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(stopIntent);
        } else {
            startService(stopIntent);
        }
    }

    @Subscribe
    public void onMessageEvent(Events.PreWorkoutCountdownTickEvent event) {
        Log.d(TAG, "PreWorkoutCountdownTickEvent: " + event.seconds);
        mViewMvc.updateTimer(event.seconds);
    }

    @Subscribe
    public void onMessageEvent(Events.PreWorkoutCountdownFinished event) {
        Log.d(TAG, "PreWorkoutCountdownFinished");
        //TODO switch to timer mode depending on workout type
        if (Power.isScreenOn(this)) {
            //TODO: this requires an additional permission, make it optional
            // extract to preferences
            Power.lockScreen(this);
        }
    }

    @Subscribe
    public void onMessageEvent(Events.RepCompletedEvent event) {
        Log.d(TAG, "RepCompletedEvent: " + event.size + " reps");
        mViewMvc.updateCounter(event.size);
    }

    @Subscribe
    public void onMessageEvent(Events.TimerTickEvent event) {
        Log.d(TAG, "TimerTickEvent: " + event.seconds);
        mViewMvc.updateTimer(event.seconds);
    }
}
