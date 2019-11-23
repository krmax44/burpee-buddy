package com.apps.adrcotfas.burpeebuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor proximity;

    @BindView(R.id.start_button) Button startButton;
    @BindView(R.id.stop_button) Button stopButton;
    @BindView(R.id.counter) TextView counter;

    long counterValue = 0;
    float proximityMaxRange = 0;

    boolean workoutStarted = false;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (proximity == null) {
            //TODO: not able to use app
        }

        proximityMaxRange = proximity.getMaximumRange();

        setupButtons();
    }

    private void setupButtons() {
        startButton.setOnClickListener(v -> {
            stopButton.setVisibility(View.VISIBLE);
            counter.setText("0");
            counter.setVisibility(View.VISIBLE);
            startButton.setVisibility(View.INVISIBLE);
            workoutStarted = true;
        });

        stopButton.setOnClickListener(v -> {
            workoutStarted = false;
            counterValue = 0;
            stopButton.setVisibility(View.INVISIBLE);
            counter.setVisibility(View.INVISIBLE);
            startButton.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        if (workoutStarted) {
            float distance = event.values[0];
            if (distance == proximityMaxRange) {
                counter.setText(String.valueOf(++counterValue));
            }
        }
    }

    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);
        sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        sensorManager.unregisterListener(this);
    }

}
