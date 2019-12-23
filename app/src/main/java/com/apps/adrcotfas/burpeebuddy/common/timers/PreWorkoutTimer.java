package com.apps.adrcotfas.burpeebuddy.common.timers;

import android.os.CountDownTimer;
import android.util.Log;

import com.apps.adrcotfas.burpeebuddy.common.bl.Events;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

public class PreWorkoutTimer extends CountDownTimer {

    private static final String TAG = "PreWorkoutTimer";
    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     */
    public PreWorkoutTimer(long millisInFuture) {
        super(millisInFuture, 1000);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        Log.d(TAG, "onTick: " + millisUntilFinished);
        // workaround for a bug in CountDownTimer which causes
        // onTick to be called twice inside a countDownInterval
        if (millisUntilFinished < 100) {
            return;
        }

        EventBus.getDefault().post(
                new Events.PreWorkoutCountdownTickEvent((int)
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)));
    }

    @Override
    public void onFinish() {
        EventBus.getDefault().post(new Events.PreWorkoutCountdownFinished());
    }
}
