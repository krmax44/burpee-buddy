package com.apps.adrcotfas.burpeebuddy.edit_challenges.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.ActionModeHelper;
import com.apps.adrcotfas.burpeebuddy.common.recyclerview.RecyclerItemClickListener;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableView;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;

import java.util.ArrayList;
import java.util.List;

public class ChallengeViewImpl extends BaseObservableView<ChallengeView.Listener>
        implements ChallengeView, ActionModeHelper.Listener{

    private RecyclerView recyclerView;
    private ChallengesFragmentAdapter adapter;
    private LinearLayout emptyState;
    private List<Challenge> challenges = new ArrayList<>();

    private ActionModeHelper actionModeHelper;

    public ChallengeViewImpl(LayoutInflater inflater, ViewGroup parent) {
        actionModeHelper = new ActionModeHelper(this, false);

        setRootView(inflater.inflate(R.layout.fragment_recycler, parent, false));

        emptyState = findViewById(R.id.empty_state);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChallengesFragmentAdapter(inflater);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext()
                , recyclerView
                , new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                actionModeHelper.onItemClick(challenges.get(position).id);
                adapter.setSelectedItems(actionModeHelper.getSelectedEntries());
            }
            @Override
            public void onItemLongClick(View view, int position) {
                actionModeHelper.onItemLongClick(challenges.get(position).id);
                adapter.setSelectedItems(actionModeHelper.getSelectedEntries());
            }
        }));
    }

    @Override
    public void bindChallenges(List<Pair<Challenge, Integer>> challenges) {
        recyclerView.setVisibility(challenges.isEmpty() ? View.GONE : View.VISIBLE);
        emptyState.setVisibility(challenges.isEmpty() ? View.VISIBLE : View.GONE);

        adapter.bindChallenges(challenges);

        this.challenges.clear();
        for (Pair<Challenge, Integer> o : challenges) {
            this.challenges.add(o.first);
        }
    }


    @Override
    public void actionSelectAllItems() {
        actionModeHelper.toggleEditButtonVisibility(false);

        List<Integer> ids = new ArrayList<>(challenges.size());
        for (Challenge w : challenges) {
            ids.add(w.id);
        }

        if (!ids.isEmpty()) {
            adapter.setSelectedItems(ids);
        }
        actionModeHelper.setSelectedEntries(ids);
    }

    @Override
    public void actionDeleteSelected() {
        for (Listener l : getListeners()) {
            l.onDeleteSelected(actionModeHelper.getSelectedEntries());
        }
    }

    @Override
    public void actionEditSelected() {}

    @Override
    public void startActionMode() {
        for (Listener l : getListeners()) {
            l.startActionMode(actionModeHelper);
            break; // workaround: there's only one listener
        }
    }

    @Override
    public void destroyActionMode() {
        adapter.setSelectedItems(new ArrayList<>());
    }

}
