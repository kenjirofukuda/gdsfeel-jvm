/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.fx.container;

import static com.gdsfeel.elements.GdsElement.BIG_VAL;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
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
public class GdsPoints extends SimpleListProperty<GdsPoint> {

  private SimpleObjectProperty<Rectangle2D> bounds = new SimpleObjectProperty<>(this, "bounds");

  public GdsPoints(Object propertyOwner, String propertyName) {
    this(propertyOwner, propertyName, FXCollections.<GdsPoint>observableArrayList());
  }

  public GdsPoints(Object propertyOwner, String propertyName, ObservableList<GdsPoint> ol) {
    super(propertyOwner, propertyName, ol);
    this.addListener(new InvalidationListener() {
      @Override
      public void invalidated(Observable o) {
        handleInvalidate(o);
      }
    });
  }

  public GdsPoints() {
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

  private void asAWTPoints(Collection<java.awt.geom.Point2D> outPoints) {
    for (GdsPoint p : this) {
      outPoints.add(p.asSwing());
    }
  }

  /*
   * @param at still AffineTransform wait until JDK 8's FX Transform
   * @param outPoints null - return value is new Object
   */
  public GdsPoints transformedPoints(AffineTransform at, GdsPoints outPoints) {
    GdsPoints result = outPoints;
    if (outPoints == null) {
      result = new GdsPoints();
    }
    ArrayList<java.awt.geom.Point2D> awtPoints = new ArrayList<>();
    asAWTPoints(awtPoints);
    java.awt.geom.Point2D[] src = awtPoints.toArray(new java.awt.geom.Point2D[0]);
    java.awt.geom.Point2D[] dest = new java.awt.geom.Point2D.Double[getSize()];
    at.transform(src, 0, dest, 0, src.length);
    for (java.awt.geom.Point2D p : dest) {
      result.add(GdsPoint.fromSwing(p));
    }
    return result;
  }
}
