package com.apps.adrcotfas.burpeebuddy.main.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.utilities.StringUtils;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;
import com.apps.adrcotfas.burpeebuddy.db.goal.GoalType;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import static com.apps.adrcotfas.burpeebuddy.edit_challenges.view.ChallengesFragmentItemViewImpl.buildChallengeText;

public class ChallengeCompleteDialog extends DialogFragment {

    private Challenge challenge;
    private boolean success;

    public static ChallengeCompleteDialog getInstance(Challenge challenge, boolean success) {
        ChallengeCompleteDialog dialog = new ChallengeCompleteDialog();
        dialog.challenge = challenge;
        dialog.success = success;
        return dialog;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public final Dialog onCreateDialog(Bundle savedInstBundle) {

        final View v = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_challenge_complete, null, false);

        final TextView text = v.findViewById(R.id.text);
        final ImageView imageView = v.findViewById(R.id.image);
        final MaterialButton button = v.findViewById(R.id.button);

        final String total = challenge.type == GoalType.REPS ? challenge.days * challenge.reps + " total " + challenge.exerciseName
                : StringUtils.secondsToTimerFormatAlt(challenge.days * challenge.duration) + " total time";

        text.setText((success ? "Congratulations! You have completed the "
                : "You have failed the ") + buildChallengeText(challenge) + " challenge." + (success ? '\n' + total : ""));

        imageView.setImageDrawable(getContext().getDrawable(
                success ? R.drawable.ic_success: R.drawable.ic_failure));

        button.setOnClickListener(v1 -> {
            AlertDialog dialog = (AlertDialog) getDialog();
            if (dialog != null) {
                dialog.dismiss();
            }
        });

        final MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(getActivity());
        final Dialog d = b
                .setTitle(success ? "Challenge complete" : "Challenge failed")
                .setView(v)
                .setCancelable(false)
                .create();
        d.setCanceledOnTouchOutside(false);

        return d;
    }
}
