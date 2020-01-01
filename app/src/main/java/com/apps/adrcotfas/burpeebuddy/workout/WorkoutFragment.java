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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.BuddyApplication;
import com.apps.adrcotfas.burpeebuddy.common.Events;
import com.apps.adrcotfas.burpeebuddy.common.utilities.Power;
import com.apps.adrcotfas.burpeebuddy.main.MainActivity;
import com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper;
import com.apps.adrcotfas.burpeebuddy.workout.manager.State;
import com.apps.adrcotfas.burpeebuddy.workout.view.WorkoutViewMvc;
import com.apps.adrcotfas.burpeebuddy.workout.view.WorkoutViewMvcImpl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import timber.log.Timber;

public class WorkoutFragment extends Fragment implements WorkoutViewMvc.Listener {

    private static final String TAG = "WorkoutFragment";
    private WorkoutViewMvc mViewMvc;

    public WorkoutFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        Timber.tag(TAG).d( "onAttach" + this.hashCode());
        super.onAttach(context);
        ((MainActivity)getActivity()).setBottomNavigationVisibility(View.INVISIBLE);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // TODO: show dialog to stop session
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.tag(TAG).d( "onCreateView " + this.hashCode());

        mViewMvc = new WorkoutViewMvcImpl(inflater, container);
        EventBus.getDefault().register(this);

        return mViewMvc.getRootView();
    }

    @Override
    public void onResume() {
        Timber.tag(TAG).d( "onResume " + this.hashCode());
        super.onResume();
        mViewMvc.registerListener(this);

        final State state = BuddyApplication.getWorkoutManager().getWorkout().state;

        switch (state) {
            case INACTIVE:
                // coming from Main
                startWorkout();
                break;
            case ACTIVE:
                // coming from another screen / the app was sent to background
                // do nothing
                break;
            case PAUSED:
                navigateToConfirmStopDialog();
                // the stop button or toggle button was pressed, the user left the app and returned
                break;
            case SET_FINISHED:
            case WORKOUT_FINISHED:
                navigateToFinishDialog();
                break;
        }
    }

    @Override
    public void onDestroy() {
        Timber.tag(TAG).d( "onDestroy " + this.hashCode());
        mViewMvc.unregisterListener(this);
        EventBus.getDefault().unregister(this);
        ((MainActivity)getActivity()).setBottomNavigationVisibility(View.VISIBLE);
        super.onDestroy();
    }

    @Override
    public void onStopButtonClicked() {
        if (BuddyApplication.getWorkoutManager().getWorkout().state == State.PRE_WORKOUT) {
            EventBus.getDefault().post(new Events.StopWorkoutEvent());
            NavHostFragment.findNavController(this).navigate(R.id.action_workout_to_main);
        } else {
            EventBus.getDefault().post(new Events.ToggleWorkoutEvent());
            navigateToConfirmStopDialog();
        }
    }

    private void startWorkout() {
        Intent startIntent = new Intent(getActivity(), WorkoutService.class);
        startIntent.putExtras(getArguments());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().startForegroundService(startIntent);
        } else {
            getActivity().startService(startIntent);
        }
    }

    @Subscribe
    public void onMessageEvent(Events.PreWorkoutCountdownFinished event) {
        //TODO: and if the workout is countable
        if (SettingsHelper.autoLockEnabled() && Power.isScreenOn(getActivity())) {
            Power.lockScreen((AppCompatActivity) getActivity());
        }
    }

    @Subscribe
    public void onMessageEvent(Events.RepComplete event) {
        mViewMvc.updateCounter(event.reps);
    }

    @Subscribe
    public void onMessageEvent(Events.TimerTickEvent event) {
        mViewMvc.updateTimer(event.seconds);
    }

    @Subscribe
    public void onMessageEvent(Events.FinishedWorkoutEvent event) {
        Timber.tag(TAG).v("FinishedWorkoutEvent " + this.hashCode());
        //TODO show finish dialog with special title (no 3/3 but "Workout finished")
        navigateToFinishDialog();
    }

    @Subscribe
    public void onMessageEvent(Events.SetFinished event) {
        Timber.tag(TAG).v("SetFinished " + this.hashCode());
        navigateToFinishDialog();
    }

    private void navigateToFinishDialog() {
        Timber.tag(TAG).v("navigateToFinishDialog");
        NavHostFragment.findNavController(this).navigate(R.id.action_workout_to_set_finished_dialog);
    }

    private void navigateToConfirmStopDialog() {
        Timber.tag(TAG).v("navigateToConfirmStopDialog");
        NavHostFragment.findNavController(this).navigate(R.id.action_workoutFragment_to_confirmStopDialog);
    }
}
