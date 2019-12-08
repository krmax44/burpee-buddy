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
    private MediaPlayer mMediaPlayer;

    public SoundPlayer(Context base) {
        super(base);
    }

    public void init() {
        mMediaPlayer = new MediaPlayer();
    }

    public void play(SoundType soundType) {
        try {

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
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(this, uri);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mMediaPlayer.prepare();

            mMediaPlayer.setOnPreparedListener(mp -> {
                Log.i(TAG, "OnPrepareListener was called");
                // TODO: check duration of custom ringtones which may be much longer than notification sounds.
                // If it's n seconds long and we're in continuous mode,
                // schedule a stop after x seconds.
                mMediaPlayer.start();
            });

        } catch (SecurityException | IOException e) {
            Log.wtf(TAG, e.getMessage());
            mMediaPlayer.release();
        }
    }

    public void stop() {
        mMediaPlayer.release();
    }
}
