package com.apps.adrcotfas.burpeebuddy.statistics.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.workout.Workout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class StatisticsViewMvcImpl
        extends BaseObservableViewMvc<StatisticsViewMvc.Listener>
    implements StatisticsViewMvc, StatisticsAdapter.Listener {

    private RecyclerView mRecyclerView;
    private StatisticsAdapter mAdapter;
    private final FloatingActionButton mFab;

    public StatisticsViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.fragment_recycler_and_fab, parent, false));

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new StatisticsAdapter(inflater, this);
        mRecyclerView.setAdapter(mAdapter);
        mFab = findViewById(R.id.fab);

        // TODO: implement
        mFab.setVisibility(View.GONE);
        mFab.setOnClickListener(v -> {
            for (StatisticsViewMvc.Listener l : getListeners()) {
                l.onWorkoutAddClicked();
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && mFab.getVisibility() == View.VISIBLE) {
                    mFab.hide();
                } else if (dy < 0 && mFab.getVisibility() !=View.VISIBLE) {
                    mFab.show();
                }
            }
        });
    }

    @Override
    public void bindWorkouts(List<Workout> workouts) {
        mAdapter.bindWorkouts(workouts);
    }

    @Override
    public void onWorkoutLongPress(Workout workout) {
        for (Listener l : getListeners()) {
            l.onWorkoutLongPress();
        }
    }
}
