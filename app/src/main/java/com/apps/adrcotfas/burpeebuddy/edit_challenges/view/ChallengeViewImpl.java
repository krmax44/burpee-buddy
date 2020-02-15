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
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.actionMode.ActionModeBaseObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;

import java.util.ArrayList;
import java.util.List;

public class ChallengeViewImpl extends ActionModeBaseObservableViewMvc<List<Pair<Challenge, Integer>>> {

    private RecyclerView recyclerView;
    private LinearLayout emptyState;
    private List<Challenge> challenges = new ArrayList<>();
    private ChallengesFragmentAdapter adapter;

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
                if (isMultiSelect()) {
                    multiSelect(challenges.get(position).id);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect()) {
                    adapter.setSelectedItems(new ArrayList<>());
                    setMultiSelect(true);

                    startActionMode();
                }
                multiSelect(challenges.get(position).id);
            }
        }));
    }

    @Override
    public void bindItems(List<Pair<Challenge, Integer>> challenges) {
        recyclerView.setVisibility(challenges.isEmpty() ? View.GONE : View.VISIBLE);
        emptyState.setVisibility(challenges.isEmpty() ? View.VISIBLE : View.GONE);

        adapter.bindItems(challenges);

        this.challenges.clear();
        for (Pair<Challenge, Integer> o : challenges) {
            this.challenges.add(o.first);
        }
    }

    @Override
    public ChallengesFragmentAdapter getAdapter() {
        return adapter;
    }
}
