package com.apps.adrcotfas.burpeebuddy.edit_goals;

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
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;
import com.apps.adrcotfas.burpeebuddy.edit_goals.dialog.AddEditGoalDialog;
import com.apps.adrcotfas.burpeebuddy.edit_goals.view.GoalsView;
import com.apps.adrcotfas.burpeebuddy.edit_goals.view.GoalsViewImpl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class GoalsFragment extends Fragment implements GoalsView.Listener {
    private static final String TAG = "GoalsFragment";

    private GoalsView mViewMvc;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        mViewMvc = new GoalsViewImpl(inflater, container);

        AppDatabase.getDatabase(getContext()).goalDao().getAll().observe(
                getViewLifecycleOwner(), goals ->
                        mViewMvc.bindGoals(goals));
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
            AddEditGoalDialog.getInstance(null, false)
                    .show(getActivity().getSupportFragmentManager(), TAG);
            return true;
        }
        return false;
    }

    @Override
    public void onGoalEditClicked(Goal goal) {
        AddEditGoalDialog.getInstance(goal, true)
                .show(getActivity().getSupportFragmentManager(), TAG);
    }

    @Subscribe
    public void onMessageEvent(Events.EditGoal event) {
        AppDatabase.editGoal(getContext(), event.id, event.goal);
    }

    @Subscribe
    public void onMessageEvent(Events.AddGoal event) {
        AppDatabase.addGoal(getContext(), event.goal);
    }

    @Subscribe
    public void onMessageEvent(Events.DeleteGoal event) {
        AppDatabase.deleteGoal(getContext(), event.id);
    }
}
