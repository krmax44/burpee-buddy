package com.apps.adrcotfas.burpeebuddy.main;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.exercisetype.ExerciseType;

import java.util.List;

public interface MainViewMvc extends ObservableViewMvc<MainViewMvc.Listener> {

    void showIntroduction();

    void updateExerciseTypes(List<ExerciseType> exerciseTypes);

    public interface Listener {
        void onStartButtonClicked();
        void onDisabledChipClicked();
    }
}
