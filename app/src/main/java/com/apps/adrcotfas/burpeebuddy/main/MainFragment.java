package com.apps.adrcotfas.burpeebuddy.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.fragment.NavHostFragment;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.BuddyApplication;
import com.apps.adrcotfas.burpeebuddy.db.AppDatabase;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalType;
import com.apps.adrcotfas.burpeebuddy.main.view.MainViewMvc;
import com.apps.adrcotfas.burpeebuddy.main.view.MainViewMvcImpl;
import com.apps.adrcotfas.burpeebuddy.workout.manager.State;
import com.apps.adrcotfas.burpeebuddy.workout.view.SetFinishedDialog;

import java.util.List;

import timber.log.Timber;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static com.apps.adrcotfas.burpeebuddy.common.BuddyApplication.getWorkoutManager;
import static com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType.COUNTABLE;
import static com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType.REP_BASED;
import static com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType.TIME_BASED;

public class MainFragment extends Fragment implements MainViewMvcImpl.Listener {

    private static final String TAG = "MainFragment";
    private MainViewMvc mViewMvc;

    private ExerciseType mExerciseType;

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
                        mViewMvc.updateExerciseTypes(exerciseTypes));

        mViewMvc.getExercise().observe(getViewLifecycleOwner(), exercise -> {
            LiveData<List<Goal>> goalsLd = new MutableLiveData<>();
            mExerciseType = exercise.type;

            if (COUNTABLE.equals(mExerciseType)) {
                goalsLd = AppDatabase.getDatabase(getContext()).goalDao().getCountableGoals();
            } else if (TIME_BASED.equals(mExerciseType)) {
                goalsLd = AppDatabase.getDatabase(getContext()).goalDao().getGoals(GoalType.TIME_BASED);
            } else if (REP_BASED.equals(mExerciseType)) {
                goalsLd = AppDatabase.getDatabase(getContext()).goalDao().getGoals(GoalType.AMRAP);
            }
            goalsLd.observe(getViewLifecycleOwner(),
                    goals -> mViewMvc.updateGoals(goals));
        });

        // when navigating from Workout to Main
        if (getWorkoutManager().getWorkout().state != State.ACTIVE &&
                MainFragmentArgs.fromBundle(getArguments()).getShowFinishedDialog()) {
            Timber.tag(TAG).d( "show finished dialog");
            SetFinishedDialog.getInstance(
                    BuddyApplication.getWorkoutManager().getWorkout())
                    .show(getActivity().getSupportFragmentManager(), TAG);
            getWorkoutManager().getWorkout().state = State.INACTIVE;
        }

        return mViewMvc.getRootView();
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.tag(TAG).d( "onResume");
        //TODO extract to settings
        getActivity().getWindow().addFlags(FLAG_KEEP_SCREEN_ON);
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
        MainFragmentDirections.ActionMainToWorkout action =
                MainFragmentDirections.actionMainToWorkout(mExerciseType.getValue(), mViewMvc.getGoal());
        NavHostFragment.findNavController(this).navigate(action);
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
    public void onDisabledChipClicked() {
        Toast.makeText(getActivity(), getString(R.string.implementation_not_ready), Toast.LENGTH_SHORT).show();
    }
}
