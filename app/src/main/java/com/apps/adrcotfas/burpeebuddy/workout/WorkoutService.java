package com.apps.adrcotfas.burpeebuddy.workout;

import android.content.Intent;

import androidx.lifecycle.LifecycleService;

import com.apps.adrcotfas.burpeebuddy.common.application.BuddyApplication;
import com.apps.adrcotfas.burpeebuddy.common.application.NotificationHelper;
import com.apps.adrcotfas.burpeebuddy.common.application.RepCounter;
import com.apps.adrcotfas.burpeebuddy.common.application.WorkoutManager;

public class WorkoutService extends LifecycleService {

    private void onStart() {
        startForeground(42, getNotificationHelper().getInProgressBuilder().build());
        getRepCounter().register(getWorkoutManager());

        getWorkoutManager().getReps().observe(
                WorkoutService.this
                , reps -> getNotificationHelper().updateNotificationProgress(String.valueOf(reps)));
    }

    private void onStop() {
        getWorkoutManager().getReps().removeObservers(WorkoutService.this);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public synchronized int onStartCommand(final Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        switch (intent.getAction()) {
            case "STOP":
                onStop();
                break;
            case "START":
                onStart();
                break;
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        onStop();
        getRepCounter().unregister();
        getWorkoutManager().resetReps();
        super.onDestroy();
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
}
