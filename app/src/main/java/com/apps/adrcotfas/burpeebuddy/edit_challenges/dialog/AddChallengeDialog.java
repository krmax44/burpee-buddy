package com.apps.adrcotfas.burpeebuddy.edit_challenges.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.Events;
import com.apps.adrcotfas.burpeebuddy.db.AppDatabase;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;
import com.apps.adrcotfas.burpeebuddy.edit_challenges.view.ChallengeConfigurator;

import org.greenrobot.eventbus.EventBus;

public class AddChallengeDialog extends DialogFragment implements ChallengeConfigurator.Listener{

    private static final String TAG = "AddChallengeDialog";


    public static AddChallengeDialog getInstance() {
        return new AddChallengeDialog();
    }

    private ChallengeConfigurator challengeConfigurator;

    @Override
    public final Dialog onCreateDialog(Bundle savedInstBundle) {
        final View v = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_add_challenge, null, false);

        challengeConfigurator = new ChallengeConfigurator(getContext(), this, this, v);

        return challengeConfigurator.createDialog();
    }

    @Override
    public void onResume() {
        super.onResume();
        toggleState(false);
    }

    @Override
    public void onPositiveButtonClick(Challenge challenge) {
        AppDatabase.getDatabase(getContext()).challengeDao().getInProgress().observe(this, challenges -> {
            boolean found = false;
            for (Challenge c : challenges) {
                if (c.exerciseName.equals(challenge.exerciseName)) {
                    found = true;
                    break;
                }
            }
            if (found) {
                Toast.makeText(getContext(), "There's already a challenge in progress for " + challenge.exerciseName
                        , Toast.LENGTH_SHORT).show();
            } else {
                EventBus.getDefault().post(
                        new Events.AddChallenge(challenge));
            }
        });
    }

    @Override
    public void toggleState(boolean valid) {
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(valid);
        }
    }
}
