package com.apps.adrcotfas.burpeebuddy.main;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainViewMvcImpl extends BaseObservableViewMvc<MainViewMvc.Listener>
        implements MainViewMvc {

    private final CoordinatorLayout coordinatorLayout;

    public MainViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.fragment_main, parent, false));

        coordinatorLayout = findViewById(R.id.top_coordinator);
        ExtendedFloatingActionButton startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(v -> onStartButtonClicked());
        Chip plank = findViewById(R.id.plank);
        plank.setOnClickListener(v -> onDisabledChipClicked());
        Chip reps = findViewById(R.id.reps);
        reps.setOnClickListener(v -> onDisabledChipClicked());
        Chip time = findViewById(R.id.time);
        time.setOnClickListener(v -> onDisabledChipClicked());
    }

    public void onStartButtonClicked() {
        for (Listener listener : getListeners()) {
            listener.onStartButtonClicked();
        }
    }

    public void onDisabledChipClicked() {
        for (Listener listener : getListeners()) {
            listener.onDisabledChipClicked();
        }
    }

    @Override
    public void showIntroduction() {
        if (SettingsHelper.showStartSnack() || !SettingsHelper.wakeupEnabled()) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, getContext().getString(R.string.snack_avoid_clicks),
                            Snackbar.LENGTH_INDEFINITE)
                    .setAction(getContext().getString(android.R.string.ok),
                            view -> SettingsHelper.setShowStartSnack(false));
            snackbar.show();
        }
    }
}
