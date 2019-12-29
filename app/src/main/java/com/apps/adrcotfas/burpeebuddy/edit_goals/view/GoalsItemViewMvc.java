package com.apps.adrcotfas.burpeebuddy.edit_goals.view;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;

public interface GoalsItemViewMvc extends ObservableViewMvc<GoalsItemViewMvc.Listener> {

    public interface Listener {
        void onGoalEditClicked(Goal goal);
    }

    void bindGoal(Goal goal);
}
