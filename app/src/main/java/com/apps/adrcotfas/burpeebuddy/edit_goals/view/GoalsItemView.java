package com.apps.adrcotfas.burpeebuddy.edit_goals.view;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableView;
import com.apps.adrcotfas.burpeebuddy.db.goal.Goal;

public interface GoalsItemView extends ObservableView<GoalsItemView.Listener> {

    public interface Listener {
    }

    void bindGoal(Goal goal, boolean selected);
}
