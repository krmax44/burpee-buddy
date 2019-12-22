package com.apps.adrcotfas.burpeebuddy.common.timers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class Timer {

    private Observable<Long> mObservable;
    private Disposable mDisposable;

    private MutableLiveData<Integer> elapsedSeconds = new MutableLiveData<>(0);

    public Timer() {
        mObservable = Observable.timer(1, TimeUnit.SECONDS, Schedulers.computation());
    }

    public void start(){
        mDisposable = mObservable.observeOn(AndroidSchedulers.mainThread())
                .repeat()
                .subscribe(tick -> {
                    elapsedSeconds.setValue(elapsedSeconds.getValue() + 1);
                }, throwable -> {
                    // handle error
                });
    }

    public void stop(){
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        elapsedSeconds.setValue(0);
    }

    public LiveData<Integer> getElapsedSeconds() {
        return elapsedSeconds;
    }
}
