package com.apps.adrcotfas.burpeebuddy.edit_goals.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class GoalsViewMvcImpl
        extends BaseObservableViewMvc<GoalsViewMvc.Listener>
        implements GoalsViewMvc, GoalsAdapter.Listener {

    private RecyclerView mRecyclerView;
    private GoalsAdapter mAdapter;

    private List<Goal> mGoals = new ArrayList<>();

    public GoalsViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.fragment_add_edit, parent, false));

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new GoalsAdapter(inflater, this);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            for (GoalsViewMvc.Listener l : getListeners()) {
                l.onGoalAddClicked();
            }
        });
    }

    @Override
    public void bindGoals(List<Goal> goals) {
        mAdapter.bindGoals(goals);
        mGoals = goals;
    }

    @Override
    public void onGoalEditClicked(Goal goal) {
        for (Listener l : getListeners()) {
            l.onGoalEditClicked(goal);
        }
    }
}
