package com.apps.adrcotfas.burpeebuddy.main.edit_exercises;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.common.ItemTouchHelperAdapter;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.main.edit_exercises.item.ExercisesItemViewMvc;
import com.apps.adrcotfas.burpeebuddy.main.edit_exercises.item.ExercisesItemViewMvcImpl;

import java.util.ArrayList;
import java.util.List;

public class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ExercisesViewHolder>
        implements ExercisesItemViewMvc.Listener, ItemTouchHelperAdapter {

    static class ExercisesViewHolder extends RecyclerView.ViewHolder {
        private final ExercisesItemViewMvc mViewMvc;

        public ExercisesViewHolder(ExercisesItemViewMvc viewMvc) {
            super(viewMvc.getRootView());
            mViewMvc = viewMvc;
        }
    }

    private final LayoutInflater mInflater;
    private final Listener mListener;
    private List<Exercise> mExercises = new ArrayList<>();

    public interface Listener {
        void onVisibilityToggle(String exercise, boolean visible);
        void onExerciseEdit(String exercise, Exercise newExercise);
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

    @Override
    public void onBindViewHolder(@NonNull ExercisesViewHolder holder, int position) {
        holder.mViewMvc.bindExercise(mExercises.get(position));
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

    }

    @Override
    public void onClearView() {

    }
}
