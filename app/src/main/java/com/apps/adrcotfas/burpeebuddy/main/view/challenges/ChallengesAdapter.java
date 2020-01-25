package com.apps.adrcotfas.burpeebuddy.main.view.challenges;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;

import java.util.ArrayList;
import java.util.List;

public class ChallengesAdapter extends RecyclerView.Adapter<ChallengesViewHolder>
    implements ChallengesItemView.Listener {

    private final LayoutInflater mInflater;
    private List<Challenge> challenges = new ArrayList<>();
    private List<Integer> mProgress = new ArrayList<>();

    public ChallengesAdapter(LayoutInflater inflater) {
        mInflater = inflater;
    }

    public void bindChallenges(List<Challenge> challenges, List<Integer> progress) {
        this.challenges = challenges;
        this.mProgress = progress;

        notifyDataSetChanged();
    }

    @Override
    public ChallengesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ChallengesItemView viewMvc = new ChallengesItemViewImpl(mInflater, parent);
        viewMvc.registerListener(this);
        return new ChallengesViewHolder(viewMvc);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengesViewHolder holder, int position) {
        holder.getViewMvc().bindChallenge(challenges.get(position), mProgress.get(position));
    }

    @Override
    public int getItemCount() {
        return challenges.size();
    }
}
