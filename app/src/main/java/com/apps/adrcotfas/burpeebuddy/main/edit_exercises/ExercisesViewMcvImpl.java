package com.apps.adrcotfas.burpeebuddy.main.edit_exercises;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ExercisesViewMcvImpl
        extends BaseObservableViewMvc<ExercisesViewMvc.Listener>
        implements ExercisesViewMvc, ExercisesAdapter.Listener {

    private RecyclerView mRecyclerView;
    private ExercisesAdapter mAdapter;
    private List<Exercise> mExercises = new ArrayList<>();

    public ExercisesViewMcvImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.fragment_edit_chips, parent, false));

        mRecyclerView = findViewById(R.id.recycler_exercises);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ExercisesAdapter(inflater, this);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        // fab.setOnClickListener(); show dialog, then call
    }

    @Override
    public void bindExercises(List<Exercise> exercises) {
        mAdapter.bindExercises(exercises);
        mExercises = exercises;
    }

    @Override
    public void onVisibilityToggle(String exercise, boolean visible) {
        for (Listener l : getListeners()) {
            l.onVisibilityToggle(exercise, visible);
        }
    }

    @Override
    public void onExerciseEdit(String exercise, Exercise newExercise) {
        for (Listener l : getListeners()) {
            l.onExerciseEdit(exercise, newExercise);
        }
    }
}
