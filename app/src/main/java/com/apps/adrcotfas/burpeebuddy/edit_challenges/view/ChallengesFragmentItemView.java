package com.apps.adrcotfas.burpeebuddy.edit_challenges.view;

import androidx.core.util.Pair;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;

public interface ChallengesFragmentItemView extends ObservableViewMvc<ChallengesFragmentItemView.Listener> {

    public interface Listener {
        void onLongPress(int id);
    }

    void bindChallenge(Pair<Challenge, Integer> challenge);
}
