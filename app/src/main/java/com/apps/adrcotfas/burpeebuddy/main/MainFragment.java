package com.apps.adrcotfas.burpeebuddy.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper;

public class MainFragment extends Fragment implements MainViewMvcImpl.Listener {

    private MainViewMvc mViewMvc;

    public MainFragment() {}

    @Override
    public void onStart() {
        super.onStart();
        if (SettingsHelper.isFirstRun()) {
            // show Intro
            SettingsHelper.setIsFirstRun(true); // TODO: set to false
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mViewMvc = new MainViewMvcImpl(inflater, container);
        return mViewMvc.getRootView();
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewMvc.registerListener(this);
        mViewMvc.showIntroduction();
    }

    @Override
    public void onDestroy() {
        mViewMvc.unregisterListener(this);
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
