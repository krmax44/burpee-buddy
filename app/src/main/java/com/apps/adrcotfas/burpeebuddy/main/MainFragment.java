package com.apps.adrcotfas.burpeebuddy.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.db.AppDatabase;

public class MainFragment extends Fragment implements MainViewMvcImpl.Listener {

    private static final String TAG = "MainFragment";
    private MainViewMvc mViewMvc;

    public MainFragment() {}

    @Override
    public void onStart() {
        Log.v(TAG, "onStart");
        super.onStart();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");

        AppDatabase.getDatabase(getContext()).exerciseTypeDao().getAll().observe(
                getViewLifecycleOwner(), exerciseTypes ->
                        mViewMvc.updateExerciseTypes(exerciseTypes));

        mViewMvc = new MainViewMvcImpl(inflater, container);
        return mViewMvc.getRootView();
    }

    @Override
    public void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();
        mViewMvc.registerListener(this);
        mViewMvc.showIntroduction();
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        if (mViewMvc != null){
            mViewMvc.unregisterListener(this);
        }
        super.onDestroy();
    }

    @Override
    public void onStartButtonClicked() {
        NavHostFragment.findNavController(this).navigate(R.id.action_main_to_workout);
    }

    @Override
    public void onDisabledChipClicked() {
        Toast.makeText(getActivity(), getString(R.string.implementation_not_ready), Toast.LENGTH_SHORT).show();
    }
}
