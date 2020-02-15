package com.apps.adrcotfas.burpeebuddy.statistics;

import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.Events;
import com.apps.adrcotfas.burpeebuddy.db.AppDatabase;
import com.apps.adrcotfas.burpeebuddy.common.ActionModeCallback;
import com.apps.adrcotfas.burpeebuddy.db.workout.Workout;
import com.apps.adrcotfas.burpeebuddy.statistics.dialog.AddEditWorkoutDialog;
import com.apps.adrcotfas.burpeebuddy.statistics.view.StatisticsViewMvc;
import com.apps.adrcotfas.burpeebuddy.statistics.view.StatisticsViewMvcImpl;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment
        implements StatisticsViewMvc.Listener, ActionModeCallback.Listener {
    private static final String TAG = "StatisticsFragment";

    private StatisticsViewMvc view;
    private List<Workout> workouts = new ArrayList<>();

    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        view = new StatisticsViewMvcImpl(inflater, container);

        actionModeCallback = new ActionModeCallback(this, false);

        AppDatabase.getDatabase(getContext()).workoutDao().getAll().observe(
                getViewLifecycleOwner(), workouts -> {
                    view.bindWorkouts(workouts);
                    this.workouts = workouts;
                });
        setHasOptionsMenu(true);
        return view.getRootView();
    }

    @Override
    public void onResume() {
        super.onResume();
        view.registerListener(this);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    @Override
    public void onDestroy() {
        if (view != null){
            view.unregisterListener(this);
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,
                                    MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_stuff, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            AddEditWorkoutDialog.getInstance(null, false)
                    .show(getActivity().getSupportFragmentManager(), TAG);
            return true;
        }
        return false;
    }

    @Subscribe
    public void onMessageEvent(Events.AddWorkout e) {
        AppDatabase.addWorkout(getContext(), e.workout);
    }

    @Subscribe
    public void onMessageEvent(Events.EditWorkout e) {
        AppDatabase.editWorkout(getContext(), e.id, e.workout);
    }

    @Override
    public void actionSelectAllItems() {
        actionModeCallback.toggleEditButtonVisibility(false);
        view.selectAllItems();
    }

    @Override
    public void actionDelete() {
        new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Delete workouts?")
                .setMessage("This will delete the selected workouts")
                .setPositiveButton(android.R.string.ok, (dialog, i) -> {
                    for (int id : view.getSelectedEntriesIds()) {
                        AppDatabase.deleteWorkout(getContext(), id);
                    }
                    finishAction();
                })
                .setNegativeButton(android.R.string.cancel, (dialog, i) -> dialog.cancel())
                .show();
    }

    @Override
    public void destroyActionMode() {
        view.unselectItems();
        actionMode = null;
    }

    @Override
    public void editSelected() {
        Workout selectedWorkout = null;
        for (Workout w : workouts) {
            if (w.id == view.getSelectedEntriesIds().get(0)) {
                selectedWorkout = w;
                break;
            }
        }
        if (selectedWorkout != null) {
            AddEditWorkoutDialog.getInstance(selectedWorkout, true)
                    .show(getActivity().getSupportFragmentManager(), TAG);
        }
    }

    @Override
    public void startActionMode() {
        if (actionMode == null) {
            actionMode = getActivity().startActionMode(actionModeCallback);
        }
    }

    @Override
    public void updateTitle(String numberOfSelectedItems) {
        actionMode.setTitle(numberOfSelectedItems);
    }

    @Override
    public void finishAction() {
        actionMode.setTitle("");
        actionMode.finish();
    }

    @Override
    public void toggleEditButtonVisibility(boolean visible) {
        actionModeCallback.toggleEditButtonVisibility(visible);
    }
}
