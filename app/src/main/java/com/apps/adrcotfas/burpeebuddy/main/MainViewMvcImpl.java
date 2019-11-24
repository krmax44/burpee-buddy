package com.apps.adrcotfas.burpeebuddy.main;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.BaseObservableViewMvc;

public class MainViewMvcImpl extends BaseObservableViewMvc<MainViewMvc.Listener>
        implements MainViewMvc {

    public MainViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.activity_main, parent, false));

        Button startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(v -> onStartButtonClicked());
    }

    public void onStartButtonClicked() {
        for(Listener listener : getListeners()) {
            listener.onStartButtonClicked();
        }
    }
}
