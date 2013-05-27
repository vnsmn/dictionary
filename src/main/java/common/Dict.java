package common;

import service.ActionListener;
import service.DicService;
import service.GraberAudio;
import service.PlayingBackAudio;
import view.ViewWordForm;

import javax.swing.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 */
public class Dict {
  static ViewWordForm viewWord;
  static JFrame frame;
  static DicService dicService;

  static public void main(String ... args) throws Exception {
    dicService = new DicService();
    viewWord = new ViewWordForm();
    JFrame.setDefaultLookAndFeelDecorated(true);
    frame = new JFrame("Dictionary");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(viewWord.getViewPanel());
    frame.setSize(700, 200);
    frame.setVisible(true);
    frame.setAlwaysOnTop(true);
    final ActionListener actionListener = new ActionListener<AbstractMap.SimpleEntry>() {
      @Override
      public void execute(AbstractMap.SimpleEntry value) {
        if ("eng".equals(value.getKey().toString())) {
          viewWord.setEnglishWord(value.getValue().toString());
          viewWord.setSelEngWord(value.getValue().toString());
        }
        if ("rus".equals(value.getKey().toString())) {
          viewWord.setRussianWord(value.getValue().toString());
        }
        if ("title".equals(value.getKey().toString())) {
          frame.setTitle(value.getValue().toString());
        }
        if ("err".equals(value.getKey().toString())) {
          viewWord.setStatusEngWord(value.getValue().toString());
        }
      }
    };
    viewWord.init();
    viewWord.addDelayListener(
        new ActionListener<Integer>() {
          @Override
          public void execute(Integer value) {
            dicService.setDelay(value);
          }
        }
    );
    viewWord.addAutoListener(
        new ActionListener<Boolean>() {
          @Override
          public void execute(Boolean value) {
            dicService.rePlay(value);
            dicService.play(actionListener);
          }
        }
    );
    viewWord.addPlayListener(
        new ActionListener<Boolean>() {
          @Override
          public void execute(Boolean value) {
            String eng = viewWord.getSelEngWord();
            viewWord.setEnglishWord(eng.toLowerCase());
            viewWord.setRussianWord(dicService.getRusWord(eng));
            dicService.sound(eng);
          }
        }
    );
    viewWord.addPlayDirectListener(
        new ActionListener<Boolean>() {
          @Override
          public void execute(Boolean value) {
            dicService.direct(value);
            dicService.play(actionListener);
            viewWord.setAutoMode(true);
          }
        }
    );
    viewWord.addRefreshListener(
        new ActionListener<Void>() {
          @Override
          public void execute(Void value) {
              dicService.refresh();
              dicService.play(actionListener);
              viewWord.setAutoMode(true);
          }
        }
    );
    viewWord.addPauseListener(
        new ActionListener<Void>() {
          @Override
          public void execute(Void value) {
            dicService.pause();
          }
        }
    );
    viewWord.addPlayNextListener(
        new ActionListener<Void>() {
          @Override
          public void execute(Void value) {
            dicService.next();
          }
        }
    );
    viewWord.addGrabSoundListener(
        new ActionListener<Void>() {
          @Override
          public void execute(Void value) {
            String s = viewWord.getSndText();
            String err = dicService.grabSound(s);
            viewWord.setStatusEngWord(err);
          }
        }
    );
    viewWord.addCreateDicListener(
        new ActionListener<Void>() {
          @Override
          public void execute(Void value) {
            try {
              dicService.createDic();
            } catch (Exception ex) {
              viewWord.setStatusEngWord(ex.getMessage());
            }
          }
        }
    );
    Thread.sleep(1000);
    dicService.play(actionListener);
  }
}