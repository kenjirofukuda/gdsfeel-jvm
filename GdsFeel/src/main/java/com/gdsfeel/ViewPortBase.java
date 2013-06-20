/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Swing version
 *
 * @author kenjiro
 */
public class ViewPortBase {

  private static Log log = LogFactory.getLog(ViewPortBase.class);
  private ArrayList<AffineTransform> transformStack;
  private AffineTransform transform;
  private AffineTransform basicTransform;
  // device
  private double portCenterX;
  private double portCenterY;
  private double portWidth;
  private double portHeight;
  // model
  private double scale;
  private double centerX;
  private double centerY;
  private long countDirty;

  public ViewPortBase() {
    transformStack = new ArrayList<>();
    scale = 1.0;
    centerX = 0;
    centerY = 0;
    portCenterX = 0;
    portCenterY = 0;
    countDirty = 0;
  }

  protected double getPortWidth() {
    return portWidth;
  }

  protected double getPortHeight() {
    return portHeight;
  }

  protected double getCenterX() {
    return centerX;
  }

  protected double getCenterY() {
    return centerY;
  }

  public void setPortSize(double w, double h) {
    int c = 0;
    if (portWidth != w) {
      portWidth = w;
      markDirty();
    }
    if (portHeight != h) {
      portHeight = h;
      markDirty();
    }
  }

  public void setCenter(double x, double y) {
    if (centerX != x) {
      centerX = x;
      markDirty();
    }
    if (centerY != y) {
      centerY = y;
      markDirty();
    }
  }

  public void setScale(double newScale) {
    Validate.isTrue(newScale != 0.0, "scale disallow 0.0;");
    if (scale == newScale) {
      return;
    }
    if (newScale <= 0.001) {
      log.error(String.format(
              "newScale is %f convert to safe margin value ==> 0.001", newScale));
      newScale = 0.001;
    }
    scale = newScale;
    markDirty();
  }

  public double getScale() {
    return scale;
  }

  public void setPortCenter(double x, double y) {
    if (portCenterX != x) {
      portCenterX = x;
      markDirty();
    }
    if (portCenterY != y) {
      portCenterY = y;
      markDirty();
    }
  }

  public void resetPortCenter() {
    setPortCenter(portWidth / 2.0, portHeight / 2.0);
  }

  public void zoom(double ratio) {
    setScale(getScale() * ratio);
  }

  public void setBounds(double x, double y, double w, double h) {
    double hRatio = (double) portWidth / w;
    double vRatio = (double) portHeight / h;
    double newScale = hRatio < vRatio ? hRatio : vRatio;
    double newCenterX = x + (w / 2.0);
    double newCenterY = y + (h / 2.0);

    if (newScale != scale) {
      Validate.isTrue(newScale != 0.0, "scale disallow 0.0; ");
      scale = newScale;
      markDirty();
    }
    if (centerX != newCenterX) {
      centerX = newCenterX;
      markDirty();
    }
    if (centerY != newCenterY) {
      centerY = newCenterY;
      markDirty();
    }
  }

  public AffineTransform getBasicTransform() {
    checkDirty();
    if (basicTransform == null) {
      basicTransform = lookupBasicTransform();
    }
    return basicTransform;
  }

  public AffineTransform getTransform() {
    checkDirty();
    if (transform == null) {
      AffineTransform newTransform = new AffineTransform();
      newTransform.concatenate(getBasicTransform());
      for (AffineTransform tx : transformStack) {
        newTransform.concatenate(tx);
      }
      transform = newTransform;
    }
    return transform;
  }

  public void pushTransform(AffineTransform newTransform) {
    Validate.notNull(newTransform);
    transformStack.add(newTransform);
    transform = null;
  }

  public AffineTransform popTransform() {
    if (transformStack.isEmpty()) {
      return null;
    }
    AffineTransform result;
    result = transformStack.remove(transformStack.size() - 1);
    transform = null;
    return result;
  }

  private AffineTransform lookupBasicTransform() {
    AffineTransform tx = new AffineTransform();
    tx.concatenate(flipTransform());
    tx.concatenate(toCenterTransform());
    tx.concatenate(AffineTransform.getScaleInstance(
            scale * getVisibleRatio(), scale * getVisibleRatio()));
    tx.concatenate(viewToCenterTransform());
    return tx;
  }

  private AffineTransform flipTransform() {
    AffineTransform tx = new AffineTransform();
    tx.translate(0, portHeight);
    tx.scale(1, -1);
    return tx;
  }

  private AffineTransform toCenterTransform() {
    AffineTransform tx = new AffineTransform();
    tx.translate(portCenterX, portHeight - portCenterY);
    return tx;
  }

  private AffineTransform viewToCenterTransform() {
    AffineTransform tx = new AffineTransform();
    tx.translate(-centerX, -centerY);
    return tx;
  }

  public double getVisibleRatio() {
    // TODO: add setter and more ...
    return 0.98;
  }

  private void markDirty() {
    countDirty += 1;
  }

  private void checkDirty() {
    if (countDirty > 0) {
      damageTransform();
    }
  }

  private void damageTransform() {
    transform = null;
    basicTransform = null;
    countDirty = 0;
  }
}
// vim: ts=2 sw=2 expandtab

