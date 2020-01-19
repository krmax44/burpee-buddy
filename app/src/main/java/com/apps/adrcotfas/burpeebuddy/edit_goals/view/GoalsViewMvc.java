package com.apps.adrcotfas.burpeebuddy.edit_goals.view;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;

import java.util.List;

public interface GoalsViewMvc extends ObservableViewMvc<GoalsViewMvc.Listener> {

    void bindGoals(List<Goal> goals);

    public interface Listener {
        void onGoalEditClicked(Goal goal);
    }
}
