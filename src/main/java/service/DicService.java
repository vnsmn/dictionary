package service;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DicService {
  private AtomicInteger delaySound = new AtomicInteger(0);
  private AtomicBoolean isByDicsPlay = new AtomicBoolean(true);
  private AtomicBoolean isAutoPlay = new AtomicBoolean(true);
  private AtomicBoolean isPausePlay = new AtomicBoolean(false);
  private Thread threadTranslate;
  private File[] sndFiles;
  private Properties translate;
  private Properties playWords;
  private Properties setup;

  //soundDir=/home/user/english/
  static private String SOUNDDIR_PROP = "soundDir";
  //dictionary=/home/user/english/setup/dictionary.dic
  static private String DICTIONARY_PATH_PROP = "dictionary";
  //currentDictionary=/home/user/english/setup/current.dic
  static private String PLAY_WORDS_PATH_PROP = "playWordsPath";
  //wordsPath=/home/user/english/setup/words.words
  static private String WORDS_PATH_PROP = "wordsPath";
  //newDictionary=/home/user/english/setup/newDic.dic
  static private String NEW_PLAY_WORDS_PATH_PROP = "newPlayWordsPath";

  public DicService() throws Exception {
    prepareSetup();
  }

  public boolean sound(final String eng) {
    File f = new File(setup.getProperty(SOUNDDIR_PROP) + eng + ".mp3");
    if (f.exists()) {
      PlayingBackAudio.playFromFile(f.getAbsolutePath(), 2);
      return true;
    } else
      try {
        Thread.sleep(1000);
      } catch (InterruptedException ex) {
      }
    return false;
  }

  public void direct(final boolean isByDic) {
    isByDicsPlay.set(isByDic);
    rePlay(false);
    rePlay(true);
  }

  public void refresh() {
    try {
      prepareSetup();
      rePlay(false);
      rePlay(true);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void pause() {
    try {
      isPausePlay.set(!isPausePlay.get());
      if (!isPausePlay.get())
        threadTranslate.resume();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void next() {
    try {
      threadTranslate.resume();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public String grabSound(String s) {
    if (s == null || s.isEmpty())
      return "empty";
    String[] ss = s.trim().toLowerCase().split(" ");
    String msgError = "";
    for (String eng : ss)
      try {
        byte[] bs = GraberAudio.get(eng);
        if (bs != null && bs.length > 0) {
          FileOutputStream f = new FileOutputStream(new File(setup.getProperty(SOUNDDIR_PROP) + eng + ".mp3"));
          try {
            f.write(bs);
          } finally {
            f.close();
          }
        } else
          msgError += " " + eng;
      } catch (Exception e) {
        msgError += " " + eng;
      }
    return msgError;
  }

  public void play(final ActionListener<AbstractMap.SimpleEntry> listener) {
    try {
      if (isByDicsPlay.get())
        playByDics(listener);
      else
        playBySounds(listener);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void prepareSetup() throws Exception {
    setup = new Properties();
    setup.load(new FileInputStream(getDicFile()));
    sndFiles = new File(setup.getProperty(SOUNDDIR_PROP)).listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.endsWith(".mp3");
      }
    });
    translate = new Properties();
    playWords = new Properties();
    translate.load(new FileInputStream(new File(setup.getProperty(DICTIONARY_PATH_PROP))));
    playWords.load(new FileInputStream(new File(setup.getProperty(PLAY_WORDS_PATH_PROP))));
  }

  public String getRusWord(String eng) {
    String rus = translate.getProperty(eng);
    return (rus == null) ? "" : new String(rus.getBytes(Charset.forName("ISO-8859-1"))).toLowerCase();
  }

  public void setDelay(Integer delay) {
    delaySound.set(delay);
  }

  public void rePlay(Boolean value) {
    isAutoPlay.set(value);
    isPausePlay.set(false);
    threadTranslate.interrupt();
    try {
      while (threadTranslate.isAlive()) {
        threadTranslate.join(1000);
        threadTranslate.resume();
        threadTranslate.interrupt();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void playBySounds(final ActionListener<AbstractMap.SimpleEntry> listener) throws Exception {
    threadTranslate = new Thread(new Runnable() {
      @Override
      public void run() {
        int i = 0;
        listener.execute(new AbstractMap.SimpleEntry("eng", ""));
        listener.execute(new AbstractMap.SimpleEntry("rus", ""));
        listener.execute(new AbstractMap.SimpleEntry("err", ""));
        String err = "";
        int errCount = 0;
        for (File f : sndFiles) {
          listener.execute(new AbstractMap.SimpleEntry("title", String.format("Dictionary %d-%d", i++, sndFiles.length)));
          if (threadTranslate.isInterrupted())
            return;
          if (!isAutoPlay.get()) {
            threadTranslate.suspend();
          }
          if (isPausePlay.get()) {
            threadTranslate.suspend();
          }
          String eng = f.getName().substring(0, f.getName().length() - 4);
          listener.execute(new AbstractMap.SimpleEntry("eng", eng));
          listener.execute(new AbstractMap.SimpleEntry("rus", getRusWord(eng)));
          if (translate.get(eng) == null)
            continue;
          try {
            if (!sound(eng)) {
              err += " " + eng;
              errCount++;
              listener.execute(new AbstractMap.SimpleEntry("err", err));
            }
            listener.execute(new AbstractMap.SimpleEntry("title", String.format("Dictionary %d-%d-%d", i++, sndFiles.length, errCount)));
            Thread.sleep(delaySound.get() * 20);
          } catch (InterruptedException e) {
            return;
          }
        }
      }
    });
    threadTranslate.start();
  }

  private void playByDics(final ActionListener<AbstractMap.SimpleEntry> listener) throws Exception {
    threadTranslate = new Thread(new Runnable() {
      @Override
      public void run() {
        listener.execute(new AbstractMap.SimpleEntry("eng", ""));
        listener.execute(new AbstractMap.SimpleEntry("rus", ""));
        listener.execute(new AbstractMap.SimpleEntry("err", ""));
        String err = "";
        while (true && !threadTranslate.isInterrupted()) {
          int i = 0;
          int errCount = 0;
          for (Object key : playWords.keySet()) {
            if (threadTranslate.isInterrupted())
              return;
            if (!isAutoPlay.get()) {
              threadTranslate.suspend();
            }
            if (isPausePlay.get()) {
              threadTranslate.suspend();
            }
            String eng = key.toString();
            listener.execute(new AbstractMap.SimpleEntry("eng", eng));
            listener.execute(new AbstractMap.SimpleEntry("rus", getRusWord(eng)));
            try {
              if (!sound(eng)) {
                err += " " + eng;
                errCount++;
                listener.execute(new AbstractMap.SimpleEntry("err", err));
              }
              listener.execute(new AbstractMap.SimpleEntry("title", String.format("Dictionary %d-%d-%d", i++, playWords.size(), errCount)));
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

  private static File getDicFile(String dicName) {
    String dicDir = System.getenv().get("DICTIONARY_HOME");
    if (!dicDir.endsWith("/"))
      dicDir = dicDir + "/";
    return new File(dicDir + dicName);
  }

  private static File getDicFile() {
    return getDicFile("dictionary.properties");
  }

  public void createDic() throws Exception {
    DicService dicService = new DicService();
    FileInputStream fis = new FileInputStream(dicService.setup.getProperty(WORDS_PATH_PROP));
    byte[] bs = new byte[1024];
    int readBytes;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    while ((readBytes = fis.read(bs)) != -1) {
      bos.write(Arrays.copyOfRange(bs, 0, readBytes));
    }
    String s = new String(bos.toByteArray()).toLowerCase();
    String ss[] = s.toLowerCase().split("\n");
    Set<String> dics = new HashSet<String>();
    dics.addAll(Arrays.asList(ss));
    String newDics = "";
    for (String key : dics) {
      if (!newDics.isEmpty())
        newDics = "\n" + newDics;
      newDics = key.toString().trim().toLowerCase() + "=" + getRusWord(key.trim().toLowerCase()) + newDics;
    }
    File f = new File(dicService.setup.getProperty(NEW_PLAY_WORDS_PATH_PROP));
    if (!f.exists())
      f.createNewFile();
    FileOutputStream fos = new FileOutputStream(f);
    fos.write(newDics.getBytes());
    fos.close();
  }
}