package com.apps.adrcotfas.burpeebuddy.common.soundplayer;

import android.content.Context;
import android.content.ContextWrapper;
import android.media.AudioAttributes;
import android.media.SoundPool;

public class SoundPlayer extends ContextWrapper {

    public SoundPlayer(Context base) {
        super(base);
    }

    public void play(int sound) {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        SoundPool soundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();

        int soundId = soundPool.load(getApplicationContext(), sound, 1);

        final float LEFT_VOLUME_VALUE = 1.0f;
        final float RIGHT_VOLUME_VALUE = 1.0f;
        final int MUSIC_LOOP = 0;
        final int SOUND_PLAY_PRIORITY = 0;
        final float PLAY_RATE = 1.0f;

        soundPool.setOnLoadCompleteListener((soundPool1, sampleId, status)
                -> soundPool1.play(soundId, LEFT_VOLUME_VALUE, RIGHT_VOLUME_VALUE, SOUND_PLAY_PRIORITY, MUSIC_LOOP, PLAY_RATE));
    }
}
