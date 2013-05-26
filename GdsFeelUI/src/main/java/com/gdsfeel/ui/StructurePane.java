/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.ui;

import com.gdsfeel.Structure;
import com.gdsfeel.elements.GdsElement;
import com.gdsfeel.elements.GdsPrimitiveElement;
import java.awt.geom.Rectangle2D;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 *
 * @author kenjiro
 */
public class StructurePane extends StackPane {

  ObjectProperty<Structure> structure = new SimpleObjectProperty<Structure>();

  private Structure getStructure() {
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

  public StructurePane() {
    super();
    setStyle("-fx-background-color: black");
  }

  private void clear() {
    getChildren().clear();
  }

  private void loadElements() {
    Group group = new Group();
    ScrollPane scroller = new ScrollPane();
    scroller.setStyle("-fx-background-color: black");
    scroller.setContent(group);
    group.setStyle("-fx-background-color: red");
    for (GdsElement e : getStructure().getElements()) {
      group.getChildren().add(makeShape(e));
    }
    getChildren().add(scroller);
  }
  private final String SOLID_STYLE = "-fx-stroke-width: 1; -fx-fill: null; ";

  private Shape makeShape(GdsElement e) {
    java.awt.Color c = java.awt.Color.GRAY;
    Shape result;
    String colorCSS = "";
    if (e instanceof GdsPrimitiveElement) {
      c = GdsPrimitiveElement.lookupFrameColor((GdsPrimitiveElement) e);
      Path path = new Path();
      int i = 0;
      for (java.awt.geom.Point2D p2 : e.outlinePoints()) {
        if (i == 0) {
          path.getElements().add(new MoveTo(p2.getX(), p2.getY()));
        }
        else {
          path.getElements().add(new LineTo(p2.getX(), p2.getY()));
        }
        i++;
      }
      result = path;
    }
    else {
      Rectangle2D b = e.getBoundingBox();
      Rectangle r = new Rectangle(b.getX(), b.getY(), b.getWidth(), b.getHeight());
      result = r;
    }
    Color frameColor = Color.rgb(c.getRed(), c.getGreen(), c.getBlue());

    result.setStyle(SOLID_STYLE + "--fx-stroke: " + frameColor.toString());
    result.setStroke(frameColor);
    return result;
  }
}
