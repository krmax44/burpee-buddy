package com.apps.adrcotfas.burpeebuddy.main.view.challenges;

import androidx.core.util.Pair;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;

public interface ChallengesItemView extends ObservableViewMvc<ChallengesItemView.Listener> {

    public interface Listener{
    }

    void bindChallenge(Pair<Challenge, Integer> challengeData);
}
