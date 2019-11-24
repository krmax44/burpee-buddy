package com.apps.adrcotfas.burpeebuddy.main;

import android.os.Bundle;

import com.apps.adrcotfas.burpeebuddy.common.BaseActivity;
import com.apps.adrcotfas.burpeebuddy.workout.WorkoutActivity;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

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
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);
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
}
