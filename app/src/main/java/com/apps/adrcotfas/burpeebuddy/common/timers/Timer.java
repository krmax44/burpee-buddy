package com.apps.adrcotfas.burpeebuddy.common.timers;

import com.apps.adrcotfas.burpeebuddy.common.bl.Events;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class Timer {

    private Disposable mDisposable;
    private int elapsedSeconds;

    public void start(){
        mDisposable = Observable.timer(1, TimeUnit.SECONDS, Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .repeat()
                .subscribe(tick -> {
                    ++elapsedSeconds;
                    EventBus.getDefault().post(new Events.TimerTickEvent(elapsedSeconds));
                }, throwable -> {
                    // handle error
                });
    }

    public void stop(){
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        elapsedSeconds = 0;
    }
}
