package com.apps.adrcotfas.burpeebuddy.statistics.view;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StatisticsViewHolder extends RecyclerView.ViewHolder {

    private final StatisticsItemView view;

    public StatisticsViewHolder(@NonNull StatisticsItemView viewMvc) {
        super(viewMvc.getRootView());
        this.view = viewMvc;
    }

    public StatisticsItemView getViewMvc() {
        return view;
    }
}
