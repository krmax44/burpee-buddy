package com.apps.adrcotfas.burpeebuddy.edit_challenges.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;

import java.util.ArrayList;
import java.util.List;

public class ChallengesFragmentAdapter extends RecyclerView.Adapter<ChallengesFragmentViewHolder>
    implements ChallengesFragmentItemView.Listener {

    private final LayoutInflater inflater;
    private final Listener listener;
    private List<Challenge> challenges = new ArrayList<>();

    public interface Listener {
    }

    public ChallengesFragmentAdapter(LayoutInflater inflater, Listener listener) {
        this.inflater = inflater;
        this.listener = listener;
    }

    public void bindChallenges(List<Challenge> challenges) {
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
        holder.getView().bindChallenge(challenges.get(position));
    }

    @Override
    public int getItemCount() {
        return challenges.size();
    }
}
