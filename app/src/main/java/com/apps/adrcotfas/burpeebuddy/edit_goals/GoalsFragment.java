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
import com.apps.adrcotfas.burpeebuddy.common.ActionModeHelper;
import com.apps.adrcotfas.burpeebuddy.common.Events;
import com.apps.adrcotfas.burpeebuddy.db.AppDatabase;
import com.apps.adrcotfas.burpeebuddy.db.goal.Goal;
import com.apps.adrcotfas.burpeebuddy.edit_goals.dialog.AddEditGoalDialog;
import com.apps.adrcotfas.burpeebuddy.edit_goals.view.GoalsView;
import com.apps.adrcotfas.burpeebuddy.edit_goals.view.GoalsViewImpl;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class GoalsFragment extends Fragment implements GoalsView.Listener {
    private static final String TAG = "GoalsFragment";

    private GoalsView view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        view = new GoalsViewImpl(inflater, container);

        AppDatabase.getDatabase(getContext()).goalDao().getAll().observe(
                getViewLifecycleOwner(), goals ->
                        view.bindGoals(goals));
        setHasOptionsMenu(true);
        return view.getRootView();
    }

    @Override
    public void onResume() {
        super.onResume();
        view.registerListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        view.destroyActionMode();
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
            AddEditGoalDialog.getInstance(null, false)
                    .show(getActivity().getSupportFragmentManager(), TAG);
            return true;
        }
        return false;
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

    @Override
    public void startActionMode(ActionModeHelper actionModeHelper) {
        actionModeHelper.setActionMode(getActivity().startActionMode(actionModeHelper));
    }

    @Override
    public void onDeleteSelected(List<Integer> ids) {
        new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Delete goals?")
                .setMessage("This will delete the selected goals")
                .setPositiveButton(android.R.string.ok, (dialog, i) -> {
                    for (int id : ids) {
                        AppDatabase.deleteGoal(getContext(), id);
                    }
                    view.destroyActionMode();
                })
                .setNegativeButton(android.R.string.cancel, (dialog, i) -> dialog.cancel())
                .show();
    }

    @Override
    public void onEditSelected(Goal goal) {
        AddEditGoalDialog.getInstance(goal, true)
                .show(getActivity().getSupportFragmentManager(), TAG);

        view.destroyActionMode();
    }
}
