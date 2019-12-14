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

import com.apps.adrcotfas.burpeebuddy.R;
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
        return mViewMvc.getRootView();
    }

    @Override
    public void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();
        mViewMvc.registerListener(this);
        if (!WorkoutService.isStarted) {
            startWorkout();
        }
    }

    // TODO: unregister from certain events like update countdown timer event in onStop
    // because we're working with the display off

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
        //TODO open finished dialog, save to ROOM etc
        stopWorkout();
    }

    private void startWorkout() {
        Intent startIntent = new Intent(getActivity(), WorkoutService.class);
        startIntent.setAction(Actions.START);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().startForegroundService(startIntent);
        } else {
            getActivity().startService(startIntent);
        }
    }

    private void stopWorkout() {
        Intent stopIntent = new Intent(getActivity(), WorkoutService.class);
        stopIntent.setAction(Actions.STOP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().startForegroundService(stopIntent);
        } else {
            getActivity().startService(stopIntent);
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

    @Subscribe
    public void onMessageEvent(Events.RepCompletedEvent event) {
        Log.d(TAG, "RepCompletedEvent: " + event.size + " reps");
        mViewMvc.updateCounter(event.size);
    }

    @Subscribe
    public void onMessageEvent(Events.TimerTickEvent event) {
        Log.d(TAG, "TimerTickEvent: " + event.seconds);
        mViewMvc.updateTimer(event.seconds);
    }

    @Subscribe
    public void onMessageEvent(Events.FinishedWorkoutEvent event) {
        NavHostFragment.findNavController(this)
                .popBackStack(R.id.mainFragment, false);
    }
}
