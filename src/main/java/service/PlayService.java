package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayService {
  private AtomicInteger delaySound = new AtomicInteger(0);
  private AtomicBoolean isAutoPlay = new AtomicBoolean(true);
  private AtomicBoolean isPausePlay = new AtomicBoolean(false);
  private Thread threadTranslate;

  public PlayService() throws Exception {
    prepareSetup();
  }

  public void setAuto(boolean isAutoPlay) {
    this.isAutoPlay.set(isAutoPlay);
  }

  public void refresh(final ActionListener<AbstractMap.SimpleEntry> listener) {
    try {
      prepareSetup();
      isPausePlay.set(false);
      stop();
      playByDics(listener);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void pause() {
    isPausePlay.set(true);
  }

  public void next() {
    if (threadTranslate == null)
      return;
    try {
      isPausePlay.set(false);
      threadTranslate.resume();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void stop() {
    if (threadTranslate == null)
      return;
    try {
      threadTranslate.interrupt();
      while (threadTranslate.isAlive()) {
        threadTranslate.join(1000);
        threadTranslate.resume();
        threadTranslate.interrupt();
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void play(final ActionListener<AbstractMap.SimpleEntry> listener) {
    try {
      isPausePlay.set(false);
      playByDics(listener);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void setDelay(Integer delay) {
    delaySound.set(delay);
  }

  private void prepareSetup() throws Exception {
    isAutoPlay.set(true);
    isPausePlay.set(false);
  }

  private void playByDics(final ActionListener<AbstractMap.SimpleEntry> listener) throws Exception {
    threadTranslate = new Thread(new Runnable() {
      @Override
      public void run() {
        listener.execute(new AbstractMap.SimpleEntry("eng", ""));
        listener.execute(new AbstractMap.SimpleEntry("rus", ""));
        listener.execute(new AbstractMap.SimpleEntry("err", ""));
        String err = "";
        while (!threadTranslate.isInterrupted()) {
          int i = 0;
          int errCount = 0;
          for (Object key : SetupProps.getPlayWordProps().keySet()) {
            if (threadTranslate.isInterrupted())
              return;
            if (!isAutoPlay.get())
              threadTranslate.suspend();
            if (isPausePlay.get())
              threadTranslate.suspend();
            String eng = key.toString();
            listener.execute(new AbstractMap.SimpleEntry("eng", eng));
            listener.execute(new AbstractMap.SimpleEntry("rus", SetupProps.getRusWord(eng)));
            try {
              if (!sound(eng)) {
                err += " " + eng;
                errCount++;
                listener.execute(new AbstractMap.SimpleEntry("err_sound", err));
              }
              if (SetupProps.getRusWord(eng).isEmpty()) {
                err += " " + eng;
                errCount++;
                listener.execute(new AbstractMap.SimpleEntry("err_rus", err));
              }
              listener.execute(new AbstractMap.SimpleEntry("title", String.format("Dictionary %d-%d-%d", i++, SetupProps.getPlayWordProps().size(), errCount)));
              Thread.sleep(delaySound.get() * 20);
            } catch (InterruptedException e) {
              return;
            }
          }
        }
      }
    });
    threadTranslate.start();
  }

  private boolean sound(final String eng) {
    File f = SetupProps.getSoundProps().get(eng);
    if (f != null && f.exists()) {
      PlayingBackAudio.playFromFile(f.getAbsolutePath(), 2);
      return true;
    } else
      try {
        Thread.sleep(1000);
      } catch (InterruptedException ex) {
      }
    return false;
  }
}