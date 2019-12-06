package com.apps.adrcotfas.burpeebuddy.common.bl;

import android.os.CountDownTimer;
import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

public class PreWorkoutCountdown extends CountDownTimer {

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     */
    public PreWorkoutCountdown(long millisInFuture) {
        super(millisInFuture, 1000);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        EventBus.getDefault().post(
                new Events.PreWorkoutCountdownTickEvent((int)
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)));
    }

    @Override
    public void onFinish() {
        EventBus.getDefault().post(new Events.PreWorkoutCountdownFinished());
    }
}
