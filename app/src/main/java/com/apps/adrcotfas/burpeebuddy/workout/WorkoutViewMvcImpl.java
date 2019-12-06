package com.apps.adrcotfas.burpeebuddy.workout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableViewMvc;

public class WorkoutViewMvcImpl extends BaseObservableViewMvc<WorkoutViewMvc.Listener>
        implements WorkoutViewMvc {

    private TextView mCounter;
    private TextView mTimer;

    public WorkoutViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.activity_workout, parent, false));

        Button stopButton = findViewById(R.id.stop_button);
        stopButton.setOnClickListener(v -> onStopButtonClicked());

        mCounter = findViewById(R.id.rep_counter);
        mTimer = findViewById(R.id.timer);
    }

    private void onStopButtonClicked() {
        for (Listener listener : getListeners()) {
            listener.onStopButtonClicked();
        }
    }

    @Override
    public void updateCounter(long value) {
        mCounter.setText(String.valueOf(value));
    }

    @Override
    public void updateTimer(long value) {
        //TODO: format to timer style
        mTimer.setText(String.valueOf(value));
    }

    @Override
    public void toggleTimerVisibility() {
        mTimer.setVisibility(
                mTimer.getVisibility() == View.VISIBLE ?
                        View.INVISIBLE : View.VISIBLE);
    }
}
