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
  public void init() {
    englishWord.setText("");
    rusWord.setText("");
  }

  public void setEnglishWord(String w) {
    englishWord.setText(w);
  }

  public void setRussianWord(String w) {
    rusWord.setText(w);
  }

  public JPanel getViewPanel() {
    return viewPanel;
  }
}