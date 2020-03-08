package com.apps.adrcotfas.burpeebuddy.main;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.apps.adrcotfas.burpeebuddy.BuildConfig;
import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.Events;
import com.apps.adrcotfas.burpeebuddy.common.utilities.DeviceInfo;
import com.apps.adrcotfas.burpeebuddy.db.AppDatabase;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.intro.MainIntroActivity;
import com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper;
import com.apps.adrcotfas.burpeebuddy.workout.WorkoutFragment;
import com.google.android.material.navigation.NavigationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_INTRO = 123;

    private AppBarConfiguration appBarConfiguration;
    private NavController navController;

    private KonfettiView mKonfetti;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupFirstRun();

        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
    }

    private void setupFirstRun() {
        if(SettingsHelper.isFirstRun()) {
            AppDatabase.getDatabase(getApplicationContext());
            AppDatabase.populateExercises(getApplicationContext());
            Intent i = new Intent(MainActivity.this, MainIntroActivity.class);
            startActivityForResult(i, REQUEST_CODE_INTRO);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INTRO) {
            LiveData<List<Exercise>> allVisible = AppDatabase.getDatabase(this).exerciseDao().getAllVisible();
            allVisible.observe(this, exercises -> {
                if (exercises.isEmpty()) {
                    AppDatabase.enableAllExercises(this);
                    allVisible.removeObservers(this);
                }
            });
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    private void openFeedback() {
        Intent email = new Intent(Intent.ACTION_SENDTO);
        email.setData(new Uri.Builder().scheme("mailto").build());
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"burpee-buddy@googlegroups.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, "[Burpee Buddy] Feedback");
        email.putExtra(Intent.EXTRA_TEXT, "\nMy device info: \n" + DeviceInfo.getDeviceInfo()
                + "\nApp version: " + BuildConfig.VERSION_NAME);
        try {
            startActivity(Intent.createChooser(email, "Send feedback"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.feedback) {
            openFeedback();
        } else {
            NavigationUI.onNavDestinationSelected(item, navController);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
