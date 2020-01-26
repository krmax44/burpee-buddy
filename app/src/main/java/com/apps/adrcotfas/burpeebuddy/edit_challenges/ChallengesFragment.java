package com.apps.adrcotfas.burpeebuddy.edit_challenges;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import java.util.List;

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

        AppDatabase.getDatabase(getContext()).challengeDao().getInProgress().observe(
                getViewLifecycleOwner(), challenges -> {
                    List<Integer> progress = new ArrayList<>(challenges.size());
                    for (Challenge c : challenges) {
                        AppDatabase.getDatabase(getContext()).workoutDao().getWorkouts(c.exerciseName, startOfToday.getMillis()).observe(
                                getViewLifecycleOwner(), workouts -> {
                                    int total = 0;
                                    for (Workout w : workouts) {
                                        if (w.type == TIME_BASED) {
                                            total += w.duration;
                                        } else {
                                            total += w.reps;
                                        }
                                    }
                                    progress.add(total);
                                    if (challenges.size() == progress.size()) {
                                        //TODO: logic to complete challenges

                                        view.bindChallenges(challenges, progress);
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
