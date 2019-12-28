package com.apps.adrcotfas.burpeebuddy.main.edit_exercises;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.common.recyclerview.ItemTouchHelperAdapter;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.main.edit_exercises.item.ExercisesItemViewMvc;
import com.apps.adrcotfas.burpeebuddy.main.edit_exercises.item.ExercisesItemViewMvcImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExercisesAdapter extends RecyclerView.Adapter<ExercisesViewHolder>
        implements ExercisesItemViewMvc.Listener, ItemTouchHelperAdapter {

    private final LayoutInflater mInflater;
    private final Listener mListener;
    private List<Exercise> mExercises = new ArrayList<>();

    public interface Listener {
        void onVisibilityToggle(String exercise, boolean visible);
        void onExerciseEdit(String exercise, Exercise newExercise);
        void onDragStarted(ExercisesViewHolder viewHolder);
        void onExercisesRearranged(List<Exercise> exercises);
    }

    public ExercisesAdapter(LayoutInflater inflater, Listener listener) {
        mInflater = inflater;
        mListener = listener;
    }

    public void bindExercises(List<Exercise> exercises) {
        mExercises = new ArrayList<>(exercises);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExercisesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ExercisesItemViewMvc viewMvc = new ExercisesItemViewMvcImpl(mInflater, parent);
        viewMvc.registerListener(this);
        return new ExercisesViewHolder(viewMvc);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ExercisesViewHolder holder, int position) {
        holder.getViewMvc().bindExercise(mExercises.get(position));
        holder.getViewMvc().getScrollHandle().setOnTouchListener((v, event) -> {
            mListener.onDragStarted(holder);
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return mExercises.size();
    }

    @Override
    public void onVisibilityToggle(String exercise, boolean visible) {
        mListener.onVisibilityToggle(exercise, visible);
    }

    @Override
    public void onExerciseEdit(String exercise, Exercise newExercise) {
        mListener.onExerciseEdit(exercise, newExercise);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mExercises, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mExercises, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onClearView() {
        mListener.onExercisesRearranged(mExercises);
    }
}
