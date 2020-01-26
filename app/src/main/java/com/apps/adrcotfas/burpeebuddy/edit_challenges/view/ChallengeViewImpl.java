package com.apps.adrcotfas.burpeebuddy.edit_challenges.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

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

    public ChallengeViewImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.fragment_recycler, parent, false));

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChallengesFragmentAdapter(inflater, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void bindChallenges(List<Challenge> challenges, List<Integer> progress) {
        adapter.bindChallenges(challenges, progress);
    }
}
