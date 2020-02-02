package com.apps.adrcotfas.burpeebuddy.edit_challenges.livedata;

import androidx.core.util.Pair;
import androidx.lifecycle.MediatorLiveData;

import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;

import java.util.List;

public class ChallengesCombinedLiveData extends MediatorLiveData<List<Pair<Challenge, Integer>>> {
    public List<Pair<Challenge, Integer>> inProgress;
    public List<Pair<Challenge, Integer>> completed;
}
