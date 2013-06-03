/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.fx.container;

import static com.gdsfeel.elements.GdsElement.BIG_VAL;
import java.util.Collection;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;

/**
 *
 * @author kenjiro
 */
public class GdsMutablePoints extends SimpleListProperty<GdsPoint> {

  private SimpleObjectProperty<Rectangle2D> bounds = new SimpleObjectProperty<>(this, "bounds");

  public GdsMutablePoints(Object aThis, String propertyName) {
    this(aThis, propertyName, FXCollections.<GdsPoint>observableArrayList());
  }

  public GdsMutablePoints(Object o, String string, ObservableList<GdsPoint> ol) {
    super(o, string, ol);
    this.addListener(new InvalidationListener() {
      @Override
      public void invalidated(Observable o) {
        handleInvalidate(o);
      }
    });
  }

  public GdsMutablePoints() {
    this(null, "");
  }

  private void handleInvalidate(Observable o) {
    Rectangle2D r = calcBoundingBox(this);
    bounds.set(r);
  }

  public ReadOnlyObjectProperty<Rectangle2D> boundsProperty() {
    return bounds;
  }

  public Rectangle2D getBounds() {
    return bounds.get();
  }

  public boolean add(double x, double y) {
    return add(new GdsPoint(x, y));
  }

  public void addAll(java.awt.geom.Point2D[] points) {
    for (java.awt.geom.Point2D p : points) {
      add(p.getX(), p.getY());
    }
  }

  public void printOn(StringBuffer sb) {
    printOn(sb, true);
  }

  public void printOn(StringBuffer sb, boolean newLine) {
    int i = 0;
    for (GdsPoint p : this) {
      sb.append(p.toStringFormatted(i++));
      if (newLine) {
        sb.append("\n");
      }
    }
  }

  public static Rectangle2D calcBoundingBox(Collection<GdsPoint> outlinePoints) {
    // TOUCH: scala
    double xmin = BIG_VAL;
    double xmax = -BIG_VAL;
    double ymin = BIG_VAL;
    double ymax = -BIG_VAL;
    for (GdsPoint p : outlinePoints) {
      if (p.getX() < xmin) {
        xmin = p.getX();
      }
      if (p.getX() > xmax) {
        xmax = p.getX();
      }
      if (p.getY() < ymin) {
        ymin = p.getY();
      }
      if (p.getY() > ymax) {
        ymax = p.getY();
      }
    }
    return new Rectangle2D(xmin, ymin, xmax - xmin, ymax - ymin);
  }
}
