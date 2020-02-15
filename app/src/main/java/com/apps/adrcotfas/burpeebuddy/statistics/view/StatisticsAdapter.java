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
    }

    private final LayoutInflater mInflater;
    private List<Workout> workouts = new ArrayList<>();
    private List<Integer> selectedItems = new ArrayList<>();

    public StatisticsAdapter(LayoutInflater inflater) {
        mInflater = inflater;
    }

    public void bindWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
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
        final boolean selected = selectedItems.contains(workouts.get(position).id);
        holder.getViewMvc().bindWorkout(workouts.get(position), selected);
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
