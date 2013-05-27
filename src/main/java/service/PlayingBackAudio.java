package service;

import javazoom.spi.mpeg.sampled.convert.MpegFormatConversionProvider;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 */
public class PlayingBackAudio {
  private static Clip clip;
  private static Object sync = new Object();
  static {
    try {
      clip = AudioSystem.getClip();
      clip.addLineListener(new LineListener() {
        @Override
        public void update(LineEvent event) {
          clip.drain();
          if (event.getType() == LineEvent.Type.STOP) {
            synchronized (sync) {
              sync.notifyAll();
            }
          }
        }
      });
    } catch (Exception ex) {
      System.exit(-1);
    }
  }

  static public void playFromFile(String audioPathName, float rate) {
    AudioInputStream audioInputStream = null;
    File fileIn = new File(audioPathName);
    try {
      MpegAudioFileReader mpegAudioFileReader = new MpegAudioFileReader();
      audioInputStream = mpegAudioFileReader.getAudioInputStream(fileIn);
      MpegFormatConversionProvider mpegFormatConversionProvider = new MpegFormatConversionProvider();
      //Clip clip = AudioSystem.getClip();
      AudioFormat decodedFormat =
          new AudioFormat(clip.getFormat().getEncoding(),
              clip.getFormat().getSampleRate() / (rate <= 0 ? 2 : rate),
              clip.getFormat().getSampleSizeInBits(),
              clip.getFormat().getChannels(),
              clip.getFormat().getFrameSize(),
              clip.getFormat().getSampleRate(),
              false);
      if (mpegFormatConversionProvider.isConversionSupported(decodedFormat, audioInputStream.getFormat())) {
        AudioInputStream convertAudioInputStream = mpegFormatConversionProvider.getAudioInputStream(decodedFormat, audioInputStream);
        clip.open(convertAudioInputStream);
        try {
          clip.start();
          clip.drain();
          synchronized (sync) {
            sync.wait(5000);
          }
        } finally {
          clip.close();
          try {
            convertAudioInputStream.close();
          } catch (Exception ex) {}
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      try {
        audioInputStream.close();
      } catch (IOException e) {}
    }
  }
}
