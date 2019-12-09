package com.apps.adrcotfas.burpeebuddy.main;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableViewMvc;

public interface MainViewMvc extends ObservableViewMvc<MainViewMvc.Listener> {

    public interface Listener {
        void onStartButtonClicked();
        void onDisabledChipClicked();
    }
}
