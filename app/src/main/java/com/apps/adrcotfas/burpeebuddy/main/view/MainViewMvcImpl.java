package com.apps.adrcotfas.burpeebuddy.main.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.MutableLiveData;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalType;
import com.apps.adrcotfas.burpeebuddy.edit_goals.view.GoalConfigurator;
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
        implements MainViewMvc, GoalConfigurator.Listener {

    private static final String TAG = "MainViewMvcImpl";

    private final CoordinatorLayout mCoordinatorLayout;
    private final ChipGroup mExerciseTypeChipGroup;
    private final ChipGroup mGoalsChipGroup;
    private final MaterialButton mStartButton;

    private List<Exercise> mExercises = new ArrayList<>();
    private List<Goal> mFavoriteGoals = new ArrayList<>();
    private MutableLiveData<Exercise> mExercise = new MutableLiveData<>();
    private final ImageView mFavoriteGoalButton;
    private GoalConfigurator mGoalConfigurator;

    public MainViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        final View view = inflater.inflate(R.layout.fragment_main, parent, false);
        setRootView(view);
        mGoalConfigurator = new GoalConfigurator(view, SettingsHelper.getGoal(), this, getContext());

        mExerciseTypeChipGroup = findViewById(R.id.exercise_type);
        mStartButton = findViewById(R.id.start_button);
        mExerciseTypeChipGroup.setSelectionRequired(true);
        mExerciseTypeChipGroup.setSingleSelection(true);
        mExerciseTypeChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            onExerciseSelected();
            updateGoalType();
        });

        mGoalsChipGroup = findViewById(R.id.goal_type);
        mGoalsChipGroup.setSelectionRequired(true);
        mGoalsChipGroup.setSingleSelection(true);
        mGoalsChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            for (Listener listener : MainViewMvcImpl.this.getListeners()) {
                if (SettingsHelper.isGoalFavoritesVisible()) {
                    listener.onGoalSelectionChanged(checkedId != View.NO_ID);
                }
            }
        });

        mCoordinatorLayout = findViewById(R.id.top_coordinator);

        FrameLayout editExercises = findViewById(R.id.edit_exercises);
        editExercises.setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onEditExercisesClicked();
            }
        });

        FrameLayout editGoals = findViewById(R.id.button_edit_goals);
        editGoals.setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onEditGoalsClicked();
            }
        });

        mStartButton.setOnClickListener(v -> onStartButtonClicked());

        FrameLayout favoriteGoalsContainer = findViewById(R.id.button_favorite_goals);
        mFavoriteGoalButton = favoriteGoalsContainer.findViewById(R.id.button_drawable);
        updateGoalSectionState(SettingsHelper.isGoalFavoritesVisible());
        favoriteGoalsContainer.setOnClickListener(v -> {
            SettingsHelper.setGoalFavoritesVisibility(!SettingsHelper.isGoalFavoritesVisible());
            updateGoalSectionState(SettingsHelper.isGoalFavoritesVisible());
        });
    }

    private void updateGoalSectionState(boolean isFavoritesVisible) {
        findViewById(R.id.goals_container).setVisibility(isFavoritesVisible ? View.VISIBLE : View.GONE);
        findViewById(R.id.goal_seekbars).setVisibility(isFavoritesVisible ? View.GONE : View.VISIBLE);
        mFavoriteGoalButton.setImageDrawable(getContext().getResources().getDrawable(
                isFavoritesVisible ? R.drawable.ic_tune : R.drawable.ic_star_outline));
        mStartButton.setEnabled(!isFavoritesVisible);
        mGoalsChipGroup.clearCheck();
    }

    private void updateGoalType() {
        final LinearLayout repsContainer = findViewById(R.id.reps_container);
        final LinearLayout durationContainer = findViewById(R.id.duration_container);
        if (mExercise.getValue().type == ExerciseType.TIME_BASED) {
            repsContainer.setVisibility(View.GONE);
            durationContainer.setVisibility(View.VISIBLE);
        } else {
            repsContainer.setVisibility(View.VISIBLE);
            durationContainer.setVisibility(View.GONE);
        }
    }

    public void onStartButtonClicked() {
        for (Listener listener : getListeners()) {
            listener.onStartButtonClicked();
        }
    }

    @Override
    public void showIntroduction() {
        if (SettingsHelper.showStartSnack()) {
            Snackbar snackbar = Snackbar
                    .make(mCoordinatorLayout, getContext().getString(R.string.snack_avoid_clicks),
                            Snackbar.LENGTH_INDEFINITE)
                    .setAction(getContext().getString(android.R.string.ok),
                            view -> SettingsHelper.setShowStartSnack(false));
            snackbar.show();
        }
    }

    @Override
    public void updateExercise(List<Exercise> exercises) {
        mExercises = exercises;
        mExerciseTypeChipGroup.removeAllViews();
        for (Exercise w : mExercises) {
            Chip c = new Chip(getContext());
            c.setText(w.name);

            ChipDrawable d = ChipDrawable.createFromAttributes(
                    getContext(), null, 0,
                    R.style.Widget_MaterialComponents_Chip_Choice);
            c.setChipDrawable(d);
            mExerciseTypeChipGroup.addView(c);
        }
        if (!mExercises.isEmpty()) {
            mExerciseTypeChipGroup.check(mExerciseTypeChipGroup.getChildAt(0).getId());
        }
    }

    @Override
    public void updateGoals(List<Goal> goals) {
        mFavoriteGoals = goals;
        mGoalsChipGroup.removeAllViews();
        for (Goal g : mFavoriteGoals) {
            Chip c = new Chip(getContext());
            c.setText(goalToString(g));
            ChipDrawable d = ChipDrawable.createFromAttributes(
                    getContext(), null, 0,
                    R.style.Widget_MaterialComponents_Chip_Choice);
            c.setChipDrawable(d);
            mGoalsChipGroup.addView(c);
        }
        mGoalsChipGroup.clearCheck();
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
        if (!SettingsHelper.isGoalFavoritesVisible()) {
            return mGoalConfigurator.getGoal();
        }

        int id = -1;
        for (int i = 0; i < mGoalsChipGroup.getChildCount(); ++i) {
            Chip crt = (Chip)mGoalsChipGroup.getChildAt(i);
            if (crt.getId() == mGoalsChipGroup.getCheckedChipId()) {
                id = mFavoriteGoals.get(i).id;
                break;
            }
        }

        if (id != -1) {
            for (Goal g : mFavoriteGoals) {
                if (g.id == id) {
                    return g;
                }
            }
        }

        Timber.tag(TAG).wtf( "Could not find the selected goal in the internal list.");
        return null;
    }

    public MutableLiveData<Exercise> getExercise() {
        return mExercise;
    }

    @Override
    public void toggleStartButtonState(boolean enabled) {
        mStartButton.setEnabled(enabled);
    }

    @Override public void onTypeChanged(GoalType type) { SettingsHelper.setGoalType(type); }
    @Override public void onSetsChanged(int sets) { SettingsHelper.setGoalSets(sets); }
    @Override public void onRepsChanged(int reps) { SettingsHelper.setGoalReps(reps); }
    @Override public void onDurationChanged(int duration) { SettingsHelper.setGoalDuration(duration); }
    @Override public void onBreakChanged(int duration) { SettingsHelper.setGoalBreak(duration); }
}
