package com.apps.adrcotfas.burpeebuddy.common;

import androidx.appcompat.app.AppCompatActivity;

import com.apps.adrcotfas.burpeebuddy.common.dependencyinjection.ControllerCompositionRoot;

public class BaseActivity extends AppCompatActivity {

    private ControllerCompositionRoot mControllerCompositionRoot;

    protected ControllerCompositionRoot getCompositionRoot() {
        if (mControllerCompositionRoot == null) {
            mControllerCompositionRoot = new ControllerCompositionRoot(this);
        }
        return mControllerCompositionRoot;
    }
}
