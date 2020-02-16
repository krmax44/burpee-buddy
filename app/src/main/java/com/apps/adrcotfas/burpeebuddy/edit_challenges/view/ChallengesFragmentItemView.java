package com.apps.adrcotfas.burpeebuddy.edit_challenges.view;

import androidx.core.util.Pair;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableView;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;

public interface ChallengesFragmentItemView extends ObservableView<ChallengesFragmentItemView.Listener> {

    public interface Listener {
    }

    void bindChallenge(Pair<Challenge, Integer> challenge, boolean selected);
}
