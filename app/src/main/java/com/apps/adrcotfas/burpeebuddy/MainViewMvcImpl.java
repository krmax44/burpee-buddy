package com.apps.adrcotfas.burpeebuddy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainViewMvcImpl implements MainViewMvc {

    private final View mRootView;
    private Button mStartButton;

    private final List<Listener> mListeners = new ArrayList<>();

    public MainViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        mRootView = inflater.inflate(R.layout.activity_main, parent, false);

        mStartButton = findViewById(R.id.start_button);
        mStartButton.setOnClickListener(v -> onStartButtonClicked());
    }

    @Override
    public View getRootView() {
        return mRootView;
    }

    private <T extends View> T findViewById(int id) {
        return getRootView().findViewById(id);
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
