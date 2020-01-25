package com.apps.adrcotfas.burpeebuddy.main.view.challenges;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.utilities.StringUtils;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalType;

import org.joda.time.DateTime;
import org.joda.time.Days;

class ChallengesItemViewImpl extends BaseObservableViewMvc<ChallengesItemView.Listener>
        implements ChallengesItemView {

    private TextView text;
    private ProgressBar progress;

    public ChallengesItemViewImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.section_challenge_summary, parent, false));

        text = findViewById(R.id.text);
        progress = findViewById(R.id.progress_bar);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindChallenge(Challenge challenge, int progressValue) {

        text.setText(challenge.exerciseName);

        DateTime start = new DateTime(challenge.date);
        DateTime now = new DateTime();
        int day = Days.daysBetween(start, now).getDays() + 1;

        if (challenge.type == GoalType.TIME) {
            text.setText(StringUtils.secondsToTimerFormatAlt(progressValue) + "/" +
                    StringUtils.secondsToTimerFormatAlt(challenge.duration) + " "
                    + challenge.exerciseName + " ‧ " + "day " + day + "/" + challenge.days);
        } else {
            text.setText(progressValue + "/" + challenge.reps + " " + challenge.exerciseName + " ‧ " + "day " + day + "/" + challenge.days);
        }
        int progress = day * 100 / challenge.days;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.progress.setProgress(progress, true);
        } else {
            this.progress.setProgress(progress);
        }
    }
}
