package com.apps.adrcotfas.burpeebuddy.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.fragment.NavHostFragment;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.BuddyApplication;
import com.apps.adrcotfas.burpeebuddy.db.AppDatabase;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalType;
import com.apps.adrcotfas.burpeebuddy.main.view.MainViewMvc;
import com.apps.adrcotfas.burpeebuddy.main.view.MainViewMvcImpl;
import com.apps.adrcotfas.burpeebuddy.workout.manager.State;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static com.apps.adrcotfas.burpeebuddy.common.BuddyApplication.getWorkoutManager;
import static com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType.TIME_BASED;

public class MainFragment extends Fragment implements MainViewMvcImpl.Listener {

    private static final String TAG = "MainFragment";
    private MainViewMvc mViewMvc;

    private Exercise mExercise;

    public MainFragment() {}

    @Override
    public void onStart() {
        Timber.tag(TAG).d( "onStart");
        super.onStart();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.tag(TAG).d( "onCreateView");
        mViewMvc = new MainViewMvcImpl(inflater, container);

        AppDatabase.getDatabase(getContext()).exerciseDao().getAllVisible().observe(
                getViewLifecycleOwner(), exerciseTypes ->
                        mViewMvc.updateExercise(exerciseTypes));

        mViewMvc.getExercise().observe(getViewLifecycleOwner(), exercise -> {

            mExercise = exercise;
            LiveData<List<Goal>> goalsLd;
            if (mExercise.type.equals(TIME_BASED)) {
                goalsLd = AppDatabase.getDatabase(getContext()).goalDao().getGoals(GoalType.TIME);
            } else {
                goalsLd = AppDatabase.getDatabase(getContext()).goalDao().getAll();
            }
            goalsLd.observe(getViewLifecycleOwner(),
                    goals -> mViewMvc.updateGoals(goals));
        });

        //TODO: get challenges from DB and update accordingly
        List<Challenge> list = new ArrayList<>();
        list.add(new Challenge("day 25/100 ‧ 3:00/3:00 plank"));
        mViewMvc.updateChallenges(list);

        // when navigating from Workout to Main
        if (getWorkoutManager().getWorkout().getState() != State.ACTIVE &&
                MainFragmentArgs.fromBundle(getArguments()).getShowFinishedDialog()) {
            Timber.tag(TAG).d( "show finished dialog");
            getWorkoutManager().getWorkout().setState(State.INACTIVE);
        }

        return mViewMvc.getRootView();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().clearFlags(FLAG_KEEP_SCREEN_ON);
        Timber.tag(TAG).d( "onResume");
        mViewMvc.registerListener(this);
        mViewMvc.showIntroduction();
    }

    @Override
    public void onDestroy() {
        Timber.tag(TAG).d( "onDestroy");
        if (mViewMvc != null){
            mViewMvc.unregisterListener(this);
        }
        super.onDestroy();
    }

    @Override
    public void onStartButtonClicked() {
        BuddyApplication.getWorkoutManager().init(mExercise, mViewMvc.getGoal());
        NavHostFragment.findNavController(this).navigate(R.id.action_main_to_workout);
    }

    @Override
    public void onEditExercisesClicked() {
        NavHostFragment.findNavController(this).navigate(R.id.action_main_to_exercises);
    }

    @Override
    public void onEditGoalsClicked() {
        NavHostFragment.findNavController(this).navigate(R.id.action_main_to_goals);
    }

    @Override
    public void onGoalSelectionChanged(boolean valid) {
        mViewMvc.toggleStartButtonState(valid);
    }
}
