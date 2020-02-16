package com.apps.adrcotfas.burpeebuddy.edit_goals.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.ActionModeHelper;
import com.apps.adrcotfas.burpeebuddy.common.recyclerview.RecyclerItemClickListener;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableView;
import com.apps.adrcotfas.burpeebuddy.db.goal.Goal;

import java.util.ArrayList;
import java.util.List;

public class GoalsViewImpl
        extends BaseObservableView<GoalsView.Listener>
        implements GoalsView, ActionModeHelper.Listener {

    private RecyclerView recyclerView;
    private GoalsAdapter adapter;

    private ActionModeHelper<Integer> actionModeHelper;
    private List<Goal> goals = new ArrayList<>();
    private LinearLayout emptyState;

    public GoalsViewImpl(LayoutInflater inflater, ViewGroup parent) {
        actionModeHelper = new ActionModeHelper<>(this, true);

        setRootView(inflater.inflate(R.layout.fragment_recycler, parent, false));
        emptyState = findViewById(R.id.empty_state);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GoalsAdapter(inflater);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext()
                , recyclerView
                , new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                actionModeHelper.onItemClick(goals.get(position).id);
                adapter.setSelectedItems(actionModeHelper.getSelectedEntries());

            }
            @Override
            public void onItemLongClick(View view, int position) {
                actionModeHelper.onItemLongClick(goals.get(position).id);
                adapter.setSelectedItems(actionModeHelper.getSelectedEntries());
            }
        }));
    }

    @Override
    public void bindGoals(List<Goal> goals) {
        recyclerView.setVisibility(goals.isEmpty() ? View.GONE : View.VISIBLE);
        emptyState.setVisibility(goals.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.bindGoals(goals);
        this.goals = goals;
    }

    @Override
    public void destroyActionMode() {
        actionModeHelper.destroyActionMode();
        adapter.setSelectedItems(new ArrayList<>());
    }

    @Override
    public void actionSelectAllItems() {
        actionModeHelper.toggleEditButtonVisibility(false);

        List<Integer> ids = new ArrayList<>(goals.size());
        for (Goal g : goals) {
            ids.add(g.id);
        }

        if (!ids.isEmpty()) {
            adapter.setSelectedItems(ids);
        }
        actionModeHelper.setSelectedEntries(ids);
    }

    @Override
    public void actionDeleteSelected() {
        for (Listener l : getListeners()) {
            l.onDeleteSelected(actionModeHelper.getSelectedEntries());
        }
    }

    @Override
    public void actionEditSelected() {
        Goal selectedGoal = null;
        for (Goal g : goals) {
            if (g.id == actionModeHelper.getSelectedEntries().get(0)) {
                selectedGoal = g;
                break;
            }
        }
        for (Listener l : getListeners()) {
            l.onEditSelected(selectedGoal);
        }
    }

    @Override
    public void startActionMode() {
        for (Listener l : getListeners()) {
            l.startActionMode(actionModeHelper);
            break; // workaround: there's only one listener
        }
    }

    @Override
    public void stopActionMode() {
        adapter.setSelectedItems(new ArrayList<>());
    }
}
