package com.apps.adrcotfas.burpeebuddy.edit_exercises.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.recyclerview.SimpleItemTouchHelperCallback;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableView;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.edit_exercises.ExercisesAdapter;

import java.util.List;

public class ExercisesViewImpl
        extends BaseObservableView<ExercisesView.Listener>
        implements ExercisesView, ExercisesAdapter.Listener {

    private RecyclerView recyclerView;
    private ExercisesAdapter adapter;
    private ItemTouchHelper itemTouchHelper;

    private LinearLayout emptyState;

    public ExercisesViewImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.fragment_recycler, parent, false));

        emptyState = findViewById(R.id.empty_state);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExercisesAdapter(inflater, this);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void bindExercises(List<Exercise> exercises) {
        recyclerView.setVisibility(exercises.isEmpty() ? View.GONE : View.VISIBLE);
        emptyState.setVisibility(exercises.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.bindExercises(exercises);
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
        itemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onExercisesRearranged(List<Exercise> exercises) {
        for (Listener l : getListeners()) {
            l.onExercisesRearranged(exercises);
        }
    }
}
