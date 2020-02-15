package com.apps.adrcotfas.burpeebuddy.edit_challenges.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.recyclerview.RecyclerItemClickListener;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;

import java.util.ArrayList;
import java.util.List;

public class ChallengeViewImpl extends BaseObservableViewMvc<ChallengeView.Listener>
        implements ChallengeView{

    private RecyclerView recyclerView;
    private ChallengesFragmentAdapter adapter;
    private LinearLayout emptyState;
    private List<Challenge> challenges = new ArrayList<>();

    private List<Integer> selectedEntries = new ArrayList<>();
    private boolean isMultiSelect = false;

    public ChallengeViewImpl(LayoutInflater inflater, ViewGroup parent) {
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
                if (isMultiSelect) {
                    multiSelect(position);
                }
            }
            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    adapter.setSelectedItems(new ArrayList<>());
                    isMultiSelect = true;

                    for(Listener l : getListeners()) {
                        l.startActionMode();
                    }
                }
                multiSelect(position);
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

    private void multiSelect(int position) {
        Challenge s = challenges.get(position);
        if (s != null) {
            int idx = selectedEntries.indexOf(s.id);
            if (idx != -1) {
                selectedEntries.remove(idx);
            }  else {
                selectedEntries.add(s.id);
            }
            if (!selectedEntries.isEmpty()) {
                for(Listener l : getListeners()) {
                    l.updateTitle(String.valueOf(selectedEntries.size()));
                }
            } else {
                for(Listener l : getListeners()) {
                    l.finishAction();
                }
            }
            adapter.setSelectedItems(selectedEntries);
        }
    }

    public void selectAllItems() {
        selectedEntries.clear();
        for (Challenge c : challenges) {
            selectedEntries.add(c.id);
        }

        if (!selectedEntries.isEmpty()) {
            for(Listener l : getListeners()) {
                l.updateTitle(String.valueOf(selectedEntries.size()));
            }
            adapter.setSelectedItems(selectedEntries);
        }  else {
            for(Listener l : getListeners()) {
                l.finishAction();
            }
        }
    }

    @Override
    public void unselectItems() {
        isMultiSelect = false;
        selectedEntries = new ArrayList<>();
        adapter.setSelectedItems(new ArrayList<>());
    }

    @Override
    public List<Integer> getSelectedEntriesIds() {
        return selectedEntries;
    }
}
