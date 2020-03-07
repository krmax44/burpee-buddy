package com.apps.adrcotfas.burpeebuddy.intro;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.apps.adrcotfas.burpeebuddy.db.AppDatabase;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.main.MainActivity;
import com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper;

import java.util.List;

public class LauncherActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_INTRO = 123;

    @Override
    public void onCreate(Bundle b){
        super.onCreate(b);

        if(SettingsHelper.isFirstRun()) {
            AppDatabase.getDatabase(getApplicationContext());
            AppDatabase.populateExercises(getApplicationContext());
            SettingsHelper.setIsFirstRun(false);
            Intent i = new Intent(LauncherActivity.this, MainIntroActivity.class);
            startActivityForResult(i, REQUEST_CODE_INTRO);
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
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
            finish();
        }
    }
}
