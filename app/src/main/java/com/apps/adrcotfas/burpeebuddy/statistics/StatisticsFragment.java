package com.apps.adrcotfas.burpeebuddy.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.apps.adrcotfas.burpeebuddy.db.AppDatabase;
import com.apps.adrcotfas.burpeebuddy.statistics.view.StatisticsViewMvc;
import com.apps.adrcotfas.burpeebuddy.statistics.view.StatisticsViewMvcImpl;

public class StatisticsFragment extends Fragment implements StatisticsViewMvc.Listener {
    private static final String TAG = "StatisticsFragment";

    private StatisticsViewMvc mViewMvc;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //EventBus.getDefault().register(this);
        mViewMvc = new StatisticsViewMvcImpl(inflater, container);

        AppDatabase.getDatabase(getContext()).workoutDao().getAll().observe(
                getViewLifecycleOwner(), workouts ->
                        mViewMvc.bindWorkouts(workouts));

        return mViewMvc.getRootView();
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewMvc.registerListener(this);
    }

    @Override
    public void onDestroy() {
        if (mViewMvc != null){
            mViewMvc.unregisterListener(this);
        }
        //EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onWorkoutAddClicked() {
        //TODO: open dialog
    }

    @Override
    public void onWorkoutLongPress() {
        //TODO: adapt toolbar
    }
}
