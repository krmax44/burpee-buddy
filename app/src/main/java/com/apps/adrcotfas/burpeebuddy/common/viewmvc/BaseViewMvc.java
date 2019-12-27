package com.apps.adrcotfas.burpeebuddy.common.viewmvc;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

public abstract class BaseViewMvc implements ViewMvc {

    private View mRootView;

    @Override
    public View getRootView() {
        return mRootView;
    }

    public Context getContext() {
        return getRootView().getContext();
    }

    public Drawable getDrawable(int id) {
        return getContext().getResources().getDrawable(id);
    }

    protected void setRootView(View view) {
        mRootView = view;
    }

    protected <T extends View> T findViewById(int id) {
        return getRootView().findViewById(id);
    }
}
