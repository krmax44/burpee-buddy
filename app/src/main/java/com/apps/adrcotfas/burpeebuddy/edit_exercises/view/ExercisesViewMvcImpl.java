package com.apps.adrcotfas.burpeebuddy.edit_exercises.view;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.recyclerview.SimpleItemTouchHelperCallback;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.edit_exercises.ExercisesAdapter;

import java.util.List;

public class ExercisesViewMvcImpl
        extends BaseObservableViewMvc<ExercisesViewMvc.Listener>
        implements ExercisesViewMvc, ExercisesAdapter.Listener {

    private RecyclerView mRecyclerView;
    private ExercisesAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;

    public ExercisesViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.fragment_recycler, parent, false));

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ExercisesAdapter(inflater, this);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void bindExercises(List<Exercise> exercises) {
        mAdapter.bindExercises(exercises);
    }

    @Override
    public void onVisibilityToggle(String exercise, boolean visible) {
        for (Listener l : getListeners()) {
            l.onVisibilityToggle(exercise, visible);
        }
    }

    @Override
    public void onExerciseEditClicked(Exercise exercise) {
        for (Listener l : getListeners()) {
            l.onExerciseEditClicked(exercise);
        }
    }

    @Override
    public void onDragStarted(ExercisesViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onExercisesRearranged(List<Exercise> exercises) {
        for (Listener l : getListeners()) {
            l.onExercisesRearranged(exercises);
        }
    }
}
