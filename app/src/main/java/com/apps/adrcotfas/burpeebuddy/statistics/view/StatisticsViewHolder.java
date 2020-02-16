package com.apps.adrcotfas.burpeebuddy.statistics.view;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StatisticsViewHolder extends RecyclerView.ViewHolder {

    private final StatisticsItemView mViewMvc;

    public StatisticsViewHolder(@NonNull StatisticsItemView viewMvc) {
        super(viewMvc.getRootView());
        this.mViewMvc = viewMvc;
    }

    public StatisticsItemView getViewMvc() {
        return mViewMvc;
    }
}
