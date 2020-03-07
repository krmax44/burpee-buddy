package com.apps.adrcotfas.burpeebuddy.intro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.db.AppDatabase;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.heinrichreimersoftware.materialintro.app.SlideFragment;

import java.util.List;

public class IntroSelectExercisesFragment extends SlideFragment {

    private ChipGroup exercisesChipGroup;
    private MaterialButton continueButton;

    public static int numExercises = 0;

    public IntroSelectExercisesFragment() {}

    public static IntroSelectExercisesFragment newInstance() {
        return new IntroSelectExercisesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        inflater.getContext().setTheme(R.style.AppTheme);
        final View v = inflater.inflate(R.layout.fragment_intro_select_exercises, null, false);
        exercisesChipGroup = v.findViewById(R.id.exercises_group);
        continueButton = v.findViewById(R.id.continue_button);
        continueButton.setOnClickListener(v1 -> {
            nextSlide();
        });

        LiveData<List<Exercise>> allExercises = AppDatabase.getDatabase(getContext()).exerciseDao().getAll();
        allExercises.observe(getViewLifecycleOwner(), exercises -> {
            allExercises.removeObservers(getViewLifecycleOwner());
            updateExercises(exercises);
        });
        return v;
    }

    private void updateExercises(List<Exercise> exercises) {
        for (Exercise e : exercises) {
            Chip c = new Chip(getContext());
            c.setText(e.name);

            ChipDrawable d = ChipDrawable.createFromAttributes(
                    getContext(), null, 0,
                    R.style.ChipStyle);
            c.setChipDrawable(d);

            c.setOnClickListener(v -> {
                numExercises = c.isChecked() ? numExercises + 1 : numExercises - 1;
                continueButton.setEnabled(numExercises > 0);
                AppDatabase.editVisibility(getContext(), c.getText().toString(), c.isChecked());
            });
            exercisesChipGroup.addView(c);
        }
    }

    @Override
    public boolean canGoForward() {
        return numExercises > 0;
    }

    @Override
    public boolean canGoBackward() {
        return false;
    }
}
