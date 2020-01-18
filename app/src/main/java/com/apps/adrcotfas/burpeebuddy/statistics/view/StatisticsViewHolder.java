package com.apps.adrcotfas.burpeebuddy.statistics.view;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StatisticsViewHolder extends RecyclerView.ViewHolder {

    private final StatisticsItemViewMvc mViewMvc;

    public StatisticsViewHolder(@NonNull StatisticsItemViewMvc viewMvc) {
        super(viewMvc.getRootView());
        this.mViewMvc = viewMvc;
    }

    public StatisticsItemViewMvc getViewMvc() {
        return mViewMvc;
    }
}
