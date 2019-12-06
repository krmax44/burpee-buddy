package com.apps.adrcotfas.burpeebuddy.common.soundplayer;

import android.content.Context;
import android.content.ContextWrapper;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.apps.adrcotfas.burpeebuddy.R;

import java.io.IOException;

public class SoundPlayer extends ContextWrapper {

    private static final String TAG = "SoundPlayer";

    public SoundPlayer(Context base) {
        super(base);
    }

    public void play(SoundType soundType) {
        try {
            final MediaPlayer mMediaPlayer = new MediaPlayer();

            int sound;
            switch (soundType) {
                case COUNTDOWN:
                    sound = R.raw.ding;
                    break;
                case COUNTDOWN_LONG:
                    sound = R.raw.long_ding;
                    break;
                case REP_COMPLETE_SPECIAL:
                    sound = R.raw.special;
                    break;
                case REP_COMPLETE:
                default:
                    sound = R.raw.coin;
                    break;
            }

            final Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + sound);

            mMediaPlayer.setDataSource(this, uri);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mMediaPlayer.prepare();

            mMediaPlayer.setOnPreparedListener(mp -> {
                // TODO: check duration of custom ringtones which may be much longer than notification sounds.
                // If it's n seconds long and we're in continuous mode,
                // schedule a stop after x seconds.
                mMediaPlayer.start();
            });
            mMediaPlayer.setOnCompletionListener(mp1 -> mMediaPlayer.release());

        } catch (SecurityException | IOException e) {
            Log.wtf(TAG, e.getMessage());
        }
    }
}
