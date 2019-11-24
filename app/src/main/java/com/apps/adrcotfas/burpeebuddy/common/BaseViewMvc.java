package com.apps.adrcotfas.burpeebuddy.common;

import android.content.Context;
import android.view.View;

public abstract class BaseViewMvc implements ViewMvc{

    private View mRootView;

    @Override
    public View getRootView() {
        return mRootView;
    }

    protected Context getContext() {
        return getRootView().getContext();
    }

    protected void setRootView(View view) {
        mRootView = view;
    }

    protected <T extends View> T findViewById(int id) {
        return getRootView().findViewById(id);
    }
}
