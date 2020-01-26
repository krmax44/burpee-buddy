package com.apps.adrcotfas.burpeebuddy.edit_challenges.view;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;

import java.util.List;

public interface ChallengeView extends ObservableViewMvc<ChallengeView.Listener> {

    void bindChallenges(List<Challenge> challenges, List<Integer> progress);

    public interface Listener {
        //TODO: implement long click for delete
    }
}
