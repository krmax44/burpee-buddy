package com.apps.adrcotfas.burpeebuddy.common.bl;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class RepCounter implements SensorEventListener {

    private final Sensor mProximitySensor;

    public interface Listener {
        void onRepCompleted();
    }

    private final SensorManager mSensorManager;
    private RepCounter.Listener mListener;
    private final float MAX_RANGE;

    public RepCounter(Context context) {
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
        mListener = null;
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
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
