package com.apps.adrcotfas.burpeebuddy.common.dependencyinjection;

import android.app.Activity;
import android.view.LayoutInflater;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ViewMvcFactory;

public class ControllerCompositionRoot {

    private final Activity mActivity;

    public ControllerCompositionRoot(Activity activity) {
        this.mActivity = activity;
    }

    private LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(mActivity);
    }

    public ViewMvcFactory getViewMvcFactory() {
        return new ViewMvcFactory(getLayoutInflater());
    }
}
