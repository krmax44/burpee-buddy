package com.apps.adrcotfas.burpeebuddy.edit_goals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.apps.adrcotfas.burpeebuddy.common.Events;
import com.apps.adrcotfas.burpeebuddy.db.AppDatabase;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;
import com.apps.adrcotfas.burpeebuddy.edit_goals.dialog.AddEditGoalDialog;
import com.apps.adrcotfas.burpeebuddy.edit_goals.view.GoalsViewMvc;
import com.apps.adrcotfas.burpeebuddy.edit_goals.view.GoalsViewMvcImpl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class GoalsFragment extends Fragment implements GoalsViewMvc.Listener {
    private static final String TAG = "GoalsFragment";

    private GoalsViewMvc mViewMvc;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        mViewMvc = new GoalsViewMvcImpl(inflater, container);

        AppDatabase.getDatabase(getContext()).goalDao().getAll().observe(
                getViewLifecycleOwner(), goals ->
                        mViewMvc.bindGoals(goals));

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
    public void onGoalAddClicked() {
        AddEditGoalDialog.getInstance(null, false)
                .show(getActivity().getSupportFragmentManager(), TAG);
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
