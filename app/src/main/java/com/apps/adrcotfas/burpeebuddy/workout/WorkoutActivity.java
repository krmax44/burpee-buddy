package com.apps.adrcotfas.burpeebuddy.workout;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.apps.adrcotfas.burpeebuddy.common.BaseActivity;
import com.apps.adrcotfas.burpeebuddy.common.application.BuddyApplication;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

public class WorkoutActivity extends BaseActivity implements WorkoutViewMvc.Listener {

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

        BuddyApplication.getWorkoutManager().getReps().observe(
                WorkoutActivity.this
                , reps -> mViewMvc.updateCounter(reps));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);
        mViewMvc.registerListener(this);

        // TODO: implement countdown timer before starting

        Intent startIntent = new Intent(WorkoutActivity.this, WorkoutService.class);
        startIntent.setAction("START");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(startIntent);
        } else {
            startService(startIntent);
        }
    }

    @Override
    protected void onDestroy() {
        mViewMvc.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public void onStopButtonClicked() {
        //TODO open finished dialog, save to ROOM etc

        Intent stopIntent = new Intent(WorkoutActivity.this, WorkoutService.class);
        stopIntent.setAction("STOP"); //TODO extract constant
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(stopIntent);
        } else {
            startService(stopIntent);
        }
    }
}
