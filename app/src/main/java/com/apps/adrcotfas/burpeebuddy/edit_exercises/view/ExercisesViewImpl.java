package com.apps.adrcotfas.burpeebuddy.edit_exercises.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.ActionModeHelper;
import com.apps.adrcotfas.burpeebuddy.common.recyclerview.SimpleItemTouchHelperCallback;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableView;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.edit_exercises.ExercisesAdapter;

import java.util.ArrayList;
import java.util.List;

public class ExercisesViewImpl
        extends BaseObservableView<ExercisesView.Listener>
        implements ExercisesView, ExercisesAdapter.Listener, ActionModeHelper.Listener {

    private ActionModeHelper<String> actionModeHelper;
    private List<Exercise> exercises;
    private RecyclerView recyclerView;
    private ExercisesAdapter adapter;
    private ItemTouchHelper itemTouchHelper;
    private LinearLayout emptyState;

    public ExercisesViewImpl(LayoutInflater inflater, ViewGroup parent) {
        actionModeHelper = new ActionModeHelper<>(this, true);

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
        this.exercises = exercises;
    }

    @Override
    public void onVisibilityToggle(String exercise, boolean visible) {
        for (Listener l : getListeners()) {
            l.onVisibilityToggle(exercise, visible);
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

    @Override
    public void onItemClick(int position) {
        actionModeHelper.onItemClick(exercises.get(position).name);
        adapter.setSelectedItems(actionModeHelper.getSelectedEntries());
    }

    @Override
    public void onItemLongClick(int position) {
        actionModeHelper.onItemLongClick(exercises.get(position).name);
        adapter.setSelectedItems(actionModeHelper.getSelectedEntries());
    }

    @Override
    public void actionSelectAllItems() {
        actionModeHelper.toggleEditButtonVisibility(false);

        List<String> ids = new ArrayList<>(exercises.size());
        for (Exercise e : exercises) {
            ids.add(e.name);
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
        Exercise selectedExercise = null;
        for (Exercise e : exercises) {
            if (e.name.equals(actionModeHelper.getSelectedEntries().get(0))) {
                selectedExercise = e;
                break;
            }
        }
        for (Listener l : getListeners()) {
            l.onEditSelected(selectedExercise);
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
