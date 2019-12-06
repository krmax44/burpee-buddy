package com.apps.adrcotfas.burpeebuddy.common.bl;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class RepCounter implements SensorEventListener {

    public interface Listener {
        void onRepCompleted();
    }

    private SensorManager mSensorManager;
    private RepCounter.Listener mListener;
    private float MAX_RANGE = 0;

    public RepCounter(Context context) {
        mSensorManager = (SensorManager)
                context.getSystemService(Context.SENSOR_SERVICE);

        Sensor proximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (proximitySensor == null) {
            //TODO: signal this to the outside
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
    }

    public void register(RepCounter.Listener listener) {
        mListener = listener;
    }

    public void unregister() {
        mListener = null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mListener == null) {
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
