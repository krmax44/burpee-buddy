package com.apps.adrcotfas.burpeebuddy.workout;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.apps.adrcotfas.burpeebuddy.common.BaseActivity;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

public class WorkoutActivity extends BaseActivity implements WorkoutViewMvc.Listener {

    WorkoutViewMvc mViewMvc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewMvc = getCompositionRoot().getViewMvcFactory().getWorkoutViewMvc(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);
        mViewMvc.registerListener(this);
    }

    @Override
    protected void onDestroy() {
        mViewMvc.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public void onStopButtonClicked() {
        // open finished dialog
    }
}
