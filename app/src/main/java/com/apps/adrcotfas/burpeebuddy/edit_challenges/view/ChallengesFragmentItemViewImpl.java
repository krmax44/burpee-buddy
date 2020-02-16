package com.apps.adrcotfas.burpeebuddy.edit_challenges.view;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.util.Pair;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.utilities.StringUtils;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableView;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalType;

import org.joda.time.DateTime;
import org.joda.time.Days;

public class ChallengesFragmentItemViewImpl extends BaseObservableView<ChallengesFragmentItemView.Listener>
        implements ChallengesFragmentItemView {

    private TextView status;
    private TextView date;
    private TextView text;
    private Challenge challenge;
    private View overlay;

    public ChallengesFragmentItemViewImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.challenge_list_item, parent, false));
        status = findViewById(R.id.status);
        date = findViewById(R.id.end_date);
        text = findViewById(R.id.description);
        overlay = findViewById(R.id.overlay);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindChallenge(Pair<Challenge, Integer> challengeData, boolean selected) {
        challenge = challengeData.first;
        final Integer progress = challengeData.second;

        if (challenge == null || progress == null) {
            return;
        }

        if (challenge.complete) {

            text.setText(buildChallengeText(challenge));
            if (challenge.failed) {
                status.setText("FAILED");
                status.setTextColor(getContext().getResources().getColor(R.color.red));
            }
            else {
                status.setText("COMPLETE");
                status.setTextColor(getContext().getResources().getColor(R.color.green));
            }
        } else {
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
            status.setText("IN PROGRESS");
            status.setTextColor(getContext().getResources().getColor(R.color.yellow));
        }
        date.setText(StringUtils.formatDate(challenge.date));

        overlay.setVisibility(selected ? View.VISIBLE : View.GONE);
    }

    public static String buildChallengeText(Challenge challenge) {
        if (challenge.type == GoalType.TIME) {
            return StringUtils.secondsToTimerFormatAlt(challenge.duration) + " " + challenge.exerciseName + " × " + challenge.days + " days";
        } else {
            return challenge.reps + " " + challenge.exerciseName + " × " + challenge.days + " days";
        }
    }
}
