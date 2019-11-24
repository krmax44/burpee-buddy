package com.apps.adrcotfas.burpeebuddy;

import android.view.View;

interface MainViewMvc {

    public interface Listener {
        void onStartButtonClicked();
    }

    View getRootView();

    void registerListener(Listener listener);

    void unregisterListener(Listener listener);
}
