package view;

import service.ActionListener;
import service.PlayingBackAudio;
import service.SetupProps;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 */
public class ViewDicForm {
  private JComboBox dicTypeComboBox;
  private JPanel panel1;
  private JTable dicTable;
  private JButton playSelButton;
  private JButton stopPlayButton;
  private JButton allUnSelButton;
  private JButton allSelButton;
  private Thread thread;

  public JPanel getViewPanel() {
    return panel1;
  }

  public void refresh(final ActionListener<String> playTypeListener) {
    dicTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        playTypeListener.execute(((AbstractMap.SimpleEntry<String, String>) dicTypeComboBox.getSelectedItem()).getValue().toString());
        fillDics();
      }
    });
    dicTypeComboBox.removeAllItems();
    for (Map.Entry<String, String> ent : SetupProps.getPlays().entrySet())
      dicTypeComboBox.addItem(new AbstractMap.SimpleEntry<String, String>(ent.getKey(), ent.getValue()) {
        public String toString() {
          return this.getKey();
        }
      });
    fillDics();
    playSelButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        synchronized (panel1) {
          thread = new Thread(new Runnable() {
            @Override
            public void run() {
              for (int row = 0; row < dicTable.getRowCount(); row ++) {
                if (dicTable.getValueAt(row, 0).equals(false))
                  continue;
                dicTable.setRowSelectionInterval(row, row);
                String eng = dicTable.getValueAt(row, 1).toString();
                File f = SetupProps.getSoundProps().get(eng);
                if (f != null && f.exists()) {
                  PlayingBackAudio.playFromFile(f.getAbsolutePath(), 2);
                  try {
                    Thread.sleep(SetupProps.getDelaySound() * 20);
                  } catch (InterruptedException ex) {
                    ex.printStackTrace();
                  }
                }
              }
              playSelButton.setEnabled(true);
            }
          });
          thread.start();
          playSelButton.setEnabled(false);
        }
      }
    });
    stopPlayButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        synchronized (panel1) {
          if (thread != null)
            thread.stop();
        }
        playSelButton.setEnabled(true);
      }
    });
    allSelButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        for (int row = 0; row < dicTable.getRowCount(); row ++) {
          if (dicTable.getValueAt(row, 0).equals(false))
            dicTable.setValueAt(true, row, 0);
        }
      }
    });
    allUnSelButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        for (int row = 0; row < dicTable.getRowCount(); row ++) {
          if (dicTable.getValueAt(row, 0).equals(true))
            dicTable.setValueAt(false, row, 0);
        }
      }
    });
  }

  private void fillDics() {
    dicTable.removeAll();
    DefaultTableModel model = new DefaultTableModel(new String[] {"sel", "eng", "rus"}, 0) {
      public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 0 ? Boolean.class : String.class;
      }
      public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
      }
    };
    dicTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    dicTable.setRowSelectionAllowed(true);
    dicTable.setModel(model);
    dicTable.getColumnModel().getColumn(0).setMaxWidth(30);
    dicTable.getColumnModel().getColumn(1).setPreferredWidth(300);
    dicTable.getColumnModel().getColumn(2).setPreferredWidth(300);
    for (Map.Entry ent : SetupProps.getPlayWordProps().entrySet())
      model.addRow(new Object[] {false, ent.getKey(), SetupProps.getRusWord(ent.getKey().toString())});
    dicTable.setAutoCreateRowSorter(true);
    List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
    sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
    dicTable.getRowSorter().setSortKeys(sortKeys);
    dicTable.repaint();
  }
}
