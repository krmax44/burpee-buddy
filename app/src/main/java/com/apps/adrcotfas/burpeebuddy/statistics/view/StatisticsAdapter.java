package com.apps.adrcotfas.burpeebuddy.statistics.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.db.workout.Workout;

import java.util.ArrayList;
import java.util.List;

public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsViewHolder>
    implements StatisticsItemViewMvc.Listener {

    public interface Listener {
        void onWorkoutLongPress(int id);
    }

    private final LayoutInflater mInflater;
    private final Listener mListener;
    private List<Workout> mWorkouts = new ArrayList<>();

    public StatisticsAdapter(LayoutInflater inflater, Listener listener) {
        mInflater = inflater;
        mListener = listener;
    }

    public void bindWorkouts(List<Workout> workouts) {
        mWorkouts = workouts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StatisticsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        StatisticsItemViewMvc viewMvc = new StatisticsItemViewMvcImpl(mInflater, parent);
        viewMvc.registerListener(this);
        return new StatisticsViewHolder(viewMvc);
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticsViewHolder holder, int position) {
        holder.getViewMvc().bindWorkout(mWorkouts.get(position));
    }

    @Override
    public int getItemCount() {
        return mWorkouts.size();
    }

    @Override
    public void onWorkoutLongPress(int id) {
        mListener.onWorkoutLongPress(id);
    }
}
