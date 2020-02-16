package com.apps.adrcotfas.burpeebuddy.edit_goals.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableView;
import com.apps.adrcotfas.burpeebuddy.db.goal.Goal;
import com.apps.adrcotfas.burpeebuddy.db.goal.GoalToString;

public class GoalsItemViewImpl extends BaseObservableView<GoalsItemView.Listener>
        implements GoalsItemView {

    private TextView mTitle;
    private View overlay;

    public GoalsItemViewImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.goal_list_item, parent, false));

        mTitle = findViewById(R.id.title);
        overlay = findViewById(R.id.overlay);
    }

    @Override
    public void bindGoal(Goal goal, boolean selected) {
        mTitle.setText(GoalToString.goalToString(goal));
        overlay.setVisibility(selected ? View.VISIBLE : View.GONE);
    }
}
