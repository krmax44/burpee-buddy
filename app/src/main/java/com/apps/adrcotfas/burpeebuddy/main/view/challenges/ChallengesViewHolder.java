package com.apps.adrcotfas.burpeebuddy.main.view.challenges;

import androidx.recyclerview.widget.RecyclerView;

public class ChallengesViewHolder extends RecyclerView.ViewHolder {

    private final ChallengesItemView mViewMvc;

    public ChallengesViewHolder(ChallengesItemView viewMvc) {
        super(viewMvc.getRootView());
        mViewMvc = viewMvc;
    }

    public ChallengesItemView getViewMvc() {
        return mViewMvc;
    }
}
