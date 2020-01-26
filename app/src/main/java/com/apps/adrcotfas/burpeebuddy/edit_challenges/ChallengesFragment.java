package com.apps.adrcotfas.burpeebuddy.edit_challenges;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.Events;
import com.apps.adrcotfas.burpeebuddy.db.AppDatabase;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;
import com.apps.adrcotfas.burpeebuddy.db.workout.Workout;
import com.apps.adrcotfas.burpeebuddy.edit_challenges.dialog.AddChallengeDialog;
import com.apps.adrcotfas.burpeebuddy.edit_challenges.view.ChallengeView;
import com.apps.adrcotfas.burpeebuddy.edit_challenges.view.ChallengeViewImpl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType.TIME_BASED;

public class ChallengesFragment extends Fragment implements ChallengeView.Listener {

    private static final String TAG = "ChallengesFragment";

    private ChallengeView view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        view = new ChallengeViewImpl(inflater, container);

        setupChallenges();

        setHasOptionsMenu(true);
        return view.getRootView();
    }

    @Override
    public void onDestroy() {
        if (view != null){
            view.unregisterListener(this);
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,
                                    MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_stuff, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            AddChallengeDialog.getInstance().
                    show(getActivity().getSupportFragmentManager(), TAG);
            return true;
        }
        return false;
    }

    private void setupChallenges() {

        final DateTime now = new DateTime();
        final DateTime startOfToday = now.toLocalDate().toDateTimeAtStartOfDay(now.getZone());

        // first add the ones in progress
        final LiveData<List<Challenge>> allLd = AppDatabase.getDatabase(getContext()).challengeDao().getInProgress();
        allLd.observe(getViewLifecycleOwner(), challenges -> {
            List<Pair<Challenge, Integer>> output = new ArrayList<>();
            Map<String, Integer> progress = new HashMap<>(challenges.size());

            for (Challenge c : challenges) {
                final LiveData<List<Workout>> workoutsLd = AppDatabase.getDatabase(
                        getContext()).workoutDao().getWorkouts(c.exerciseName, startOfToday.getMillis());

                workoutsLd.observe(getViewLifecycleOwner(), workouts -> {
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
                            output.add(new Pair<>(crt, crtProgress));
                        }
                        // then add the completed or failed challenges
                        final LiveData<List<Challenge>> completedLd = AppDatabase.getDatabase(getContext()).challengeDao().getCompleted();
                        completedLd.observe(getViewLifecycleOwner(), completed -> {
                            completedLd.removeObservers(getViewLifecycleOwner());
                            for (int i = 0; i < completed.size(); ++i) {
                                final Challenge crt = completed.get(i);
                                output.add(new Pair<>(crt, 0));
                            }
                            view.bindChallenges(output);
                        });
                    }
                });
            }
        });
    }

    @Subscribe
    public void onMessageEvent(Events.AddChallenge event) {
        AppDatabase.addChallenge(getContext(), event.challenge);
    }
}
