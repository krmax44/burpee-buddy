package com.apps.adrcotfas.burpeebuddy.main.view.challenges;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.util.Pair;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.utilities.StringUtils;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableView;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;
import com.apps.adrcotfas.burpeebuddy.db.goal.GoalType;

import org.joda.time.DateTime;
import org.joda.time.Days;

class ChallengesItemViewImpl extends BaseObservableView<ChallengesItemView.Listener>
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
    public void bindChallenge(Pair<Challenge, Integer> challengeData) {

        final Challenge challenge = challengeData.first;
        final Integer progress = challengeData.second;

        if (challenge == null || progress == null) {
            return;
        }

        text.setText(challenge.exerciseName);

        DateTime start = new DateTime(challenge.date);

        final DateTime end = new DateTime().plusDays(1);
        final DateTime endOfToday = end.toLocalDate().toDateTimeAtStartOfDay(end.getZone());

        int day = Days.daysBetween(start, endOfToday).getDays();

        if (challenge.type == GoalType.TIME) {
            text.setText(StringUtils.secondsToTimerFormatAlt(progress) + "/" +
                    StringUtils.secondsToTimerFormatAlt(challenge.duration) + " "
                    + challenge.exerciseName + " ‧ " + "day " + day + "/" + challenge.days);
        } else {
            text.setText(progress + "/" + challenge.reps + " " + challenge.exerciseName + " ‧ " + "day " + day + "/" + challenge.days);
        }
        int progressPercent = (int) Math.ceil(day * 100.F / (double) challenge.days);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.progress.setProgress(progressPercent, true);
        } else {
            this.progress.setProgress(progressPercent);
        }
    }
}
