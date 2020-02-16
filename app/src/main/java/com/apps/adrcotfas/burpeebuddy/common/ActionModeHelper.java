package com.apps.adrcotfas.burpeebuddy.common;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.apps.adrcotfas.burpeebuddy.R;

import java.util.ArrayList;
import java.util.List;

public class ActionModeHelper<IdType> implements ActionMode.Callback {

    public interface Listener {
        void actionSelectAllItems();
        void actionDeleteSelected();
        void actionEditSelected();
        void startActionMode();
        void stopActionMode();
    }

    private List<IdType> selectedEntries = new ArrayList<>();

    private Listener listener;
    private Menu menu;
    private boolean showEdit;

    private ActionMode actionMode;
    private boolean isMultiSelect = false;

    public ActionModeHelper(Listener listener, boolean showEdit) {
        this.listener = listener;
        this.showEdit = showEdit;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_all_entries_selection, menu);
        this.menu = menu;
        menu.getItem(0).setVisible(showEdit);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false; // do nothing
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                listener.actionEditSelected();
                break;
            case R.id.action_select_all:
                listener.actionSelectAllItems();
                break;
            case R.id.action_delete:
                listener.actionDeleteSelected();
                break;
        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        listener.stopActionMode();
        finishAction();
        isMultiSelect = false;
        selectedEntries = new ArrayList<>();
        actionMode = null;
    }

    public void toggleEditButtonVisibility(boolean visible) {
        if (!visible && selectedEntries.size() == 1) {
            return;
        }
        menu.getItem(0).setVisible(visible);
    }


    public void onItemClick(IdType id) {
        if (isMultiSelect) {
            multiSelect(id);
        }
    }

    public void onItemLongClick(IdType id) {
        if (!isMultiSelect) {
            isMultiSelect = true;
            selectedEntries.clear();
            listener.startActionMode();
        }
        multiSelect(id);
    }

    private void multiSelect(IdType id) {
        int idx = selectedEntries.indexOf(id);
        if (idx != -1) {
            selectedEntries.remove(idx);
        }  else {
            selectedEntries.add(id);
        }
        if (!selectedEntries.isEmpty()) {
            toggleEditButtonVisibility(showEdit && selectedEntries.size() == 1);
            actionMode.setTitle(String.valueOf(selectedEntries.size()));
        } else {
            finishAction();
        }
    }

    private void finishAction() {
        if (actionMode != null) {
            actionMode.setTitle("");
            actionMode.finish();
        }
    }

    public List<IdType> getSelectedEntries() {
        return selectedEntries;
    }

    public void setSelectedEntries(List<IdType> ids) {
        selectedEntries = ids;
        if (!selectedEntries.isEmpty()) {
            actionMode.setTitle(String.valueOf(selectedEntries.size()));
        }  else {
            finishAction();
        }
    }

    public void setActionMode(ActionMode actionMode) {
        if (this.actionMode == null) {
            this.actionMode = actionMode;
        }
    }

    public void destroyActionMode() {
        onDestroyActionMode(actionMode);
    }

    public boolean isMultiSelect() {
        return isMultiSelect;
    }
}
