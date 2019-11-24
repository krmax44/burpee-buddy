package com.apps.adrcotfas.burpeebuddy.workout.repcounter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.lang.ref.WeakReference;

public class RepCounter implements SensorEventListener {

    private final int INITIAL_COUNTER_VALUE = -1;

    public interface Listener {
        public void onRepCompleted();

        public void onSensorError();
    }

    private final WeakReference<Context> mContext;
    private SensorManager mSensorManager;

    private Listener mListener;
    private float MAX_RANGE = 0;

    private long mReps = INITIAL_COUNTER_VALUE;

    public RepCounter(Context context, Listener listener) {
        this.mContext = new WeakReference<>(context);
        this.mListener = listener;
        init();
    }

    private void init() {
        mSensorManager = (SensorManager)
                mContext.get().getSystemService(Context.SENSOR_SERVICE);

        Sensor proximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (proximitySensor == null) {
            mListener.onSensorError();
            return;
        }

        MAX_RANGE = proximitySensor.getMaximumRange();

        mSensorManager.registerListener(
                this,
                proximitySensor,
                android.hardware.SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
        mReps = INITIAL_COUNTER_VALUE;
    }

    public long getReps() {
        return mReps;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float distance = event.values[0];
        if (distance == MAX_RANGE) {
            ++mReps;
            mListener.onRepCompleted();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }
}
