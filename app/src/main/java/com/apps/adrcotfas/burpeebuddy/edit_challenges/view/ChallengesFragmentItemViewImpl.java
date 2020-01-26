package com.apps.adrcotfas.burpeebuddy.edit_challenges.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.utilities.StringUtils;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalType;

import org.joda.time.DateTime;
import org.joda.time.Days;

public class ChallengesFragmentItemViewImpl extends BaseObservableViewMvc<ChallengesFragmentItemView.Listener>
        implements ChallengesFragmentItemView {

    private Challenge challenge;

    TextView status;
    TextView date;
    TextView text;

    public ChallengesFragmentItemViewImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.challenge_list_item, parent, false));
        status = findViewById(R.id.status);
        date = findViewById(R.id.end_date);
        text = findViewById(R.id.description);
    }

    @Override
    public void bindChallenge(Challenge challenge, int progressValue) {
        this.challenge = challenge;

        if (challenge.complete) {
            if (challenge.type == GoalType.TIME) {
                text.setText(StringUtils.secondsToTimerFormatAlt(challenge.duration) + " " + challenge.exerciseName + " x " + challenge.days + " days");
            } else {
                text.setText(challenge.reps + " " + challenge.exerciseName + " x " + challenge.days + " days");
            }
            if (challenge.failed) {
                status.setText("failed");
                status.setTextColor(getContext().getResources().getColor(R.color.red));
            }
            else {
                status.setText("complete");
                status.setTextColor(getContext().getResources().getColor(R.color.green));
            }
        } else {
            DateTime start = new DateTime(challenge.date);
            final DateTime end = new DateTime().plusDays(1);
            final DateTime endOfToday = end.toLocalDate().toDateTimeAtStartOfDay(end.getZone());

            int day = Days.daysBetween(start, endOfToday).getDays() + 1;

            if (challenge.type == GoalType.TIME) {
                text.setText(StringUtils.secondsToTimerFormatAlt(progressValue) + "/" +
                        StringUtils.secondsToTimerFormatAlt(challenge.duration) + " "
                        + challenge.exerciseName + " ‧ " + "day " + day + "/" + challenge.days);
            } else {
                text.setText(progressValue + "/" + challenge.reps + " " + challenge.exerciseName + " ‧ " + "day " + day + "/" + challenge.days);
            }
            status.setText("in progress");
            status.setTextColor(getContext().getResources().getColor(R.color.yellow));
        }
        date.setText(StringUtils.formatDate(challenge.date));
    }
}
