package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 */
public class SetupProps {
  //soundDir=/home/user/english/
  static private String SOUNDDIR_PROP = "soundDir";
  //dictionary=/home/user/english/setup/dictionary.dic
  static private String DICTIONARY_PATH_PROP = "dictionary";
  //currentDictionary=/home/user/english/setup/current.dic
  static private String PLAY_WORDS_PATH_PROP = "playDicFile";

  static private Properties setupProps;
  static private Map<String, File> sndFiles;
  static private Properties playWords;
  static private Properties dictionary;
  static private Map<String, String> plays;
  static private String playWordsPath;
  static private AtomicInteger delaySound = new AtomicInteger(0);

  static {
    try {
      SetupProps.refresh();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static public void refresh() throws Exception {
    setupProps = new Properties();
    setupProps.load(new FileInputStream(getSetupFile()));
    dictionary = new Properties();
    playWords = new Properties();
    dictionary.load(new FileInputStream(new File(setupProps.getProperty(DICTIONARY_PATH_PROP))));
    playWords.load(new FileInputStream(new File(getPlayWordsPath())));
    sndFiles = new HashMap<String, File>();
    for (File f : new File(setupProps.getProperty(SOUNDDIR_PROP)).listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.endsWith(".mp3");
      }
    })) {
      sndFiles.put(f.getName().substring(0, f.getName().length() - 4), f);
    }
    plays = new TreeMap<String, String>();
    Enumeration keys = setupProps.keys();
    while (keys.hasMoreElements()) {
      String key = keys.nextElement().toString();
      if (key.startsWith("playDicFile")) {
        File f = new File(setupProps.getProperty(key));
        if (f.exists())
          plays.put(f.getName(), f.getAbsolutePath());
      }
    }
  }

  public static String getPlayWordsPath() {
    return playWordsPath == null || playWordsPath.isEmpty() ? setupProps.getProperty(PLAY_WORDS_PATH_PROP) : playWordsPath;
  }

  public static void setPlayWordsPath(String s) {
    playWordsPath = s;
  }

  public static String getDicPath() {
    return setupProps.getProperty(DICTIONARY_PATH_PROP);
  }

  public static String getSoundDirPath() {
    return setupProps.getProperty(SOUNDDIR_PROP);
  }

  static public Properties getPlayWordProps() {
    return playWords;
  }

  static public Properties getDictionaryProps() {
    return dictionary;
  }

  static public Map<String, File> getSoundProps() {
    return sndFiles;
  }

  static public Map<String, String> getPlays() {
    return plays;
  }

  static public String getRusWord(String eng) {
    String rus = dictionary.getProperty(eng);
    return convertToIso(rus);
  }

  static public String convertToIso(String rus) {
    return (rus == null) ? "" : new String(rus.getBytes(Charset.forName("ISO-8859-1"))).toLowerCase();
  }


  static public int getDelaySound() {
    return delaySound.get();
  }

  static public void setDelaySound(int delay) {
    delaySound.set(delay);
  }

  private static File getDicFile(String dicName) {
    String dicDir = System.getenv().get("DICTIONARY_HOME");
    if (!dicDir.endsWith("/"))
      dicDir = dicDir + "/";
    return new File(dicDir + dicName);
  }

  private static File getSetupFile() {
    return getDicFile("dictionary.properties");
  }
}
