package com.apps.adrcotfas.burpeebuddy.main.edit_exercises;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.apps.adrcotfas.burpeebuddy.db.AppDatabase;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;

import java.util.List;

public class ExercisesFragment extends Fragment implements ExercisesViewMvc.Listener {
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
        mViewMvc.registerListener(this);
    }

    @Override
    public void onDestroy() {
        if (mViewMvc != null){
            mViewMvc.unregisterListener(this);
        }
        super.onDestroy();
    }

    @Override
    public void onAddExercise(Exercise exercise) {
        // add it to the database
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
    public void onExerciseEdit(String exercise, Exercise newExercise) {
        // edit exercise in database
    }
}
