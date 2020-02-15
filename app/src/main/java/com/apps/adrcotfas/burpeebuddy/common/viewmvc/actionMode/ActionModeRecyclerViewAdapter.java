package com.apps.adrcotfas.burpeebuddy.common.viewmvc.actionMode;

import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

public abstract class ActionModeRecyclerViewAdapter<ItemsType, ViewHolderType extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<ViewHolderType> {

    private List<Integer> selectedItems = new ArrayList<>();

    public abstract void bindItems(ItemsType items);

    public List<Integer> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(List<Integer> selectedItems) {
        this.selectedItems = selectedItems;
        notifyDataSetChanged();
    }
}
