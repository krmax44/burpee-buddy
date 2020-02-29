package com.apps.adrcotfas.burpeebuddy.main;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.Events;
import com.apps.adrcotfas.burpeebuddy.db.AppDatabase;
import com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper;
import com.apps.adrcotfas.burpeebuddy.workout.WorkoutFragment;
import com.google.android.material.navigation.NavigationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private AppBarConfiguration appBarConfiguration;
    private NavController navController;

    private KonfettiView mKonfetti;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mKonfetti = findViewById(R.id.viewKonfetti);

        NavHostFragment fragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        navController = fragment.getNavController();
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.mainFragment)
                .setDrawerLayout(drawerLayout)
                .build();

        // disable nav drawer in the workout screen
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if(destination.getId() == R.id.workoutFragment) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        });

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        if (SettingsHelper.isFirstRun()) {
            AppDatabase.getDatabase(getApplicationContext());
            AppDatabase.populateExercises(getApplicationContext());
            SettingsHelper.setIsFirstRun(false);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavDestination destination = navController.getCurrentDestination();
        if (destination != null && destination.getId() == R.id.workoutFragment) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                    if (fragment != null) {
                        WorkoutFragment workoutFragment =
                                (WorkoutFragment)fragment.getChildFragmentManager().getPrimaryNavigationFragment();
                                if (workoutFragment != null){
                                    workoutFragment.onStopButtonClicked();
                                }
                    }
            return false;
        }
        return NavigationUI.navigateUp(navController, appBarConfiguration);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onMessageEvent(Events.Konfetti event) {
        mKonfetti.build()
                //TODO: add nicer colors
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(new Size(12, 5))
                .setPosition(-50f, mKonfetti.getWidth() + 50f, -50f, -50f)
                .streamFor(300, 3000L);
    }
}
