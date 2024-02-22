package me.longluo.breakout.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.List;

import me.longluo.breakout.R;

public class SoundManager {

    private final List<MediaPlayer> sounds = new ArrayList<>(4);

    public SoundManager(Context context) {
        // wall top collision
        sounds.add(MediaPlayer.create(context, R.raw.beep1));
        // wall side collision
        sounds.add(MediaPlayer.create(context, R.raw.beep2));
        // defender collision
        sounds.add(MediaPlayer.create(context, R.raw.beep3));
        // boost ball speed
        sounds.add(MediaPlayer.create(context, R.raw.boost));
        init();
    }

    private void init() {
        for (MediaPlayer sound : sounds) {
            sound.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    public void playWallTopCollision() {
        sounds.get(0).start();
    }

    public void playWallCollision() {
        sounds.get(1).start();
    }

    public void playDefenderCollision() {
        sounds.get(2).start();
    }

    public void playBoost() {
        sounds.get(3).start();
    }

    public void stop() {
        for (MediaPlayer sound : sounds) {
            sound.stop();
        }
    }
}
