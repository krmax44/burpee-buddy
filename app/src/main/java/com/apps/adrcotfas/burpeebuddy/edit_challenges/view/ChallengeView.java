package com.apps.adrcotfas.burpeebuddy.edit_challenges.view;

import androidx.core.util.Pair;

import com.apps.adrcotfas.burpeebuddy.common.ActionModeHelper;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableView;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;

import java.util.List;

public interface ChallengeView extends ObservableView<ChallengeView.Listener> {

    void bindChallenges(List<Pair<Challenge, Integer>> challenges);
    void destroyActionMode();

    interface Listener {
        void startActionMode(ActionModeHelper actionModeHelper);
        void onDeleteSelected(List<Integer> selectedEntriesIds);
    }
}
