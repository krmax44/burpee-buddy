package com.apps.adrcotfas.burpeebuddy.edit_exercises.view;

import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableView;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;

public interface ExercisesItemView extends ObservableView<ExercisesItemView.Listener> {

    interface Listener {
        void onVisibilityToggle(String exercise, boolean visible);
    }

    void bindExercise(Exercise exercise, boolean selected);

    /**
     * Returns the scroll handle used for rearranging the items inside the list
     */
    FrameLayout getScrollHandle();

    ConstraintLayout getParentView();

    /**
     * Returns the parent layout
     */
    RelativeLayout getItem();
}
