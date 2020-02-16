package com.apps.adrcotfas.burpeebuddy.statistics.view;

import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.ActionModeCallback;
import com.apps.adrcotfas.burpeebuddy.common.recyclerview.RecyclerItemClickListener;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.workout.Workout;

import java.util.ArrayList;
import java.util.List;

public class StatisticsViewMvcImpl
        extends BaseObservableViewMvc<StatisticsViewMvc.Listener>
        implements StatisticsViewMvc, StatisticsAdapter.Listener, ActionModeCallback.Listener {

    private RecyclerView recyclerView;
    private StatisticsAdapter adapter;
    private LinearLayout emptyState;

    private List<Workout> workouts = new ArrayList<>();
    private List<Integer> selectedEntries = new ArrayList<>();

    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback;
    private boolean isMultiSelect = false;

    public StatisticsViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {

        actionModeCallback = new ActionModeCallback(this, true);

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
                if (isMultiSelect) {
                    multiSelect(position);
                }
            }
            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    adapter.setSelectedItems(new ArrayList<>());
                    isMultiSelect = true;
                    startActionMode();
                }
                multiSelect(position);
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

    private void multiSelect(int position) {
        Workout w = workouts.get(position);
        if (w != null) {
            int idx = selectedEntries.indexOf(w.id);
            if (idx != -1) {
                selectedEntries.remove(idx);
            }  else {
                selectedEntries.add(w.id);
            }
            if (!selectedEntries.isEmpty()) {
                toggleEditButtonVisibility(selectedEntries.size() == 1);
                updateTitle(String.valueOf(selectedEntries.size()));
            } else {
                finishAction();
            }
            adapter.setSelectedItems(selectedEntries);
        }
    }

    public void selectAllItems() {
        selectedEntries.clear();
        for (Workout w : workouts) {
            selectedEntries.add(w.id);
        }

        if (!selectedEntries.isEmpty()) {
            updateTitle(String.valueOf(selectedEntries.size()));
            adapter.setSelectedItems(selectedEntries);
        }  else {
            finishAction();
        }
    }

    @Override
    public void actionSelectAllItems() {
        actionModeCallback.toggleEditButtonVisibility(false);
        selectAllItems();
    }

    @Override
    public void actionDeleteSelected() {
        for (Listener l : getListeners()) {
            l.onDeleteSelected(selectedEntries);
        }
    }

    @Override
    public void actionEditSelected() {
        Workout selectedWorkout = null;
        for (Workout w : workouts) {
            if (w.id == selectedEntries.get(0)) {
                selectedWorkout = w;
                break;
            }
        }
        for (Listener l : getListeners()) {
            l.onEditSelected(selectedWorkout);
        }
    }

    @Override
    public void destroyActionMode() {
        isMultiSelect = false;
        selectedEntries = new ArrayList<>();
        adapter.setSelectedItems(new ArrayList<>());
        actionMode = null;
    }

    public void startActionMode() {
        if (actionMode == null) {
            for (Listener l : getListeners()) {
                actionMode = l.startActionMode(actionModeCallback);
                break;
            }
        }
    }

    public void updateTitle(String numberOfSelectedItems) {
        actionMode.setTitle(numberOfSelectedItems);
    }

    public void finishAction() {
        actionMode.setTitle("");
        actionMode.finish();
    }

    public void toggleEditButtonVisibility(boolean visible) {
        actionModeCallback.toggleEditButtonVisibility(visible);
    }
}
