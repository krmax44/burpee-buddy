package com.apps.adrcotfas.burpeebuddy.edit_goals.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;

import java.util.List;

public class GoalsViewMvcImpl
        extends BaseObservableViewMvc<GoalsViewMvc.Listener>
        implements GoalsViewMvc, GoalsAdapter.Listener {

    private RecyclerView recyclerView;
    private GoalsAdapter adapter;

    private LinearLayout emptyState;

    public GoalsViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.fragment_recycler, parent, false));
        emptyState = findViewById(R.id.empty_state);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GoalsAdapter(inflater, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void bindGoals(List<Goal> goals) {
        recyclerView.setVisibility(goals.isEmpty() ? View.GONE : View.VISIBLE);
        emptyState.setVisibility(goals.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.bindGoals(goals);
    }

    @Override
    public void onGoalEditClicked(Goal goal) {
        for (Listener l : getListeners()) {
            l.onGoalEditClicked(goal);
        }
    }
}
