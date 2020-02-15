package com.apps.adrcotfas.burpeebuddy.edit_challenges;

import android.os.Bundle;
import android.view.ActionMode;
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
import com.apps.adrcotfas.burpeebuddy.common.ActionModeCallback;
import com.apps.adrcotfas.burpeebuddy.common.Events;
import com.apps.adrcotfas.burpeebuddy.db.AppDatabase;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;
import com.apps.adrcotfas.burpeebuddy.db.workout.Workout;
import com.apps.adrcotfas.burpeebuddy.edit_challenges.dialog.AddChallengeDialog;
import com.apps.adrcotfas.burpeebuddy.edit_challenges.livedata.ChallengesCombinedLiveData;
import com.apps.adrcotfas.burpeebuddy.edit_challenges.view.ChallengeView;
import com.apps.adrcotfas.burpeebuddy.edit_challenges.view.ChallengeViewImpl;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType.TIME_BASED;

public class ChallengesFragment extends Fragment
        implements ChallengeView.Listener, ActionModeCallback.Listener {

    private static final String TAG = "ChallengesFragment";

    private ChallengeView view;
    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        view = new ChallengeViewImpl(inflater, container);
        view.registerListener(this);

        actionModeCallback = new ActionModeCallback(this, false);

        setupChallenges();

        setHasOptionsMenu(true);
        return view.getRootView();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (actionMode != null) {
            actionMode.finish();
        }
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

        final LiveData<List<Challenge>> completedLd = AppDatabase.getDatabase(getContext()).challengeDao().getCompleted();

        ChallengesCombinedLiveData result = new ChallengesCombinedLiveData();

        result.addSource(completedLd, completed -> {
            List<Pair<Challenge, Integer>> list = new ArrayList<>();
            result.completed = new ArrayList<>();
            for (int i = 0; i < completed.size(); ++i) {
                final Challenge crt = completed.get(i);
                Pair<Challenge, Integer> e = new Pair<>(crt, 0);
                list.add(e);
                result.completed.add(e);
            }
            if (result.inProgress != null) {
                list.addAll(result.inProgress);
            }
            result.setValue(list);
        });

        final LiveData<List<Challenge>> inProgressLd = AppDatabase.getDatabase(getContext()).challengeDao().getInProgress();
        result.addSource(inProgressLd, challenges -> {

            Map<String, Integer> progress = new HashMap<>(challenges.size());

            if (challenges.isEmpty()) {
                result.setValue(result.completed);
            }

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

                        List<Pair<Challenge, Integer>> list = new ArrayList<>();
                        result.inProgress = new ArrayList<>();
                        for (int i = 0; i < challenges.size(); ++i) {
                            final Challenge crt = challenges.get(i);
                            final Integer crtProgress = progress.get(crt.exerciseName);

                            final Pair<Challenge, Integer> e = new Pair<>(crt, crtProgress);
                            list.add(e);
                            result.inProgress.add(e);
                        }
                        if (result.completed != null) {
                            list.addAll(result.completed);
                        }
                        result.setValue(list);
                    }
                });
            }
        });
        result.observe(getViewLifecycleOwner(), output -> view.bindChallenges(output));
    }

    @Subscribe
    public void onMessageEvent(Events.AddChallenge event) {
        AppDatabase.addChallenge(getContext(), event.challenge);
    }

    @Override
    public void actionSelectAllItems() {
        view.selectAllItems();
    }

    @Override
    public void actionDelete() {
        new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Delete challenges?")
                .setMessage("This will delete the selected challenges")
                .setPositiveButton(android.R.string.ok, (dialog, i) -> {
                    for (int id : view.getSelectedEntriesIds()) {
                        AppDatabase.deleteChallenge(getContext(), id);
                    }
                    finishAction();
                })
                .setNegativeButton(android.R.string.cancel, (dialog, i) -> dialog.cancel())
                .show();
    }

    @Override
    public void destroyActionMode() {
        view.unselectItems();
        actionMode = null;
    }

    @Override
    public void editSelected() {
        // do nothing
    }

    @Override
    public void startActionMode() {
        if (actionMode == null) {
            actionMode = getActivity().startActionMode(actionModeCallback);
        }
    }

    @Override
    public void updateTitle(String numberOfSelectedItems) {
        actionMode.setTitle(numberOfSelectedItems);
    }

    @Override
    public void finishAction() {
        actionMode.setTitle("");
        actionMode.finish();
    }
}
