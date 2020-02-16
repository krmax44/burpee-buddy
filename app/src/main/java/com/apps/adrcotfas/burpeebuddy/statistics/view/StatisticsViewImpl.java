package com.apps.adrcotfas.burpeebuddy.statistics.view;

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
import com.apps.adrcotfas.burpeebuddy.db.workout.Workout;

import java.util.ArrayList;
import java.util.List;

public class StatisticsViewImpl
        extends BaseObservableView<StatisticsView.Listener>
        implements StatisticsView, ActionModeHelper.Listener {

    private RecyclerView recyclerView;
    private StatisticsAdapter adapter;
    private LinearLayout emptyState;

    private List<Workout> workouts = new ArrayList<>();

    private ActionModeHelper<Integer> actionModeHelper;

    public StatisticsViewImpl(LayoutInflater inflater, ViewGroup parent) {

        actionModeHelper = new ActionModeHelper<>(this, true);

        setRootView(inflater.inflate(R.layout.fragment_recycler, parent, false));
        emptyState = findViewById(R.id.empty_state);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new StatisticsAdapter(inflater);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext()
                , recyclerView
                , new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                actionModeHelper.onItemClick(workouts.get(position).id);
                adapter.setSelectedItems(actionModeHelper.getSelectedEntries());

            }
            @Override
            public void onItemLongClick(View view, int position) {
                actionModeHelper.onItemLongClick(workouts.get(position).id);
                adapter.setSelectedItems(actionModeHelper.getSelectedEntries());
            }
        }));
    }

    @Override
    public void bindWorkouts(List<Workout> workouts) {
        recyclerView.setVisibility(workouts.isEmpty() ? View.GONE : View.VISIBLE);
        emptyState.setVisibility(workouts.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.bindWorkouts(workouts);
        this.workouts = workouts;
    }

    @Override
    public void actionSelectAllItems() {
        actionModeHelper.toggleEditButtonVisibility(false);

        List<Integer> ids = new ArrayList<>(workouts.size());
        for (Workout w : workouts) {
            ids.add(w.id);
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
        Workout selectedWorkout = null;
        for (Workout w : workouts) {
            if (w.id == actionModeHelper.getSelectedEntries().get(0)) {
                selectedWorkout = w;
                break;
            }
        }
        for (Listener l : getListeners()) {
            l.onEditSelected(selectedWorkout);
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

    @Override
    public void destroyActionMode() {
        actionModeHelper.destroyActionMode();
        adapter.setSelectedItems(new ArrayList<>());
    }
}
