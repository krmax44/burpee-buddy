package com.apps.adrcotfas.burpeebuddy.edit_exercises.view;

import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableView;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;

public interface ExercisesItemView extends ObservableView<ExercisesItemView.Listener> {

    public interface Listener {
        void onExerciseEditClicked(Exercise exercise);
        void onVisibilityToggle(String exercise, boolean visible);
    }

    void bindExercise(Exercise exercise);

    /**
     * Returns the scroll handle used for rearranging the items inside the list
     */
    FrameLayout getScrollHandle();

    /**
     * Returns the parent layout
     */
    RelativeLayout getItem();
}
