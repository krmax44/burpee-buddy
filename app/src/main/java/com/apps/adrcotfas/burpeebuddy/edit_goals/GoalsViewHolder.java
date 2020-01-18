package com.apps.adrcotfas.burpeebuddy.edit_goals;

import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.edit_goals.view.GoalsItemViewMvc;

public class GoalsViewHolder extends RecyclerView.ViewHolder {

    private final GoalsItemViewMvc mViewMvc;

    public GoalsViewHolder(GoalsItemViewMvc viewMvc) {
        super(viewMvc.getRootView());
        this.mViewMvc = viewMvc;
    }

    public GoalsItemViewMvc getViewMvc() {
        return mViewMvc;
    }
}
