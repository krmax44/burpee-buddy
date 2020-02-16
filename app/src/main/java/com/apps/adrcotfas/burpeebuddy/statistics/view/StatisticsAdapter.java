package com.apps.adrcotfas.burpeebuddy.statistics.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.db.workout.Workout;

import java.util.ArrayList;
import java.util.List;

public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsViewHolder>
    implements StatisticsItemView.Listener {

    public interface Listener {
    }

    private final LayoutInflater inflater;
    private List<Workout> workouts = new ArrayList<>();
    private List<Integer> selectedItems = new ArrayList<>();

    public StatisticsAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public void bindWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StatisticsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        StatisticsItemView view = new StatisticsItemViewImpl(inflater, parent);
        view.registerListener(this);
        return new StatisticsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticsViewHolder holder, int position) {
        final Workout workout = workouts.get(position);
        final boolean selected = selectedItems.contains(workout.id);
        holder.getViewMvc().bindWorkout(workout, selected);
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    public void setSelectedItems(List<Integer> selectedItems) {
        this.selectedItems = selectedItems;
        notifyDataSetChanged();
    }
}
