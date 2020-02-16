package com.apps.adrcotfas.burpeebuddy.edit_goals.view;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableView;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;

import java.util.List;

public interface GoalsView extends ObservableView<GoalsView.Listener> {

    void bindGoals(List<Goal> goals);

    public interface Listener {
        void onGoalEditClicked(Goal goal);
    }
}
