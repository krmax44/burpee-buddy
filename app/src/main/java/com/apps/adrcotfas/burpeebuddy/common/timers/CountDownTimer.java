package com.apps.adrcotfas.burpeebuddy.common.timers;


import com.apps.adrcotfas.burpeebuddy.common.Events;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class CountDownTimer extends android.os.CountDownTimer {

    public interface Listener {
        void onFinishedAmrapSet();
    }

    private static final String TAG = "CountDownTimer";
    private Listener listener;
    private TimerType type;

    public CountDownTimer(TimerType type, long millisInFuture) {
        super(millisInFuture, 1000);
        this.type = type;
    }

    public CountDownTimer(TimerType type, long millisInFuture, Listener listener) {
        super(millisInFuture, 1000);
        this.type = type;
        this.listener = listener;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        Timber.tag(TAG).v("onTick: " + millisUntilFinished);

        // workaround for a bug in CountDownTimer which causes
        // onTick to be called twice inside a countDownInterval
        if (millisUntilFinished < 100) {
            return;
        }

        final int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
        EventBus.getDefault().post(new Events.TimerTickEvent(type, seconds));
    }

    @Override
    public void onFinish() {
        if (type.equals(TimerType.PRE_WORKOUT_COUNT_DOWN)) {
            EventBus.getDefault().post(new Events.PreWorkoutCountdownFinished());
        } else {
            listener.onFinishedAmrapSet();
        }
    }
}
