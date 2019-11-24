package com.apps.adrcotfas.burpeebuddy.main;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.BaseViewMvc;

import java.util.ArrayList;
import java.util.List;

public class MainViewMvcImpl extends BaseViewMvc implements MainViewMvc {

    private final List<Listener> mListeners = new ArrayList<>();

    public MainViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.activity_main, parent, false));

        Button startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(v -> onStartButtonClicked());
    }

    @Override
    public void registerListener(Listener listener) {
        mListeners.add(listener);
    }

    @Override
    public void unregisterListener(Listener listener) {
        mListeners.remove(listener);
    }

    public void onStartButtonClicked() {
        for(Listener listener : mListeners) {
            listener.onStartButtonClicked();
        }
    }
}
