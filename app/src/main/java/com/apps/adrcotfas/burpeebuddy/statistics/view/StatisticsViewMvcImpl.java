package com.apps.adrcotfas.burpeebuddy.statistics.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.workout.Workout;

import java.util.List;

public class StatisticsViewMvcImpl
        extends BaseObservableViewMvc<StatisticsViewMvc.Listener>
    implements StatisticsViewMvc, StatisticsAdapter.Listener {

    private RecyclerView mRecyclerView;
    private StatisticsAdapter mAdapter;

    public StatisticsViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.fragment_recycler, parent, false));

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new StatisticsAdapter(inflater, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void bindWorkouts(List<Workout> workouts) {
        mAdapter.bindWorkouts(workouts);
    }

    @Override
    public void onWorkoutLongPress(int id) {
        for (Listener l : getListeners()) {
            l.onWorkoutLongPress(id);
        }
    }
}
