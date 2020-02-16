package com.apps.adrcotfas.burpeebuddy.workout;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.BuddyApplication;
import com.apps.adrcotfas.burpeebuddy.common.Events;
import com.apps.adrcotfas.burpeebuddy.db.AppDatabase;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalType;
import com.apps.adrcotfas.burpeebuddy.db.workout.Workout;
import com.apps.adrcotfas.burpeebuddy.workout.manager.InProgressWorkout;
import com.apps.adrcotfas.burpeebuddy.workout.manager.State;
import com.apps.adrcotfas.burpeebuddy.workout.view.WorkoutView;
import com.apps.adrcotfas.burpeebuddy.workout.view.WorkoutViewImpl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import timber.log.Timber;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

public class WorkoutFragment extends Fragment implements WorkoutView.Listener {

    private static final String TAG = "WorkoutFragment";
    private WorkoutView mViewMvc;

    public static boolean isInPausedBreakState = false;

    public WorkoutFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                    onStopButtonClicked();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.tag(TAG).d( "onCreateView " + this.hashCode());
        getActivity().getWindow().addFlags(FLAG_KEEP_SCREEN_ON);
        mViewMvc = new WorkoutViewImpl(inflater, container);
        EventBus.getDefault().register(this);

        return mViewMvc.getRootView();
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewMvc.registerListener(this);

        final State state = BuddyApplication.getWorkoutManager().getWorkout().getState();
        Timber.tag(TAG).d( "onResume, current state: %s", state.toString());

        switch (state) {
            case INACTIVE:
                // coming from Main
                mViewMvc.toggleRowAppearance(false);
                startWorkout();
                break;
            case ACTIVE:
                // coming from another screen / the app was sent to background
                setFinishSetButtonVisibility(View.VISIBLE);
                break;
            case PAUSED:
            case BREAK_PAUSED:
                navigateToConfirmStopDialog();
                // the stop button or toggle button was pressed, the user left the app and returned
                break;
            case SET_FINISHED:
            case WORKOUT_FINISHED:
                navigateToFinishDialog();
                break;
            case WORKOUT_FINISHED_IDLE:
                break;
        }
    }

    @Override
    public void onDestroy() {
        Timber.tag(TAG).d( "onDestroy ");
        mViewMvc.unregisterListener(this);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onStopButtonClicked() {
        final State state = BuddyApplication.getWorkoutManager().getWorkout().getState();
        if (state == State.PRE_WORKOUT || state == State.WORKOUT_FINISHED_IDLE) {
            EventBus.getDefault().post(new Events.StopWorkoutEvent());
            NavHostFragment.findNavController(this).navigate(R.id.action_workout_to_main);
        } else {
            if (state == State.BREAK_ACTIVE) {
                isInPausedBreakState = true;
            }
            EventBus.getDefault().post(new Events.ToggleWorkoutEvent());
            navigateToConfirmStopDialog();
        }
    }

    @Override
    public void onFinishSetButtonClicked() {
        EventBus.getDefault().post(new Events.UserTriggeredFinishSet());
    }

    private void startWorkout() {
        Intent startIntent = new Intent(getActivity(), WorkoutService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().startForegroundService(startIntent);
        } else {
            getActivity().startService(startIntent);
        }
    }

    private void setFinishSetButtonVisibility(int visible) {
        if (getWorkout().getGoalType() == GoalType.REPS &&
                getWorkout().getExerciseType() == ExerciseType.UNCOUNTABLE) {
            mViewMvc.setFinishSetButtonVisibility(visible);
        }
    }

    @Subscribe
    public void onMessageEvent(Events.PreWorkoutCountdownFinished event) {
        //TODO: handle this later
        //TODO: and if the workout is countable
//        if (SettingsHelper.autoLockEnabled() && Power.isScreenOn(getActivity())) {
//            Power.lockScreen((AppCompatActivity) getActivity());
//        }
        setFinishSetButtonVisibility(View.VISIBLE);
        mViewMvc.toggleRowAppearance(true);
    }

    @Subscribe
    public void onMessageEvent(Events.RepComplete event) {
        mViewMvc.onRepComplete();
    }

    @Subscribe
    public void onMessageEvent(Events.TimerTickEvent event) {
        mViewMvc.onTimerTick(event.seconds);
    }

    @Subscribe
    public void onMessageEvent(Events.FinishedWorkoutEvent event) {
        Timber.tag(TAG).v("FinishedWorkoutEvent ");

        getActivity().getWindow().clearFlags(FLAG_KEEP_SCREEN_ON);

        navigateToFinishDialog();
        mViewMvc.onWorkoutFinished();
    }

    @Subscribe
    public void onMessageEvent(Events.FinishedWorkoutIdle event) {
        final Workout workout = new Workout(
                getWorkout().getExerciseName(),
                getWorkout().getExerciseType(),
                System.currentTimeMillis(),
                getWorkout().getTotalDuration(),
                getWorkout().getTotalReps(),
                getWorkout().getTotalAvgPace());

        AppDatabase.addWorkout(getContext(), workout);
    }

    @Subscribe
    public void onMessageEvent(Events.SetFinished event) {
        if (!event.justTheSound) {
            navigateToFinishDialog();
        }
    }

    @Subscribe
    public void onMessageEvent(Events.StartBreak event) {
        if (!isInPausedBreakState) {
            mViewMvc.onStartBreak();
            mViewMvc.toggleRowAppearance(false);
        } else {
            isInPausedBreakState = false;
        }
        setFinishSetButtonVisibility(View.GONE);
    }

    @Subscribe
    public void onMessageEvent(Events.ToggleWorkoutEvent event) {
    }

    private void navigateToFinishDialog() {
        Timber.tag(TAG).v("navigateToFinishDialog");
        final NavController navController = NavHostFragment.findNavController(this);
        final NavDestination destination = navController.getCurrentDestination();
        if (destination != null && destination.getId() == R.id.workoutFragment) {
            navController.navigate(R.id.action_workout_to_set_finished_dialog);
        }
    }

    private void navigateToConfirmStopDialog() {
        Timber.tag(TAG).v("navigateToConfirmStopDialog");

        final NavDestination destination = NavHostFragment.findNavController(this).getCurrentDestination();
        if (destination != null && destination.getId() == R.id.workoutFragment) {
            NavHostFragment.findNavController(this).navigate(R.id.action_workoutFragment_to_confirmStopDialog);
        }
    }

    private InProgressWorkout getWorkout() {
        return BuddyApplication.getWorkoutManager().getWorkout();
    }
}
