package com.apps.adrcotfas.burpeebuddy.workout;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;

import androidx.annotation.Nullable;

import com.apps.adrcotfas.burpeebuddy.common.BaseActivity;
import com.apps.adrcotfas.burpeebuddy.common.bl.Events;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class WorkoutActivity extends BaseActivity implements WorkoutViewMvc.Listener {

    WorkoutViewMvc mViewMvc;
    private  static final int REQUEST_ENABLE = 0;

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
        startIntent.setAction("START");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(startIntent);
        } else {
            startService(startIntent);
        }
    }

    private void stopWorkout() {
        Intent stopIntent = new Intent(WorkoutActivity.this, WorkoutService.class);
        stopIntent.setAction("STOP"); //TODO extract constant
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(stopIntent);
        } else {
            startService(stopIntent);
        }
    }

    @Subscribe
    public void onMessageEvent(Events.PreWorkoutCountdownFinished event) {
        //TODO switch to timer mode depending on workout type
        mViewMvc.toggleTimerVisibility();

        if (isScreenOn()) {
            lockScreen();
        }
    }

    private boolean isScreenOn() {
        return ((PowerManager) getSystemService(Context.POWER_SERVICE)).isScreenOn();
    }

    private void lockScreen() {
        ComponentName adminComponent = new ComponentName(
                WorkoutActivity.this,
                android.app.admin.DeviceAdminReceiver.class);
        DevicePolicyManager devicePolicyManager =
                (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        if (!devicePolicyManager.isAdminActive(adminComponent)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
            startActivityForResult(intent, REQUEST_ENABLE);
        } else {
            devicePolicyManager.lockNow();
        }
    }

    @Subscribe
    public void onMessageEvent(Events.PreWorkoutCountdownTickEvent event) {
        mViewMvc.updateTimer(event.seconds);
    }

    @Subscribe
    public void onMessageEvent(Events.RepCompletedEvent event) {
        mViewMvc.updateCounter(event.size);
    }
}
