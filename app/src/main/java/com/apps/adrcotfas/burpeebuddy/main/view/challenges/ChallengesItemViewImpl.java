package com.apps.adrcotfas.burpeebuddy.main.view.challenges;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;

class ChallengesItemViewImpl extends BaseObservableViewMvc<ChallengesItemView.Listener>
        implements ChallengesItemView {

    private Challenge mChallenge;

    private TextView mText;
    private ProgressBar mProgressBar;

    public ChallengesItemViewImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.section_challenge_summary, parent, false));

        mText = findViewById(R.id.text);
        mProgressBar = findViewById(R.id.progress_bar);
    }

    @Override
    public void bindChallenge(Challenge challenge) {
        //TODO: update text and progressbar
        mChallenge = challenge;
        mText.setText(challenge.exerciseName);
    }
}
