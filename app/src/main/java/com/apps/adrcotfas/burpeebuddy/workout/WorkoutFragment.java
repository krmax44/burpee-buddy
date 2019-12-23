package com.apps.adrcotfas.burpeebuddy.workout;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.apps.adrcotfas.burpeebuddy.common.bl.BuddyApplication;
import com.apps.adrcotfas.burpeebuddy.common.bl.Events;
import com.apps.adrcotfas.burpeebuddy.common.utilities.Power;
import com.apps.adrcotfas.burpeebuddy.main.MainActivity;
import com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class WorkoutFragment extends Fragment implements WorkoutViewMvc.Listener {

    private static final String TAG = "WorkoutFragment";
    private WorkoutViewMvc mViewMvc;

    public WorkoutFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        Log.v(TAG, "onAttach");
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
        Log.v(TAG, "onCreateView");

        mViewMvc = new WorkoutViewMvcImpl(inflater, container);
        EventBus.getDefault().register(this);

        BuddyApplication.getWorkoutManager().getWorkout().totalReps.observe(
                getViewLifecycleOwner(), reps -> onRepCompleted(reps));
        BuddyApplication.getWorkoutManager().getTimer().getElapsedSeconds().observe(
                getViewLifecycleOwner(), seconds -> onTimerTick(seconds));

        return mViewMvc.getRootView();
    }

    @Override
    public void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();
        mViewMvc.registerListener(this);

        final State state = BuddyApplication.getWorkoutManager().getWorkout().state;

        if (state.equals(State.INACTIVE)) {
            startWorkout();
        } else if (state.equals(State.FINISHED)) {
            navigateToMainAndShowFinishDialog();
        }
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        mViewMvc.unregisterListener(this);
        EventBus.getDefault().unregister(this);
        ((MainActivity)getActivity()).setBottomNavigationVisibility(View.VISIBLE);
        super.onDestroy();
    }

    @Override
    public void onStopButtonClicked() {
        EventBus.getDefault().post(new Events.StopWorkoutEvent());
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
    public void onMessageEvent(Events.PreWorkoutCountdownTickEvent event) {
        Log.d(TAG, "PreWorkoutCountdownTickEvent: " + event.seconds);
        mViewMvc.updateTimer(event.seconds);
    }

    @Subscribe
    public void onMessageEvent(Events.PreWorkoutCountdownFinished event) {
        Log.d(TAG, "PreWorkoutCountdownFinished");
        //TODO switch to timer mode depending on workout type

        if (SettingsHelper.autoLockEnabled() && Power.isScreenOn(getActivity())) {
            Power.lockScreen((AppCompatActivity) getActivity());
        }
    }

    public void onRepCompleted(int reps) {
        Log.d(TAG, "RepCompletedEvent: " + reps + " reps");
        mViewMvc.updateCounter(reps);
    }

    public void onTimerTick(int seconds) {
        Log.d(TAG, "TimerTickEvent: " + seconds);
        mViewMvc.updateTimer(seconds);
    }

    @Subscribe
    public void onMessageEvent(Events.FinishedWorkoutEvent event) {
        navigateToMainAndShowFinishDialog();
    }

    @Subscribe
    public void onMessageEvent(Events.StopWorkoutEvent event) {
        navigateToMainAndShowFinishDialog();
    }

    private void navigateToMainAndShowFinishDialog() {
        WorkoutFragmentDirections.ActionWorkoutToMain action = WorkoutFragmentDirections.actionWorkoutToMain();
        action.setShowFinishedDialog(true);
        NavHostFragment.findNavController(this).navigate(action);
    }
}
