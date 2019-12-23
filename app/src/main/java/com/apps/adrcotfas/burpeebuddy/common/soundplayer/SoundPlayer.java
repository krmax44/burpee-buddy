package com.apps.adrcotfas.burpeebuddy.common.soundplayer;

import android.content.Context;
import android.content.ContextWrapper;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;

import com.apps.adrcotfas.burpeebuddy.R;

import java.io.IOException;

import timber.log.Timber;

import static com.apps.adrcotfas.burpeebuddy.common.soundplayer.SoundType.COUNTDOWN_LONG;

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
                // TODO: check duration of custom ringtones which may be much longer than notification sounds.
                // If it's n seconds long and we're in continuous mode,
                // schedule a stop after x seconds.
                mMediaPlayer.start();
            });

        } catch (SecurityException | IOException e) {
            Timber.tag(TAG).wtf(e.getMessage());
            mMediaPlayer.release();
        }
    }

    public void stop() {
        mMediaPlayer.release();
    }

    public void onWorkoutStop() {
        play(COUNTDOWN_LONG);
        // workaround to make sure that the sound played above is audible
        new Handler().postDelayed(this::stop, 1000);
    }
}
