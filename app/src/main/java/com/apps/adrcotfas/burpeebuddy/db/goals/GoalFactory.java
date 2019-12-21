package com.apps.adrcotfas.burpeebuddy.db.goals;

import java.util.ArrayList;
import java.util.List;

public class GoalFactory {

    public static List<Goal> getDefaultGoals() {
        List<Goal> goals = new ArrayList<>();

        goals.add(Goal.getRepBasedGoal(3, 10, 30));
        goals.add(Goal.getRepBasedGoal(4, 15, 30));
        goals.add(Goal.getRepBasedGoal(5, 20, 30));

        goals.add(Goal.getAmrapBasedGoal(3, 60, 30));
        goals.add(Goal.getAmrapBasedGoal(4, 60, 30));
        goals.add(Goal.getAmrapBasedGoal(5, 60, 30));

        goals.add(Goal.getTimeBasedGoal(3, 30, 30));
        goals.add(Goal.getTimeBasedGoal(3, 60, 30));
        goals.add(Goal.getTimeBasedGoal(3, 90, 30));
        return goals;
    }
}
