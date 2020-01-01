package com.apps.adrcotfas.burpeebuddy.workout.manager;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

//TODO: fusion with light sensor too
public class RepCounter implements SensorEventListener {

    private final Sensor mProximitySensor;

    /**
     * A workaround to skip the first rep because it's not a real, just the sensor changing state.
     */
    private boolean mSkipFirstRep;

    public interface Listener {
        void onRepCompleted();
    }

    private final SensorManager mSensorManager;
    private RepCounter.Listener mListener;
    private final float MAX_RANGE;

    public RepCounter(Context context) {
        mSkipFirstRep = true;
        mSensorManager = (SensorManager)
                context.getSystemService(Context.SENSOR_SERVICE);

        mProximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (mProximitySensor == null) {
            //TODO: signal this to the outside in a nicer way
            throw new RuntimeException("Device does not have a proximity sensor");
        }
        MAX_RANGE = mProximitySensor.getMaximumRange();
    }

    public void register(RepCounter.Listener listener) {
        mListener = listener;
        mSensorManager.registerListener(
                this,
                mProximitySensor,
                android.hardware.SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregister() {
        mSkipFirstRep = true;
        mListener = null;
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mListener == null || mSkipFirstRep) {
            mSkipFirstRep = false;
            return;
        }
        float distance = event.values[0];
        if (distance == MAX_RANGE) {
            mListener.onRepCompleted();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }
}
