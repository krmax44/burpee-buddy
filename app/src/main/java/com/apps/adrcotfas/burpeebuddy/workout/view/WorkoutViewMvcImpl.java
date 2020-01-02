package com.apps.adrcotfas.burpeebuddy.workout.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.utilities.TimerFormat;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableViewMvc;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class WorkoutViewMvcImpl extends BaseObservableViewMvc<WorkoutViewMvc.Listener>
        implements WorkoutViewMvc {

    private TextView mCounter;
    private TextView mTimer;
    private final ExtendedFloatingActionButton mFinishSetButton;

    public WorkoutViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.fragment_workout, parent, false));

        ExtendedFloatingActionButton stopButton = findViewById(R.id.stop_button);
        stopButton.setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onStopButtonClicked();
            }
        });

        mFinishSetButton = findViewById(R.id.finish_set_button);
        mFinishSetButton.setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onFinishSetButtonClicked();
            }
        });

        mCounter = findViewById(R.id.rep_counter);
        mTimer = findViewById(R.id.timer);
    }


    @Override
    public void updateCounter(int reps) {
        mCounter.setText(String.valueOf(reps));
    }

    @Override
    public void updateTimer(int seconds) {
        mTimer.setText(TimerFormat.secondsToTimerFormat(seconds));
    }

    @Override
    public void onStartWorkout() {
        mFinishSetButton.setVisibility(View.VISIBLE);
    }
    @Override
    public void onStartBreak() {
        mFinishSetButton.setVisibility(View.GONE);
    }

}
