package com.apps.adrcotfas.burpeebuddy.common.viewmvc;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.apps.adrcotfas.burpeebuddy.main.MainViewMvc;
import com.apps.adrcotfas.burpeebuddy.main.MainViewMvcImpl;
import com.apps.adrcotfas.burpeebuddy.workout.WorkoutViewMvc;
import com.apps.adrcotfas.burpeebuddy.workout.WorkoutViewMvcImpl;

public class ViewMvcFactory {
    private final LayoutInflater mLayoutInflater;

    public ViewMvcFactory(LayoutInflater mLayoutInflater) {
        this.mLayoutInflater = mLayoutInflater;
    }

    public MainViewMvc getMainViewMvc(@Nullable ViewGroup parent) {
        return new MainViewMvcImpl(mLayoutInflater, parent);
    }

    public WorkoutViewMvc getWorkoutViewMvc(@Nullable ViewGroup parent) {
        return new WorkoutViewMvcImpl(mLayoutInflater, parent);
    }
}
