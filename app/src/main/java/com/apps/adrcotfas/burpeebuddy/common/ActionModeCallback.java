package com.apps.adrcotfas.burpeebuddy.common;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.apps.adrcotfas.burpeebuddy.R;

public class ActionModeCallback implements ActionMode.Callback{

    public interface Listener {
        void actionSelectAllItems();
        void actionDelete();
        void destroyActionMode();
        void editSelected();
    }

    private Listener listener;
    private Menu menu;
    private boolean showEdit;

    public ActionModeCallback(Listener listener, boolean showEdit) {
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
                listener.editSelected();
                break;
            case R.id.action_select_all:
                listener.actionSelectAllItems();
                break;
            case R.id.action_delete:
                listener.actionDelete();
                break;
        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        listener.destroyActionMode();
    }

    public void toggleEditButtonVisibility(boolean visible) {
        menu.getItem(0).setVisible(visible);
    }
}
