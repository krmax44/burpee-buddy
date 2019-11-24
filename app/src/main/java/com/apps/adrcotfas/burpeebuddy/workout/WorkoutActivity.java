package com.apps.adrcotfas.burpeebuddy.workout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.apps.adrcotfas.burpeebuddy.common.BaseActivity;
import com.apps.adrcotfas.burpeebuddy.workout.repcounter.RepCounter;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

public class WorkoutActivity extends BaseActivity implements WorkoutViewMvc.Listener, RepCounter.Listener {

    WorkoutViewMvc mViewMvc;
    RepCounter mRepCounter;

    public static void start(Context context, int workoutType) {
        Intent intent = new Intent(context, WorkoutActivity.class);
        //TODO later intent.putExtra(EXTRA_WORKOUT_TYPE, workoutType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewMvc = getCompositionRoot().getViewMvcFactory().getWorkoutViewMvc(null);
        mRepCounter = new RepCounter(this, this);
        setContentView(mViewMvc.getRootView());
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);
        mViewMvc.registerListener(this);

        // TODO: implement countdown timer before starting
    }

    @Override
    protected void onDestroy() {
        mViewMvc.unregisterListener(this);
        mRepCounter.stop();
        super.onDestroy();
    }

    @Override
    public void onStopButtonClicked() {
        mRepCounter.stop();
        //TODO open finished dialog, save to ROOM etc
        onBackPressed();
    }

    @Override
    public void onRepCompleted() {
        mViewMvc.updateCounter(mRepCounter.getReps());
    }

    @Override
    public void onSensorError() {
        // TODO: report that the app cannot be used because there is no proximity sensor
    }
}
