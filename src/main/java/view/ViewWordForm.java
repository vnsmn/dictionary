package view;

import service.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;

/**
 */
public class ViewWordForm {

  private JPanel viewPanel;
  private JLabel rusWord;
  private JTextPane englishWord;
  private boolean isVisEng = true;
  private boolean isVisRus = true;

  public void init() {
    englishWord.setText("");
    rusWord.setText("");
  }

  public void setEnglishWord(String w) {
    if (isVisEng)
      englishWord.setText(w);
    else
      englishWord.setText("");
  }

  public void setRussianWord(String w) {
    if (isVisRus)
      rusWord.setText(w);
    else
      rusWord.setText("");
  }

  public JPanel getViewPanel() {
    return viewPanel;
  }

  public void setVisEng(boolean visEng) {
    isVisEng = visEng;
    if (!isVisEng) englishWord.setText("");
  }

  public void setVisRus(boolean visRus) {
    isVisRus = visRus;
    if (!isVisRus) rusWord.setText("");
  }
}