package com.apps.adrcotfas.burpeebuddy.common.bl;

import com.apps.adrcotfas.burpeebuddy.common.timers.TimerType;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;

public class Events {

    public static class PreWorkoutCountdownFinished {}

    public static class RepComplete {
        public int reps;
        public boolean lastRepThisSet;

        public RepComplete(int reps) {
            this.reps = reps;
        }

        public RepComplete(int reps, boolean lastRepThisSet) {
            this.reps = reps;
            this.lastRepThisSet = lastRepThisSet;
        }
    }

    public static class TimerTickEvent {
        public int seconds;
        public TimerType type;

        public TimerTickEvent(TimerType type, int seconds) {
            this.seconds = seconds;
            this.type = type;
        }
    }

    public static class SetComplete {}

    /**
     * When the user presses a stop button
     */
    public static class StopWorkoutEvent {}

    /**
     * When the reps or time based goal is achieved
     */
    public static class FinishedWorkoutEvent {}

    public static class EditExercise {
        public String exerciseToEdit;
        public Exercise exercise;

        public EditExercise(String exerciseToEdit, Exercise exercise) {
            this.exerciseToEdit = exerciseToEdit;
            this.exercise = exercise;
        }
    }

    public static class AddExercise {
        public Exercise exercise;

        public AddExercise(Exercise exercise) {
            this.exercise = exercise;
        }
    }

    public static class DeleteExercise {
        public String name;
        public DeleteExercise(String name) {
            this.name = name;
        }
    }
}
