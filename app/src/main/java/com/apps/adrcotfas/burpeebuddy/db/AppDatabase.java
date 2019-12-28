package com.apps.adrcotfas.burpeebuddy.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseTypeDao;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseTypeFactory;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalDao;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalFactory;
import com.apps.adrcotfas.burpeebuddy.db.workout.Workout;
import com.apps.adrcotfas.burpeebuddy.db.workout.WorkoutDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Workout.class, Exercise.class, Goal.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static AppDatabase INSTANCE;
    private static ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null || !INSTANCE.isOpen()) {
            synchronized (LOCK) {
                if (INSTANCE == null || !INSTANCE.isOpen()) {
                    recreateInstance(context);
                }
            }
        }
        return INSTANCE;
    }

    public static void closeInstance() {
        if (INSTANCE.isOpen()) {
            INSTANCE.getOpenHelper().close();
        }
    }

    public static void recreateInstance(Context context) {
        INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, context.getString(R.string.database_name))
                .setJournalMode(JournalMode.TRUNCATE)
                .build();
    }

    public abstract WorkoutDao workoutDao();
    public abstract ExerciseTypeDao exerciseTypeDao();
    public abstract GoalDao goalDao();

    /**
     * To be called once when the user first opens the app to populate the database with
     * the default exercises and goals
     */
    public static void populateExercises(Context context) {
        mExecutorService.execute(() -> {
            getDatabase(context).exerciseTypeDao().insertAll(
                    ExerciseTypeFactory.getDefaultWorkouts());
            getDatabase(context).goalDao().insertAll(
                    GoalFactory.getDefaultGoals());
            });
    }

    // TODO: maybe move these methods to separate ViewModels
    public static void editVisibility(Context context, String exercise, boolean visibility) {
        mExecutorService.execute(()
                -> getDatabase(context).exerciseTypeDao().editVisibility(exercise, visibility));
    }

    public static void editExerciseOrder(Context context, String name, int i) {
        mExecutorService.execute(()
                -> getDatabase(context).exerciseTypeDao().editOrder(name, i));
    }
}
