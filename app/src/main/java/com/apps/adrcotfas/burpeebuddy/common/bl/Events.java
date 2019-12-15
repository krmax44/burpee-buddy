package com.apps.adrcotfas.burpeebuddy.common.bl;

public class Events {
    public static class PreWorkoutCountdownFinished {}

    public static class PreWorkoutCountdownTickEvent {
        public PreWorkoutCountdownTickEvent(int seconds) {
            this.seconds = seconds;
        }
        public int seconds;
    }

    public static class TimerTickEvent {
        public TimerTickEvent(int elapsedSeconds) {
            this.seconds = elapsedSeconds;
        }
        public int seconds;
    }

    public static class RepCompletedEvent {
        public RepCompletedEvent(int size) {
            this.size = size;
        }
        public int size;
    }

    public static class FinishedWorkoutEvent {}
}
