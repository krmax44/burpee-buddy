package com.apps.adrcotfas.burpeebuddy.main.view;

import androidx.lifecycle.MutableLiveData;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;

import java.util.List;

public interface MainViewMvc extends ObservableViewMvc<MainViewMvc.Listener> {

    void showIntroduction();

    void updateExerciseTypes(List<Exercise> exercises);
    void updateGoals(List<Goal> goals);

    /**
     * Returns the currently selected exercise
     */
    MutableLiveData<Exercise> getExercise();

    /**
     * Returns the currently selected goal
     */
    Goal getGoal();

    public interface Listener {
        void onStartButtonClicked();
        void onEditExercisesClicked();
        void onEditGoalsClicked();
    }
}
