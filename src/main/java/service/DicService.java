package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

public class DicService {

  public String grabSound(String s) {
    if (s == null || s.isEmpty())
      return "empty";
    String[] ss = s.trim().toLowerCase().split(" ");
    String msgError = "";
    for (String eng : ss)
      try {
        byte[] bs = GraberAudio.get(eng);
        if (bs != null && bs.length > 0) {
          FileOutputStream f = new FileOutputStream(new File(SetupProps.getSoundDirPath() + eng + ".mp3"));
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

  public void refreshDic() throws Exception {
    Properties words = new Properties();
    FileInputStream fis = new FileInputStream(SetupProps.getPlayWordsPath());
    words.load(fis);
    fis.close();

    Map<String, String> newDics = new HashMap();
    String updatePlayWords = "";
    for (Object obj : words.keySet()) {
      String key = obj.toString();
      String eng = key.toString().trim().toLowerCase();
      String rus = SetupProps.convertToIso(words.getProperty(eng));
      if (SetupProps.getDictionaryProps().containsKey(eng)) {
        String rus2 = SetupProps.getRusWord(eng);
        if (rus2 != null && !rus2.contains(rus))
          rus = rus2 + (rus == null ? "" : ";" + rus);
        else if (rus2 != null)
          rus = rus2;
      }
      newDics.put(eng, rus);
      SetupProps.getDictionaryProps().put(eng, "");
      if (!updatePlayWords.isEmpty())
        updatePlayWords = "\n" + updatePlayWords;
      updatePlayWords = eng + "=" + rus + updatePlayWords;
    }

    File f = new File(SetupProps.getPlayWordsPath());
    if (!f.exists())
      f.createNewFile();
    FileOutputStream fos = new FileOutputStream(f);
    fos.write(updatePlayWords.getBytes());
    fos.close();

    String rows = "";
    List l = new ArrayList();
    l.addAll(SetupProps.getDictionaryProps().keySet());
    Collections.sort(l, new Comparator() {
      @Override
      public int compare(Object o1, Object o2) {
        return o2.toString().compareToIgnoreCase(o1.toString());
      }
    });
    for (Object key : l) {
      if (!rows.isEmpty())
        rows = "\n" + rows;
      String rus = (newDics.containsKey(key.toString()))
      ? newDics.get(key.toString())
      : SetupProps.getRusWord(key.toString());
      rows = key.toString().trim().toLowerCase() + "=" + rus + rows;
    }
    f = new File(SetupProps.getDicPath());
    if (!f.exists())
      f.createNewFile();
    fos = new FileOutputStream(f);
    fos.write(rows.getBytes());
    fos.close();
  }
}