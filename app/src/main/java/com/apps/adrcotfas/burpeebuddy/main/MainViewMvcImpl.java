package com.apps.adrcotfas.burpeebuddy.main;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.exercisetype.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.exercisetype.ExerciseTypeFactory;
import com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainViewMvcImpl extends BaseObservableViewMvc<MainViewMvc.Listener>
        implements MainViewMvc {

    private static final String TAG = "MainViewMvcImpl";

    private final CoordinatorLayout mCoordinatorLayout;
    private final ChipGroup mExerciseTypeChipGroup;
    private List<ExerciseType> mExerciseTypes = new ArrayList<>();

    public MainViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.fragment_main, parent, false));

        mExerciseTypeChipGroup = findViewById(R.id.exercise_type);

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

    @Override
    public void updateExerciseTypes(List<ExerciseType> exerciseTypes) {
        mExerciseTypes = exerciseTypes;
        mExerciseTypeChipGroup.removeAllViews();
        for (ExerciseType w : mExerciseTypes) {
            Chip c = new Chip(getContext());
            c.setText(w.getName());

            //TODO: adapt this later
            if (w.getName().equals(ExerciseTypeFactory.BURPEES) ||
                    w.getName().equals(ExerciseTypeFactory.PUSHUPS)) {
                ChipDrawable d = ChipDrawable.createFromAttributes(
                        getContext(), null, 0,
                        R.style.Widget_MaterialComponents_Chip_Choice);
                c.setChipDrawable(d);
            } else {
                c.setOnClickListener(v -> onDisabledChipClicked());
            }
            mExerciseTypeChipGroup.addView(c);
        }

        mExerciseTypeChipGroup.setSelectionRequired(true);
        mExerciseTypeChipGroup.setSingleSelection(true);
        mExerciseTypeChipGroup.check(mExerciseTypeChipGroup.getChildAt(0).getId());
    }

    /**
     * Returns the currently selected exercise type
     */
    public ExerciseType getCurrentExerciseType() {
        String name = "";
        for (int i = 0; i < mExerciseTypeChipGroup.getChildCount(); ++i) {
            Chip crt = (Chip)mExerciseTypeChipGroup.getChildAt(i);
            if (crt.getId() == mExerciseTypeChipGroup.getCheckedChipId()) {
                name = crt.getText().toString();
            }
        }

        if (name.isEmpty()) {
            Log.wtf(TAG, "No exercise was selected.");
            return mExerciseTypes.get(0);
        }

        for (ExerciseType e : mExerciseTypes) {
            if (e.getName().equals(name)) {
                return e;
            }
        }

        Log.wtf(TAG, "The selected exercise is not part of the internal exercises.");
        return mExerciseTypes.get(0);
    }
}
