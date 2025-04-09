package org.example.landoflustrous.model;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

public class AudioPlayer {
    private MediaPlayer mediaPlayer;

    public void startBackgroundMusic() {

        String musicFile = "/audio/bgm.mp3";
        Media sound = new Media(getClass().getResource(musicFile).toExternalForm());

        mediaPlayer = new MediaPlayer(sound);

        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
    }

    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
        }
    }

    public void resumeMusic() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
            mediaPlayer.play();
        }
    }
}
