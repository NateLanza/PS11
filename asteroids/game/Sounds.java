package asteroids.game;

import java.io.BufferedInputStream;
import java.io.IOException;
import javax.sound.sampled.*;
import java.util.TreeMap;
import java.lang.IllegalArgumentException;

public class Sounds
{

    /** Represents all sound files needed */
    private TreeMap<String, Clip> clips;
    
    /**
     * Initializes all sound files needed from /sounds/
     */
    public Sounds ()
    {
        clips = new TreeMap<String, Clip>();
        
        addClip("bangAlienShip");
        addClip("bangLarge");
        addClip("bangMedium");
        addClip("bangShip");
        addClip("bangSmall");
        addClip("fire");
        addClip("saucerBig");
        addClip("saucerSmall");
        addClip("thrust");
    }
    
    /**
     * Plays a sound, if the sound isn't already playing
     * @param name: the name of a .wav file in /sounds/ (without extension)
     * Note: the .wav must be added to the sound list in the constructor
     * @throws IllegalArgumentException if the sound file is null
     */
    public void play (String name) throws IllegalArgumentException {
        //Check if sound valid
        if (clips.get(name) == null) 
            throw new IllegalArgumentException("Sound not found!");
        
        Clip sound = clips.get(name);
        if (sound.isRunning()) return;
        sound.setFramePosition(0);
        sound.start();
    }
    
    /**
     * Adds an audio clip to the list from a sound file.
     * @param name: The name of a .wav file in /sounds/ (without extension)
     */
    private void addClip (String name)
    {
        String soundFile = "/sounds/" + name + ".wav";
        try (BufferedInputStream sound = new BufferedInputStream(getClass().getResourceAsStream(soundFile)))
        {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(sound));
            clips.put(name, clip);
        }
        catch (LineUnavailableException e)
        {
            return;
        }
        catch (IOException e)
        {
            return;
        }
        catch (UnsupportedAudioFileException e)
        {
            return;
        }
    }
    
}
