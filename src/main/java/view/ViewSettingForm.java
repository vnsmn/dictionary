package view;

import service.ActionListener;
import service.SetupProps;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;

/**
 */
public class ViewSettingForm {

  private JPanel panel1;
  private JTabbedPane tabbedPane1;
  private JSlider sliderDelay;
  private JCheckBox checkBoxEng;
  private JCheckBox checkBoxRus;
  private JCheckBox autoCheckBox;
  private JCheckBox playOrderCheckBox;
  private JButton refreshButton;
  private JButton pauseButton;
  private JButton nextButton;
  private JButton captureButton;
  private JButton playButton;

  private JTextPane grabWordsTextPane;
  private JButton createDicButton;
  private JTextField errTextField;
  private JTextField errRusTextField;
  private JTextField errSndTextField;
  private JComboBox playComboBox;

  private void createUIComponents() {
  }

  public JPanel getViewPanel() {
    return panel1;
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
        if (playButton.isSelected()) {
          playListener.execute(true);
          playButton.setSelected(false);
          playButton.setText("stop");
        } else {
          playListener.execute(false);
          playButton.setSelected(true);
          playButton.setText("start");
        }
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

  public String getPlayCategory() {
    return playComboBox.getSelectedItem().toString();
  }

  public String getSelEngWord() {
    return grabWordsTextPane.getText();
  }

  public void setAutoMode(boolean b) {
    autoCheckBox.setSelected(b);
  }

  public void setSelEngWord(String selEngWord) {
    grabWordsTextPane.setText(selEngWord);
  }

  public String getGrabWords() {
    return grabWordsTextPane.getText();
  }

  public JTextField getErrTextField() {
    return errTextField;
  }

  public void setErrText(String s) {
    if (!this.errTextField.getText().contains(s))
      this.errTextField.setText(s);
  }

  public String getErrRusText() {
    return errRusTextField.getText();
  }

  public void setErrRusText(String s) {
    if (!this.errRusTextField.getText().contains(s))
      this.errRusTextField.setText(s);
  }

  public String getErrSndText() {
    return errSndTextField.getText();
  }

  public void setErrSndText(String s) {
    if (!this.errSndTextField.getText().contains(s))
      this.errSndTextField.setText(s);
  }

//  public void refreshPlays() {
//    playComboBox.removeAllItems();
//    for (String s : SetupProps.getPlays())
//      playComboBox.addItem(s);
//  }
}
