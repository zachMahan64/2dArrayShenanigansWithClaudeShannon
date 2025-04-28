//Zach Mahan, Date: 20250430, Purpose: Make a 2D array game

import javax.sound.sampled.*;
import java.io.File;

public class Music {
    public static void playMusicOnLoop(String filePath) {
        Clip clip = null;
        try {
            File musicFile = new File(filePath);
            if (!musicFile.exists()) {
                System.out.println("no cigar: " + filePath);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
        } catch (Exception e) {
            System.out.println("Error encounter loading music");
        }


        if (clip != null && !clip.isRunning()) {
            setVolume(clip, 0.1f);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        }
    }
    public static void playSound(String filePath) {
        Clip clip = null;
        try {
            File musicFile = new File(filePath);
            if (!musicFile.exists()) {
                System.out.println("no cigar: " + filePath);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
        } catch (Exception e) {
            System.out.println("Error encounter loading music");
        }


        if (clip != null && !clip.isRunning()) {
            clip.start();
        }
    }
    public static void setVolume(Clip clip, float volume) {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
    }
}
