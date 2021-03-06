package com.apps.adrcotfas.burpeebuddy.edit_exercises;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.common.recyclerview.ItemTouchHelperAdapter;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.edit_exercises.view.ExercisesItemView;
import com.apps.adrcotfas.burpeebuddy.edit_exercises.view.ExercisesItemViewImpl;
import com.apps.adrcotfas.burpeebuddy.edit_exercises.view.ExercisesViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExercisesAdapter extends RecyclerView.Adapter<ExercisesViewHolder>
        implements ExercisesItemView.Listener, ItemTouchHelperAdapter {

    private final LayoutInflater inflater;
    private final Listener listener;
    private List<Exercise> exercises = new ArrayList<>();
    private List<String> selectedItems = new ArrayList<>();

    public interface Listener {
        void onVisibilityToggle(String exercise, boolean visible);
        void onDragStarted(ExercisesViewHolder viewHolder);
        void onExercisesRearranged(List<Exercise> exercises);

        void onItemClick(int position);
        void onItemLongClick(int position);
    }

    public ExercisesAdapter(LayoutInflater inflater, Listener listener) {
        this.inflater = inflater;
        this.listener = listener;
    }

    public void bindExercises(List<Exercise> exercises) {
        this.exercises = new ArrayList<>(exercises);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExercisesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ExercisesItemView viewMvc = new ExercisesItemViewImpl(inflater, parent);
        viewMvc.registerListener(this);
        return new ExercisesViewHolder(viewMvc);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ExercisesViewHolder holder, int position) {
        final Exercise exercise = exercises.get(position);
        final boolean selected = selectedItems.contains(exercise.name);

        holder.getView().bindExercise(exercise, selected);
        holder.getView().getScrollHandle().setOnTouchListener((v, event) -> {
            listener.onDragStarted(holder);
            return false;
        });

        holder.getView().getParentView().setOnClickListener(v -> listener.onItemClick(position));
        holder.getView().getParentView().setOnLongClickListener(v -> {
            listener.onItemLongClick(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    @Override
    public void onVisibilityToggle(String exercise, boolean visible) {
        listener.onVisibilityToggle(exercise, visible);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(exercises, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(exercises, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onClearView() {
        listener.onExercisesRearranged(exercises);
    }

    public void setSelectedItems(List<String> selectedItems) {
        this.selectedItems = selectedItems;
        notifyDataSetChanged();
    }
}
