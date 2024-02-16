// https://stackoverflow.com/questions/6045384/playing-mp3-and-wav-in-java

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;
import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;

public class AudioFilePlayer {

    private final String THISCLAS = this.getClass().getName();
    private final String CLASDESC = "Audio file player.";
    private final String DEFPATH  = "audio\\beep-3.wav";
    private final    int DEFLOOP  = 5;

    public static void main(String[] args) {
        final AudioFilePlayer instce = new AudioFilePlayer(); //create the class instance
        int al = args.length;
        String path = al > 0 ? (args[0].equals(".") ? instce.DEFPATH : args[0]                  ) : instce.DEFPATH;
        int    loop = al > 1 ? (args[1].equals(".") ? instce.DEFLOOP : Integer.parseInt(args[1])) : instce.DEFLOOP;
        for (int n=0; n<loop; n+=1)
            instce.play(path);
      //player.play("beep-3.wav");
      //player.play("something.mp3");
      //player.play("something.ogg");
    }

    public void play(String filePath) {
        final File file = new File(filePath);

        try (final AudioInputStream in = getAudioInputStream(file)) {

            final AudioFormat outFormat = getOutFormat(in.getFormat());
            final Info info = new Info(SourceDataLine.class, outFormat);

            try (final SourceDataLine line =
                     (SourceDataLine) AudioSystem.getLine(info)) {

                if (line != null) {
                    line.open(outFormat);
                    line.start();
                    stream(getAudioInputStream(outFormat, in), line);
                    line.drain();
                    line.stop();
                }
            }

        } catch (UnsupportedAudioFileException 
               | LineUnavailableException 
               | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private AudioFormat getOutFormat(AudioFormat inFormat) {
        final int ch = inFormat.getChannels();

        final float rate = inFormat.getSampleRate();
        return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }

    private void stream(AudioInputStream in, SourceDataLine line) 
        throws IOException {
        final byte[] buffer = new byte[4096];
        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
            line.write(buffer, 0, n);
        }
    }
}