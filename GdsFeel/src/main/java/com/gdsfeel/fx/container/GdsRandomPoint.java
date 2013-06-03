/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.fx.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.geometry.Rectangle2D;

/**
 *
 * @author kenjiro
 */
public class GdsRandomPoint {

  private double xRange;
  private double yRange;
  private double xStart;
  private double yStart;
  private double roundUnit;

  public GdsRandomPoint(double xStart, double xEnd,
          double yStart, double yEnd) {
    xRange = xEnd - xStart;
    yRange = yEnd - yStart;

    this.xStart = xStart;
    this.yStart = yStart;
    this.roundUnit = 0.0;
  }

  public GdsRandomPoint(Rectangle2D bounds) {
    this(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY());
  }

  /**
   * Get the value of roundUnit
   *
   * @return the value of roundUnit
   */
  public double getRoundUnit() {
    return roundUnit;
  }

  /**
   * Set the value of roundUnit
   *
   * @param roundUnit new value of roundUnit
   */
  public void setRoundUnit(double roundUnit) {
    this.roundUnit = roundUnit;
  }

  public GdsPoint next() {
    double x = Math.random() * xRange + xStart;
    double y = Math.random() * yRange + yStart;
    if (roundUnit != 0.0) {
      x = Math.round(x / roundUnit) * roundUnit;
      y = Math.round(y / roundUnit) * roundUnit;
    }
    return new GdsPoint(x, y);
  }

  public Collection<GdsPoint> values(int nItem) {
    List<GdsPoint> points = new ArrayList<>();
    for (int i = 0; i < nItem; i++) {
      points.add(next());
    }
    return points;
  }
}
