package com.apps.adrcotfas.burpeebuddy.common.viewmvc.actionMode;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableViewMvc;

import java.util.ArrayList;
import java.util.List;

public abstract class ActionModeBaseObservableViewMvc<ItemsType>
        extends BaseObservableViewMvc<ActionModeBaseObservableViewMvc.Listener> {

    private List<Integer> mSelectedEntries = new ArrayList<>();
    private boolean mIsMultiSelect = false;

    public interface Listener {
        void startActionMode();
        void updateTitle(String valueOf);
        void finishAction();
    }

    public abstract void bindItems(ItemsType challenges);
    public abstract ActionModeRecyclerViewAdapter getAdapter();

    public void setMultiSelect(boolean value) {
        mIsMultiSelect = value;
    }

    public boolean isMultiSelect() {
        return mIsMultiSelect;
    }

    protected void startActionMode() {
        for(Listener l : getListeners()) {
            l.startActionMode();
        }
    }

    protected void multiSelect(int id) {
        int idx = mSelectedEntries.indexOf(id);
        if (idx != -1) {
            mSelectedEntries.remove(idx);
        }  else {
            mSelectedEntries.add(id);
        }
        if (!mSelectedEntries.isEmpty()) {
            for(Listener l : getListeners()) {
                l.updateTitle(String.valueOf(mSelectedEntries.size()));
            }
        } else {
            for(Listener l : getListeners()) {
                l.finishAction();
            }
        }
        getAdapter().setSelectedItems(mSelectedEntries);
    }

    public void selectAllItems(List<Integer> ids) {
        mSelectedEntries.clear();
        mSelectedEntries.addAll(ids);

        if (!mSelectedEntries.isEmpty()) {
            for(Listener l : getListeners()) {
                l.updateTitle(String.valueOf(mSelectedEntries.size()));
            }
            getAdapter().setSelectedItems(mSelectedEntries);
        }  else {
            for(Listener l : getListeners()) {
                l.finishAction();
            }
        }
    }

    public void unselectItems() {
        mIsMultiSelect = false;
        mSelectedEntries = new ArrayList<>();
        getAdapter().setSelectedItems(new ArrayList<>());
    }

    public List<Integer> getSelectedEntries() {
        return mSelectedEntries;
    }
}
