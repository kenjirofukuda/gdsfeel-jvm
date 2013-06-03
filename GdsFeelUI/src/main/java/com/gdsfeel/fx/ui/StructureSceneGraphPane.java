/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.fx.ui;

import com.gdsfeel.Structure;
import com.gdsfeel.elements.GdsElement;
import com.gdsfeel.elements.GdsPrimitiveElement;
import com.gdsfeel.util.Conv;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

/**
 * JavaFX Scene Graph direct use example
 *
 * @author kenjiro
 */
public class StructureSceneGraphPane extends StackPane {

  ObjectProperty<Structure> structure = new SimpleObjectProperty<Structure>();
  Map<Structure, Group> groupMap = new HashMap<Structure, Group>();
  DoubleProperty viewScale = new SimpleDoubleProperty(1.0);
  ObjectProperty<Group> activeGroup = new SimpleObjectProperty<Group>();

  public Group getActiveGroup() {
    return activeGroup.get();
  }

  public DoubleProperty viewScaleProperty() {
    return viewScale;
  }

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

  public StructureSceneGraphPane() {
    super();
    viewScale.addListener(new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
        if (getActiveGroup() != null && t1 != null) {
          getActiveGroup().setScaleX(t1.doubleValue());
          getActiveGroup().setScaleY(t1.doubleValue());
        }
      }
    });
    activeGroup.addListener(new ChangeListener<Group>() {
      public void changed(ObservableValue<? extends Group> ov, Group t, Group t1) {
        if (t1 != null) {
          viewScale.set(t1.getScaleX());
        }
      }
    });
    setStyle("-fx-background-color: black");
  }

  private void clear() {
    getChildren().clear();
  }

  private void loadElements() {
    Group group;
    if (groupMap.containsKey(getStructure())) {
      group = groupMap.get(getStructure());
    }
    else {
      group = groupFromElements(getStructure().getElements());
      groupMap.put(getStructure(), group);
    }
    group.setStyle("-fx-background-color: red");

    ScrollPane scroller = new ScrollPane();
    scroller.setStyle("-fx-background-color: black");
    activeGroup.set(group);
    scroller.setContent(getActiveGroup());
    getChildren().add(scroller);
  }

  private Group groupFromElements(GdsElement[] elements) {
    return elementsToGroup(elements, null);
  }

  private Group elementsToGroup(GdsElement[] elements, Group group) {
    Group result = group;
    if (result == null) {
      result = new Group();
    }
    for (GdsElement e : elements) {
      result.getChildren().add(makeShape(e));
    }
    return result;
  }

  private Shape makeShape(GdsElement e) {
    java.awt.Color c = java.awt.Color.GRAY;
    Shape result;
    if (e instanceof GdsPrimitiveElement) {
      c = GdsPrimitiveElement.lookupFrameColor(e, java.awt.Color.GRAY);
      Path path = new Path();
      awtPointsToFXPath(e.outlinePoints(), path);
      result = path;
    }
    else {
      result = Conv.shapeFromSwing(e.getBoundingBox());
    }
    Color frameColor = Conv.fromSwing(c);
    Color fillColor = frameColor.deriveColor(1, 1, 0.5, 0.2);
    e.setRuntimeProperty("shape", result);
    e.setRuntimeProperty("dragger", new NodeDragger(result));
    e.setRuntimeProperty("hiliter", new ShapeHiliter(result));
    result.setFill(fillColor);
    result.setStroke(frameColor);
    result.setSmooth(false);
    result.setStrokeWidth(0.1);
    return result;
  }

  private void awtPointsToFXPath(java.awt.geom.Point2D[] points, Path path) {
    int i = 0;
    for (java.awt.geom.Point2D p2 : points) {
      if (i == 0) {
        path.getElements().add(new MoveTo(p2.getX(), p2.getY()));
      }
      else {
        path.getElements().add(new LineTo(p2.getX(), p2.getY()));
      }
      i++;
    }
  }
}
