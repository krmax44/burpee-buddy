package com.apps.adrcotfas.burpeebuddy.common.bl;

public class Events {

    public static class PreWorkoutCountdownTickEvent {
        public PreWorkoutCountdownTickEvent(int seconds) {
            this.seconds = seconds;
        }
        public int seconds;
    }

    public static class PreWorkoutCountdownFinished {}

    public static class RepComplete {
        public RepComplete(int reps) {
            this.reps = reps;
        }
        public int reps;
    }

    public static class TimerTickEvent {
        public TimerTickEvent(int seconds) {
            this.seconds = seconds;
        }
        public int seconds;
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
}
