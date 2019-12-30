package com.apps.adrcotfas.burpeebuddy.common.timers;

import com.apps.adrcotfas.burpeebuddy.common.Events;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class Timer {

    private Disposable mDisposable;
    public int elapsedSeconds;

    public void start(){
        mDisposable = Observable.timer(1, TimeUnit.SECONDS, Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .repeat()
                .subscribe(tick -> {
                    ++elapsedSeconds;
                    EventBus.getDefault().post(
                            new Events.TimerTickEvent(TimerType.COUNT_UP, elapsedSeconds));
                }, throwable -> Timber.wtf("Something is wrong here."));
    }

    public void stop(){
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        elapsedSeconds = 0;
    }
}
