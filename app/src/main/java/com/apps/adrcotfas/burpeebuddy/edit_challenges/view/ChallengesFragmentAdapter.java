package com.apps.adrcotfas.burpeebuddy.edit_challenges.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.actionMode.ActionModeRecyclerViewAdapter;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;

import java.util.ArrayList;
import java.util.List;

public class ChallengesFragmentAdapter
        extends ActionModeRecyclerViewAdapter<List<Pair<Challenge, Integer>>, ChallengesFragmentViewHolder>
        implements ChallengesFragmentItemView.Listener {

    private final LayoutInflater inflater;
    private List<Pair<Challenge, Integer>> challenges = new ArrayList<>();

    public ChallengesFragmentAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public void bindItems(List<Pair<Challenge, Integer>> items) {
        this.challenges = new ArrayList<>(items);
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
        final boolean selected = getSelectedItems().contains(challenges.get(position).first.id);
        holder.getView().bindChallenge(
                challenges.get(position), selected);
    }

    @Override
    public int getItemCount() {
        return challenges.size();
    }
}
