package com.apps.adrcotfas.burpeebuddy.edit_goals.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;

import java.util.List;

public class GoalsViewMvcImpl
        extends BaseObservableViewMvc<GoalsViewMvc.Listener>
        implements GoalsViewMvc, GoalsAdapter.Listener {

    private RecyclerView mRecyclerView;
    private GoalsAdapter mAdapter;

    public GoalsViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.fragment_recycler, parent, false));

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new GoalsAdapter(inflater, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void bindGoals(List<Goal> goals) {
        mAdapter.bindGoals(goals);
    }

    @Override
    public void onGoalEditClicked(Goal goal) {
        for (Listener l : getListeners()) {
            l.onGoalEditClicked(goal);
        }
    }
}
