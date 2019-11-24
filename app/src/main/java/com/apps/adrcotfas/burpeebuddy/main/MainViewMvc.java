package com.apps.adrcotfas.burpeebuddy.main;

import com.apps.adrcotfas.burpeebuddy.common.ObservableViewMvc;

public interface MainViewMvc extends ObservableViewMvc<MainViewMvc.Listener> {

    public interface Listener {
        void onStartButtonClicked();
    }
}
