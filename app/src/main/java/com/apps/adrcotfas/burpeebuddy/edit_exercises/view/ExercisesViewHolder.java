package com.apps.adrcotfas.burpeebuddy.edit_exercises.view;

import android.os.Build;

import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.common.recyclerview.ItemTouchHelperViewHolder;

public class ExercisesViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

    private final ExercisesItemView view;

    public ExercisesItemView getView() {
        return view;
    }

    public ExercisesViewHolder(ExercisesItemView viewMvc) {
        super(viewMvc.getRootView());
        view = viewMvc;
    }

    @Override
    public void onItemSelected() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.getItem().setElevation(4);
        }
    }

    @Override
    public void onItemClear() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.getItem().setElevation(0);
        }
    }
}
