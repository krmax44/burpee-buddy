package com.apps.adrcotfas.burpeebuddy.workout;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

public class WorkoutActivity extends AppCompatActivity implements WorkoutViewMvc.Listener {

    WorkoutViewMvc mViewMvc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewMvc = new WorkoutViewMvcImpl(LayoutInflater.from(this), null);
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
