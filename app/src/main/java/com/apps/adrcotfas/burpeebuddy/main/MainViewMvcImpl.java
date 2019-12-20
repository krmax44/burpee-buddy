package com.apps.adrcotfas.burpeebuddy.main;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.WorkoutType;
import com.apps.adrcotfas.burpeebuddy.db.WorkoutTypeFactory;
import com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainViewMvcImpl extends BaseObservableViewMvc<MainViewMvc.Listener>
        implements MainViewMvc {

    private final CoordinatorLayout mCoordinatorLayout;

    public MainViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.fragment_main, parent, false));

        ChipGroup workoutChipGroup = findViewById(R.id.workout_type);
        fillWorkoutChipGroup(workoutChipGroup);

        mCoordinatorLayout = findViewById(R.id.top_coordinator);
        ExtendedFloatingActionButton startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(v -> onStartButtonClicked());
        Chip reps = findViewById(R.id.reps);
        reps.setOnClickListener(v -> onDisabledChipClicked());
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
        if (SettingsHelper.showStartSnack() && !SettingsHelper.wakeupEnabled()) {
            Snackbar snackbar = Snackbar
                    .make(mCoordinatorLayout, getContext().getString(R.string.snack_avoid_clicks),
                            Snackbar.LENGTH_INDEFINITE)
                    .setAction(getContext().getString(android.R.string.ok),
                            view -> SettingsHelper.setShowStartSnack(false));
            snackbar.show();
        }
    }

    private void fillWorkoutChipGroup(ChipGroup group) {
        for (WorkoutType w : WorkoutTypeFactory.getDefaultWorkouts()) {
            Chip c = new Chip(getContext());
            c.setText(w.getName());

            //TODO: adapt this later
            if (w.getName().equals(WorkoutTypeFactory.BURPEES) ||
                    w.getName().equals(WorkoutTypeFactory.PUSHUPS)) {
                ChipDrawable d = ChipDrawable.createFromAttributes(
                        getContext(), null, 0,
                        R.style.Widget_MaterialComponents_Chip_Choice);
                c.setChipDrawable(d);
            } else {
                c.setOnClickListener(v -> onDisabledChipClicked());
            }
            group.addView(c);
        }
        group.setSingleSelection(true);
        group.check(group.getChildAt(0).getId());
    }
}
