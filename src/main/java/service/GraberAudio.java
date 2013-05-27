package service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

/**
 */
public class GraberAudio {
  public static byte[] get(String eng) throws IOException {
    URL url = new URL(String.format("http://ssl.gstatic.com/dictionary/static/sounds/de/0/%s.mp3", eng));
    URLConnection urlConnection = url.openConnection();
    urlConnection.setDoOutput(true);
    InputStream inputStream = urlConnection.getInputStream();
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    try {
      byte[] bs = new byte[1024];
      int size = 0;
      while((size = inputStream.read(bs)) != -1) {
        bos.write(Arrays.copyOfRange(bs, 0, size));
      }
      } finally {
        inputStream.close();
      }
    return bos.toByteArray();
  }

  static public void main(String ... args) throws Exception {
    get("advise");
  }
}
