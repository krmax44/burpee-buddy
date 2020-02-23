package com.apps.adrcotfas.burpeebuddy.common;

import com.apps.adrcotfas.burpeebuddy.common.timers.TimerType;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.db.goal.Goal;
import com.apps.adrcotfas.burpeebuddy.db.workout.Workout;

public class Events {

    public static class PreWorkoutCountdownFinished {}

    public static class RepComplete {
        public boolean lastRepInSet = false;
        public RepComplete() {
        }
        public RepComplete(boolean lastRepInSet) {
            this.lastRepInSet = lastRepInSet;
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

    public static class StartBreak {
        public int duration;
        public StartBreak(int duration) {
            this.duration = duration;
        }
    }

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

    public static class EditGoal {
        public int id;
        public Goal goal;

        public EditGoal(int id, Goal goal) {
            this.id = id;
            this.goal = goal;
        }
    }

    public static class AddGoal {
        public Goal goal;

        public AddGoal(Goal goal) {
            this.goal = goal;
        }
    }

    public static class DeleteGoal {
        public int id;
        public DeleteGoal(int id) {
            this.id = id;
        }
    }

    public static class SetFinished {
        public boolean justTheSound;

        public SetFinished() {
            this.justTheSound = false;
        }
        public SetFinished(boolean justTheSound) {
            this.justTheSound = justTheSound;
        }
    }

    public static class ToggleWorkoutEvent {
    }

    public static class UserTriggeredFinishSet {
    }

    public static class FinishedWorkoutIdle {
    }

    public static class DeleteWorkout {
        public int id;
        public DeleteWorkout(int id) {
            this.id = id;
        }
    }

    public static class AddWorkout {
        public Workout workout;
        public AddWorkout(Workout workout) {
            this.workout = workout;
        }
    }

    public static class EditWorkout {
        public int id;
        public Workout workout;
        public EditWorkout(int id, Workout workout) {
            this.id = id;
            this.workout = workout;
        }
    }

    public static class AddChallenge {
        public Challenge challenge;
        public AddChallenge(Challenge challenge) {
            this.challenge = challenge;
        }
    }

    public static class Konfetti {
    }
}
