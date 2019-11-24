package com.apps.adrcotfas.burpeebuddy.main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

public class MainActivity extends AppCompatActivity implements MainViewMvcImpl.Listener{

    MainViewMvc mViewMvc;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewMvc = new MainViewMvcImpl(LayoutInflater.from(this), null);
        setContentView(mViewMvc.getRootView());
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);
        mViewMvc.registerListener(this);
    }

    @Override
    protected void onDestroy() {
        mViewMvc.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public void onStartButtonClicked() {
        //TODO jump to the start workout activity
    }
}
