/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.ui;

import com.gdsfeel.Kind;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author kenjiro
 */
public class KindEntryPanel extends JPanel {

  protected JPanel colorPanel;
  protected JLabel kindLabel;
  protected JCheckBox selectableCheckBox;
  protected JCheckBox visibleCheckBox;
  protected Kind.Entry model;

  private final int BASE_SIZE = 18;
  
  public KindEntryPanel() {
    initComponents();
  }

  /**
   * Get the value of model
   *
   * @return the value of model
   */
  public Kind.Entry getModel() {
    return model;
  }

  /**
   * Set the value of model
   *
   * @param model new value of model
   */
  public void setModel(Kind.Entry model) {
    this.model = model;
    visibleCheckBox.setSelected(model.isVisible());
    selectableCheckBox.setSelected(model.isSelectable());
    colorPanel.setBackground(model.getColor());
    kindLabel.setText(model.getKind().getAddrev());
    kindLabel.setToolTipText(model.getKind().getDescription());
  }

  private void initComponents() {
    visibleCheckBox = new JCheckBox();
    selectableCheckBox = new JCheckBox();
    colorPanel = new JPanel();
    kindLabel = new JLabel();

    setName("Form"); // NOI18N
    setLayout(new FlowLayout(FlowLayout.LEADING, 1, 1));

    kindLabel.setText(""); // NOI18N
    kindLabel.setName("kindLabel"); // NOI18N
    validateSize(kindLabel, BASE_SIZE + 2, BASE_SIZE);
    add(kindLabel);
    
    visibleCheckBox.setText(""); // NOI18N
    visibleCheckBox.setName("visibleCheckBox"); // NOI18N
    visibleCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        visibleCheckBoxActionPerformed(evt);
      }
    });
    validateSize(visibleCheckBox, BASE_SIZE);
    add(visibleCheckBox);

    selectableCheckBox.setText(""); // NOI18N
    selectableCheckBox.setName("selectableCheckBox"); // NOI18N
    selectableCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        selectableCheckBoxActionPerformed(evt);
      }
    });
    validateSize(selectableCheckBox, BASE_SIZE);
    add(selectableCheckBox);

    colorPanel.setName("colorPanel"); // NOI18N
    colorPanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent evt) {
        colorPanelMouseClicked(evt);
      }
    });

    validateSize(colorPanel, BASE_SIZE);
    add(colorPanel);

  }

  private void validateSize(JComponent c, int size) {
     validateSize(c, size, size);
  }

  private void validateSize(JComponent c, int width, int height) {
    c.setPreferredSize(new Dimension(width, height));
    c.setMinimumSize(c.getPreferredSize());
    c.setMaximumSize(c.getPreferredSize());
  }

  protected void visibleCheckBoxActionPerformed(ActionEvent evt) {
    model.setVisible(visibleCheckBox.isSelected());
  }

  protected void selectableCheckBoxActionPerformed(ActionEvent evt) {
    model.setVisible(selectableCheckBox.isSelected());
  }

  protected void colorPanelMouseClicked(MouseEvent evt) {
    // TODO add your handling code here:
    
  }
}
