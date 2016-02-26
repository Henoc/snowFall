package snowFall;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class ClipControl {

	Clip clip = null;

	public ClipControl(File soundFile){

		AudioInputStream audioInputStream;
        try{
            audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            AudioFormat audioFormat = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, audioFormat);
            clip = (Clip)AudioSystem.getLine(info);
            clip.open(audioInputStream);
        }
        catch (UnsupportedAudioFileException e)
        {   e.printStackTrace();  }
        catch (IOException e)
        {   e.printStackTrace();  }
        catch (LineUnavailableException e)
        {   e.printStackTrace();  }

	}

	public void start(){
		clip.setFramePosition(0);
		clip.start();
	}

	public void stop(){
		clip.stop();
	}
}
