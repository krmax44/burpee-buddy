package com.apps.adrcotfas.burpeebuddy.db.goal;

import java.util.ArrayList;
import java.util.List;

public class GoalGenerator {

    public static List<Goal> getDefaultGoals() {
        List<Goal> goals = new ArrayList<>();

        goals.add(Goal.getRepBasedGoal(1, 30, 30));
        goals.add(Goal.getRepBasedGoal(3, 5, 30));
        goals.add(Goal.getRepBasedGoal(4, 15, 30));
        goals.add(Goal.getRepBasedGoal(5, 20, 30));

        goals.add(Goal.getTimeBasedGoal(3, 30, 30));
        goals.add(Goal.getTimeBasedGoal(3, 60, 30));
        goals.add(Goal.getTimeBasedGoal(3, 90, 30));
        return goals;
    }
}