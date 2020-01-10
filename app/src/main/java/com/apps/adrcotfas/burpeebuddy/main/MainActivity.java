package com.apps.adrcotfas.burpeebuddy.main;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.db.AppDatabase;
import com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper;
import com.apps.adrcotfas.burpeebuddy.workout.WorkoutFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "MainActivity";

    private AppBarConfiguration appBarConfiguration;
    private NavController navController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.mainFragment)
                .setDrawerLayout(drawerLayout)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        if (SettingsHelper.isFirstRun()) {
            AppDatabase.getDatabase(getApplicationContext());
            AppDatabase.populateExercises(getApplicationContext());
            SettingsHelper.setIsFirstRun(false); // TODO: set to false
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
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settingsFragment) {
            navController.navigate(R.id.settingsFragment);
            return true;
        }
        return false;
    }
}
