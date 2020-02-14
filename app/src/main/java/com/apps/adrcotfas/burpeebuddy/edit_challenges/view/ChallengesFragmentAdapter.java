package com.apps.adrcotfas.burpeebuddy.edit_challenges.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;

import java.util.ArrayList;
import java.util.List;

public class ChallengesFragmentAdapter extends RecyclerView.Adapter<ChallengesFragmentViewHolder>
    implements ChallengesFragmentItemView.Listener {

    private final LayoutInflater inflater;
    private List<Pair<Challenge, Integer>> challenges = new ArrayList<>();
    private List<Integer> selectedItems = new ArrayList<>();

    public ChallengesFragmentAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public void bindChallenges(List<Pair<Challenge, Integer>> challenges) {
        this.challenges = new ArrayList<>(challenges);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChallengesFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ChallengesFragmentItemView view = new ChallengesFragmentItemViewImpl(inflater, parent);
        view.registerListener(this);
        return new ChallengesFragmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengesFragmentViewHolder holder, int position) {
        final boolean selected = selectedItems.contains(challenges.get(position).first.id);
        holder.getView().bindChallenge(
                challenges.get(position), selected);
    }

    @Override
    public int getItemCount() {
        return challenges.size();
    }

    public void setSelectedItems(List<Integer> selectedItems) {
        this.selectedItems = selectedItems;
        notifyDataSetChanged();
    }
}
