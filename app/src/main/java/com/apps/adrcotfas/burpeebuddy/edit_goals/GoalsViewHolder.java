package com.apps.adrcotfas.burpeebuddy.edit_goals;

import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.edit_goals.view.GoalsItemView;

public class GoalsViewHolder extends RecyclerView.ViewHolder {

    private final GoalsItemView mViewMvc;

    public GoalsViewHolder(GoalsItemView viewMvc) {
        super(viewMvc.getRootView());
        this.mViewMvc = viewMvc;
    }

    public GoalsItemView getViewMvc() {
        return mViewMvc;
    }
}
