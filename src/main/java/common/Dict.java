package common;

import service.*;
import view.ViewDicForm;
import view.ViewSettingForm;
import view.ViewWordForm;

import javax.swing.*;
import java.util.*;

/**
 */
public class Dict {
  static ViewWordForm viewWord;
  static ViewSettingForm viewSetting;
  static ViewDicForm viewDicForm;
  static JFrame frame;
  static JFrame frameSettings;
  static JFrame frameDicView;
  static DicService dicService;
  static PlayService playService;


  static public void main(String ... args) throws Exception {
    dicService = new DicService();
    playService = new PlayService();
    viewWord = new ViewWordForm();
    viewSetting = new ViewSettingForm();
    viewDicForm = new ViewDicForm();
    JFrame.setDefaultLookAndFeelDecorated(true);
    frame = new JFrame("Dictionary");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(viewWord.getViewPanel());
    frame.setSize(700, 200);
    frame.setVisible(true);
    frame.setAlwaysOnTop(true);
    frameSettings = new JFrame("Settings");
    frameSettings.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frameSettings.getContentPane().add(viewSetting.getViewPanel());
    frameSettings.setSize(viewSetting.getViewPanel().getPreferredSize().width + 10,
        viewSetting.getViewPanel().getPreferredSize().height + 10);
    frameSettings.setVisible(true);
    frameSettings.setAlwaysOnTop(true);
    frameDicView = new JFrame("Dic View");
    frameDicView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frameDicView.getContentPane().add(viewDicForm.getViewPanel());
    frameDicView.setSize(viewDicForm.getViewPanel().getPreferredSize().width + 10,
        viewDicForm.getViewPanel().getPreferredSize().height + 10);
    frameDicView.setVisible(true);
    frameDicView.setAlwaysOnTop(true);

    final ActionListener actionListener = new ActionListener<AbstractMap.SimpleEntry>() {
      @Override
      public void execute(AbstractMap.SimpleEntry value) {
        if ("eng".equals(value.getKey().toString())) {
          viewWord.setEnglishWord(value.getValue().toString());
        }
        if ("rus".equals(value.getKey().toString())) {
          viewWord.setRussianWord(value.getValue().toString());
        }
        if ("title".equals(value.getKey().toString())) {
          frame.setTitle(value.getValue().toString());
        }
        if ("err".equals(value.getKey().toString())) {
          viewSetting.setErrText(value.getValue().toString());
        }
        if ("err_rus".equals(value.getKey().toString())) {
          viewSetting.setErrRusText(value.getValue().toString());
        }
        if ("err_snd".equals(value.getKey().toString())) {
          viewSetting.setErrSndText(value.getValue().toString());
        }
      }
    };

    final ActionListener<String> refreshActionListener = new ActionListener<String>() {
      @Override
      public void execute(String value) {
        if (value != null && !value.isEmpty())
          SetupProps.setPlayWordsPath(value);
        try {
          viewSetting.setErrSndText("");
          viewSetting.setErrRusText("");
          viewSetting.setErrSndText("");
          viewSetting.stopPlay();
          playService.stop();
          SetupProps.refresh();
          playService.refresh(actionListener);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };

    viewSetting.addVisibleEngListener(new ActionListener<Boolean>() {
      @Override
      public void execute(Boolean value) {
        viewWord.setVisEng(value);
      }
    });

    viewSetting.addVisibleRusListener(new ActionListener<Boolean>() {
      @Override
      public void execute(Boolean value) {
        viewWord.setVisRus(value);
      }
    });

    viewSetting.addAutoListener(
        new ActionListener<Boolean>() {
          @Override
          public void execute(Boolean value) {
            playService.setAuto(value);
          }
        }
    );

    viewSetting.addPlayListener(
        new ActionListener<Boolean>() {
          @Override
          public void execute(Boolean value) {
            viewWord.setEnglishWord("");
            viewWord.setRussianWord("");
            viewSetting.setErrSndText("");
            if (value)
              playService.play(actionListener);
            else
              playService.stop();
          }
        }
    );

    viewSetting.addPlayNextListener(
        new ActionListener<Void>() {
          @Override
          public void execute(Void value) {
            playService.next();
          }
        }
    );

    viewSetting.addRefreshListener(
        new ActionListener<Void>() {
          @Override
          public void execute(Void value) {
            try {
              refreshActionListener.execute("");
              viewDicForm.refresh(refreshActionListener);
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        }
    );

    viewSetting.addPauseListener(
        new ActionListener<Void>() {
          @Override
          public void execute(Void value) {
            playService.pause();
          }
        }
    );

    viewSetting.addDelayListener(
        new ActionListener<Integer>() {
          @Override
          public void execute(Integer value) {
            SetupProps.setDelaySound(value);
            playService.setDelay(value);
          }
        }
    );

    viewSetting.addGrabSoundListener(
        new ActionListener<Void>() {
          @Override
          public void execute(Void value) {
            String s = viewSetting.getGrabWords();
            String err = dicService.grabSound(s);
            viewSetting.setErrSndText(err);
          }
        }
    );

    viewSetting.addCreateDicListener(
        new ActionListener<Void>() {
          @Override
          public void execute(Void value) {
            try {
              dicService.refreshDic();
              SetupProps.refresh();
              playService.refresh(actionListener);
            } catch (Exception ex) {
              viewSetting.setErrSndText(ex.getMessage());
            }
          }
        }
    );

    viewDicForm.refresh(refreshActionListener);
  }
}