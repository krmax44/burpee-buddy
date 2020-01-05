package com.apps.adrcotfas.burpeebuddy.workout.manager;

public enum State {
    INACTIVE,
    PRE_WORKOUT,
    ACTIVE,
    PAUSED,
    BREAK_ACTIVE,
    BREAK_PAUSED,
    SET_FINISHED,
    WORKOUT_FINISHED,
    WORKOUT_FINISHED_IDLE
}

/*
 main screen: INACTIVE
 workout screen:
 - INACTIVE to PRE_WORKOUT
 - PRE_WORKOUT to ACTIVE (wait)
 - PRE_WORKOUT to INACTIVE (stop button -> navigate to main)
 - ACTIVE to PAUSED (stop button -> confirmation dialog)
 - ACTIVE to SET_FINISHED (user triggered manually / countdown finished or reps finished -> show dialog if auto-break is off)
 - ACTIVE to BREAK_ACTIVE (auto-break is on or user pressed ok in previous dialog)
 - BREAK_ACTIVE to BREAK_PAUSED (user pressed stop)
 - BREAK_PAUSED to BREAK_ACTIVE (user pressed cancel in confirmation dialog)
 - BREAK_PAUSED to INACTIVE (user pressed ok in confirmation dialog)
 - BREAK_ACTIVE to ACTIVE (break finished or skip break)
 - ACTIVE to WORKOUT_FINISHED (user triggered manually / countdown finished or reps finished -> show dialog)
 - WORKOUT_FINISHED to WORKOUT_FINISHED_IDLE (user pressed ok in finished dialog)
 - WORKOUT_FINISHED_IDLE to INACTIVE (navigate to main fragment)
 */