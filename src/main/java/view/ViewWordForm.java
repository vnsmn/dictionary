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
  private JSlider sliderDelay;
  private JCheckBox checkBoxEng;
  private JCheckBox checkBoxRus;
  private JCheckBox autoCheckBox;
  private JButton playButton;
  private JTextPane wordTextPane;
  private JTextPane englishWord;
  private JCheckBox playOrderCheckBox;
  private JButton refreshButton;
  private JButton pauseButton;
  private JButton nextButton;
  private JButton captureButton;
  private JTextPane sndTextPane;
  private JTextField statusTextField;
  private JButton createDicButton;

  public void init() {
    englishWord.setText("");
    rusWord.setText("");
    checkBoxEng.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        englishWord.setVisible(checkBoxEng.isSelected());
      }
    });
    checkBoxRus.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        rusWord.setVisible(checkBoxRus.isSelected());
      }
    });
  }

  public void addDelayListener(final ActionListener<Integer> listener) {
    sliderDelay.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        listener.execute(sliderDelay.getValue());
      }
    });
  }

  public void addAutoListener(final ActionListener<Boolean> autoListener) {
    autoCheckBox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        autoListener.execute(autoCheckBox.isSelected());
      }
    });
  }

  public void addPlayListener(final ActionListener<Boolean> playListener) {
    playButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        playListener.execute(true);
      }
    });
  }

  public void addPlayDirectListener(final ActionListener<Boolean> playOrderListener) {
    playOrderCheckBox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        playOrderListener.execute(playOrderCheckBox.isSelected());
      }
    });
  }

  public void addRefreshListener(final ActionListener<Void> refreshListener) {
    refreshButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        refreshListener.execute(null);
      }
    });
  }

  public void addPauseListener(final ActionListener<Void> pauseListener) {
    pauseButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        pauseListener.execute(null);
      }
    });
  }

  public void addPlayNextListener(final ActionListener<Void> nextListener) {
    nextButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        nextListener.execute(null);
      }
    });
  }

  public void addGrabSoundListener(final ActionListener<Void> grabSoundListener) {
    captureButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        grabSoundListener.execute(null);
      }
    });
  }

  public void addCreateDicListener(final ActionListener<Void> createDicListener) {
    createDicButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        createDicListener.execute(null);
      }
    });
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

  public String getSelEngWord() {
    return wordTextPane.getText();
  }

  public String getSndText() {
    return sndTextPane.getText();
  }

  public void setAutoMode(boolean b) {
    autoCheckBox.setSelected(b);
  }

  public void setSelEngWord(String selEngWord) {
    wordTextPane.setText(selEngWord);
  }

  public void setStatusEngWord(String status) {
    statusTextField.setText(status);
  }
}