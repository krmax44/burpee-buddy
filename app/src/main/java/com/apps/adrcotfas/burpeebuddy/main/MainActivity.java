package com.apps.adrcotfas.burpeebuddy.main;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.bl.BuddyApplication;
import com.apps.adrcotfas.burpeebuddy.common.bl.Events;
import com.apps.adrcotfas.burpeebuddy.db.AppDatabase;
import com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper;
import com.apps.adrcotfas.burpeebuddy.workout.WorkoutFinishedDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private BottomNavigationView navView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);

        navView = findViewById(R.id.nav_view);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavigationUI.setupWithNavController(navView, navHostFragment.getNavController());
            NavigationUI.setupActionBarWithNavController(this, navHostFragment.getNavController());
        }

        if (SettingsHelper.isFirstRun()) {
            AppDatabase.getDatabase(getApplicationContext());
            AppDatabase.populateExercises(getApplicationContext());
            SettingsHelper.setIsFirstRun(false); // TODO: set to false
        }
    }

    public void setBottomNavigationVisibility(int visibility) {
        navView.setVisibility(visibility);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onMessageEvent(Events.FinishedWorkoutEvent event) {
        WorkoutFinishedDialog.getInstance(
                BuddyApplication.getWorkoutManager().getWorkout().totalReps.getValue())
                .show(getSupportFragmentManager(), TAG);
    }
}
