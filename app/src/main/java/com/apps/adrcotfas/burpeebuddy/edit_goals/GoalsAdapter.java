package com.apps.adrcotfas.burpeebuddy.edit_goals;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;
import com.apps.adrcotfas.burpeebuddy.edit_goals.items.GoalsItemViewMvc;
import com.apps.adrcotfas.burpeebuddy.edit_goals.items.GoalsItemViewMvcImpl;

import java.util.ArrayList;
import java.util.List;

class GoalsAdapter extends RecyclerView.Adapter<GoalsViewHolder>
        implements GoalsItemViewMvc.Listener {

    private final LayoutInflater mInflater;
    private final Listener mListener;
    private List<Goal> mGoals = new ArrayList<>();

    public interface Listener {
        void onGoalEditClicked(Goal goal);
    }

    public GoalsAdapter(LayoutInflater inflater, Listener listener) {
        mInflater = inflater;
        mListener = listener;
    }

    public void bindGoals(List<Goal> goals) {
        mGoals = goals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GoalsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GoalsItemViewMvc viewMvc = new GoalsItemViewMvcImpl(mInflater, parent);
        viewMvc.registerListener(this);
        return new GoalsViewHolder(viewMvc);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalsViewHolder holder, int position) {
        holder.getViewMvc().bindGoal(mGoals.get(position));
    }

    @Override
    public int getItemCount() {
        return mGoals.size();
    }

    @Override
    public void onGoalEditClicked(Goal goal) {
        mListener.onGoalEditClicked(goal);
    }
}
