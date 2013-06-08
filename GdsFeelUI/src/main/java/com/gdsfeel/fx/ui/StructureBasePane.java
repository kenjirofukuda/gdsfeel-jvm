/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.fx.ui;

import com.gdsfeel.Structure;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.StackPane;

/**
 *
 * @author kenjiro
 */
public class StructureBasePane extends StackPane {

  ObjectProperty<Structure> structure = new SimpleObjectProperty<>();
  DoubleProperty viewScale = new SimpleDoubleProperty(1.0);

  protected Structure getStructure() {
    return structure.get();
  }

  public void setStructure(Structure s) {
    structure.set(s);
    if (s == null) {
      clear();
    }
    else {
      loadElements();
    }
  }

  public ObjectProperty<Structure> structureProperty() {
    return structure;
  }

  public DoubleProperty viewScaleProperty() {
    return viewScale;
  }

  protected void clear() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  protected void loadElements() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
