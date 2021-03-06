package com.apps.adrcotfas.burpeebuddy.edit_goals.view;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSeekBar;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.db.goal.Goal;
import com.apps.adrcotfas.burpeebuddy.db.goal.GoalType;
import com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper;
import com.google.android.material.radiobutton.MaterialRadioButton;

import static com.apps.adrcotfas.burpeebuddy.common.BuddyApplication.DURATION_FACTOR;
import static com.apps.adrcotfas.burpeebuddy.common.BuddyApplication.REPS_FACTOR;
import static com.apps.adrcotfas.burpeebuddy.db.goal.GoalToString.formatSecondsAlt;

public class GoalConfigurator {

    private static final String TAG = "GoalConfigurator";

    private TextView mSetsDesc;
    private TextView mRepsDesc;
    private TextView mDurationDesc;

    public interface Listener {
        void onTypeChanged(GoalType type);
        void onSetsChanged(int sets);
        void onRepsChanged(int reps);
        void onDurationChanged(int duration);
    }

    private Goal mGoal;
    private Listener mListener;
    private Context mContext;

    private AppCompatSeekBar mSetsSeekbar;
    private AppCompatSeekBar mRepsSeekbar;
    private AppCompatSeekBar mDurationSeekbar;
    private MaterialRadioButton mRepBasedRadio;
    private MaterialRadioButton mDurationRadio;


    public GoalConfigurator(View v, Goal goal, Listener listener, Context context) {
        mGoal = goal;
        mListener = listener;
        mContext = context;
        setupViews(v);
    }

    private void setupViews(View v) {
        mRepBasedRadio = v.findViewById(R.id.rep_radio);
        mDurationRadio = v.findViewById(R.id.time_radio);

        RadioGroup typeContainer = v.findViewById(R.id.goal_type_container);
        LinearLayout repsContainer = v.findViewById(R.id.reps_container);
        LinearLayout durationContainer = v.findViewById(R.id.duration_container);

        mSetsSeekbar = v.findViewById(R.id.sets_seekbar);
        mRepsSeekbar = v.findViewById(R.id.reps_seekbar);
        mDurationSeekbar = v.findViewById(R.id.duration_seekbar);

        mSetsDesc = v.findViewById(R.id.sets_value);
        mRepsDesc = v.findViewById(R.id.reps_value);
        mDurationDesc = v.findViewById(R.id.duration_value);

        setSets(mGoal.sets);
        setReps(mGoal.reps);
        setDuration(mGoal.duration);

        mRepBasedRadio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mListener.onTypeChanged(GoalType.REPS);
                repsContainer.setVisibility(View.VISIBLE);
                durationContainer.setVisibility(View.GONE);
            }
        });

        mDurationRadio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mListener.onTypeChanged(GoalType.TIME);
                repsContainer.setVisibility(View.GONE);
                durationContainer.setVisibility(View.VISIBLE);
            }
        });

        typeContainer.clearCheck();
        switch (mGoal.type) {
            case TIME:
                mDurationRadio.setChecked(true);
                break;
            case REPS:
                mRepBasedRadio.setChecked(true);
                break;
        }

        mSetsSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int sets, boolean fromUser) {
                mListener.onSetsChanged(sets);
                mSetsDesc.setText(String.valueOf(sets));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mRepsSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int reps = progress * REPS_FACTOR;
                mListener.onRepsChanged(reps);
                mRepsDesc.setText(String.valueOf(reps));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mDurationSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                final int seconds = progress * DURATION_FACTOR;
                mListener.onDurationChanged(seconds);
                mDurationDesc.setText(formatSecondsAlt(seconds));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setDuration(int duration) {
        mDurationDesc.setText(formatSecondsAlt(duration));
        final int durationProgress = duration / DURATION_FACTOR;
        mDurationSeekbar.setProgress(durationProgress);
    }

    private void setReps(int reps) {
        mRepsDesc.setText(String.valueOf(reps));
        final int repsProgress = reps / REPS_FACTOR;
        mRepsSeekbar.setProgress(repsProgress);
    }

    private void setSets(int sets) {
        mSetsDesc.setText(String.valueOf(sets));
        mSetsSeekbar.setProgress(sets);
    }

    public Goal getGoal() {
        mGoal.sets = mSetsSeekbar.getProgress();
        mGoal.reps = mRepsSeekbar.getProgress() * REPS_FACTOR;
        mGoal.duration = mDurationSeekbar.getProgress() * DURATION_FACTOR;
        mGoal.duration_break = SettingsHelper.getBreakDuration();
        mGoal.type = mDurationRadio.isChecked() ? GoalType.TIME : GoalType.REPS;
        return mGoal;
    }
}
