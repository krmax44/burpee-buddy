package com.apps.adrcotfas.burpeebuddy.main.edit_exercises;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.apps.adrcotfas.burpeebuddy.common.bl.Events;
import com.apps.adrcotfas.burpeebuddy.db.AppDatabase;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.main.edit_exercises.dialog.AddEditExerciseDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class ExercisesFragment extends Fragment implements ExercisesViewMvc.Listener {
    private static final String TAG = "ExercisesFragment";

    private ExercisesViewMvc mViewMvc;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mViewMvc = new ExercisesViewMcvImpl(inflater, container);

        AppDatabase.getDatabase(getContext()).exerciseTypeDao().getAll().observe(
                getViewLifecycleOwner(), exerciseTypes ->
                        mViewMvc.bindExercises(exerciseTypes));

        return mViewMvc.getRootView();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        mViewMvc.registerListener(this);
    }

    @Override
    public void onDestroy() {
        if (mViewMvc != null){
            mViewMvc.unregisterListener(this);
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onVisibilityToggle(String exercise, boolean visibility) {
        AppDatabase.editVisibility(getContext(), exercise, visibility);
    }

    //TODO: to avoid clipping it may be needed to call this when the user navigates back
    // instead of every time the user rearranges a row
    @Override
    public void onExercisesRearranged(List<Exercise> exercises) {
        for (int i = 0; i < exercises.size(); ++i) {
            AppDatabase.editExerciseOrder(getContext(), exercises.get(i).name, i);
        }
    }

    @Override
    public void onExerciseAddClicked() {
        AddEditExerciseDialog.getInstance(null, false)
                .show(getActivity().getSupportFragmentManager(), TAG);
    }

    @Override
    public void onExerciseEditClicked(Exercise exercise) {
        AddEditExerciseDialog.getInstance(exercise, true)
                .show(getActivity().getSupportFragmentManager(), TAG);
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
}
