package com.apps.adrcotfas.burpeebuddy.edit_goals;

import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.edit_goals.items.GoalsItemViewMvc;

class GoalsViewHolder extends RecyclerView.ViewHolder {

    private final GoalsItemViewMvc mViewMvc;

    public GoalsViewHolder(GoalsItemViewMvc mViewMvc) {
        super(mViewMvc.getRootView());
        this.mViewMvc = mViewMvc;
    }

    public GoalsItemViewMvc getViewMvc() {
        return mViewMvc;
    }
}
