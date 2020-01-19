package com.apps.adrcotfas.burpeebuddy.statistics;

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
import com.apps.adrcotfas.burpeebuddy.statistics.dialog.AddEditWorkoutDialog;
import com.apps.adrcotfas.burpeebuddy.statistics.view.StatisticsViewMvc;
import com.apps.adrcotfas.burpeebuddy.statistics.view.StatisticsViewMvcImpl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class StatisticsFragment extends Fragment implements StatisticsViewMvc.Listener {
    private static final String TAG = "StatisticsFragment";

    private StatisticsViewMvc mViewMvc;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        mViewMvc = new StatisticsViewMvcImpl(inflater, container);

        AppDatabase.getDatabase(getContext()).workoutDao().getAll().observe(
                getViewLifecycleOwner(), workouts ->
                        mViewMvc.bindWorkouts(workouts));
        setHasOptionsMenu(true);
        return mViewMvc.getRootView();
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewMvc.registerListener(this);
    }

    @Override
    public void onDestroy() {
        if (mViewMvc != null){
            mViewMvc.unregisterListener(this);
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
            AddEditWorkoutDialog.getInstance(null, false)
                    .show(getActivity().getSupportFragmentManager(), TAG);
            return true;
        }
        return false;
    }

    @Override
    public void onWorkoutLongPress(int id) {
        AppDatabase.deleteWorkout(getContext(), id);
    }

    @Subscribe
    public void onMessageEvent(Events.AddWorkout e) {
        AppDatabase.addWorkout(getContext(), e.workout);
    }

    @Subscribe
    public void onMessageEvent(Events.EditWorkout e) {
        AppDatabase.editWorkout(getContext(), e.id, e.workout);
    }
}
