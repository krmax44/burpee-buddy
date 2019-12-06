package com.apps.adrcotfas.burpeebuddy.common.viewmvc;

import android.view.View;

public abstract class BaseViewMvc implements ViewMvc {

    private View mRootView;

    @Override
    public View getRootView() {
        return mRootView;
    }

    protected void setRootView(View view) {
        mRootView = view;
    }

    protected <T extends View> T findViewById(int id) {
        return getRootView().findViewById(id);
    }
}
