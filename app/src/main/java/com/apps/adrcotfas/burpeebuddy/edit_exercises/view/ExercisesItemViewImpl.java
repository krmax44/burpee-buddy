package com.apps.adrcotfas.burpeebuddy.edit_exercises.view;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableView;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;

public class ExercisesItemViewImpl extends BaseObservableView<ExercisesItemView.Listener>
        implements ExercisesItemView {

    private final ConstraintLayout parent;
    private final RelativeLayout item;
    private final TextView title;
    private Exercise exercise;
    private final ImageView visibilityButton;
    private final FrameLayout scroll;
    private View overlay;

    public ExercisesItemViewImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.exercises_list_item, parent, false));

        this.parent = findViewById(R.id.parent);
        item = findViewById(R.id.item);
        scroll = findViewById(R.id.scroll_icon_container);
        title = findViewById(R.id.title);
        visibilityButton = findViewById(R.id.visibility_icon);
        findViewById(R.id.visibility_icon_container).setOnClickListener(v -> {
            visibilityButton.setImageDrawable(getDrawable(exercise.visible
                    ? R.drawable.ic_eye_off_outline
                    : R.drawable.ic_eye_outline));

            for (Listener l : getListeners()) {
                l.onVisibilityToggle(exercise.name, !exercise.visible);
            }
        });
        overlay = findViewById(R.id.overlay);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void bindExercise(Exercise exercise, boolean selected) {
        this.exercise = exercise;
        title.setText(exercise.name);
        visibilityButton.setImageDrawable(getDrawable(this.exercise.visible
                ? R.drawable.ic_eye_outline
                : R.drawable.ic_eye_off_outline));
        overlay.setVisibility(selected ? View.VISIBLE : View.GONE);
    }

    @Override
    public FrameLayout getScrollHandle() {
        return scroll;
    }

    @Override
    public ConstraintLayout getParentView() {
        return parent;
    }

    @Override
    public RelativeLayout getItem() {
        return item;
    }
}
