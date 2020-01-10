package com.apps.adrcotfas.burpeebuddy.workout.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;

public class ViewHelper {
    static void setBackgroundTint(Context context, View view, int colorId) {
        view.setBackgroundTintList(ColorStateList.valueOf(
                context.getResources().getColor(colorId)));
    }
}
