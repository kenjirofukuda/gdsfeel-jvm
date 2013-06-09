/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.fx.container;

import java.awt.geom.Point2D;

/**
 *
 * @author kenjiro
 */
public final class GdsPoint {

  private double[] v;

  public GdsPoint(double x, double y) {
    v = new double[2];
    v[0] = x;
    v[1] = y;
  }

  public int getDimension() {
    return v.length;
  }

  public final double getX() {
    return v[0];
  }

  public final double getY() {
    return v[1];
  }

  public final java.awt.geom.Point2D asSwing() {
    return new Point2D.Double(getX(), getY());
  }

  public final javafx.geometry.Point2D asFX() {
    return new javafx.geometry.Point2D(getX(), getY());
  }

  public static GdsPoint fromSwing(java.awt.geom.Point2D p) {
    return new GdsPoint(p.getX(), p.getY());
  }

  public static GdsPoint fromFX(javafx.geometry.Point2D p) {
    return new GdsPoint(p.getX(), p.getY());
  }

  public static GdsPoint xy(double x, double y) {
    return new GdsPoint(x, y);
  }

  @Override
  public final String toString() {
    return "GP[" + v[0] + "," + v[1] + "]";
  }

  public final String toStringFormatted() {
    return String.format("GP[%10.3f, %10.3f]", v[0], v[1]);
  }

  public final String toStringFormatted(int index) {
    return String.format("[%3d]GP[%10.3f, %10.3f]", index, v[0], v[1]);
  }
}
