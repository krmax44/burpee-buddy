package com.apps.adrcotfas.burpeebuddy.edit_goals.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableView;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalToString;

public class GoalsItemViewImpl extends BaseObservableView<GoalsItemView.Listener>
        implements GoalsItemView {

    private Goal mGoal;
    private TextView mTitle;

    public GoalsItemViewImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.view_goal_list_item, parent, false));

        mTitle = findViewById(R.id.title);
        findViewById(R.id.edit).setOnClickListener(v -> {
            for (Listener l : getListeners()) {
                l.onGoalEditClicked(mGoal);
            }
        });
    }

    @Override
    public void bindGoal(Goal goal) {
        mGoal = goal;
        mTitle.setText(GoalToString.goalToString(goal));
    }
}
