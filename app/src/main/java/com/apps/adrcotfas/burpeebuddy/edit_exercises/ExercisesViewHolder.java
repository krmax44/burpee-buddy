package com.apps.adrcotfas.burpeebuddy.edit_exercises;

import android.os.Build;

import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.common.recyclerview.ItemTouchHelperViewHolder;
import com.apps.adrcotfas.burpeebuddy.edit_exercises.items.ExercisesItemViewMvc;

class ExercisesViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

    private final ExercisesItemViewMvc mViewMvc;

    public ExercisesItemViewMvc getViewMvc() {
        return mViewMvc;
    }

    public ExercisesViewHolder(ExercisesItemViewMvc viewMvc) {
        super(viewMvc.getRootView());
        mViewMvc = viewMvc;
    }

    @Override
    public void onItemSelected() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mViewMvc.getItem().setElevation(4);
        }
    }

    @Override
    public void onItemClear() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mViewMvc.getItem().setElevation(0);
        }
    }
}
