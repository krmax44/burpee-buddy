package com.apps.adrcotfas.burpeebuddy.main.view.challenges;

import androidx.core.util.Pair;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableView;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;

public interface ChallengesItemView extends ObservableView<ChallengesItemView.Listener> {

    public interface Listener{
    }

    void bindChallenge(Pair<Challenge, Integer> challengeData);
}
