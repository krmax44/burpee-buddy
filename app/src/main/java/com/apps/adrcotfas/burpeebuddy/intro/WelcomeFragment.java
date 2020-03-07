package com.apps.adrcotfas.burpeebuddy.intro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.apps.adrcotfas.burpeebuddy.R;
import com.google.android.material.button.MaterialButton;
import com.heinrichreimersoftware.materialintro.app.SlideFragment;

public class WelcomeFragment extends SlideFragment {

    public WelcomeFragment() {}

    public static WelcomeFragment newInstance() {
        return new WelcomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        inflater.getContext().setTheme(R.style.AppTheme);
        final View v = inflater.inflate(R.layout.fragment_intro_welcome, null, false);
        MaterialButton continueButton = v.findViewById(R.id.continue_button);
        continueButton.setOnClickListener(v1 -> {
            nextSlide();
        });

        return v;
    }
}
