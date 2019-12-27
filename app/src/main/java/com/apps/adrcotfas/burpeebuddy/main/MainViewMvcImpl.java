package com.apps.adrcotfas.burpeebuddy.main;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.MutableLiveData;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;
import com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.apps.adrcotfas.burpeebuddy.db.goals.GoalToString.goalToString;

public class MainViewMvcImpl extends BaseObservableViewMvc<MainViewMvc.Listener>
        implements MainViewMvc {

    private static final String TAG = "MainViewMvcImpl";

    private final CoordinatorLayout mCoordinatorLayout;
    private final ChipGroup mExerciseTypeChipGroup;
    private final ChipGroup mGoalsChipGroup;

    private List<Exercise> mExercises = new ArrayList<>();
    private List<Goal> mGoals = new ArrayList<>();

    public MutableLiveData<Exercise> getExercise() {
        return mExercise;
    }

    private MutableLiveData<Exercise> mExercise = new MutableLiveData<>();

    public MainViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.fragment_main, parent, false));

        mExerciseTypeChipGroup = findViewById(R.id.exercise_type);
        mExerciseTypeChipGroup.setSelectionRequired(true);
        mExerciseTypeChipGroup.setSingleSelection(true);
        mExerciseTypeChipGroup.setOnCheckedChangeListener((group, checkedId) -> onExerciseSelected());

        mGoalsChipGroup = findViewById(R.id.goal_type);
        mGoalsChipGroup.setSelectionRequired(true);
        mGoalsChipGroup.setSingleSelection(true);

        mCoordinatorLayout = findViewById(R.id.top_coordinator);

        FrameLayout editExercises = findViewById(R.id.edit_exercises);
        editExercises.setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onEditExercisesClicked();
            }
        });

        MaterialButton startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(v -> onStartButtonClicked());
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
    public void updateExerciseTypes(List<Exercise> exercises) {
        mExercises = exercises;
        mExerciseTypeChipGroup.removeAllViews();
        for (Exercise w : mExercises) {
            Chip c = new Chip(getContext());
            c.setText(w.name);

            ChipDrawable d = ChipDrawable.createFromAttributes(
                    getContext(), null, 0,
                    R.style.Widget_MaterialComponents_Chip_Choice);
            c.setChipDrawable(d);
             //   c.setOnClickListener(v -> onDisabledChipClicked());
            mExerciseTypeChipGroup.addView(c);
        }
        if (!mExercises.isEmpty()) {
            mExerciseTypeChipGroup.check(mExerciseTypeChipGroup.getChildAt(0).getId());
        }
    }

    @Override
    public void updateGoals(List<Goal> goals) {
        mGoals = goals;
        mGoalsChipGroup.removeAllViews();
        for (Goal g : mGoals) {
            Chip c = new Chip(getContext());
            c.setText(goalToString(g));
            ChipDrawable d = ChipDrawable.createFromAttributes(
                    getContext(), null, 0,
                    R.style.Widget_MaterialComponents_Chip_Choice);
            c.setChipDrawable(d);
            mGoalsChipGroup.addView(c);
        }
        if (!goals.isEmpty()) {
            mGoalsChipGroup.check(mGoalsChipGroup.getChildAt(0).getId());
        }
    }

    private void onExerciseSelected() {
        String name = "";
        for (int i = 0; i < mExerciseTypeChipGroup.getChildCount(); ++i) {
            Chip crt = (Chip)mExerciseTypeChipGroup.getChildAt(i);
            if (crt.getId() == mExerciseTypeChipGroup.getCheckedChipId()) {
                name = crt.getText().toString();
                break;
            }
        }

        if (name.isEmpty()) {
            Timber.tag(TAG).wtf("No exercise was selected.");
            mExercise.setValue(mExercises.get(0));
            return;
        }

        for (Exercise e : mExercises) {
            if (e.name.equals(name)) {
                mExercise.setValue(e);
                return;
            }
        }

        Timber.tag(TAG).wtf( "The selected exercise is not part of the internal exercises.");
        mExercise.setValue(mExercises.get(0));
    }

    @Override
    public Goal getGoal() {
        int id = -1;
        for (int i = 0; i < mGoalsChipGroup.getChildCount(); ++i) {
            Chip crt = (Chip)mGoalsChipGroup.getChildAt(i);
            if (crt.getId() == mGoalsChipGroup.getCheckedChipId()) {
                id = mGoals.get(i).getId();
                break;
            }
        }

        if (id != -1) {
            for (Goal g : mGoals) {
                if (g.getId() == id) {
                    return g;
                }
            }
        }

        Timber.tag(TAG).wtf( "Could not find the selected goal in the internal list.");
        return null;
    }
}
