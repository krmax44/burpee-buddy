package com.apps.adrcotfas.burpeebuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

public class MainActivity extends AppCompatActivity implements MainViewMvcImpl.Listener{

    MainViewMvc mMainViewMvc;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMainViewMvc = new MainViewMvcImpl(LayoutInflater.from(this), null);
        setContentView(mMainViewMvc.getRootView());
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);
        mMainViewMvc.registerListener(this);
    }

    @Override
    protected void onDestroy() {
        mMainViewMvc.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public void onStartButtonClicked() {
        //TODO jump to the start workout activity
    }
}
