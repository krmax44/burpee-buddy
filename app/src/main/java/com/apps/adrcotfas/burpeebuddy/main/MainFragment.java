package com.apps.adrcotfas.burpeebuddy.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.fragment.NavHostFragment;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.BuddyApplication;
import com.apps.adrcotfas.burpeebuddy.common.Events;
import com.apps.adrcotfas.burpeebuddy.db.AppDatabase;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.db.goal.Goal;
import com.apps.adrcotfas.burpeebuddy.db.goal.GoalType;
import com.apps.adrcotfas.burpeebuddy.db.workout.Workout;
import com.apps.adrcotfas.burpeebuddy.main.dialog.ChallengeCompleteDialog;
import com.apps.adrcotfas.burpeebuddy.main.view.MainView;
import com.apps.adrcotfas.burpeebuddy.main.view.MainViewImpl;
import com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper;
import com.apps.adrcotfas.burpeebuddy.settings.reminders.ReminderHelper;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType.COUNTABLE;
import static com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType.TIME_BASED;
import static com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType.UNCOUNTABLE;

public class MainFragment extends Fragment implements MainViewImpl.Listener {

    private static final String TAG = "MainFragment";
    private MainView view;

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
        view = new MainViewImpl(inflater, container);
        setHasOptionsMenu(true);

        ReminderHelper.removeNotification(getContext());

        AppDatabase.getDatabase(getContext()).exerciseDao().getAllVisible().observe(
                getViewLifecycleOwner(), exercises ->
                        view.updateExercises(exercises));

        view.getExercise().observe(getViewLifecycleOwner(), exercise -> {
            if (mExercise != null &&
                    ((exercise.type == TIME_BASED && mExercise.type == exercise.type) ||
                     (exercise.type != TIME_BASED && mExercise.type != TIME_BASED))) {
                mExercise = exercise;
                return;
            }
            mExercise = exercise;

            LiveData<List<Goal>> goalsLd;
            if (mExercise.type.equals(TIME_BASED)) {
                goalsLd = AppDatabase.getDatabase(getContext()).goalDao().getGoals(GoalType.TIME);
            } else {
                goalsLd = AppDatabase.getDatabase(getContext()).goalDao().getAll();
            }
            goalsLd.observe(getViewLifecycleOwner(),
                    goals -> view.updateGoals(goals));
        });
        return view.getRootView();
    }

    private void setupChallenges() {
        final DateTime now = new DateTime();
        final DateTime startOfToday = now.toLocalDate().toDateTimeAtStartOfDay(now.getZone());
        final long startOfTodayM = startOfToday.getMillis();

        final DateTime yesterday = new DateTime().minusDays(1);
        final DateTime startOfYesterday = yesterday.toLocalDate().toDateTimeAtStartOfDay(yesterday.getZone());
        final long startOfYesterdayM = startOfYesterday.getMillis();

        // a challenge corresponds to an exercise
        // you can have only one challenge in progress for one exercise at a time
        final LiveData<List<Challenge>> challengesLd =
                AppDatabase.getDatabase(getContext()).challengeDao().getInProgress();

        challengesLd.observe(getViewLifecycleOwner(), challenges -> {

            // accumulate yesterday's progress for each challenge
            Map<String, Pair<Integer, Integer>> progress = new HashMap<>(challenges.size());
            List<Pair<Challenge, Integer>> output = new ArrayList<>();

            if (challenges.isEmpty()) {
                view.updateChallenges(output);
            }

            for (Challenge c : challenges) {
                // get workouts starting from yesterday to check for failed challenges and set the progress for today
                LiveData<List<Workout>> workoutsLd = AppDatabase.getDatabase(getContext()).workoutDao().getWorkouts(
                        c.exerciseName, startOfYesterdayM);

                workoutsLd.observe(getViewLifecycleOwner(), workouts -> {
                    // accumulate total time or reps for each exercise
                    int totalToday = 0;
                    int totalYesterday = 0;

                    for (Workout w : workouts) {
                        final DateTime workoutDt = new DateTime(w.timestamp);
                        final DateTime workoutStartOfDay = workoutDt.toLocalDate().toDateTimeAtStartOfDay(workoutDt.getZone());

                        if (c.type == GoalType.TIME) {
                            if (workoutStartOfDay.equals(startOfToday)) {
                                totalToday += w.duration;
                            } else if (workoutStartOfDay.equals(startOfYesterday)) {
                                totalYesterday += w.duration;
                            }
                        } else {
                            if (workoutStartOfDay.equals(startOfToday)) {
                                totalToday += w.reps;
                            } else if (workoutStartOfDay.equals(startOfYesterday)) {
                                totalYesterday += w.reps;
                            }
                        }
                    }

                    progress.put(c.exerciseName, new Pair<>(totalToday, totalYesterday));

                    if (challenges.size() == progress.size()) {

                        for (Challenge ch : challenges) {
                            final int progressToday = progress.get(ch.exerciseName).first;
                            final int progressYesterday = progress.get(ch.exerciseName).second;

                            // did not start today and did not complete yesterday
                            if ((ch.date < startOfTodayM)
                                    && ((ch.type == GoalType.TIME && progressYesterday < ch.duration)
                                    || ch.type == GoalType.REPS && progressYesterday < ch.reps)) {
                                final ChallengeCompleteDialog dialog = ChallengeCompleteDialog.getInstance(ch, true);
                                dialog.show(getParentFragmentManager(), TAG);
                                AppDatabase.completeChallenge(getContext(), ch.id, startOfYesterdayM, true);
                                continue;
                            }

                            // last day was yesterday or earlier
                            final DateTime lastDay = new DateTime(c.date).plusDays(c.days);
                            if (lastDay.isBefore(startOfToday)) {
                                final ChallengeCompleteDialog dialog = ChallengeCompleteDialog.getInstance(ch, true);
                                dialog.show(getParentFragmentManager(), TAG);
                                AppDatabase.completeChallenge(getContext(), c.id,
                                        startOfYesterdayM, true);
                                continue;
                            }

                            final DateTime endOfChallenge = new DateTime(ch.date).plusDays(ch.days - 1);
                            final boolean thisIsTheLastDay = endOfChallenge.getMillis() == startOfTodayM;

                            if (thisIsTheLastDay && ((ch.type == GoalType.TIME && progressToday >= ch.duration) ||
                                 (ch.type == GoalType.REPS && progressToday >= ch.reps))) {
                                // Hurray, challenge completed
                                EventBus.getDefault().post(new Events.Konfetti());
                                final ChallengeCompleteDialog dialog = ChallengeCompleteDialog.getInstance(ch, false);
                                dialog.show(getParentFragmentManager(), TAG);
                                AppDatabase.completeChallenge(getContext(), ch.id, startOfTodayM, false);
                            } else {
                                output.add(new Pair<>(ch, progressToday));
                            }
                        }
                        view.updateChallenges(output);
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
        view.registerListener(this);
        view.showIntroduction();
        setupChallenges();
    }

    @Override
    public void onDestroy() {
        Timber.tag(TAG).d( "onDestroy");
        if (view != null){
            view.unregisterListener(this);
        }
        super.onDestroy();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem toggleSensor = menu.findItem(R.id.toggle_proximity);
        final boolean proximityEnabled = SettingsHelper.isProximityEnabled();
        toggleSensor.setIcon(proximityEnabled ? R.drawable.ic_sensor_enabled : R.drawable.ic_sensor_disabled);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu,
                                    MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.toggle_proximity) {
            SettingsHelper.setProximitySensorState(!SettingsHelper.isProximityEnabled());
            final boolean proximityEnabled = SettingsHelper.isProximityEnabled();
            item.setIcon(proximityEnabled ? R.drawable.ic_sensor_enabled : R.drawable.ic_sensor_disabled);
            Toast.makeText(getContext(), "The proximity sensor was " +
                    (proximityEnabled ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    public void onStartButtonClicked() {
        // this is a workaround for the case with disabled proximity sensor
        // the COUNTABLE exercises act as UNCOUNTABLE
        Exercise tmp = mExercise;
        if (!SettingsHelper.isProximityEnabled() && tmp.type == COUNTABLE) {
            tmp.type = UNCOUNTABLE;
        }

        BuddyApplication.getWorkoutManager().init(tmp, view.getGoal());
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
    public void onEditChallengesClicked() {
        NavHostFragment.findNavController(this).navigate(R.id.action_main_to_challenges);
    }
}
