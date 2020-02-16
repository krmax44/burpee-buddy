package com.apps.adrcotfas.burpeebuddy.edit_goals.view;

import com.apps.adrcotfas.burpeebuddy.common.ActionModeHelper;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableView;
import com.apps.adrcotfas.burpeebuddy.db.goal.Goal;

import java.util.List;

public interface GoalsView extends ObservableView<GoalsView.Listener> {

    void bindGoals(List<Goal> goals);
    void destroyActionMode();

    public interface Listener {
        void startActionMode(ActionModeHelper actionModeHelper);
        void onDeleteSelected(List<Integer> selectedEntriesIds);
        void onEditSelected(Goal goal);
    }
}
