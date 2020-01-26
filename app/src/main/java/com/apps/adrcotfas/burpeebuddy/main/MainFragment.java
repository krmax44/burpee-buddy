package com.apps.adrcotfas.burpeebuddy.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
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
import com.apps.adrcotfas.burpeebuddy.db.workout.Workout;
import com.apps.adrcotfas.burpeebuddy.main.view.MainViewMvc;
import com.apps.adrcotfas.burpeebuddy.main.view.MainViewMvcImpl;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
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

        setupChallenges();

        //TODO: needed?
//        // when navigating from Workout to Main
//        if (getWorkoutManager().getWorkout().getState() != State.ACTIVE &&
//                MainFragmentArgs.fromBundle(getArguments()).getShowFinishedDialog()) {
//            Timber.tag(TAG).d( "show finished dialog");
//            getWorkoutManager().getWorkout().setState(State.INACTIVE);
//        }

        return mViewMvc.getRootView();
    }

    private void setupChallenges() {
        final DateTime now = new DateTime();
        final DateTime startOfToday = now.toLocalDate().toDateTimeAtStartOfDay(now.getZone());

        final DateTime yesterday = new DateTime().minusDays(1);
        final DateTime startOfYesterday = yesterday.toLocalDate().toDateTimeAtStartOfDay(yesterday.getZone());

        setupChallengesFailed(startOfToday, startOfYesterday);
        setupChallengesInProgress(startOfToday);
    }

    private void setupChallengesFailed(DateTime startOfToday, DateTime startOfYesterday) {
        // a challenge corresponds to an exercise
        // you can have only one challenge in progress for one exercise at a time
        final LiveData<List<Challenge>> challengesLd =
                AppDatabase.getDatabase(getContext()).challengeDao().getInProgress();

        challengesLd.observe(getViewLifecycleOwner(), challenges -> {
            challengesLd.removeObservers(getViewLifecycleOwner());

            // accumulate yesterday's progress for each challenge
            Map<String, Integer> progress = new HashMap<>(challenges.size());

            for (Challenge c : challenges) {
                // get yesterday's workouts to check for failed challenges
                LiveData<List<Workout>> workoutsLd = AppDatabase.getDatabase(getContext()).workoutDao().getWorkouts(
                        c.exerciseName, startOfYesterday.getMillis(), startOfToday.getMillis());

                workoutsLd.observe(getViewLifecycleOwner(), workouts -> {
                    workoutsLd.removeObservers(getViewLifecycleOwner());

                    // accumulate total time or reps for each exercise
                    int total = 0;
                    for (Workout w : workouts) {
                        if (c.type == GoalType.TIME) {
                            total += w.duration;
                        } else {
                            total += w.reps;
                        }
                    }

                    progress.put(c.exerciseName, total);

                    if (challenges.size() == progress.size()) {
                        for (int i = 0; i < challenges.size(); ++i) {
                            Challenge crt = challenges.get(i);
                            Integer crtProgress = progress.get(crt.exerciseName);

                            if (crtProgress == null) {
                                Timber.tag(TAG).wtf("something went wrong here");
                                continue;
                            }

                            // if you started the challenge today, skip this
                            if (crt.date == startOfToday.getMillis()) {
                                continue;
                            }

                            if ((crt.type == GoalType.TIME && crtProgress < crt.duration) ||
                                    crt.type == GoalType.REPS && crtProgress < crt.reps) {
                                //TODO: challenge failed -> notify user
                                AppDatabase.completeChallenge(getContext(), crt.id,
                                        startOfYesterday.getMillis(), true);
                            }
                        }
                    }
                });
            }
        });
    }

    private void setupChallengesInProgress(DateTime startOfToday) {
        List<Pair<Challenge, Integer>> output = new ArrayList<>();
        final LiveData<List<Challenge>> challengesLd = AppDatabase.getDatabase(getContext()).challengeDao().getInProgress();

        challengesLd.observe(getViewLifecycleOwner(), challenges -> {
            challengesLd.removeObservers(getViewLifecycleOwner());

            Map<String, Integer> progress = new HashMap<>(challenges.size());
            for (Challenge c : challenges) {
                final LiveData<List<Workout>> workoutsLd = AppDatabase.getDatabase(
                        getContext()).workoutDao().getWorkouts(c.exerciseName, startOfToday.getMillis());
                workoutsLd.observe(getViewLifecycleOwner(), workouts -> {
                    workoutsLd.removeObservers(getViewLifecycleOwner());
                    int total = 0;
                    for (Workout w : workouts) {
                        if (w.type == TIME_BASED) {
                            total += w.duration;
                        } else {
                            total += w.reps;
                        }
                    }
                    progress.put(c.exerciseName, total);
                    if (challenges.size() == progress.size()) {
                        for (int i = 0; i < challenges.size(); ++i) {
                            final Challenge crt = challenges.get(i);
                            final Integer crtProgress = progress.get(crt.exerciseName);

                            if (crtProgress == null) {
                                Timber.tag(TAG).wtf("something went wrong here");
                                continue;
                            }

                            final DateTime endOfChallenge = new DateTime(crt.date).plusDays(crt.days - 1);
                            final boolean thisIsTheLastDay = endOfChallenge.getMillis() == startOfToday.getMillis();
                            if (thisIsTheLastDay &&
                                    ((crt.type == GoalType.TIME && crtProgress >= crt.duration) ||
                                            crt.type == GoalType.REPS && crtProgress >= crt.reps)) {
                                // Hurray, challenge completed
                                //TODO: notify user
                                AppDatabase.completeChallenge(getContext(), crt.id,
                                        startOfToday.getMillis(), false);
                            } else {
                                output.add(new Pair<>(crt, crtProgress));
                            }
                        }
                        mViewMvc.updateChallenges(output);
                    }
                });
            }
        });
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
