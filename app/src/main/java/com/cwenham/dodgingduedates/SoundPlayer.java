package com.cwenham.dodgingduedates;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

public class SoundPlayer {

    private static SoundPool soundPool;
    private static int getPointSound;
    private static int hitBlackSound;
    private final int SOUND_POOL_MAX = 2;
    private static MediaPlayer mediaPlayer;

    public SoundPlayer(Context context) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes).setMaxStreams(SOUND_POOL_MAX).build();
        } else {
            soundPool = new SoundPool(SOUND_POOL_MAX, AudioManager.STREAM_MUSIC, 0);
        }

        //Load sounds
        getPointSound = soundPool.load(context, R.raw.hit, 1);
        hitBlackSound = soundPool.load(context, R.raw.over, 1);

        //Background music
        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = MediaPlayer.create(context, R.raw.);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(0.7f, 0.7f);
    }

    public static void playGetPointSound() {
        soundPool.play(getPointSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public static void playHitBlackSound() {
        soundPool.play(hitBlackSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public static void playBGM() {
        if(mediaPlayer != null)
            mediaPlayer.start();
    }

    public static void pauseBGM() {
        if(mediaPlayer != null)
            mediaPlayer.pause();
    }

    public static  void seekToTop() {
        if(mediaPlayer != null)
            mediaPlayer.seekTo(0);
    }
}
