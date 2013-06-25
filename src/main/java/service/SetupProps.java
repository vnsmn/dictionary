package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.nio.charset.Charset;
import java.util.*;

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
//  static private List<String> plays;

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
//    plays = new ArrayList();
//    Enumeration keys = setupProps.keys();
//    while (keys.hasMoreElements()) {
//      String key = keys.nextElement().toString();
//      if (key.startsWith("playDicFile"))
//        plays.add(setupProps.getProperty(key));
//    }
  }

  public static String getPlayWordsPath() {
    return setupProps.getProperty(PLAY_WORDS_PATH_PROP);
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

//  static public List<String> getPlays() {
//    return plays;
//  }

  static public String getRusWord(String eng) {
    String rus = dictionary.getProperty(eng);
    return convertToIso(rus);
  }

  static public String convertToIso(String rus) {
    return (rus == null) ? "" : new String(rus.getBytes(Charset.forName("ISO-8859-1"))).toLowerCase();
  }

  static public String convertToUtf(String rus) {
    return (rus == null) ? "" : new String(rus.getBytes(Charset.forName("UTF-8"))).toLowerCase();
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
