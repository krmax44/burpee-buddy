package com.apps.adrcotfas.burpeebuddy.main.view;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.util.Pair;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableView;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.goal.Goal;
import com.apps.adrcotfas.burpeebuddy.db.goal.GoalType;
import com.apps.adrcotfas.burpeebuddy.edit_goals.view.GoalConfigurator;
import com.apps.adrcotfas.burpeebuddy.main.view.challenges.ChallengesAdapter;
import com.apps.adrcotfas.burpeebuddy.main.view.challenges.LinePagerIndicatorDecoration;
import com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;
import timber.log.Timber;

import static com.apps.adrcotfas.burpeebuddy.db.goal.GoalToString.goalToString;

public class MainViewImpl extends BaseObservableView<MainView.Listener>
        implements MainView, GoalConfigurator.Listener {

    private static final String TAG = "MainViewImpl";

    private final CoordinatorLayout mCoordinatorLayout;

    private final FrameLayout mAddChallengeButton;
    private final LinearLayout mChallengesContainer;

    private final ChipGroup mExerciseTypeChipGroup;
    private final MaterialButton mAddExerciseButton;
    private final FrameLayout mAddGoalButton;

    private final ChipGroup mGoalsChipGroup;
    private final MaterialButton mStartButton;

    private List<Exercise> mExercises = new ArrayList<>();
    private List<Goal> mFavoriteGoals = new ArrayList<>();

    private MutableLiveData<Exercise> mExercise = new MutableLiveData<>();
    private final ImageView mFavoriteGoalButton;
    private GoalConfigurator mGoalConfigurator;
    private final FrameLayout mEditGoalButton;

    private ScrollView mExercisesContainer;
    private ScrollView mGoalsContainer;

    private RecyclerView mChallengesRecycler;
    private ChallengesAdapter mChallengesAdapter;

    private KonfettiView mKonfetti;

    public MainViewImpl(LayoutInflater inflater, ViewGroup parent) {
        final View view = inflater.inflate(R.layout.fragment_main, parent, false);
        setRootView(view);
        mGoalConfigurator = new GoalConfigurator(view, SettingsHelper.getGoal(), this, getContext());

        mAddChallengeButton = findViewById(R.id.add_challenge_button);
        mChallengesContainer = findViewById(R.id.challenges_container);
        mAddChallengeButton.setOnClickListener(v -> onAddChallengeButtonClicked());
        mChallengesContainer.setVisibility(View.GONE);
        mAddChallengeButton.setVisibility(View.VISIBLE);

        mExercisesContainer = findViewById(R.id.exercises_container);
        mGoalsContainer = findViewById(R.id.goals_container);

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
            for (Listener listener : MainViewImpl.this.getListeners()) {
                if (SettingsHelper.isGoalFavoritesVisible()) {
                    listener.onGoalSelectionChanged(checkedId != View.NO_ID);
                }
            }
        });

        mCoordinatorLayout = findViewById(R.id.top_coordinator);

        mEditGoalButton = findViewById(R.id.button_edit_goals);
        mEditGoalButton.findViewById(R.id.button_edit).setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onEditGoalsClicked();
            }
        });

        mStartButton.setOnClickListener(v -> onStartButtonClicked());

        mAddExerciseButton = findViewById(R.id.add_exercise_button);
        mAddGoalButton = findViewById(R.id.add_goal_button);

        mAddExerciseButton.setOnClickListener(v -> onAddExerciseButtonClicked());
        mAddGoalButton.setOnClickListener(v -> onAddGoalButtonClicked());

        mKonfetti = findViewById(R.id.viewKonfetti);

        FrameLayout favoriteGoalsContainer = findViewById(R.id.button_favorite_goals);
        mFavoriteGoalButton = favoriteGoalsContainer.findViewById(R.id.button_drawable);
        updateGoalSectionState(SettingsHelper.isGoalFavoritesVisible());
        favoriteGoalsContainer.setOnClickListener(v -> {
            SettingsHelper.setGoalFavoritesVisibility(!SettingsHelper.isGoalFavoritesVisible());
            updateGoalSectionState(SettingsHelper.isGoalFavoritesVisible());
        });

        setupChallengesView(inflater);
    }

    private void onAddGoalButtonClicked() {
        for (Listener l : getListeners()) {
            l.onAddGoalButtonClicked();
        }
    }

    private void onAddExerciseButtonClicked() {
        for (Listener l : getListeners()) {
            l.onAddExerciseButtonClicked();
        }
    }

    private void setupChallengesView(LayoutInflater inflater) {
        mChallengesRecycler = findViewById(R.id.horizontal_scroll);
        mChallengesRecycler.setLayoutManager(
                new LinearLayoutManager(getContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false));
        mChallengesAdapter = new ChallengesAdapter(inflater);
        mChallengesRecycler.setAdapter(mChallengesAdapter);
        PagerSnapHelper snapHelperCenter = new PagerSnapHelper();
        snapHelperCenter.attachToRecyclerView(mChallengesRecycler);
    }

    private void updateGoalSectionState(boolean isFavoritesVisible) {
        findViewById(R.id.goals_container).setVisibility(isFavoritesVisible ? View.VISIBLE : View.GONE);
        findViewById(R.id.goal_seekbars).setVisibility(isFavoritesVisible ? View.GONE : View.VISIBLE);

        if (!isFavoritesVisible || mGoalsChipGroup.getChildCount() != 0) {
            mAddGoalButton.setVisibility(View.GONE);
        } else {
            mAddGoalButton.setVisibility(View.VISIBLE);
        }

        mFavoriteGoalButton.setImageDrawable(getContext().getResources().getDrawable(
                isFavoritesVisible ? R.drawable.ic_tune : R.drawable.ic_star_outline));
        mEditGoalButton.setVisibility(isFavoritesVisible ? View.VISIBLE : View.GONE);
        mStartButton.setEnabled(!isFavoritesVisible);
        mGoalsChipGroup.clearCheck();
    }

    private void updateGoalType() {
        if (mExercise.getValue().type == ExerciseType.TIME_BASED) {
            findViewById(R.id.rep_based).setEnabled(false);
            ((RadioButton)findViewById(R.id.time_based)).setChecked(true);
        } else {
            findViewById(R.id.rep_based).setEnabled(true);
        }
    }

    public void onStartButtonClicked() {
        for (Listener listener : getListeners()) {
            listener.onStartButtonClicked();
        }
    }

    public void onAddChallengeButtonClicked() {
        for (Listener listener : getListeners()) {
            listener.onAddChallengeButtonClicked();
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
    public void updateExercises(List<Exercise> exercises) {

        mAddExerciseButton.setVisibility(exercises.isEmpty() ? View.VISIBLE : View.GONE);
        mExercisesContainer.setVisibility(exercises.isEmpty() ? View.GONE : View.VISIBLE);
        mStartButton.setEnabled(!exercises.isEmpty());

        mExercises = exercises;
        mExerciseTypeChipGroup.removeAllViews();
        for (Exercise w : mExercises) {
            Chip c = new Chip(getContext());
            c.setText(w.name);

            ChipDrawable d = ChipDrawable.createFromAttributes(
                    getContext(), null, 0,
                    R.style.ChipStyle);
            c.setChipDrawable(d);
            mExerciseTypeChipGroup.addView(c);
        }
        if (!mExercises.isEmpty()) {
            mExerciseTypeChipGroup.check(mExerciseTypeChipGroup.getChildAt(0).getId());
        }
    }

    @Override
    public void updateGoals(List<Goal> goals) {

        if (SettingsHelper.isGoalFavoritesVisible()) {
            mAddGoalButton.setVisibility(goals.isEmpty() ? View.VISIBLE : View.GONE);
            mGoalsContainer.setVisibility(goals.isEmpty() ? View.GONE : View.VISIBLE);
        }

        mFavoriteGoals = goals;
        mGoalsChipGroup.removeAllViews();
        for (Goal g : mFavoriteGoals) {
            Chip c = new Chip(getContext());
            c.setText(goalToString(g));
            ChipDrawable d = ChipDrawable.createFromAttributes(
                    getContext(), null, 0,
                    R.style.ChipStyle);
            c.setChipDrawable(d);
            mGoalsChipGroup.addView(c);
        }
        mGoalsChipGroup.clearCheck();
    }

    @Override
    public void updateChallenges(List<Pair<Challenge, Integer>> challenges) {

        if (challenges.isEmpty()) {
            mChallengesContainer.setVisibility(View.GONE);
            mAddChallengeButton.setVisibility(View.VISIBLE);
            return;
        } else {
            mChallengesContainer.setVisibility(View.VISIBLE);
            mAddChallengeButton.setVisibility(View.GONE);
        }

        mChallengesAdapter.bindChallenges(challenges);

        if (challenges.size() > 1) {
            mChallengesRecycler.addItemDecoration(new LinePagerIndicatorDecoration());
        }

//        for (int i = 0; i < challenges.size(); ++i) {
//            final Challenge c = challenges.get(i);
//            final Integer crtProgress = progress.get(c.exerciseName);
//            final LinearLayout challengesContainer = findViewById(R.id.challenges);
//
//            if (crtProgress != null &&
//                    ((c.type == GoalType.TIME && crtProgress < c.duration) ||
//                      c.type == GoalType.REPS && crtProgress < c.reps)) {
//                challengesContainer.setBackgroundColor(
//                        getContext().getResources().getColor(R.color.transparent_red));
//                break;
//            }
//        }
    }

    @Override
    public void showKonfeti() {
        mKonfetti.build()
                //TODO: add nicer colors
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(new Size(12, 5))
                .setPosition(-50f, mKonfetti.getWidth() + 50f, -50f, -50f)
                .streamFor(300, 3000L);
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
