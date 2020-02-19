package com.apps.adrcotfas.burpeebuddy.edit_exercises;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.ActionModeHelper;
import com.apps.adrcotfas.burpeebuddy.common.Events;
import com.apps.adrcotfas.burpeebuddy.db.AppDatabase;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.edit_exercises.dialog.AddEditExerciseDialog;
import com.apps.adrcotfas.burpeebuddy.edit_exercises.view.ExercisesView;
import com.apps.adrcotfas.burpeebuddy.edit_exercises.view.ExercisesViewImpl;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class ExercisesFragment extends Fragment implements ExercisesView.Listener {
    private static final String TAG = "ExercisesFragment";

    private ExercisesView view;
    private List<Exercise> rearrangedExercises = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        view = new ExercisesViewImpl(inflater, container);

        AppDatabase.getDatabase(getContext()).exerciseDao().getAll().observe(
                getViewLifecycleOwner(), exercises -> {
                    view.bindExercises(exercises);
                    rearrangedExercises = exercises;
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
        view.destroyActionMode();
    }

    @Override
    public void onDestroy() {
        if (view != null){
            view.unregisterListener(this);
        }

        for (int i = 0; i < rearrangedExercises.size(); ++i) {
            AppDatabase.editExerciseOrder(getContext(), rearrangedExercises.get(i).name, i);
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
            AddEditExerciseDialog.getInstance(null, false)
                    .show(getActivity().getSupportFragmentManager(), TAG);
            return true;
        }
        return false;
    }

    @Override
    public void onVisibilityToggle(String exercise, boolean visibility) {
        AppDatabase.editVisibility(getContext(), exercise, visibility);
    }

    @Override
    public void onExercisesRearranged(List<Exercise> exercises) {
        rearrangedExercises = exercises;
    }

    @Subscribe
    public void onMessageEvent(Events.EditExercise event) {
        AppDatabase.editExercise(getContext(), event.exerciseToEdit, event.exercise);
    }

    @Subscribe
    public void onMessageEvent(Events.AddExercise event) {
        AppDatabase.addExercise(getContext(), event.exercise);
    }

    @Subscribe
    public void onMessageEvent(Events.DeleteExercise event) {
        AppDatabase.deleteExercise(getContext(), event.name);
    }

    @Override
    public void startActionMode(ActionModeHelper actionModeHelper) {
        actionModeHelper.setActionMode(getActivity().startActionMode(actionModeHelper));
    }

    @Override
    public void onDeleteSelected(List<String> names) {
        new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Delete workouts?")
                .setMessage("This will delete the selected workouts")
                .setPositiveButton(android.R.string.ok, (dialog, i) -> {
                    for (String name : names) {
                        AppDatabase.deleteExercise(getContext(), name);
                    }
                    view.destroyActionMode();
                })
                .setNegativeButton(android.R.string.cancel, (dialog, i) -> dialog.cancel())
                .show();
    }

    @Override
    public void onEditSelected(Exercise exercise) {
        AddEditExerciseDialog.getInstance(exercise, true)
                .show(getActivity().getSupportFragmentManager(), TAG);
        view.destroyActionMode();
    }
}
