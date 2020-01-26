package com.apps.adrcotfas.burpeebuddy.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;
import com.apps.adrcotfas.burpeebuddy.db.challenge.ChallengeDao;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseDao;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseGenerator;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalDao;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalGenerator;
import com.apps.adrcotfas.burpeebuddy.db.workout.Workout;
import com.apps.adrcotfas.burpeebuddy.db.workout.WorkoutDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Workout.class, Exercise.class, Goal.class, Challenge.class},
        version = 1, exportSchema = false)
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
    public abstract ExerciseDao exerciseDao();
    public abstract GoalDao goalDao();
    public abstract ChallengeDao challengeDao();

    /**
     * To be called once when the user first opens the app to populate the database with
     * the default exercises and goals
     */
    public static void populateExercises(Context context) {
        mExecutorService.execute(() -> {
            getDatabase(context).exerciseDao().insertAll(
                    ExerciseGenerator.getDefaultWorkouts());
            getDatabase(context).goalDao().insertAll(
                    GoalGenerator.getDefaultGoals());
        });
    }

    // TODO: maybe move these methods to separate ViewModels
    public static void editVisibility(Context context, String exercise, boolean visibility) {
        mExecutorService.execute(()
                -> getDatabase(context).exerciseDao().editVisibility(exercise, visibility));
    }

    public static void editExerciseOrder(Context context, String name, int i) {
        mExecutorService.execute(()
                -> getDatabase(context).exerciseDao().editOrder(name, i));
    }

    public static void editExercise(Context context, String name, Exercise exercise) {
        mExecutorService.execute(()
                -> getDatabase(context).exerciseDao().editExercise(
                name,
                exercise.name,
                exercise.type));
    }

    public static void addExercise(Context context, Exercise exercise) {
        mExecutorService.execute(()
                -> getDatabase(context).exerciseDao().addExercise(exercise));
    }

    public static void deleteExercise(Context context, String name) {
        mExecutorService.execute(()
                -> getDatabase(context).exerciseDao().delete(name));
    }

    public static void addChallenge(Context context, Challenge challenge) {
        mExecutorService.execute(()
                -> getDatabase(context).challengeDao().addChallenge(challenge));
    }

    public static void deleteChallenge(Context context, int id) {
        mExecutorService.execute(()
                -> getDatabase(context).challengeDao().delete(id));
    }
    public static void addGoal(Context context, Goal goal) {
        mExecutorService.execute(()
                -> getDatabase(context).goalDao()
                .addGoal(goal));
    }

    public static void editGoal(Context context, int id, Goal goal) {
        mExecutorService.execute(()
                -> getDatabase(context).goalDao()
                .editGoal(id, goal.type, goal.sets, goal.reps, goal.duration, goal.duration_break));
    }

    public static void deleteGoal(Context context, int id) {
        mExecutorService.execute(()
                -> getDatabase(context).goalDao()
                .deleteGoal(id));
    }

    public static void addWorkout(Context context, Workout workout) {
        mExecutorService.execute(()
                -> getDatabase(context).workoutDao().addWorkout(workout));
    }

    public static void editWorkout(Context context, int id, Workout workout) {
        mExecutorService.execute(()
                -> getDatabase(context).workoutDao().editWorkout(
                        id,
                workout.type,
                workout.timestamp,
                workout.duration,
                workout.reps,
                workout.pace));
    }

    public static void deleteWorkout(Context context, int id) {
        mExecutorService.execute(()
            -> getDatabase(context).workoutDao().delete(id));
    }

    public static void completeChallenge(Context context, int id, long date, boolean failed) {
        mExecutorService.execute(()
                -> getDatabase(context).challengeDao().completeChallenge(id, date, failed));
    }
}
