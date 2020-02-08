package com.apps.adrcotfas.burpeebuddy.edit_challenges.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;

import java.util.List;

public class ChallengeViewImpl extends BaseObservableViewMvc<ChallengeView.Listener>
        implements ChallengeView, ChallengesFragmentAdapter.Listener {

    private RecyclerView recyclerView;
    private ChallengesFragmentAdapter adapter;
    private LinearLayout emptyState;

    public ChallengeViewImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.fragment_recycler, parent, false));

        emptyState = findViewById(R.id.empty_state);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChallengesFragmentAdapter(inflater, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void bindChallenges(List<Pair<Challenge, Integer>> challenges) {
        recyclerView.setVisibility(challenges.isEmpty() ? View.GONE : View.VISIBLE);
        emptyState.setVisibility(challenges.isEmpty() ? View.VISIBLE : View.GONE);

        adapter.bindChallenges(challenges);
    }

    @Override
    public void onLongPress(int id) {
        for (Listener l : getListeners()) {
            l.onLongClick(id);
        }
    }
}
