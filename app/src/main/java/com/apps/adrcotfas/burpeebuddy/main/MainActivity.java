package com.apps.adrcotfas.burpeebuddy.main;

import android.os.Bundle;
import android.widget.Toast;

import com.apps.adrcotfas.burpeebuddy.common.BaseActivity;
import com.apps.adrcotfas.burpeebuddy.workout.WorkoutActivity;

public class MainActivity extends BaseActivity implements MainViewMvcImpl.Listener {

    MainViewMvc mViewMvc;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewMvc = getCompositionRoot().getViewMvcFactory().getMainViewMvc(null);
        setContentView(mViewMvc.getRootView());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewMvc.registerListener(this);
    }

    @Override
    protected void onDestroy() {
        mViewMvc.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public void onStartButtonClicked() {
        WorkoutActivity.start(this, 0);
    }

    @Override
    public void onDisabledChipClicked() {
        Toast.makeText(this, "This feature is coming soon.", Toast.LENGTH_SHORT).show();
    }
}
