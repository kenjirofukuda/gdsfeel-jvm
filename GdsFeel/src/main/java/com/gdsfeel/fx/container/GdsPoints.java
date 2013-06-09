/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.fx.container;

import static com.gdsfeel.elements.GdsElement.BIG_VAL;
import java.awt.geom.AffineTransform;
import java.util.Collection;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import org.apache.commons.lang.Validate;

/**
 *
 * @author kenjiro
 */
public class GdsPoints extends SimpleListProperty<GdsPoint> {

  private SimpleObjectProperty<Rectangle2D> bounds;

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
    if (bounds == null) {
      return;
    }
    bounds.set(null);
  }

  public ReadOnlyObjectProperty<Rectangle2D> boundsProperty() {
    if (bounds == null) {
      bounds = new SimpleObjectProperty<>(this, "bounds");
    }
    return bounds;
  }

  public Rectangle2D getBounds() {
    if (bounds == null) {
      bounds = new SimpleObjectProperty<>(this, "bounds");
    }
    if (bounds.get() == null) {
      Rectangle2D r = calcBoundingBox(this);
      bounds.set(r);
    }
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
    if (outlinePoints.isEmpty()) {
      return Rectangle2D.EMPTY;
    }
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
    Validate.isTrue((xmax - xmin) >= 0, String.format("(%f - %f) >= 0", xmax, xmin));
    Validate.isTrue((ymax - ymin) >= 0, String.format("(%f - %f) >= 0", ymax, ymin));
    return new Rectangle2D(xmin, ymin, xmax - xmin, ymax - ymin);
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
    int numPoints = getSize();
    int allocSize = numPoints * 2;
    double[] outlineXY = new double[allocSize];
    this.flattenXY(outlineXY);
    double[] xy = new double[allocSize];
    at.transform(outlineXY, 0, xy, 0, numPoints);
    GdsPoint[] l = new GdsPoint[numPoints];
    for (int i = 0; i < numPoints; i++) {
      int ai = i * 2;
      l[i] = GdsPoint.xy(xy[ai], xy[ai + 1]);
    }
    result.addAll(l);
    return result;
  }

  /*
   * @parama xy must specify new double[length * 2] area
   */
  public void flattenXY(double[] xy) {
    for (int i = 0; i < getSize(); i++) {
      GdsPoint p = get(i);
      int ai = i * 2;
      xy[ai] = p.getX();
      xy[ai + 1] = p.getY();
    }
  }
}
