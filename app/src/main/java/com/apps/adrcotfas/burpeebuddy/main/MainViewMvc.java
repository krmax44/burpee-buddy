package com.apps.adrcotfas.burpeebuddy.main;

import com.apps.adrcotfas.burpeebuddy.common.ObservableViewMvc;

interface MainViewMvc extends ObservableViewMvc<MainViewMvc.Listener> {

    public interface Listener {
        void onStartButtonClicked();
    }
}
