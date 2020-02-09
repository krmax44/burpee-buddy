package com.apps.adrcotfas.burpeebuddy.statistics.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.workout.Workout;

import java.util.List;

public class StatisticsViewMvcImpl
        extends BaseObservableViewMvc<StatisticsViewMvc.Listener>
    implements StatisticsViewMvc, StatisticsAdapter.Listener {

    private RecyclerView recyclerView;
    private StatisticsAdapter adapter;

    private LinearLayout emptyState;

    public StatisticsViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.fragment_recycler, parent, false));
        emptyState = findViewById(R.id.empty_state);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new StatisticsAdapter(inflater, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void bindWorkouts(List<Workout> workouts) {
        recyclerView.setVisibility(workouts.isEmpty() ? View.GONE : View.VISIBLE);
        emptyState.setVisibility(workouts.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.bindWorkouts(workouts);
    }

    @Override
    public void onWorkoutLongPress(int id) {
        for (Listener l : getListeners()) {
            l.onWorkoutLongPress(id);
        }
    }
}
