package com.apps.adrcotfas.burpeebuddy.edit_challenges.view;

import androidx.recyclerview.widget.RecyclerView;

public class ChallengesFragmentViewHolder extends RecyclerView.ViewHolder {

    private final ChallengesFragmentItemView view;

    public ChallengesFragmentViewHolder(ChallengesFragmentItemView view) {
        super(view.getRootView());
        this.view = view;
    }

    public ChallengesFragmentItemView getView() {
        return view;
    }
}
