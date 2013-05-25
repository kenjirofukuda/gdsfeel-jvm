/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.ui.swing;

import com.gdsfeel.Kind;
import com.gdsfeel.Kinds;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author kenjiro
 */
public class KindsPanel extends JPanel {
  
  private Kinds model;

  public KindsPanel () {
    initComponents();
  }
  
  /**
   * Get the value of model
   *
   * @return the value of model
   */
  public Kinds getModel() {
    return model;
  }

  /**
   * Set the value of model
   *
   * @param model new value of model
   */
  public void setModel(Kinds model) {
    this.model = model;
    KindAllPanel all = new KindAllPanel();
    all.setModel(new Kind.Entry(new Kind("--", "all attribute change")));
    add(all);
    for (Kind k : Kinds.KEYS) {
      Kind.Entry e = model.get(k);
      KindEntryPanel p = new KindEntryPanel();
      p.setModel(e);
      add(p);
    }
  }

  private void initComponents() {
    setName("Form"); // NOI18N
    setLayout(new GridLayout(Kinds.KEYS.length + 1, 1));
  }
}


class KindAllPanel extends KindEntryPanel {

  @Override
  protected void visibleCheckBoxActionPerformed(ActionEvent evt) {
    super.visibleCheckBoxActionPerformed(evt); //To change body of generated methods, choose Tools | Templates.
    KindsPanel p = (KindsPanel) this.getParent();
    for (Component c : p.getComponents()) {
      KindEntryPanel e = (KindEntryPanel) c;
      e.visibleCheckBox.setSelected(visibleCheckBox.isSelected());
    }
  }

  @Override
  protected void selectableCheckBoxActionPerformed(ActionEvent evt) {
    super.selectableCheckBoxActionPerformed(evt); //To change body of generated methods, choose Tools | Templates.
    KindsPanel p = (KindsPanel) this.getParent();
    for (Component c : p.getComponents()) {
      KindEntryPanel e = (KindEntryPanel) c;
      e.selectableCheckBox.setSelected(selectableCheckBox.isSelected());
    }
  }

  @Override
  protected void colorPanelMouseClicked(MouseEvent evt) {
    super.colorPanelMouseClicked(evt); //To change body of generated methods, choose Tools | Templates.
  }
  
}