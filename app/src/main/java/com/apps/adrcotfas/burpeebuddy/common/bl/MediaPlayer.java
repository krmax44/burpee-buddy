package com.apps.adrcotfas.burpeebuddy.common.bl;

import android.content.Context;
import android.content.ContextWrapper;
import android.media.AudioManager;
import android.net.Uri;
import android.util.Log;

import com.apps.adrcotfas.burpeebuddy.R;

import java.io.IOException;

public class MediaPlayer extends ContextWrapper {

    private static final String TAG = "MediaPlayer";

    public MediaPlayer(Context base) {
        super(base);
    }

    public void play() {
        try {
            final android.media.MediaPlayer mMediaPlayer = new android.media.MediaPlayer();
            final Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ding);

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
