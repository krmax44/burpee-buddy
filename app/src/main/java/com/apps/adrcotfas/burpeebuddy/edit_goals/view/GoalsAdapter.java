package com.apps.adrcotfas.burpeebuddy.edit_goals.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.db.goal.Goal;
import com.apps.adrcotfas.burpeebuddy.edit_goals.GoalsViewHolder;

import java.util.ArrayList;
import java.util.List;

class GoalsAdapter extends RecyclerView.Adapter<GoalsViewHolder>
        implements GoalsItemView.Listener {

    private final LayoutInflater inflater;
    private List<Goal> goals = new ArrayList<>();
    private List<Integer> selectedItems = new ArrayList<>();

    public GoalsAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public void bindGoals(List<Goal> goals) {
        this.goals = goals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GoalsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GoalsItemView viewMvc = new GoalsItemViewImpl(inflater, parent);
        viewMvc.registerListener(this);
        return new GoalsViewHolder(viewMvc);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalsViewHolder holder, int position) {
        final Goal goal = goals.get(position);
        final boolean selected = selectedItems.contains(goal.id);
        holder.getViewMvc().bindGoal(goals.get(position), selected);
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    public void setSelectedItems(List<Integer> selectedItems) {
        this.selectedItems = selectedItems;
        notifyDataSetChanged();
    }
}
