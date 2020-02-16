package com.apps.adrcotfas.burpeebuddy.edit_goals.view;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableView;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;

public interface GoalsItemView extends ObservableView<GoalsItemView.Listener> {

    public interface Listener {
        void onGoalEditClicked(Goal goal);
    }

    void bindGoal(Goal goal);
}
